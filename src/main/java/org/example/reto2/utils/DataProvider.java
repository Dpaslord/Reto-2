package org.example.reto2.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.logging.Logger;

/**
 * Clase de utilidad para proporcionar y gestionar la SessionFactory de Hibernate.
 * Se encarga de la configuración inicial de Hibernate y de la conexión a la base de datos.
 */
public class DataProvider {

    private static final Logger logger = Logger.getLogger(DataProvider.class.getName());
    public static SessionFactory sessionFactory = null;

    /**
     * Constructor privado para evitar instanciación de la clase de utilidad.
     */
    private DataProvider() {
        // Constructor privado
    }

    /**
     * Obtiene la SessionFactory de Hibernate. Si no ha sido inicializada, la configura
     * utilizando el archivo hibernate.cfg.xml y las variables de entorno para las credenciales de la DB.
     *
     * @return La SessionFactory de Hibernate.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            logger.info("Inicializando SessionFactory de Hibernate...");
            try {
                var configuration = new Configuration().configure();
                // Se asume que DB_USER y DB_PASSWORD están configuradas como variables de entorno
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");

                if (dbUser == null || dbPassword == null) {
                    logger.warning("Variables de entorno DB_USER o DB_PASSWORD no configuradas. Intentando con valores por defecto o configurados en hibernate.cfg.xml.");
                } else {
                    configuration.setProperty("hibernate.connection.username", dbUser);
                    configuration.setProperty("hibernate.connection.password", dbPassword);
                    logger.info("Credenciales de DB cargadas desde variables de entorno.");
                }

                sessionFactory = configuration.buildSessionFactory();
                logger.info("SessionFactory de Hibernate inicializada exitosamente.");
            } catch (Exception e) {
                logger.severe("Error al inicializar la SessionFactory de Hibernate: " + e.getMessage());
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }
}
