package org.example.reto2.pelicula;

import org.example.reto2.utils.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Repositorio para la gestión de entidades {@link Pelicula} en la base de datos.
 * Proporciona métodos para realizar operaciones CRUD sobre películas.
 */
public class PeliculaRepository implements Repository<Pelicula> {

    private static final Logger logger = Logger.getLogger(PeliculaRepository.class.getName());
    private final SessionFactory sessionFactory;

    /**
     * Constructor que inicializa el repositorio con una SessionFactory.
     * @param sessionFactory La SessionFactory de Hibernate.
     */
    public PeliculaRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        logger.info("PeliculaRepository inicializado.");
    }

    /**
     * Guarda una entidad de película en la base de datos.
     * Si la entidad ya existe, la actualiza; de lo contrario, la persiste.
     * @param entity La entidad de película a guardar.
     * @return La entidad de película guardada/actualizada.
     */
    @Override
    public Pelicula save(Pelicula entity) {
        logger.info("Guardando película: " + entity.getTitulo());
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(entity); // Usa merge para persistir o actualizar
            session.getTransaction().commit();
            logger.info("Película " + entity.getTitulo() + " guardada exitosamente con ID: " + entity.getId());
            return entity;
        } catch (Exception e) {
            logger.severe("Error al guardar película " + entity.getTitulo() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una entidad de película de la base de datos.
     * @param entity La entidad de película a eliminar.
     * @return Un Optional que contiene la entidad eliminada si la operación fue exitosa,
     *         o un Optional vacío si la entidad no pudo ser eliminada o no existía.
     */
    @Override
    public Optional<Pelicula> delete(Pelicula entity) {
        logger.info("Intentando eliminar película: " + entity.getTitulo() + " (ID: " + entity.getId() + ")");
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(entity);
            session.getTransaction().commit();
            logger.info("Película " + entity.getTitulo() + " eliminada exitosamente.");
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            logger.severe("Error al eliminar película " + entity.getTitulo() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Elimina una entidad de película por su ID de la base de datos.
     * @param id El ID de la película a eliminar.
     * @return Un Optional que contiene la entidad eliminada si la operación fue exitosa,
     *         o un Optional vacío si la entidad no pudo ser eliminada o no existía.
     */
    @Override
    public Optional<Pelicula> deleteById(Long id) {
        logger.info("Intentando eliminar película por ID: " + id);
        try(Session session = sessionFactory.openSession()){
            Pelicula pelicula = session.find(Pelicula.class, id);
            if(pelicula != null){
                session.beginTransaction();
                session.remove(pelicula);
                session.getTransaction().commit();
                logger.info("Película con ID " + id + " eliminada exitosamente.");
            } else {
                logger.warning("Película con ID " + id + " no encontrada para eliminar.");
            }
            return Optional.ofNullable(pelicula);
        } catch (Exception e) {
            logger.severe("Error al eliminar película por ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una entidad de película por su ID.
     * @param id El ID de la película a buscar.
     * @return Un Optional que contiene la película si se encuentra,
     *         o un Optional vacío si no se encuentra ninguna película con ese ID.
     */
    @Override
    public Optional<Pelicula> findById(Long id) {
        logger.info("Buscando película por ID: " + id);
        try(Session session = sessionFactory.openSession()){
            Optional<Pelicula> pelicula = Optional.ofNullable(session.find(Pelicula.class, id));
            if (pelicula.isPresent()) {
                logger.info("Película con ID " + id + " encontrada.");
            } else {
                logger.info("Película con ID " + id + " no encontrada.");
            }
            return pelicula;
        } catch (Exception e) {
            logger.severe("Error al buscar película por ID " + id + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Recupera todas las entidades de película de la base de datos.
     * @return Una Lista de todas las películas encontradas.
     */
    public List<Pelicula> findAll() {
        logger.info("Recuperando todas las películas.");
        try (Session session = sessionFactory.openSession()) {
            List<Pelicula> peliculas = session.createQuery("from Pelicula", Pelicula.class).list();
            logger.info(peliculas.size() + " películas recuperadas.");
            return peliculas;
        } catch (Exception e) {
            logger.severe("Error al recuperar todas las películas: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Cuenta el número total de entidades de película en la base de datos.
     * @return El número total de películas.
     */
    @Override
    public Long count() {
        logger.info("Contando el número de películas.");
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("select count(p) from Pelicula p", Long.class).uniqueResult();
            logger.info("Número total de películas: " + count);
            return count;
        } catch (Exception e) {
            logger.severe("Error al contar películas: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Busca una película por sus atributos para evitar duplicados.
     * @param titulo El título de la película.
     * @param anio El año de la película.
     * @param director El director de la película.
     * @return Un Optional que contiene la película si se encuentra, o un Optional vacío si no.
     */
    public Optional<Pelicula> findByAttributes(String titulo, int anio, String director) {
        logger.info("Buscando película por atributos: " + titulo);
        try (Session session = sessionFactory.openSession()) {
            Query<Pelicula> q = session.createQuery(
                    "from Pelicula where titulo = :titulo and anio = :anio and director = :director", Pelicula.class);
            q.setParameter("titulo", titulo);
            q.setParameter("anio", anio);
            q.setParameter("director", director);
            return Optional.ofNullable(q.uniqueResult());
        } catch (Exception e) {
            logger.severe("Error al buscar película por atributos: " + e.getMessage());
            return Optional.empty();
        }
    }
}
