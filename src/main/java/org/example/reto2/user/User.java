package org.example.reto2.user;

import jakarta.persistence.*;
import lombok.Data;
import org.example.reto2.copia.Copia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Representa un usuario en el sistema.
 * Contiene información como el ID, email, contraseña, si es administrador
 * y una lista de las copias de películas que posee.
 */
@Data
@Entity
@Table(name="user")
public class User implements Serializable {

    private static final Logger logger = Logger.getLogger(User.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;
    private String password;

    @Column(name="is_admin")
    private Boolean isAdmin;

    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "user", fetch = FetchType.EAGER)
    private List<Copia> copias = new ArrayList<>();

    /**
     * Añade una copia de película a la colección del usuario.
     * Establece la relación bidireccional entre la copia y el usuario.
     * @param copia La copia de película a añadir.
     */
    public void addCopia(Copia copia) {
        copia.setUser(this);
        this.copias.add(copia);
        logger.info("Copia " + copia.getId() + " añadida al usuario " + this.getEmail());
    }
}
