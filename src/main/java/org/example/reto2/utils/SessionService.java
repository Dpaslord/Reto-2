package org.example.reto2.utils;

/**
 * Interfaz genérica para la gestión de sesiones de usuario.
 * Define las operaciones básicas para iniciar sesión, verificar el estado de la sesión,
 * cerrar sesión y almacenar/recuperar objetos en la sesión.
 *
 * @param <T> El tipo de objeto que representa al usuario activo en la sesión.
 */
public interface SessionService<T> {
    /**
     * Inicia una sesión para el usuario especificado.
     * @param u El objeto de usuario para iniciar sesión.
     */
    void login(T u);

    /**
     * Verifica si hay una sesión activa.
     * @return true si hay un usuario logueado, false en caso contrario.
     */
    boolean isLoggedIn();

    /**
     * Cierra la sesión actual.
     */
    void logout();

    /**
     * Obtiene el usuario actualmente activo en la sesión.
     * @return El objeto de usuario activo.
     */
    T getActive();

    /**
     * Almacena un objeto en la sesión con una clave específica.
     * @param key La clave para identificar el objeto.
     * @param o El objeto a almacenar.
     */
    void setObject( String key, Object o );

    /**
     * Recupera un objeto de la sesión utilizando su clave.
     * @param key La clave del objeto a recuperar.
     * @return El objeto almacenado, o null si no se encuentra.
     */
    Object getObject(String key);
}
