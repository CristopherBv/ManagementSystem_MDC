package gestionmats.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class NavigationUtils {
    /**
     * Cierra la ventana anterior y crea una nueva ventana maximizada para el sistema principal.
     */
    public static void abrirVentanaPrincipal(Stage stageAnterior, Scene scene, String titulo) {
        // 1. Destruir la ventana de login
        if (stageAnterior != null) {
            stageAnterior.close();
        }

        // 2. Crear una ventana completamente nueva
        Stage nuevoStage = new Stage();
        nuevoStage.setScene(scene);
        nuevoStage.setTitle(titulo);
        nuevoStage.setResizable(true);

        // 3. Aplicar dimensiones relativas a la pantalla
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        nuevoStage.setWidth(bounds.getWidth() * 0.9);
        nuevoStage.setHeight(bounds.getHeight() * 0.9);

        nuevoStage.centerOnScreen();
        nuevoStage.show(); // Mostrar la nueva ventana
    }

    /**
     * Cierra el sistema principal y crea una nueva ventana estricta para el Login.
     */
    public static void irALogin(Node nodoCualquiera) {
        try {
            // 1. Obtener la ventana actual (el cascarón) y destruirla
            Stage stageActual = (Stage) nodoCualquiera.getScene().getWindow();
            if (stageActual != null) {
                stageActual.close();
            }

            // 2. Cargar el FXML del login
            FXMLLoader fxmlLoader = new FXMLLoader(NavigationUtils.class.getResource("/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // 3. Crear una nueva ventana limpia para el login
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Inicio de Sesión");
            loginStage.setResizable(false);

            // Al ser un Stage nuevo, sizeToScene() funciona inmediatamente sin Platform.runLater
            loginStage.sizeToScene();
            loginStage.centerOnScreen();
            loginStage.show();

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