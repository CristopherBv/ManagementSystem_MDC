package gestionmats.controllers.almacenista;

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

import gestionmats.utils.UIComponents;


public class AlmacenistaController implements Initializable {

    @FXML private Label lblUserName, lblFecha, lblHora;
    @FXML private Button navDespacho, navRecepcion, btnLogout;
    @FXML private StackPane contentArea;

    private Timeline clockTimeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupClock();

        UIComponents.applyNavHover(navDespacho);
        UIComponents.applyNavHover(navRecepcion);

        // Como no encontramos dónde guardan al usuario, ponemos un texto fijo por ahora
        // para que no truene al compilar.
        if (lblUserName != null) {
            lblUserName.setText("Almacenista en Línea");
        }

        // Cargamos la primera vista al empezar
        showDespacho();
    }

    private void setupClock() {
        // Usamos Locale para que los días salgan en español
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", new Locale("es", "MX"));
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            LocalDateTime now = LocalDateTime.now();
            lblFecha.setText(now.format(fmtFecha));
            lblHora.setText(now.format(fmtHora));
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void loadView(String fxmlFile) {
        try {
            // Ajustamos la ruta para que coincida con donde creaste tus archivos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/almacenista/" + fxmlFile));
            Parent view = loader.load();

            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (IOException e) {
            System.err.println("Error: No se pudo cargar la sub-vista " + fxmlFile);
            e.printStackTrace();
        }
    }

    @FXML private void showDespacho() { loadView("DespachoView.fxml"); }
    @FXML private void showRecepcion() { loadView("RecepcionView.fxml"); }

    @FXML
    private void handleLogout() {
        // Por ahora solo cerramos la app o imprimimos en consola
        System.out.println("Cerrando sesión de almacenista...");
        System.exit(0);
    }
}