package org.example.reto2.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.pelicula.PeliculaRepository;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista principal del administrador (admin-main-view.fxml).
 * Muestra una tabla con todas las películas de la base de datos y permite
 * al administrador añadir nuevas películas y cerrar sesión.
 */
public class AdminMainController implements Initializable {

    private static final Logger logger = Logger.getLogger(AdminMainController.class.getName());

    @javafx.fxml.FXML
    private TableView<Pelicula> tableViewPeliculas;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colTitulo;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colGenero;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, Integer> colAnio;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colDirector;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colDescripcion;
    @javafx.fxml.FXML
    private TextField txtSearchPeliculas;

    private PeliculaRepository peliculaRepository;
    private ObservableList<Pelicula> masterData = FXCollections.observableArrayList();
    private FilteredList<Pelicula> filteredData;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura las columnas de la tabla y refresca la tabla con todas las películas.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando AdminMainController.");
        peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colDirector.setCellValueFactory(new PropertyValueFactory<>("director"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        masterData.addAll(peliculaRepository.findAll());
        filteredData = new FilteredList<>(masterData, p -> true);

        txtSearchPeliculas.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pelicula -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (pelicula.getTitulo().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (pelicula.getGenero().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (pelicula.getDirector().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Pelicula> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableViewPeliculas.comparatorProperty());
        tableViewPeliculas.setItems(sortedData);

        refreshTable();
        logger.info("AdminMainController inicializado.");
    }

    /**
     * Maneja la acción de añadir una nueva película.
     * Redirige a la vista de añadir película.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addPelicula(ActionEvent actionEvent) {
        logger.info("Redirigiendo a add-pelicula-view para añadir una nueva película.");
        JavaFXUtil.setScene("/org/example/reto2/add-pelicula-view.fxml");
    }

    /**
     * Maneja la acción de editar una película seleccionada de la tabla.
     * Redirige a la vista de editar película, pasando la película seleccionada a la sesión.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void editPelicula(ActionEvent actionEvent) {
        Pelicula selectedPelicula = tableViewPeliculas.getSelectionModel().getSelectedItem();
        if (selectedPelicula != null) {
            logger.info("Redirigiendo a edit-pelicula-view para editar película con ID: " + selectedPelicula.getId());
            SimpleSessionService.getInstance().setObject("peliculaToEdit", selectedPelicula);
            JavaFXUtil.setScene("/org/example/reto2/edit-pelicula-view.fxml");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Ninguna película seleccionada", "Por favor, selecciona una película para editar.", "");
            logger.warning("Intento de editar película sin selección.");
        }
    }

    /**
     * Maneja la acción de eliminar una película seleccionada de la tabla.
     * Incluye un diálogo de confirmación.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void deletePelicula(ActionEvent actionEvent) {
        Pelicula selectedPelicula = tableViewPeliculas.getSelectionModel().getSelectedItem();
        if (selectedPelicula != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de que desea eliminar la película?");
            alert.setContentText("Película: " + selectedPelicula.getTitulo() + "\n" +
                                 "Año: " + selectedPelicula.getAnio() +
                                 "\n\nEsta acción eliminará la película permanentemente.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    logger.info("Administrador confirmó eliminación de película con ID: " + selectedPelicula.getId());
                    peliculaRepository.delete(selectedPelicula);
                    refreshTable();
                    logger.info("Película eliminada. Tabla refrescada.");
                } catch (Exception e) {
                    logger.severe("Error al eliminar película: " + e.getMessage());
                    JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de Eliminación", 
                                        "No se pudo eliminar la película.", 
                                        "Es posible que la película tenga copias asociadas y no pueda ser borrada.");
                }
            } else {
                logger.info("Administrador canceló la eliminación de película con ID: " + selectedPelicula.getId());
            }
        } else {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Ninguna película seleccionada", "Por favor, selecciona una película para eliminar.", "");
            logger.warning("Intento de eliminar película sin selección.");
        }
    }

    /**
     * Maneja la acción de gestionar usuarios.
     * Redirige a la vista de gestión de usuarios.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void manageUsers(ActionEvent actionEvent) {
        logger.info("Redirigiendo a admin-users-view para gestionar usuarios.");
        JavaFXUtil.setScene("/org/example/reto2/admin-users-view.fxml");
    }

    /**
     * Maneja la acción de cerrar sesión del administrador.
     * Cierra la sesión actual y redirige a la vista de login.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void logout(ActionEvent actionEvent) {
        logger.info("Administrador solicitando cerrar sesión.");
        SimpleSessionService.getInstance().logout();
        JavaFXUtil.setScene("/org/example/reto2/login-view.fxml");
        logger.info("Sesión de administrador cerrada. Redirigiendo a login-view.");
    }

    /**
     * Refresca la tabla de películas con los datos más recientes de la base de datos.
     */
    private void refreshTable() {
        logger.info("Refrescando tabla de películas.");
        masterData.clear();
        masterData.addAll(peliculaRepository.findAll());
        tableViewPeliculas.refresh();
        logger.info("Tabla de películas refrescada. Número de películas: " + masterData.size());
    }
}
