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
     * Abre el sistema principal (Grande) y luego cierra el Login (Pequeño)
     */
    public static void abrirVentanaPrincipal(Stage stageAnterior, Scene scene, String titulo) {
        // 1. Preparamos la ventana nueva
        Stage nuevoStage = new Stage();
        nuevoStage.setScene(scene);
        nuevoStage.setTitle(titulo);

        // Usamos el maximizado nativo del sistema operativo para la zona POS
        // Esto es mucho más limpio que calcular el 90% manualmente
        nuevoStage.setMaximized(true);

        // 2. MOSTRAMOS la nueva ventana PRIMERO (Solapamiento)
        nuevoStage.show();

        // 3. CERRAMOS la ventana vieja DESPUÉS
        if (stageAnterior != null) {
            stageAnterior.close();
        }
    }

    /**
     * Abre el Login (Pequeño) y luego cierra el sistema principal
     */
    public static void irALogin(Node nodoCualquiera) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(NavigationUtils.class.getResource("/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // 1. Preparamos el Login pequeño
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Inicio de Sesión - Sistema de Gestión");

            // Bloqueamos el tamaño para que sea estricto a lo que dice tu FXML
            loginStage.setResizable(false);
            loginStage.sizeToScene();

            // 2. MOSTRAMOS el Login PRIMERO
            loginStage.show();
            loginStage.centerOnScreen(); // Lo centramos una vez que ya está visible

            // 3. CERRAMOS el sistema gigante DESPUÉS
            Stage stageActual = (Stage) nodoCualquiera.getScene().getWindow();
            if (stageActual != null) {
                stageActual.close();
            }

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