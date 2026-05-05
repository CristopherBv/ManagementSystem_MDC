package gestionmats.utils;

import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

/**
 * UIComponents — Sistema centralizado de estilos, animaciones y utilidades UI.
 *
 * Reemplaza UIEfectos con un enfoque más modular:
 *  - Animaciones CSS-first (sin inline styles en los botones con clase CSS)
 *  - Transiciones fluidas con ScaleTransition + FadeTransition
 *  - Helpers de navegación y diálogos
 *  - Notificaciones toast temporales
 */
public final class UIComponents {

    private UIComponents() { /* Clase utilitaria, no instanciar */ }

    // ══════════════════════════════════════════════════════
    // CONSTANTES DE DURACIÓN
    // ══════════════════════════════════════════════════════
    private static final Duration HOVER_DURATION  = Duration.millis(120);
    private static final Duration FADE_DURATION   = Duration.millis(200);
    private static final Duration TOAST_DURATION  = Duration.seconds(2.8);

    // ══════════════════════════════════════════════════════
    // ANIMACIONES EN BOTONES
    // ══════════════════════════════════════════════════════

    /**
     * Aplica efecto de escala suave al hacer hover (cualquier nodo).
     * No modifica el estilo — funciona 100% con clases CSS.
     */
    public static void applyHoverScale(Node node, double hoverScale) {
        if (node == null) return;
        node.setCursor(Cursor.HAND);

        ScaleTransition scaleIn  = new ScaleTransition(HOVER_DURATION, node);
        ScaleTransition scaleOut = new ScaleTransition(HOVER_DURATION, node);
        scaleIn.setToX(hoverScale);  scaleIn.setToY(hoverScale);
        scaleOut.setToX(1.0);        scaleOut.setToY(1.0);

        node.setOnMouseEntered(e -> { scaleIn.playFromStart(); scaleOut.stop(); });
        node.setOnMouseExited(e  -> { scaleOut.playFromStart(); scaleIn.stop(); });
        node.setOnMousePressed(e -> quickScale(node, hoverScale - 0.04));
        node.setOnMouseReleased(e -> quickScale(node, hoverScale));
    }

    /** Escala instantánea para feedback de press */
    private static void quickScale(Node node, double scale) {
        ScaleTransition st = new ScaleTransition(Duration.millis(60), node);
        st.setToX(scale); st.setToY(scale);
        st.play();
    }

    // ── Variantes para cada tipo de botón ─────────────────

    /** Botón de agregar/primario — escala ligeramente más */
    public static void applyButtonAdd(Button btn) {
        applyHoverScale(btn, 1.04);
    }

    /** Botón de editar */
    public static void applyButtonEdit(Button btn) {
        applyHoverScale(btn, 1.03);
    }

    /** Botón de eliminar — escala mínima para no asustar */
    public static void applyButtonDelete(Button btn) {
        applyHoverScale(btn, 1.03);
    }

    /** Botón ghost/secundario — sin escala, solo cursor */
    public static void applyButtonGhost(Button btn) {
        if (btn == null) return;
        btn.setCursor(Cursor.HAND);
    }

