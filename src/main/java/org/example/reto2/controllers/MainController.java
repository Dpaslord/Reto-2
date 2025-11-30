package org.example.reto2.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
 * Controlador para la vista principal de la aplicación (main-view.fxml).
 * Gestiona la visualización de las copias de películas del usuario,
 * así como las acciones de añadir, editar y eliminar copias, y cerrar sesión.
 */
public class MainController implements Initializable {

    private static final Logger logger = Logger.getLogger(MainController.class.getName());

    @javafx.fxml.FXML
    private TableView<Copia> tableView;
    @javafx.fxml.FXML
    private TableColumn<Copia, String> colTitulo;
    @javafx.fxml.FXML
    private TableColumn<Copia, String> colEstado;
    @javafx.fxml.FXML
    private TableColumn<Copia, String> colSoporte;
    @javafx.fxml.FXML
    private TableColumn<Copia, Integer> colCantidad;

    private User currentUser;
    private CopiaService copiaService;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura las columnas de la tabla, añade un listener para doble clic en las filas
     * y refresca la tabla con las copias del usuario actual.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando MainController.");
        currentUser = (User) SimpleSessionService.getInstance().getObject("user");
        copiaService = new CopiaService();

        colTitulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPelicula().getTitulo()));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colSoporte.setCellValueFactory(new PropertyValueFactory<>("soporte"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Copia selectedCopia = tableView.getSelectionModel().getSelectedItem();
                if (selectedCopia != null) {
                    Pelicula pelicula = selectedCopia.getPelicula();
                    String content = "Título: " + pelicula.getTitulo() + "\n" +
                            "Género: " + pelicula.getGenero() + "\n" +
                            "Año: " + pelicula.getAnio() + "\n" +
                            "Director: " + pelicula.getDirector() + "\n" +
                            "Descripción: " + pelicula.getDescripcion();
                    JavaFXUtil.showModal(Alert.AlertType.INFORMATION, "Detalles de la Película", "Información de la película seleccionada", content);
                    logger.info("Mostrando detalles de la película para la copia con ID: " + selectedCopia.getId());
                }
            }
        });

        refreshTable();
        logger.info("MainController inicializado para el usuario: " + currentUser.getEmail());
    }

    /**
     * Maneja la acción de cerrar sesión del usuario.
     * Cierra la sesión actual y redirige a la vista de login.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void logout(ActionEvent actionEvent) {
        logger.info("Usuario " + currentUser.getEmail() + " solicitando cerrar sesión.");
        SimpleSessionService.getInstance().logout();
        JavaFXUtil.setScene("/org/example/reto2/login-view.fxml");
        logger.info("Sesión cerrada. Redirigiendo a login-view.");
    }

    /**
     * Maneja la acción de cerrar la aplicación.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void close(ActionEvent actionEvent) {
        logger.info("Solicitud de cierre de aplicación.");
        System.exit(0);
    }

    /**
     * Maneja la acción de eliminar una copia seleccionada de la tabla.
     * Si la copia tiene cantidad > 1, decrementa la cantidad. Si es 1, la elimina.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void deleteCopia(ActionEvent actionEvent) {
        Copia selectedCopia = tableView.getSelectionModel().getSelectedItem();
        if (selectedCopia != null) {
            logger.info("Intentando eliminar/decrementar copia con ID: " + selectedCopia.getId());
            currentUser = copiaService.deleteCopiaFromUser(currentUser, selectedCopia);
            SimpleSessionService.getInstance().setObject("user", currentUser); // Actualizar usuario en sesión
            refreshTable();
            logger.info("Operación de eliminación/decremento de copia completada. Tabla refrescada.");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Ninguna copia seleccionada", "Por favor, selecciona una copia para eliminar.", "");
            logger.warning("Intento de eliminar copia sin selección.");
        }
    }

    /**
     * Maneja la acción de añadir una nueva copia.
     * Redirige a la vista de añadir copia.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addCopia(ActionEvent actionEvent) {
        logger.info("Redirigiendo a add-copia-view para añadir una nueva copia.");
        JavaFXUtil.setScene("/org/example/reto2/add-copia-view.fxml");
    }

    /**
     * Maneja la acción de editar una copia seleccionada de la tabla.
     * Redirige a la vista de editar copia, pasando la copia seleccionada a la sesión.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void editCopia(ActionEvent actionEvent) {
        Copia selectedCopia = tableView.getSelectionModel().getSelectedItem();
        if (selectedCopia != null) {
            logger.info("Redirigiendo a edit-copia-view para editar copia con ID: " + selectedCopia.getId());
            SimpleSessionService.getInstance().setObject("copiaToEdit", selectedCopia);
            JavaFXUtil.setScene("/org/example/reto2/edit-copia-view.fxml");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Ninguna copia seleccionada", "Por favor, selecciona una copia para editar.", "");
            logger.warning("Intento de editar copia sin selección.");
        }
    }

    /**
     * Refresca la tabla de copias con los datos más recientes del usuario actual.
     */
    private void refreshTable() {
        logger.info("Refrescando tabla de copias para el usuario: " + currentUser.getEmail());
        currentUser = (User) SimpleSessionService.getInstance().getObject("user"); // Asegurarse de tener la última versión del usuario
        tableView.setItems(FXCollections.observableList(currentUser.getCopias()));
        tableView.refresh();
        logger.info("Tabla de copias refrescada. Número de copias: " + currentUser.getCopias().size());
    }

}
