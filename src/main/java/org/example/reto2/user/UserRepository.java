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
     * Si la entidad ya existe (basado en su ID), la actualiza; de lo contrario, la persiste.
     * @param entity La entidad de usuario a guardar.
     * @return La entidad de usuario guardada/actualizada.
     */
    @Override
    public User save(User entity) {
        logger.info("Guardando usuario: " + entity.getEmail());
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(entity); // Usa merge para persistir o actualizar
            session.getTransaction().commit();
            logger.info("Usuario " + entity.getEmail() + " guardado exitosamente con ID: " + entity.getId());
            return entity;
        } catch (Exception e) {
            logger.severe("Error al guardar usuario " + entity.getEmail() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una entidad de usuario de la base de datos.
     * @param entity La entidad de usuario a eliminar.
     * @return Un Optional que contiene la entidad eliminada si la operación fue exitosa,
     *         o un Optional vacío si la entidad no pudo ser eliminada o no existía.
     */
    @Override
    public Optional<User> delete(User entity) {
        logger.info("Intentando eliminar usuario: " + entity.getEmail() + " (ID: " + entity.getId() + ")");
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(entity);
            session.getTransaction().commit();
            logger.info("Usuario " + entity.getEmail() + " eliminado exitosamente.");
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.severe("Error al eliminar usuario " + entity.getEmail() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una entidad de usuario por su ID de la base de datos.
     * @param id El ID del usuario a eliminar.
     * @return Un Optional que contiene la entidad eliminada si la operación fue exitosa,
     *         o un Optional vacío si la entidad no pudo ser eliminada o no existía.
     */
    @Override
    public Optional<User> deleteById(Long id) {
        logger.info("Intentando eliminar usuario por ID: " + id);
        try(Session session = sessionFactory.openSession()){
            User user = session.find(User.class, id.intValue()); // Hibernate IDs are typically Integer for IDENTITY strategy
            if(user != null){
                session.beginTransaction();
                session.remove(user);
                session.getTransaction().commit();
                logger.info("Usuario con ID " + id + " eliminado exitosamente.");
            } else {
                logger.warning("Usuario con ID " + id + " no encontrado para eliminar.");
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.severe("Error al eliminar usuario por ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una entidad de usuario por su ID.
     * @param id El ID del usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra,
     *         o un Optional vacío si no se encuentra ninguna entidad con ese ID.
     */
    @Override
    public Optional<User> findById(Long id) {
        logger.info("Buscando usuario por ID: " + id);
        try(Session session = sessionFactory.openSession()){
            Optional<User> user = Optional.ofNullable(session.find(User.class, id.intValue())); // Hibernate IDs are typically Integer for IDENTITY strategy
            if (user.isPresent()) {
                logger.info("Usuario con ID " + id + " encontrado.");
            } else {
                logger.info("Usuario con ID " + id + " no encontrado.");
            }
            return user;
        } catch (Exception e) {
            logger.severe("Error al buscar usuario por ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Recupera todas las entidades de usuario de la base de datos.
     * @return Una Lista de todas las entidades de usuario encontradas.
     */
    @Override
    public List<User> findAll() {
        logger.info("Recuperando todos los usuarios.");
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery("from User", User.class).list();
            logger.info(users.size() + " usuarios recuperados.");
            return users;
        } catch (Exception e) {
            logger.severe("Error al recuperar todos los usuarios: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cuenta el número total de entidades de usuario en la base de datos.
     * @return El número total de usuarios.
     */
    @Override
    public Long count() {
        logger.info("Contando el número de usuarios.");
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("select count(u) from User u", Long.class).uniqueResult();
            logger.info("Número total de usuarios: " + count);
            return count;
        } catch (Exception e) {
            logger.severe("Error al contar usuarios: " + e.getMessage());
            throw e;
        }
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