    /** Botón con efecto pulse suave (ej. logout) */
    public static void applyButtonPulse(Button btn) {
        if (btn == null) return;
        btn.setCursor(Cursor.HAND);

        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), btn);
        pulse.setFromX(1.0); pulse.setToX(1.025);
        pulse.setFromY(1.0); pulse.setToY(1.025);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        // Detener pulse en hover para no superponerse
        btn.setOnMouseEntered(e -> pulse.pause());
        btn.setOnMouseExited(e  -> pulse.play());
    }

    /**
     * Hover sutil para botones de navegación lateral.
     * Solo cursor — el hover visual lo maneja CSS.
     */
    public static void applyNavHover(Button btn) {
        if (btn == null) return;
        btn.setCursor(Cursor.HAND);
    }

    // ══════════════════════════════════════════════════════
    // TRANSICIONES DE CONTENIDO
    // ══════════════════════════════════════════════════════

    /** Fade-in de un nodo desde 0 a 1 */
    public static void fadeIn(Node node, double millis) {
        if (node == null) return;
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(millis), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    /** Fade-out y luego ejecutar acción */
    public static void fadeOutThen(Node node, double millis, Runnable then) {
        if (node == null) { if (then != null) then.run(); return; }
        FadeTransition ft = new FadeTransition(Duration.millis(millis), node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setOnFinished(e -> { if (then != null) then.run(); });
        ft.play();
    }

    /** Slide-in desde abajo (translateY: +20 → 0) con fade */
    public static void slideInFromBottom(Node node, double millis) {
        if (node == null) return;
        node.setOpacity(0);
        node.setTranslateY(20);

        FadeTransition  ft = new FadeTransition(Duration.millis(millis), node);
        TranslateTransition tt = new TranslateTransition(Duration.millis(millis), node);
        ft.setToValue(1.0);
        tt.setToY(0);

        ParallelTransition pt = new ParallelTransition(ft, tt);
        pt.play();
    }

    // ══════════════════════════════════════════════════════
    // NOTIFICACIONES TOAST
    // ══════════════════════════════════════════════════════

    /**
     * Muestra un toast temporal en la esquina de la escena.
     *
     * @param message Texto a mostrar
     * @param type    "success" | "warn" | "info" | "error"
     */
    public static void showNotification(String message, String type) {
        // Implementación usando Alert estilizada mientras no existe un StackPane global
        Alert.AlertType alertType = switch (type) {
            case "success" -> Alert.AlertType.INFORMATION;
            case "warn"    -> Alert.AlertType.WARNING;
            case "error"   -> Alert.AlertType.ERROR;
            default        -> Alert.AlertType.INFORMATION;
        };

        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle("SDG MDC");
        alert.setHeaderText(null);
        styleAlert(alert);
        alert.show();

        // Auto-cerrar después de TOAST_DURATION
        PauseTransition delay = new PauseTransition(TOAST_DURATION);
        delay.setOnFinished(e -> {
            if (alert.isShowing()) alert.close();
        });
        delay.play();
    }

    /**
     * Versión con StackPane de overlay: llama a esto si tienes un
     * StackPane raíz accesible (más elegante que Alert).
     *
     * @param container StackPane raíz de la escena
     * @param message   Texto del toast
     * @param type      "success" | "warn" | "error" | "info"
     */
    public static void showToast(StackPane container, String message, String type) {
        String bgColor = switch (type) {
            case "success" -> "rgba(34,197,94,0.15)";
            case "warn"    -> "rgba(251,191,36,0.15)";
            case "error"   -> "rgba(239,68,68,0.15)";
            default        -> "rgba(245,130,32,0.15)";
        };
        String borderColor = switch (type) {
            case "success" -> "rgba(34,197,94,0.40)";
            case "warn"    -> "rgba(251,191,36,0.40)";
            case "error"   -> "rgba(239,68,68,0.40)";
            default        -> "rgba(245,130,32,0.40)";
        };
        String textColor = switch (type) {
            case "success" -> "#4ade80";
            case "warn"    -> "#fbbf24";
            case "error"   -> "#f87171";
            default        -> "#F58220";
        };

        Label lbl = new Label(message);
        lbl.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 10 18;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0.2, 0, 4);"
        );

        HBox toast = new HBox(lbl);
        toast.setStyle("-fx-alignment: CENTER;");
        StackPane.setAlignment(toast, javafx.geometry.Pos.BOTTOM_CENTER);
        toast.setTranslateY(-24);

        container.getChildren().add(toast);

        // Animación: fade in → pausa → fade out → remover
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        PauseTransition hold = new PauseTransition(TOAST_DURATION);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> container.getChildren().remove(toast));

        new SequentialTransition(fadeIn, hold, fadeOut).play();
    }

    // ══════════════════════════════════════════════════════
    // DIÁLOGOS
    // ══════════════════════════════════════════════════════

    /**
     * Diálogo de confirmación estilizado.
     * @return true si el usuario presionó "Confirmar"
     */
    public static boolean showConfirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);

        ButtonType btnConfirm = new ButtonType("Confirmar", ButtonType.OK.getButtonData());
        ButtonType btnCancel  = new ButtonType("Cancelar",  ButtonType.CANCEL.getButtonData());
        alert.getButtonTypes().setAll(btnConfirm, btnCancel);

        styleAlert(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == btnConfirm;
    }

    /** Aplica estilos básicos oscuros al Alert de JavaFX */
    private static void styleAlert(Alert alert) {
        alert.getDialogPane().setStyle(
            "-fx-background-color: #16181f;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );
        alert.getDialogPane().lookup(".content.label")
                .setStyle("-fx-text-fill: #d1d5db; -fx-font-size: 13px;");
    }

    // ══════════════════════════════════════════════════════
    // NAVEGACIÓN ENTRE VISTAS
    // ══════════════════════════════════════════════════════

    /**
     * Navega a una nueva vista FXML cerrando la escena actual.
     *
     * @param sourceNode Cualquier nodo de la escena actual (para obtener el Stage)
     * @param fxmlPath   Ruta del FXML destino (ej. "/gestionmats/views/Login.fxml")
     * @param title      Título de la nueva ventana
     */
    public static void navigateTo(Node sourceNode, String fxmlPath, String title) {
        try {
            Stage currentStage = (Stage) sourceNode.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(UIComponents.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene newScene = new Scene(root);
            Stage newStage = new Stage();
            newStage.setScene(newScene);
            newStage.setTitle(title);
            newStage.setResizable(false);

            // Transición: fade out → abrir nueva escena
            Node sceneRoot = currentStage.getScene().getRoot();
            fadeOutThen(sceneRoot, 250, () -> {
                currentStage.close();
                newStage.show();
                fadeIn(root, 300);
            });

        } catch (IOException e) {
            e.printStackTrace();
            showNotification("Error al navegar: " + e.getMessage(), "error");
        }
    }

    /**
     * Navega reemplazando la escena en el mismo Stage (sin nueva ventana).
     */
    public static void navigateInStage(Node sourceNode, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) sourceNode.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(UIComponents.class.getResource(fxmlPath));
            Parent root = loader.load();

            Node currentRoot = stage.getScene().getRoot();
            fadeOutThen(currentRoot, 200, () -> {
                stage.getScene().setRoot(root);
                stage.setTitle(title);
                fadeIn(root, 250);
            });

        } catch (IOException e) {
            e.printStackTrace();
            showNotification("Error de navegación.", "error");
        }
    }
}
