package org.example.reto2.copia;

import jakarta.persistence.*;
import lombok.Data;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.user.User;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Representa una copia específica de una película que posee un usuario.
 * Incluye detalles como la película asociada, el usuario propietario,
 * el estado de la copia, el tipo de soporte y la cantidad de unidades de esta copia.
 */
@Data
@Entity
@Table(name="copia")
public class Copia implements Serializable {

    private static final Logger logger = Logger.getLogger(Copia.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="id_pelicula")
    private Pelicula pelicula;

    @ManyToOne
    @JoinColumn(name="id_usuario")
    private User user;

    private String estado;
    private String soporte;
    private Integer cantidad;

    // Constructor, getters y setters son generados por Lombok (@Data)
    // No se añaden métodos específicos para logging aquí, ya que las operaciones
    // sobre Copia se registran en CopiaService.
}
