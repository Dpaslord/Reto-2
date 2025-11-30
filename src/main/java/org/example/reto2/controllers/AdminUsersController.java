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
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.user.User;
import org.example.reto2.user.UserRepository;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de gestión de usuarios por parte del administrador (admin-users-view.fxml).
 * Muestra una tabla con todos los usuarios registrados y permite al administrador
 * añadir, editar y eliminar usuarios.
 */
public class AdminUsersController implements Initializable {

    private static final Logger logger = Logger.getLogger(AdminUsersController.class.getName());

    @javafx.fxml.FXML
    private TableView<User> tableViewUsers;
    @javafx.fxml.FXML
    private TableColumn<User, Integer> colUserId;
    @javafx.fxml.FXML
    private TableColumn<User, String> colUserEmail;
    @javafx.fxml.FXML
    private TableColumn<User, Boolean> colUserIsAdmin;
    @javafx.fxml.FXML
    private TableColumn<User, Integer> colUserCopiasCount; // Para mostrar el número de copias
    @javafx.fxml.FXML
    private TextField txtSearchUsers; // Campo de búsqueda para usuarios

    private UserRepository userRepository;
    private ObservableList<User> masterData = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura las columnas de la tabla, implementa el filtrado y refresca la tabla de usuarios.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando AdminUsersController.");
        userRepository = new UserRepository(DataProvider.getSessionFactory());

        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUserIsAdmin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
        // Para el conteo de copias, necesitamos una PropertyValueFactory personalizada o un getter en User
        colUserCopiasCount.setCellValueFactory(cellData -> {
            // Asegurarse de que la lista de copias esté inicializada para evitar NullPointerException
            if (cellData.getValue().getCopias() != null) {
                return new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCopias().size()).asObject();
            }
            return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
        });


        // 1. Inicializar masterData y filteredData
        masterData.addAll(userRepository.findAll());
        filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Añadir listener al campo de búsqueda
        txtSearchUsers.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                // Si el campo de búsqueda está vacío, muestra todos los usuarios.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Coincide con el email.
                }
                return false; // No hay coincidencia.
            });
        });

        // 3. Envolver la FilteredList en una SortedList.
        SortedList<User> sortedData = new SortedList<>(filteredData);

        // 4. Vincular el comparador de SortedList al comparador de TableView.
        sortedData.comparatorProperty().bind(tableViewUsers.comparatorProperty());

        // 5. Añadir los datos ordenados (y filtrados) a la tabla.
        tableViewUsers.setItems(sortedData);

        refreshTable();
        logger.info("AdminUsersController inicializado.");
    }

    /**
     * Maneja la acción de añadir un nuevo usuario.
     * Redirige a la vista de añadir usuario.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addUser(ActionEvent actionEvent) {
        logger.info("Redirigiendo a add-user-view para añadir un nuevo usuario.");
        JavaFXUtil.setScene("/org/example/reto2/add-user-view.fxml");
    }

    /**
     * Maneja la acción de editar un usuario seleccionado de la tabla.
     * Redirige a la vista de editar usuario, pasando el usuario seleccionado a la sesión.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void editUser(ActionEvent actionEvent) {
        User selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            logger.info("Redirigiendo a edit-user-view para editar usuario con ID: " + selectedUser.getId());
            SimpleSessionService.getInstance().setObject("userToEdit", selectedUser);
            JavaFXUtil.setScene("/org/example/reto2/edit-user-view.fxml");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Ningún usuario seleccionado", "Por favor, selecciona un usuario para editar.", "");
            logger.warning("Intento de editar usuario sin selección.");
        }
    }

    /**
     * Maneja la acción de eliminar un usuario seleccionado de la tabla.
     * Incluye un diálogo de confirmación.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void deleteUser(ActionEvent actionEvent) {
        User selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de que desea eliminar el usuario?");
            alert.setContentText("Usuario: " + selectedUser.getEmail() + "\n" +
                                 "ID: " + selectedUser.getId() +
                                 "\n\nEsta acción eliminará el usuario y todas sus copias permanentemente.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logger.info("Administrador confirmó eliminación de usuario con ID: " + selectedUser.getId());
                // Es importante que el ID del usuario sea Long para el método deleteById
                userRepository.deleteById(selectedUser.getId().longValue());
                refreshTable();
                logger.info("Usuario eliminado. Tabla refrescada.");
            } else {
                logger.info("Administrador canceló la eliminación de usuario con ID: " + selectedUser.getId());
            }
        } else {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Ningún usuario seleccionado", "Por favor, selecciona un usuario para eliminar.", "");
            logger.warning("Intento de eliminar usuario sin selección.");
        }
    }

    /**
     * Maneja la acción de volver a la vista principal del administrador.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void goBack(ActionEvent actionEvent) {
        logger.info("Volviendo a admin-main-view desde la gestión de usuarios.");
        JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml");
    }

    /**
     * Maneja la acción de cerrar sesión del administrador.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void logout(ActionEvent actionEvent) {
        logger.info("Administrador solicitando cerrar sesión desde la gestión de usuarios.");
        SimpleSessionService.getInstance().logout();
        JavaFXUtil.setScene("/org/example/reto2/login-view.fxml");
        logger.info("Sesión de administrador cerrada. Redirigiendo a login-view.");
    }

    /**
     * Refresca la tabla de usuarios con los datos más recientes de la base de datos.
     */
    private void refreshTable() {
        logger.info("Refrescando tabla de usuarios.");
        masterData.clear();
        masterData.addAll(userRepository.findAll());
        tableViewUsers.refresh();
        logger.info("Tabla de usuarios refrescada. Número de usuarios: " + masterData.size());
    }
}
