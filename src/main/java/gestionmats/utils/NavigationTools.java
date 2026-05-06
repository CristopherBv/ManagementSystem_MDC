package gestionmats.utils;

import gestionmats.services.GestorSesion;
import gestionmats.utils.UIComponents;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * NavigationTools — Sistema centralizado de enrutamiento y control de vistas.
 * Responsabilidad: Manejar el cambio de ventanas, transiciones entre FXML y gestión de cierres de sesión.
 */
public final class NavigationTools {

    private NavigationTools() { /* Clase utilitaria, no instanciar */ }

    /**
     * Navega a una nueva vista FXML cerrando la escena actual.
     *
     * @param sourceNode Cualquier nodo de la escena actual (para obtener el Stage)
     * @param fxmlPath   Ruta del FXML destino (ej. "/views/Login.fxml")
     * @param title      Título de la nueva ventana
     */
    public static void navigateTo(Node sourceNode, String fxmlPath, String title) {
        try {
            Stage currentStage = (Stage) sourceNode.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(NavigationTools.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(newScene);
            newStage.setTitle(title);
            newStage.setResizable(false);

            // Transición delegada a UIComponents: fade out → abrir nueva escena
            Node sceneRoot = currentStage.getScene().getRoot();
            UIComponents.fadeOutThen(sceneRoot, 250, () -> {
                currentStage.close();
                newStage.show();
                UIComponents.fadeIn(root, 300);
            });

        } catch (IOException e) {
            e.printStackTrace();
            UIComponents.showNotification("Error al navegar: " + e.getMessage(), "error");
        }
    }

    /**
     * Navega reemplazando la escena en el mismo Stage (sin abrir una ventana nueva).
     */
    public static void navigateInStage(Node sourceNode, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) sourceNode.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(NavigationTools.class.getResource(fxmlPath));
            Parent root = loader.load();

            Node currentRoot = stage.getScene().getRoot();
            UIComponents.fadeOutThen(currentRoot, 200, () -> {
                stage.getScene().setRoot(root);
                stage.setTitle(title);
                UIComponents.fadeIn(root, 250);
            });

        } catch (IOException e) {
            e.printStackTrace();
            UIComponents.showNotification("Error de navegación.", "error");
        }
    }

    /**
     * MÉTODO UNIVERSAL (DRY)
     * Maneja la lógica de cerrar sesión desde cualquier pantalla.
     *
     * @param btnLogout El botón que disparó el evento (necesario para obtener la ventana)
     * @param relojActivo El Timeline del reloj (puede ser null si la vista no tiene reloj)
     */
    public static void manejarLogout(Button btnLogout, javafx.animation.Timeline relojActivo) {
        boolean confirmed = UIComponents.showConfirmDialog(
                "Cerrar sesión",
                "¿Desea cerrar la sesión actual?"
        );

        if (confirmed) {
            // 1. Detenemos el reloj si la vista lo tenía corriendo
            if (relojActivo != null) {
                relojActivo.stop();
            }

            // 2. Limpiamos el usuario del Singleton
            GestorSesion.getInstancia().cerrarSesion();

            // 3. Navegamos de vuelta al Login
            navigateTo(btnLogout, "/views/Login.fxml", "SDG MDC – Autenticación");
        }
    }
}