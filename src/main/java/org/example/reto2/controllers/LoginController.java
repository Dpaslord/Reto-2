package org.example.reto2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.reto2.session.AuthService;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.user.User;
import org.example.reto2.user.UserRepository;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;
import org.hibernate.SessionFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista de inicio de sesión (login-view.fxml).
 * Gestiona la autenticación de usuarios y la redirección a las vistas
 * correspondientes según el rol del usuario (administrador o normal).
 */
public class LoginController implements Initializable {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    @javafx.fxml.FXML
    private TextField txtContraseña;
    @javafx.fxml.FXML
    private TextField txtCorreo;
    @javafx.fxml.FXML
    private Label info;
    @javafx.fxml.FXML
    private Button btnEntrar;

    private UserRepository userRepository;
    private AuthService authService;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Inicializa el repositorio de usuarios y el servicio de autenticación.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando LoginController.");
        
        SessionFactory sessionFactory = DataProvider.getSessionFactory();
        if (sessionFactory == null) {
            logger.severe("No se pudo establecer conexión con la base de datos. Deshabilitando controles de login.");

            info.setText("Error de conexión. Verifique Docker y reinicie.");
            info.setStyle("-fx-text-fill: red;");

            txtCorreo.setDisable(true);
            txtContraseña.setDisable(true);
            btnEntrar.setDisable(true);
            return;
        }

        userRepository = new UserRepository(sessionFactory);
        authService = new AuthService(userRepository);
        logger.info("LoginController inicializado.");
    }

    /**
     * Maneja la acción de intentar iniciar sesión.
     * Valida las credenciales del usuario y redirige a la vista apropiada.
     * Si es administrador, muestra un modal para elegir la vista.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void entrar(ActionEvent actionEvent) {
        logger.info("Intento de inicio de sesión para el correo: " + txtCorreo.getText());
        Optional<User> user = authService.validateUser(txtCorreo.getText(),txtContraseña.getText() );
        if (user.isPresent()){
            SimpleSessionService sessionService = SimpleSessionService.getInstance();
            sessionService.login(user.get());
            sessionService.setObject("user", user.get());
            logger.info("Usuario " + user.get().getEmail() + " autenticado exitosamente.");

            if (user.get().getIsAdmin()) {
                logger.info("Usuario es administrador. Mostrando modal de selección de vista.");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Seleccionar Vista");
                alert.setHeaderText("Bienvenido, Administrador");
                alert.setContentText("¿Qué vista desea abrir?");

                ButtonType btnPeliculas = new ButtonType("Ver Películas (Admin)");
                ButtonType btnCopias = new ButtonType("Ver Mis Copias (Usuario)");

                alert.getButtonTypes().setAll(btnPeliculas, btnCopias, ButtonType.CANCEL);
                alert.initOwner(JavaFXUtil.getStage(actionEvent));

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isEmpty() || result.get() == ButtonType.CANCEL) {
                    sessionService.logout();
                    txtCorreo.clear();
                    txtContraseña.clear();
                    info.setText("Inicio de sesión cancelado.");
                    logger.info("Administrador canceló la selección, cerrando sesión.");
                } else if (result.get() == btnPeliculas) {
                    JavaFXUtil.setScene("/org/example/reto2/admin-main-view.fxml");
                    logger.info("Administrador seleccionó ver películas.");
                } else if (result.get() == btnCopias) {
                    JavaFXUtil.setScene("/org/example/reto2/main-view.fxml");
                    logger.info("Administrador seleccionó ver sus copias.");
                }
            } else {
                JavaFXUtil.setScene("/org/example/reto2/main-view.fxml");
                logger.info("Usuario normal, redirigiendo a main-view.");
            }
        } else {
            info.setText("Usuario o contraseña incorrectos");
            logger.warning("Fallo en el inicio de sesión para el correo: " + txtCorreo.getText() + ". Credenciales incorrectas.");
        }
    }

    /**
     * Maneja la acción de salir de la aplicación.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void Salir(ActionEvent actionEvent) {
        logger.info("Solicitud de cierre de aplicación desde la vista de login.");
        System.exit(0);
    }
}
