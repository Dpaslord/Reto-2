package org.example.reto2.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.reto2.pelicula.Pelicula;
import org.example.reto2.pelicula.PeliculaRepository;
import org.example.reto2.session.SimpleSessionService;
import org.example.reto2.utils.DataProvider;
import org.example.reto2.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Controlador para la vista principal del administrador (admin-main-view.fxml).
 * Muestra una tabla con todas las películas de la base de datos y permite
 * al administrador añadir nuevas películas y cerrar sesión.
 */
public class AdminMainController implements Initializable {

    private static final Logger logger = Logger.getLogger(AdminMainController.class.getName());

    @javafx.fxml.FXML
    private TableView<Pelicula> tableViewPeliculas;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colTitulo;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colGenero;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, Integer> colAnio;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colDirector;
    @javafx.fxml.FXML
    private TableColumn<Pelicula, String> colDescripcion;

    private PeliculaRepository peliculaRepository;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura las columnas de la tabla y refresca la tabla con todas las películas.
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info("Inicializando AdminMainController.");
        peliculaRepository = new PeliculaRepository(DataProvider.getSessionFactory());

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colDirector.setCellValueFactory(new PropertyValueFactory<>("director"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        refreshTable();
        logger.info("AdminMainController inicializado.");
    }

    /**
     * Maneja la acción de añadir una nueva película.
     * Redirige a la vista de añadir película.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void addPelicula(ActionEvent actionEvent) {
        logger.info("Redirigiendo a add-pelicula-view para añadir una nueva película.");
        JavaFXUtil.setScene("/org/example/reto2/add-pelicula-view.fxml");
    }

    /**
     * Maneja la acción de cerrar sesión del administrador.
     * Cierra la sesión actual y redirige a la vista de login.
     * @param actionEvent El evento de acción que disparó este método.
     */
    @javafx.fxml.FXML
    public void logout(ActionEvent actionEvent) {
        logger.info("Administrador solicitando cerrar sesión.");
        SimpleSessionService.getInstance().logout();
        JavaFXUtil.setScene("/org/example/reto2/login-view.fxml");
        logger.info("Sesión de administrador cerrada. Redirigiendo a login-view.");
    }

    /**
     * Refresca la tabla de películas con los datos más recientes de la base de datos.
     */
    private void refreshTable() {
        logger.info("Refrescando tabla de películas.");
        tableViewPeliculas.setItems(FXCollections.observableList(peliculaRepository.findAll()));
        tableViewPeliculas.refresh();
        logger.info("Tabla de películas refrescada. Número de películas: " + tableViewPeliculas.getItems().size());
    }
}
