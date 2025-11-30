package org.example.reto2.pelicula;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Representa una película en la base de datos.
 * Contiene información como el ID, título, género, año de lanzamiento,
 * descripción y director.
 */
@Data
@Entity
@Table(name="pelicula")
public class Pelicula implements Serializable {

    private static final Logger logger = Logger.getLogger(Pelicula.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String genero;
    private Integer anio;
    private String descripcion;
    private String director;

    // Constructor, getters y setters son generados por Lombok (@Data)
    // No se añaden métodos específicos para logging aquí, ya que las operaciones
    // sobre Pelicula se registran en PeliculaRepository.
}
