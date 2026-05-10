package gestionmats.controllers.almacenista;

import gestionmats.model.Usuario;
import gestionmats.services.GestorSesion;
import gestionmats.utils.NavigationTools;
import gestionmats.utils.UIComponents;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Locale;


public class AlmacenistaController implements Initializable {

    @FXML private Label lblPageTitle, lblPageSub, lblUserName, lblFecha, lblHora;
    @FXML private Button navDespacho, navRecepcion, btnLogout;
    @FXML private StackPane contentArea;

    private Timeline clockTimeline;
    private Button activeNavBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupClock();
        setupNavButtons();

        Usuario usuarioLogueado = GestorSesion.getInstancia().getUsuarioActual();
        if (usuarioLogueado != null) {
            // Actualizamos el nombre en la barra lateral izquierda
            lblUserName.setText(usuarioLogueado.getNombre() + " " + usuarioLogueado.getPrimerApellido());
        }

        // Cargamos la primera vista al empezar
        showDespacho();
    }

    private void setupClock() {
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", new java.util.Locale("es", "MX"));
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime now = LocalDateTime.now();
            lblFecha.setText(now.format(fmtFecha));
            lblHora.setText(now.format(fmtHora));
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void setupNavButtons() {
        UIComponents.applyNavHover(navDespacho);
        UIComponents.applyNavHover(navRecepcion);
        UIComponents.applyNavHover(btnLogout);
    }

    private void loadView(String fxmlFile) {
        try {
            // Ajustamos la ruta para que coincida con donde creaste tus archivos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/almacenista/" + fxmlFile));
            Parent view = loader.load();

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
                view.setVisible(true); // Forzar visibilidad
            }
        } catch (IOException e) {
            UIComponents.showNotification("Error: No se pudo cargar la sub-vista " + fxmlFile, "error");
        }
    }

    private void loadModule(String fxmlPath, Button navBtn, String title, String sub) {
        try {
            // estética de los botones
            if (activeNavBtn != null) {
                activeNavBtn.getStyleClass().remove("nav-btn-active");
            }
            navBtn.getStyleClass().add("nav-btn-active");
            activeNavBtn = navBtn;

            // Usamos la ruta completa
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("No se encontró el archivo FXML en: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent module = loader.load();

            // Inyección en el área central (StackPane)
            if (contentArea != null) {
                contentArea.getChildren().setAll(module);

                // Aplicamos efecto visual
                UIComponents.fadeIn(module, 300);
            }

            // Actualización de textos en la Topbar
            lblPageTitle.setText(title);
            lblPageSub.setText(sub);

        } catch (IOException e) {
            // Si tienes el método de notificación, úsalo; si no, imprime el error
            UIComponents.showNotification("Error al cargar módulo: " + fxmlPath, "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void showDespacho() {
        loadModule("/views/almacenista/DespachoView.fxml",
                navDespacho,
                "Gestión de Despacho",
                "Salida de materiales a clientes");
    }

    @FXML
    private void showRecepcion() {
        loadModule("/views/almacenista/RecepcionView.fxml",
                navRecepcion,
                "Recepción de Entradas",
                "Ingreso de materiales de proveedores");
    }

    @FXML
    private void handleLogout() {
        NavigationTools.manejarLogout(btnLogout, clockTimeline);
    }
}