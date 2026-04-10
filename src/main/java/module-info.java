module com.gestionmats.app {
    // Le decimos a Java que necesitamos estas librerías de JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.compiler;

    opens gestionmats to javafx.fxml;
    exports gestionmats;
    // Le damos permiso a JavaFX para que lea tus FXML y Controladores (Esto quita el error rojo)
    opens gestionmats.controllers to javafx.fxml;
    // Le decimos a Java cuál es el paquete principal que se va a ejecutar
    exports gestionmats.controllers;

}