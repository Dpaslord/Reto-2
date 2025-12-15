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
    @Column(name = "año")
    private Integer anio;
    private String descripcion;
    private String director;

}
