package org.example.reto2.session;

import org.example.reto2.user.User;
import org.example.reto2.utils.SessionService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Implementación simple de un servicio de sesión para gestionar el usuario activo
 * y almacenar objetos temporales durante la vida de la sesión.
 * Sigue el patrón Singleton.
 */
public class SimpleSessionService implements SessionService<User> {

    private static final Logger logger = Logger.getLogger(SimpleSessionService.class.getName());
    private static SimpleSessionService instance;
    private User currentUser;
    private Map<String, Object> objects = new HashMap<>();

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private SimpleSessionService() {
        logger.info("SimpleSessionService inicializado.");
    }

    /**
     * Obtiene la única instancia de SimpleSessionService.
     * @return La instancia de SimpleSessionService.
     */
    public static SimpleSessionService getInstance() {
        if (instance == null) {
            instance = new SimpleSessionService();
        }
        return instance;
    }

    /**
     * Inicia la sesión para un usuario dado.
     * @param user El usuario que inicia sesión.
     */
    @Override
    public void login(User user) {
        this.currentUser = user;
        logger.info("Usuario " + user.getEmail() + " ha iniciado sesión.");
    }

    /**
     * Cierra la sesión actual, estableciendo el usuario activo a null y limpiando los objetos de sesión.
     */
    @Override
    public void logout() {
        if (this.currentUser != null) {
            logger.info("Usuario " + this.currentUser.getEmail() + " ha cerrado sesión.");
        } else {
            logger.info("Sesión cerrada (no había usuario activo).");
        }
        this.currentUser = null;
        this.objects.clear(); // Limpiar objetos de la sesión al cerrar
    }

    /**
     * Verifica si hay un usuario actualmente logueado.
     * @return true si hay un usuario activo, false en caso contrario.
     */
    @Override
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    /**
     * Obtiene el usuario actualmente activo en la sesión.
     * @return El usuario activo.
     */
    @Override
    public User getActive() {
        return currentUser;
    }

    /**
     * Almacena un objeto en la sesión con una clave específica.
     * @param key La clave para identificar el objeto.
     * @param value El objeto a almacenar.
     */
    @Override
    public void setObject(String key, Object value) {
        objects.put(key, value);
        logger.fine("Objeto '" + key + "' almacenado en la sesión.");
    }

    /**
     * Recupera un objeto de la sesión utilizando su clave.
     * @param key La clave del objeto a recuperar.
     * @return El objeto almacenado, o null si no se encuentra.
     */
    @Override
    public Object getObject(String key) {
        Object obj = objects.get(key);
        if (obj != null) {
            logger.fine("Objeto '" + key + "' recuperado de la sesión.");
        } else {
            logger.fine("Objeto '" + key + "' no encontrado en la sesión.");
        }
        return obj;
    }
}
