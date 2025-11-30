package org.example.reto2;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.reto2.utils.JavaFXUtil;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Clase principal de la aplicación JavaFX.
 * Extiende {@link javafx.application.Application} y es el punto de entrada
 * para la interfaz gráfica de usuario.
 */
public class App extends Application {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    /**
     * Método de inicio de la aplicación JavaFX.
     * Inicializa el Stage principal y carga la vista de inicio de sesión.
     * @param stage El Stage principal de la aplicación.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Iniciando aplicación JavaFX.");
        JavaFXUtil.initStage(stage);
        JavaFXUtil.setScene("/org/example/reto2/login-view.fxml");
        logger.info("Aplicación iniciada. Cargando login-view.");
    }
}
