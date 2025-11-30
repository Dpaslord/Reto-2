package org.example.reto2.user;

import org.example.reto2.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Repositorio para la gestión de entidades {@link User} en la base de datos.
 * Proporciona métodos para realizar operaciones CRUD y consultas específicas de usuarios.
 */
public class UserRepository implements Repository<User> {

    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());
    private SessionFactory sessionFactory;

    /**
     * Constructor que inicializa el repositorio con una SessionFactory.
     * @param sessionFactory La SessionFactory de Hibernate.
     */
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        logger.info("UserRepository inicializado.");
    }

    /**
     * Guarda una entidad de usuario en la base de datos.
     * Actualmente no implementado.
     * @param entity La entidad de usuario a guardar.
     * @return null (no implementado).
     */
    @Override
    public User save(User entity) {
        logger.warning("Método save para User no implementado.");
        return null;
    }

    /**
     * Elimina una entidad de usuario de la base de datos.
     * Actualmente no implementado.
     * @param entity La entidad de usuario a eliminar.
     * @return Optional.empty() (no implementado).
     */
    @Override
    public Optional<User> delete(User entity) {
        logger.warning("Método delete para User no implementado.");
        return Optional.empty();
    }

    /**
     * Elimina una entidad de usuario por su ID de la base de datos.
     * Actualmente no implementado.
     * @param id El ID del usuario a eliminar.
     * @return Optional.empty() (no implementado).
     */
    @Override
    public Optional<User> deleteById(Long id) {
        logger.warning("Método deleteById para User no implementado.");
        return Optional.empty();
    }

    /**
     * Busca una entidad de usuario por su ID.
     * Actualmente no implementado.
     * @param id El ID del usuario a buscar.
     * @return Optional.empty() (no implementado).
     */
    @Override
    public Optional<User> findById(Long id) {
        logger.warning("Método findById para User no implementado.");
        return Optional.empty();
    }

    /**
     * Recupera todas las entidades de usuario de la base de datos.
     * Actualmente no implementado.
     * @return Una lista vacía (no implementado).
     */
    @Override
    public List<User> findAll() {
        logger.warning("Método findAll para User no implementado.");
        return List.of();
    }

    /**
     * Cuenta el número total de entidades de usuario en la base de datos.
     * Actualmente no implementado.
     * @return 0L (no implementado).
     */
    @Override
    public Long count() {
        logger.warning("Método count para User no implementado.");
        return 0L;
    }

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email La dirección de correo electrónico del usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra, o un Optional vacío si no.
     */
    public Optional<User> findByEmail(String email) {
        logger.info("Buscando usuario por email: " + email);
        try(Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery(
                    "from User where email=:email",User.class);
            q.setParameter("email", email);
            Optional<User> user = Optional.ofNullable(q.uniqueResult());
            if (user.isPresent()) {
                logger.info("Usuario con email " + email + " encontrado.");
            } else {
                logger.info("Usuario con email " + email + " no encontrado.");
            }
            return user;
        } catch (Exception e) {
            logger.severe("Error al buscar usuario por email " + email + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
