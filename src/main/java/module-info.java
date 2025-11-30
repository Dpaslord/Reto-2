module org.example.reto2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires org.hibernate.orm.hikaricp;
    requires jakarta.xml.bind;
    requires jakarta.activation;


    opens org.example.reto2 to javafx.fxml;
    exports org.example.reto2;
    exports org.example.reto2.controllers;
    opens org.example.reto2.controllers to javafx.fxml;
    exports org.example.reto2.copia;
    opens org.example.reto2.copia to javafx.fxml, org.hibernate.orm.core;
    exports org.example.reto2.pelicula;
    opens org.example.reto2.pelicula to javafx.fxml, org.hibernate.orm.core;
    exports org.example.reto2.session;
    opens org.example.reto2.session to javafx.fxml, org.hibernate.orm.core;
    exports org.example.reto2.user;
    opens org.example.reto2.user to javafx.fxml, org.hibernate.orm.core;
    exports org.example.reto2.utils;
    opens org.example.reto2.utils to javafx.fxml;

}