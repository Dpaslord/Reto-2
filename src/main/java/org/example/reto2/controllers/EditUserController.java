package org.example.reto2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
 * Controlador para la vista de edición de un usuario (edit-user-view.fxml).
 * Permite al administrador modificar la contraseña y el rol de administrador
 * de un usuario existente.
 */
public class EditUserController implements Initializable {

    private static final Logger logger = Logger.getLogger(EditUserController.class.getName());

    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private PasswordField pwdPassword;
    @javafx.fxml.FXML
    private PasswordField pwdConfirmPassword;
    @javafx.fxml.FXML
    private CheckBox chkIsAdmin;

    private UserRepository userRepository;
    private User userToEdit;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Inicializa el repositorio de usuarios y precarga los datos del usuario a editar.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando EditUserController.");
        userRepository = new UserRepository(DataProvider.getSessionFactory());

        userToEdit = (User) SimpleSessionService.getInstance().getObject("userToEdit");
        if (userToEdit != null) {
            txtEmail.setText(userToEdit.getEmail());
            chkIsAdmin.setSelected(userToEdit.getIsAdmin());
            logger.info("Cargando datos del usuario con ID " + userToEdit.getId() + " para edición.");
        } else {
            logger.warning("No se encontró usuario para editar en la sesión.");
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo cargar el usuario para editar.", "Por favor, selecciona un usuario de la lista.");
            JavaFXUtil.setScene("/org/example/reto2/admin-users-view.fxml");
        }
        logger.info("EditUserController inicializado.");
    }

    /**
     * Maneja la acción de guardar los cambios del usuario editado.
     * Valida los campos de entrada y actualiza el User en la base de datos.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void saveUser(ActionEvent actionEvent) {
        logger.info("Intento de guardar cambios para el usuario con ID: " + (userToEdit != null ? userToEdit.getId() : "N/A"));
        if (userToEdit != null) {
            String newEmail = txtEmail.getText();
            String newPassword = pwdPassword.getText();
            String confirmPassword = pwdConfirmPassword.getText();
            Boolean isAdmin = chkIsAdmin.isSelected();

            if (newEmail.isEmpty()) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Campo Vacío", "El email no puede estar vacío.", "");
                logger.warning("Intento de guardar usuario con email vacío.");
                return;
            }

            if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Contraseñas no coinciden", "Las nuevas contraseñas no coinciden.", "");
                logger.warning("Contraseñas no coinciden al intentar guardar cambios de usuario.");
                return;
            }

            // Comprobar si el nuevo email ya está en uso por OTRO usuario
            Optional<User> userWithSameEmail = userRepository.findByEmail(newEmail);
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(userToEdit.getId())) {
                JavaFXUtil.showModal(Alert.AlertType.WARNING, "Email Duplicado", "El email introducido ya está registrado por otro usuario.", "");
                logger.warning("Intento de cambiar email a uno duplicado.");
                return;
            }

            try {
                userToEdit.setEmail(newEmail);
                if (!newPassword.isEmpty()) {
                    userToEdit.setPassword(newPassword);
                    logger.info("Contraseña del usuario " + userToEdit.getEmail() + " será actualizada.");
                }
                userToEdit.setIsAdmin(isAdmin);

                userRepository.save(userToEdit);
                JavaFXUtil.setScene("/org/example/reto2/admin-users-view.fxml");
                logger.info("Usuario con ID " + userToEdit.getId() + " actualizado exitosamente. Redirigiendo a admin-users-view.");
            } catch (Exception e) {
                JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el usuario", "Ocurrió un error al guardar los cambios del usuario: " + e.getMessage());
                logger.severe("Error inesperado al actualizar usuario: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja la acción de cancelar la edición de un usuario.
     * Redirige de vuelta a la vista de gestión de usuarios.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void cancel(ActionEvent actionEvent) {
        logger.info("Operación de edición de usuario cancelada. Redirigiendo a admin-users-view.");
        JavaFXUtil.setScene("/org/example/reto2/admin-users-view.fxml");
    }
}
