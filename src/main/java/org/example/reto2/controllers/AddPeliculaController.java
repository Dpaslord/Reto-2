package org.example.reto2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.pelicula.PeliculaRepository;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de añadir una nueva película (add-pelicula-view.fxml).
 * Permite al usuario administrador introducir los datos de una nueva película
 * y guardarla en la base de datos.
 */
public class AddPeliculaController implements Initializable {

    private static final Logger logger = Logger.getLogger(AddPeliculaController.class.getName());

    @javafx.fxml.FXML
    private TextField txtTitulo;
    @javafx.fxml.FXML
    private TextField txtGenero;
    @javafx.fxml.FXML
    private TextField txtAnio;
    @javafx.fxml.FXML
    private TextField txtDirector;
    @javafx.fxml.FXML
    private TextArea txtDescripcion;

    private PeliculaRepository peliculaRepository;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Inicializa el repositorio de películas.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando AddPeliculaController.");
        peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());
        logger.info("AddPeliculaController inicializado.");
    }

    /**
     * Maneja la acción de añadir una nueva película a la base de datos.
     * Valida los campos de entrada y guarda la nueva Pelicula.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addPelicula(ActionEvent actionEvent) {
        logger.info("Intento de añadir nueva película.");
        
        // Validación de campos vacíos
        if (txtTitulo.getText().isEmpty() || txtGenero.getText().isEmpty() || txtAnio.getText().isEmpty() ||
            txtDirector.getText().isEmpty() || txtDescripcion.getText().isEmpty()) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Campos Vacíos", "Todos los campos son obligatorios.", "Por favor, rellene todos los campos.");
            logger.warning("Intento de añadir película con campos vacíos.");
            return;
        }

        // Validación de longitud de campos
        if (txtTitulo.getText().length() > 255) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Título demasiado largo", "El título no puede exceder los 255 caracteres.", "");
            logger.warning("Intento de añadir película con título demasiado largo.");
            return;
        }
        if (txtGenero.getText().length() > 255) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Género demasiado largo", "El género no puede exceder los 255 caracteres.", "");
            logger.warning("Intento de añadir película con género demasiado largo.");
            return;
        }
        if (txtDirector.getText().length() > 255) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Director demasiado largo", "El nombre del director no puede exceder los 255 caracteres.", "");
            logger.warning("Intento de añadir película con nombre de director demasiado largo.");
            return;
        }
        if (txtDescripcion.getText().length() > 500) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Descripción demasiado larga", "La descripción no puede exceder los 500 caracteres.", "");
            logger.warning("Intento de añadir película con descripción demasiado larga.");
            return;
        }

        if (!txtGenero.getText().matches(".*[a-zA-Z]+.*")) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Formato de Género Inválido", "El género no puede ser solo números.", "");
            logger.warning("Intento de añadir película con género puramente numérico.");
            return;
        }
        if (!txtDirector.getText().matches(".*[a-zA-Z]+.*")) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Formato de Director Inválido", "El nombre del director no puede ser solo números.", "");
            logger.warning("Intento de añadir película con director puramente numérico.");
            return;
        }

        try {
            String anioStr = txtAnio.getText();
            if (!anioStr.matches("\\d{4}")) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Formato de Año Inválido", "El año debe ser un número de 4 dígitos.", "");
                logger.warning("Formato de año inválido al intentar añadir película: " + anioStr);
                return;
            }
            int anio = Integer.parseInt(anioStr);
            if (anio < 1950 || anio > 2025) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Año fuera de rango", "El año debe estar entre 1950 y 2025.", "");
                logger.warning("Año fuera de rango al intentar añadir película: " + anio);
                return;
            }
            Optional<Pelicula> existingPelicula = peliculaRepository.findByAttributes(txtTitulo.getText(), anio, txtDirector.getText());
            if (existingPelicula.isPresent()) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Película Duplicada", "Ya existe una película con el mismo título, año y director.", "");
                logger.warning("Intento de añadir una película duplicada.");
                return;
            }

            Pelicula newPelicula = new Pelicula();
            newPelicula.setTitulo(txtTitulo.getText());
            newPelicula.setGenero(txtGenero.getText());
            newPelicula.setAnio(anio);
            newPelicula.setDirector(txtDirector.getText());
            newPelicula.setDescripcion(txtDescripcion.getText());

            peliculaRepository.save(newPelicula);
            JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml");
            logger.info("Película '" + newPelicula.getTitulo() + "' añadida exitosamente. Redirigiendo a admin-main-view.");
        } catch (NumberFormatException e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de entrada", "Año inválido", "Por favor, introduce un número válido para el año.");
            logger.severe("Error de formato de número para el año: " + txtAnio.getText() + ". " + e.getMessage());
        } catch (Exception e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo añadir la película", "Ocurrió un error al guardar la película: " + e.getMessage());
            logger.severe("Error inesperado al añadir película: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de cancelar la adición de una película.
     * Redirige de vuelta a la vista principal del administrador.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void cancel(ActionEvent actionEvent) {
        logger.info("Operación de añadir película cancelada. Redirigiendo a admin-main-view.");
        JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml");
    }
}
