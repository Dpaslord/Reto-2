package org.example.reto2.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Clase de utilidad para gestionar la navegación y ventanas modales en aplicaciones JavaFX.
 * Proporciona métodos estáticos para inicializar el Stage principal, cambiar escenas
 * y mostrar alertas modales.
 */
public class JavaFXUtil {

    private static final Logger logger = Logger.getLogger(JavaFXUtil.class.getName());
    private static Stage stage;

    /**
     * Constructor privado para evitar instanciación de la clase de utilidad.
     */
    private JavaFXUtil() {
        // Constructor privado
    }

    /**
     * Inicializa el Stage principal de la aplicación.
     * Debe ser llamado al inicio de la aplicación.
     * @param stage El Stage principal de la aplicación.
     */
    public static void initStage(Stage stage) {
        JavaFXUtil.stage = stage;
        logger.info("Stage principal inicializado.");
    }

    /**
     * Obtiene el Stage principal de la aplicación.
     * @param actionEvent El ActionEvent que disparó la solicitud (puede ser null si no aplica).
     * @return El Stage principal de la aplicación.
     */
    public static Stage getStage(ActionEvent actionEvent) {
        return stage;
    }

    /**
     * Carga un archivo FXML y establece la escena en el Stage principal.
     * También devuelve el controlador asociado a la escena cargada.
     * @param fxml La ruta del archivo FXML a cargar (ej. "/org/example/reto2/main-view.fxml").
     * @param <T> El tipo del controlador de la escena.
     * @return El controlador de la escena cargada, o null si ocurre un error.
     */
    public static <T> T setScene(String fxml){
        logger.info("Cargando escena FXML: " + fxml);
        try{
            FXMLLoader loader = new FXMLLoader(JavaFXUtil.class.getResource(fxml));
            Parent root = loader.load();
            T controller = loader.getController();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
            logger.info("Escena " + fxml + " cargada exitosamente.");
            return controller;
        }
        catch(IOException ex){
            logger.severe("Error al cargar la escena FXML " + fxml + ": " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            logger.severe("Error inesperado al establecer la escena " + fxml + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Muestra un diálogo modal de alerta al usuario.
     * @param type El tipo de alerta (INFORMATION, WARNING, ERROR, CONFIRMATION).
     * @param title El título de la ventana de alerta.
     * @param header El texto del encabezado de la alerta.
     * @param content El contenido principal del mensaje de la alerta.
     */
    public static void showModal(Alert.AlertType type, String title, String header, String content){
        logger.info("Mostrando modal: Tipo=" + type + ", Título='" + title + "', Encabezado='" + header + "', Contenido='" + content + "'");
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.showAndWait();
        logger.info("Modal cerrado.");
    }

}
