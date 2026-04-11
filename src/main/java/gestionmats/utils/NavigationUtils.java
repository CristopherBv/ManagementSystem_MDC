package gestionmats.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationUtils {

    /**
     * Cambia la escena actual a la pantalla de Login (Logout).
     * @param nodeCualquiera Un nodo de la escena actual para obtener el Stage.
     */
    public static void irALogin(Node nodeCualquiera) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(NavigationUtils.class.getResource("/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) nodeCualquiera.getScene().getWindow();
            stage.setTitle("Inicio de Sesión - Constructora");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            AlertUtils.mostrarError("Error de Navegación", "No se pudo regresar al login.");
            e.printStackTrace();
        }
    }

    /**
     * Carga un FXML dentro de un contenedor StackPane (usado en el Cascarón).
     */
    public static void cargarVista(StackPane contentArea, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtils.class.getResource(fxmlPath));
            Node vista = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);
        } catch (IOException e) {
            mostrarEnConstruccion(contentArea, "Error al cargar: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un mensaje temporal de "Módulo en construcción" en el área central.
     */
    public static void mostrarEnConstruccion(StackPane contentArea, String modulo) {
        contentArea.getChildren().clear();
        Label lblConstruccion = new Label(modulo + "\n(Módulo en construcción)");
        lblConstruccion.setStyle("-fx-font-size: 24px; -fx-text-fill: #a3a3a3; -fx-text-alignment: center;");
        contentArea.getChildren().add(lblConstruccion);
    }
}
