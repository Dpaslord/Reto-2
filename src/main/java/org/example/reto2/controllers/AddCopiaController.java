package org.example.reto2.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.reto2.copia.Copia;
import org.example.reto2.copia.CopiaService;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.pelicula.PeliculaRepository;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.user.User;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de añadir una nueva copia de película (add-copia-view.fxml).
 * Permite al usuario seleccionar una película existente, definir el estado, soporte y cantidad
 * de la nueva copia a añadir a su colección.
 */
public class AddCopiaController implements Initializable {

    private static final Logger logger = Logger.getLogger(AddCopiaController.class.getName());

    @javafx.fxml.FXML
    private ComboBox<Pelicula> comboPelicula;
    @javafx.fxml.FXML
    private ComboBox<String> comboEstado;
    @javafx.fxml.FXML
    private ComboBox<String> comboSoporte;
    @javafx.fxml.FXML
    private TextField txtCantidad;

    private PeliculaRepository peliculaRepository;
    private CopiaService copiaService;
    private User currentUser;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Carga las películas disponibles en el ComboBox, y las opciones para estado y soporte.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando AddCopiaController.");
        peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());
        copiaService = new CopiaService();
        currentUser = (User) SimpleSessionService.getInstance().getObject("user");

        comboPelicula.setItems(FXCollections.observableList(peliculaRepository.findAll()));
        comboEstado.setItems(FXCollections.observableArrayList("bueno", "gastado", "dañado"));
        comboSoporte.setItems(FXCollections.observableArrayList("dvd", "blue-ray")); // Corregido

        logger.info("AddCopiaController inicializado. Películas y opciones cargadas.");
    }

    /**
     * Maneja la acción de añadir una nueva copia a la colección del usuario.
     * Valida los campos de entrada y crea una nueva Copia en la base de datos.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addCopia(ActionEvent actionEvent) {
        logger.info("Intento de añadir nueva copia.");
        Pelicula selectedPelicula = comboPelicula.getSelectionModel().getSelectedItem();
        String selectedEstado = comboEstado.getSelectionModel().getSelectedItem();
        String selectedSoporte = comboSoporte.getSelectionModel().getSelectedItem();
        String cantidadText = txtCantidad.getText();

        if (selectedPelicula == null || selectedEstado == null || selectedSoporte == null || cantidadText.isEmpty()) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, rellena todos los campos.", "");
            logger.warning("Campos incompletos al intentar añadir copia.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadText);
            if (cantidad <= 0) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Cantidad Inválida", "La cantidad debe ser un número positivo.", "");
                logger.warning("Cantidad inválida (<= 0) al intentar añadir copia.");
                return;
            }

            Copia newCopia = new Copia();
            newCopia.setPelicula(selectedPelicula);
            newCopia.setEstado(selectedEstado);
            newCopia.setSoporte(selectedSoporte);
            newCopia.setCantidad(cantidad);

            currentUser = copiaService.createNewCopia(newCopia, currentUser);
            SimpleSessionService.getInstance().setObject("user", currentUser);
            JavaFXUtil.setScene("/org/example/reto2/main-view.fxml");
            logger.info("Copia añadida exitosamente. Redirigiendo a main-view.");
        } catch (NumberFormatException e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de entrada", "Cantidad inválida", "Por favor, introduce un número válido para la cantidad.");
            logger.severe("Error de formato de número para la cantidad: " + cantidadText + ". " + e.getMessage());
        } catch (Exception e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo añadir la copia", "Ocurrió un error al guardar la copia: " + e.getMessage());
            logger.severe("Error inesperado al añadir copia: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de cancelar la adición de una copia.
     * Redirige de vuelta a la vista principal del usuario.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void cancel(ActionEvent actionEvent) {
        logger.info("Operación de añadir copia cancelada. Redirigiendo a main-view.");
        JavaFXUtil.setScene("/org/example/reto2/main-view.fxml");
    }
}
