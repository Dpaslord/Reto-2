package org.example.reto2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.reto2.user.User;
import org.example.reto2.user.UserRepository;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de añadir un nuevo usuario (add-user-view.fxml).
 * Permite al administrador crear nuevos usuarios, incluyendo su email, contraseña
 * y si tendrán privilegios de administrador.
 */
public class AddUserController implements Initializable {

    private static final Logger logger = Logger.getLogger(AddUserController.class.getName());

    @javafx.fxml.FXML
    private TextField txtEmail;
    @javafx.fxml.FXML
    private PasswordField pwdPassword;
    @javafx.fxml.FXML
    private PasswordField pwdConfirmPassword;
    @javafx.fxml.FXML
    private CheckBox chkIsAdmin;

    private UserRepository userRepository;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Inicializa el repositorio de usuarios.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando AddUserController.");
        userRepository = new UserRepository(DataProvider.getSessionFactory());
        logger.info("AddUserController inicializado.");
    }

    /**
     * Maneja la acción de añadir un nuevo usuario a la base de datos.
     * Valida los campos de entrada y guarda el nuevo User.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addUser(ActionEvent actionEvent) {
        logger.info("Intento de añadir nuevo usuario.");
        String email = txtEmail.getText();
        String password = pwdPassword.getText();
        String confirmPassword = pwdConfirmPassword.getText();
        Boolean isAdmin = chkIsAdmin.isSelected();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, rellena todos los campos.", "");
            logger.warning("Campos incompletos al intentar añadir usuario.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JavaFXUtil.showModal(Alert.AlertType.WARNING, "Contraseñas no coinciden", "Las contraseñas introducidas no coinciden.", "");
            logger.warning("Contraseñas no coinciden al intentar añadir usuario.");
            return;
        }

        // Aquí se podría añadir validación de formato de email, complejidad de contraseña, etc.

        try {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password); // ¡IMPORTANTE! En un entorno real, aquí se debería hashear la contraseña.
            newUser.setIsAdmin(isAdmin);

            userRepository.save(newUser);
            JavaFXUtil.setScene("/org/example/reto2/admin-users-view.fxml");
            logger.info("Usuario '" + newUser.getEmail() + "' añadido exitosamente. Redirigiendo a admin-users-view.");
        } catch (Exception e) {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Error", "No se pudo añadir el usuario", "Ocurrió un error al guardar el usuario: " + e.getMessage());
            logger.severe("Error inesperado al añadir usuario: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción de cancelar la adición de un usuario.
     * Redirige de vuelta a la vista de gestión de usuarios.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void cancel(ActionEvent actionEvent) {
        logger.info("Operación de añadir usuario cancelada. Redirigiendo a admin-users-view.");
        JavaFXUtil.setScene("/org/example/reto2/admin-users-view.fxml");
    }
}
