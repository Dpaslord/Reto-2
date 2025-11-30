package org.example.reto2.session;

import org.example.reto2.user.User;
import org.example.reto2.user.UserRepository;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servicio de autenticación para validar las credenciales de los usuarios.
 * Utiliza un {@link UserRepository} para buscar usuarios en la base de datos.
 */
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private UserRepository userRepository;

    /**
     * Constructor que inicializa el servicio de autenticación con un repositorio de usuarios.
     * @param userRepository El repositorio de usuarios a utilizar.
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("AuthService inicializado.");
    }

    /**
     * Valida las credenciales de un usuario (email y contraseña).
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     * @return Un {@code Optional} que contiene el objeto {@link User} si las credenciales son válidas,
     *         o un {@code Optional.empty()} si las credenciales son incorrectas o el usuario no existe.
     */
    public Optional<User> validateUser(String email, String password) {
        logger.info("Intentando validar usuario con email: " + email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                logger.info("Usuario " + email + " validado exitosamente.");
                return user;
            } else  {
                logger.warning("Contraseña incorrecta para el usuario: " + email);
                return Optional.empty();
            }
        }
        logger.warning("Usuario con email " + email + " no encontrado.");
        return Optional.empty();
    }

}
