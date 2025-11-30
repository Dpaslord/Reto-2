package org.example.reto2;

import javafx.application.Application;

import java.util.logging.Logger;

/**
 * Clase lanzadora de la aplicación JavaFX.
 * Esta clase es necesaria para evitar problemas de classpath con algunos entornos
 * y para asegurar que la aplicación JavaFX se inicie correctamente.
 */
public class Launcher {

    private static final Logger logger = Logger.getLogger(Launcher.class.getName());

    /**
     * Método principal que inicia la aplicación JavaFX.
     * @param args Argumentos de la línea de comandos pasados a la aplicación.
     */
    public static void main(String[] args) {
        logger.info("Iniciando la aplicación JavaFX a través del Launcher.");
        Application.launch(App.class, args);
    }
}
