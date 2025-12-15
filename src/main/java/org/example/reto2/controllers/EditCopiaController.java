package org.example.reto2.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.reto2.copia.Copia;
import org.example.reto2.copia.CopiaService;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.user.User;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de edición de una copia de película (edit-copia-view.fxml).
 * Permite al usuario modificar el estado, soporte y cantidad de una copia existente
 * de su colección.
 */
public class EditCopiaController implements Initializable {

    private static final Logger logger = Logger.getLogger(EditCopiaController.class.getName());

    @javafx.fxml.FXML
    private ComboBox<Pelicula> comboPelicula;
    @javafx.fxml.FXML
    private ComboBox<String> comboEstado;
    @javafx.fxml.FXML
    private ComboBox<String> comboSoporte;
    @javafx.fxml.FXML
    private TextField txtCantidad;

    private CopiaService copiaService;
    private Copia copiaToEdit;
    private User currentUser;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Carga las opciones para estado y soporte, y precarga los datos de la copia a editar
     * en los campos correspondientes.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando EditCopiaController.");
        copiaService = new CopiaService();
        currentUser = (User) SimpleSessionService.getInstance().getObject("user");

        // Configurar el ComboBox de Películas para mostrar solo el título
        comboPelicula.setConverter(new StringConverter<Pelicula>() {
            @Override
            public String toString(Pelicula pelicula) {
                return pelicula == null ? null : pelicula.getTitulo();
            }

            @Override
            public Pelicula fromString(String string) {
                // No es necesario implementar la conversión inversa si el ComboBox no es editable
                return null;
            }
        });
        comboPelicula.setCellFactory(param -> new ListCell<Pelicula>() {
            @Override
            protected void updateItem(Pelicula item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitulo());
                }
            }
        });

        comboEstado.setItems(FXCollections.observableArrayList("bueno", "gastado", "dañado"));
        comboSoporte.setItems(FXCollections.observableArrayList("dvd", "blue-ray"));

        copiaToEdit = (Copia) SimpleSessionService.getInstance().getObject("copiaToEdit");
        if (copiaToEdit != null) {
            comboPelicula.setItems(FXCollections.observableArrayList(copiaToEdit.getPelicula()));
            comboPelicula.getSelectionModel().select(copiaToEdit.getPelicula());
            comboPelicula.setDisable(true); // Deshabilitar el ComboBox de película
            comboEstado.getSelectionModel().select(copiaToEdit.getEstado());
            comboSoporte.getSelectionModel().select(copiaToEdit.getSoporte());
            txtCantidad.setText(String.valueOf(copiaToEdit.getCantidad()));
            logger.info("Cargando datos de la copia con ID " + copiaToEdit.getId() + " para edición.");
        } else {
            logger.warning("No se encontró copia para editar en la sesión.");
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo cargar la copia para editar.", "Por favor, selecciona una copia de la lista.");
            JavaFXUtil.setScene("/org/example/reto2/main-view.fxml"); // Volver a la vista principal
        }
        logger.info("EditCopiaController inicializado.");
    }

    /**
     * Maneja la acción de guardar los cambios de la copia editada.
     * Valida los campos de entrada y actualiza la Copia en la base de datos.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void saveCopia(ActionEvent actionEvent) {
        logger.info("Intento de guardar cambios para la copia con ID: " + (copiaToEdit != null ? copiaToEdit.getId() : "N/A"));
        if (copiaToEdit != null) {
            String selectedEstado = comboEstado.getSelectionModel().getSelectedItem();
            String selectedSoporte = comboSoporte.getSelectionModel().getSelectedItem();
            String cantidadText = txtCantidad.getText();

            if (selectedEstado == null || selectedSoporte == null || cantidadText.isEmpty()) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, rellena todos los campos.", "");
                logger.warning("Campos incompletos al intentar guardar copia.");
                return;
            }

            try {
                if (!cantidadText.matches("\\d+")) {
                    JavaFXUtil.showModal(Alert.AlertType.WARNING, "Formato de Cantidad Inválido", "La cantidad debe ser un número entero.", "");
                    logger.warning("Formato de cantidad inválido al intentar guardar copia: " + cantidadText);
                    return;
                }
                int cantidad = Integer.parseInt(cantidadText);
                if (cantidad <= 0 || cantidad > 9999) {
                    JavaFXUtil.showModal(Alert.AlertType.WARNING, "Cantidad Inválida", "La cantidad debe ser un número entre 1 y 9999.", "");
                    logger.warning("Cantidad inválida (fuera de rango) al intentar guardar copia.");
                    return;
                }

                copiaToEdit.setEstado(selectedEstado);
                copiaToEdit.setSoporte(selectedSoporte);
                copiaToEdit.setCantidad(cantidad);

                currentUser = copiaService.updateCopia(copiaToEdit, currentUser);
                SimpleSessionService.getInstance().setObject("user", currentUser);
                JavaFXUtil.setScene("/org/example/reto2/main-view.fxml");
                logger.info("Copia con ID " + copiaToEdit.getId() + " actualizada exitosamente. Redirigiendo a main-view.");
            } catch (NumberFormatException e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de entrada", "Cantidad inválida", "Por favor, introduce un número válido para la cantidad.");
                logger.severe("Error de formato de número para la cantidad: " + cantidadText + ". " + e.getMessage());
            } catch (Exception e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la copia", "Ocurrió un error al guardar la copia: " + e.getMessage());
                logger.severe("Error inesperado al actualizar copia: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja la acción de cancelar la edición de una copia.
     * Redirige de vuelta a la vista principal del usuario.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void cancel(ActionEvent actionEvent) {
        logger.info("Operación de edición de copia cancelada. Redirigiendo a main-view.");
        JavaFXUtil.setScene("/org/example/reto2/main-view.fxml");
    }
}
