package org.example.reto2.utils;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para la implementación de patrones de repositorio.
 * Define las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) básicas
 * que cualquier repositorio debe proporcionar para una entidad {@code T}.
 *
 * @param <T> El tipo de entidad que gestionará este repositorio.
 */
public interface Repository<T> {

    /**
     * Guarda una entidad en la base de datos.
     * Si la entidad ya existe (basado en su ID), la actualiza; de lo contrario, la persiste.
     * @param entity La entidad a guardar.
     * @return La entidad guardada/actualizada.
     */
    T save(T entity);

    /**
     * Elimina una entidad de la base de datos.
     * @param entity La entidad a eliminar.
     * @return Un {@code Optional} que contiene la entidad eliminada si la operación fue exitosa,
     *         o un {@code Optional.empty()} si la entidad no pudo ser eliminada o no existía.
     */
    Optional<T> delete(T entity);

    /**
     * Elimina una entidad de la base de datos por su identificador.
     * @param id El identificador de la entidad a eliminar.
     * @return Un {@code Optional} que contiene la entidad eliminada si la operación fue exitosa,
     *         o un {@code Optional.empty()} si la entidad no pudo ser eliminada o no existía.
     */
    Optional<T> deleteById(Long id);

    /**
     * Busca una entidad por su identificador.
     * @param id El identificador de la entidad a buscar.
     * @return Un {@code Optional} que contiene la entidad si se encuentra,
     *         o un {@code Optional.empty()} si no se encuentra ninguna entidad con ese ID.
     */
    Optional<T> findById(Long id);

    /**
     * Recupera todas las entidades del tipo {@code T} de la base de datos.
     * @return Una {@code List} de todas las entidades encontradas.
     */
    List<T> findAll();

    /**
     * Cuenta el número total de entidades del tipo {@code T} en la base de datos.
     * @return El número total de entidades.
     */
    Long count();
}
