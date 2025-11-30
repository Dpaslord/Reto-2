package org.example.reto2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.pelicula.PeliculaRepository;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de edición de una película (edit-pelicula-view.fxml).
 * Permite al administrador modificar los datos de una película existente.
 */
public class EditPeliculaController implements Initializable {

    private static final Logger logger = Logger.getLogger(EditPeliculaController.class.getName());

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
    private Pelicula peliculaToEdit;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Inicializa el repositorio de películas y precarga los datos de la película a editar.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando EditPeliculaController.");
        peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());

        peliculaToEdit = (Pelicula) SimpleSessionService.getInstance().getObject("peliculaToEdit");
        if (peliculaToEdit != null) {
            txtTitulo.setText(peliculaToEdit.getTitulo());
            txtGenero.setText(peliculaToEdit.getGenero());
            txtAnio.setText(String.valueOf(peliculaToEdit.getAnio()));
            txtDirector.setText(peliculaToEdit.getDirector());
            txtDescripcion.setText(peliculaToEdit.getDescripcion());
            logger.info("Cargando datos de la película con ID " + peliculaToEdit.getId() + " para edición.");
        } else {
            logger.warning("No se encontró película para editar en la sesión.");
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo cargar la película para editar.", "Por favor, selecciona una película de la lista.");
            JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml"); // Volver a la vista principal del admin
        }
        logger.info("EditPeliculaController inicializado.");
    }

    /**
     * Maneja la acción de guardar los cambios de la película editada.
     * Valida los campos de entrada y actualiza la Pelicula en la base de datos.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void savePelicula(ActionEvent actionEvent) {
        logger.info("Intento de guardar cambios para la película con ID: " + (peliculaToEdit != null ? peliculaToEdit.getId() : "N/A"));
        if (peliculaToEdit != null) {
            try {
                peliculaToEdit.setTitulo(txtTitulo.getText());
                peliculaToEdit.setGenero(txtGenero.getText());

                int anio = Integer.parseInt(txtAnio.getText());
                if (anio <= 0) {
                    JavaFXUtil.showModal(Alert.AlertType.WARNING, "Año Inválido", "El año debe ser un número positivo.", "");
                    logger.warning("Año inválido (<= 0) al intentar guardar película.");
                    return;
                }
                peliculaToEdit.setAnio(anio);

                peliculaToEdit.setDirector(txtDirector.getText());
                peliculaToEdit.setDescripcion(txtDescripcion.getText());

                peliculaRepository.save(peliculaToEdit); // El método save ya maneja la actualización si el ID existe
                JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml");
                logger.info("Película con ID " + peliculaToEdit.getId() + " actualizada exitosamente. Redirigiendo a admin-main-view.");
            } catch (NumberFormatException e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de entrada", "Año inválido", "Por favor, introduce un número válido para el año.");
                logger.severe("Error de formato de número para el año: " + txtAnio.getText() + ". " + e.getMessage());
            } catch (Exception e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo actualizar la película", "Ocurrió un error al guardar la película: " + e.getMessage());
                logger.severe("Error inesperado al actualizar película: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja la acción de cancelar la edición de una película.
     * Redirige de vuelta a la vista principal del administrador.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void cancel(ActionEvent actionEvent) {
        logger.info("Operación de edición de película cancelada. Redirigiendo a admin-main-view.");
        JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml");
    }
}
