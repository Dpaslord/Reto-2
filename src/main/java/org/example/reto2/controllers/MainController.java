package org.example.reto2.controllers;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.input.MouseEvent;
import org.example.reto2.copia.Copia;
import org.example.reto2.copia.CopiaService;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.user.User;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
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
    @javafx.fxml.FXML
    private TextField txtSearch; // Campo de búsqueda

    private User currentUser;
    private CopiaService copiaService;
    private ObservableList<Copia> masterData = FXCollections.observableArrayList();
    private FilteredList<Copia> filteredData;

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

        // 1. Inicializar masterData y filteredData
        masterData.addAll(currentUser.getCopias());
        filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Añadir listener al campo de búsqueda
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(copia -> {
                // Si el campo de búsqueda está vacío, muestra todas las copias.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (copia.getPelicula().getTitulo().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Coincide con el título de la película.
                } else if (copia.getEstado().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Coincide con el estado de la copia.
                } else if (copia.getSoporte().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Coincide con el soporte de la copia.
                }
                return false; // No hay coincidencia.
            });
        });

        // 3. Envolver la FilteredList en una SortedList.
        SortedList<Copia> sortedData = new SortedList<>(filteredData);

        // 4. Vincular el comparador de SortedList al comparador de TableView.
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        // 5. Añadir los datos ordenados (y filtrados) a la tabla.
        tableView.setItems(sortedData);


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

        refreshTable(); // La llamada inicial a refreshTable ya poblará masterData
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
     * Incluye un diálogo de confirmación.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void deleteCopia(ActionEvent actionEvent) {
        Copia selectedCopia = tableView.getSelectionModel().getSelectedItem();
        if (selectedCopia != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de que desea eliminar/decrementar esta copia?");
            alert.setContentText("Película: " + selectedCopia.getPelicula().getTitulo() + "\n" +
                                 "Estado: " + selectedCopia.getEstado() + "\n" +
                                 "Soporte: " + selectedCopia.getSoporte() + "\n" +
                                 "Cantidad actual: " + selectedCopia.getCantidad() +
                                 "\n\nSi la cantidad es mayor que 1, se decrementará. Si es 1, la copia se eliminará por completo.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    logger.info("Usuario confirmó eliminación/decremento de copia con ID: " + selectedCopia.getId());
                    currentUser = copiaService.deleteCopiaFromUser(currentUser, selectedCopia);
                    SimpleSessionService.getInstance().setObject("user", currentUser); // Actualizar usuario en sesión
                    refreshTable();
                    logger.info("Operación de eliminación/decremento de copia completada. Tabla refrescada.");
                } catch (Exception e) {
                    logger.severe("Error al eliminar/decrementar copia: " + e.getMessage());
                    JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error de Eliminación", 
                                        "No se pudo eliminar la copia.", 
                                        "Ocurrió un error inesperado. Por favor, intente de nuevo.");
                }
            } else {
                logger.info("Usuario canceló la eliminación/decremento de copia con ID: " + selectedCopia.getId());
            }
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
        masterData.clear();
        masterData.addAll(currentUser.getCopias());
        // No es necesario llamar a tableView.setItems(sortedData) de nuevo, ya está vinculado
        tableView.refresh();
        logger.info("Tabla de copias refrescada. Número de copias: " + currentUser.getCopias().size());
    }

}
