package org.example.reto2.copia;

import org.example.reto2.user.User;
import org.example.reto2.utils.DataProvider;
import org.hibernate.Session;

import java.util.logging.Logger;

/**
 * Servicio para la gestión de operaciones relacionadas con las {@link Copia Copias} de películas.
 * Proporciona métodos para añadir, eliminar y actualizar copias de la colección de un usuario.
 */
public class CopiaService {

    private static final Logger logger = Logger.getLogger(CopiaService.class.getName());

    /**
     * Elimina una copia específica de la colección de un usuario.
     * Si la copia tiene una cantidad mayor que 1, decrementa la cantidad.
     * Si la cantidad es 1, elimina la copia por completo.
     *
     * @param user El usuario propietario de la copia.
     * @param copia La copia a eliminar o decrementar.
     * @return El objeto User actualizado después de la operación.
     */
    public User deleteCopiaFromUser(User user, Copia copia) {
        logger.info("Intentando eliminar/decrementar copia con ID " + copia.getId() + " para el usuario " + user.getEmail());
        try(Session s = DataProvider.getSessionFactory().openSession()) {
            s.beginTransaction();

            User currentUser = s.find(User.class, user.getId());
            Copia copiaToDelete = s.find(Copia.class, copia.getId());

            if (copiaToDelete != null) {
                if (copiaToDelete.getCantidad() > 1) {
                    // Decrementar la cantidad si es mayor que 1
                    copiaToDelete.setCantidad(copiaToDelete.getCantidad() - 1);
                    s.merge(copiaToDelete);
                    logger.info("Cantidad de copia con ID " + copia.getId() + " decrementada a " + copiaToDelete.getCantidad());
                } else {
                    // Eliminar la copia si la cantidad es 1
                    currentUser.getCopias().removeIf(c -> c.getId().equals(copia.getId()));
                    s.remove(copiaToDelete);
                    logger.info("Copia con ID " + copia.getId() + " eliminada completamente.");
                }
            } else {
                logger.warning("Copia con ID " + copia.getId() + " no encontrada para eliminar.");
            }

            s.getTransaction().commit();
            logger.info("Transacción de eliminación/decremento de copia completada.");

            // Refrescar el usuario para asegurar que la lista de copias esté actualizada
            s.refresh(currentUser);
            return currentUser;
        } catch (Exception e) {
            logger.severe("Error al eliminar/decrementar copia con ID " + copia.getId() + ": " + e.getMessage());
            throw e; // Re-lanzar la excepción para que el controlador pueda manejarla
        }
    }

    /**
     * Crea y añade una nueva copia de película a la colección de un usuario.
     *
     * @param newCopia La nueva copia a añadir.
     * @param actualUser El usuario al que se le añadirá la copia.
     * @return El objeto User actualizado después de añadir la copia.
     */
    public User createNewCopia(Copia newCopia, User actualUser) {
        logger.info("Intentando crear nueva copia para la película " + newCopia.getPelicula().getTitulo() + " y el usuario " + actualUser.getEmail());
        try(Session s = DataProvider.getSessionFactory().openSession()) {
            actualUser.addCopia(newCopia); // Establece la relación bidireccional
            s.beginTransaction();
            s.merge(actualUser); // Persiste los cambios en el usuario (que incluye la nueva copia)
            s.getTransaction().commit();
            logger.info("Nueva copia creada y añadida al usuario " + actualUser.getEmail() + ". ID de copia: " + newCopia.getId());
            return s.find(User.class, actualUser.getId()); // Devuelve el usuario actualizado desde la DB
        } catch (Exception e) {
            logger.severe("Error al crear nueva copia para el usuario " + actualUser.getEmail() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Actualiza los datos de una copia existente en la base de datos.
     *
     * @param copia La copia con los datos actualizados.
     * @param actualUser El usuario propietario de la copia.
     * @return El objeto User actualizado después de la operación.
     */
    public User updateCopia(Copia copia, User actualUser) {
        logger.info("Intentando actualizar copia con ID " + copia.getId() + " para el usuario " + actualUser.getEmail());
        try (Session s = DataProvider.getSessionFactory().openSession()) {
            s.beginTransaction();
            s.merge(copia); // Actualiza la copia
            s.getTransaction().commit();
            logger.info("Copia con ID " + copia.getId() + " actualizada correctamente.");
            return s.find(User.class, actualUser.getId()); // Devuelve el usuario actualizado
        } catch (Exception e) {
            logger.severe("Error al actualizar copia con ID " + copia.getId() + ": " + e.getMessage());
            throw e;
        }
    }
}
