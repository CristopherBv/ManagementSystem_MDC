package gestionmats.controllers.almacenista;

import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.VentaDaoCsv;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AlmacenistaController implements Initializable {

    @FXML private HBox cardDespacho;
    @FXML private HBox cardRecepcion;
    @FXML private Label lblCountDespacho;
    @FXML private Label lblCountRecepcion;
    @FXML private StackPane contentArea;

    // Elementos del Header
    @FXML private Button btnLogout;
    @FXML private Label lblUserName;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;

    private Timeline clockTimeline;
    private VentaDaoCsv ventaDao = new VentaDaoCsv();
    private OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupClock();

        Usuario usuarioLogueado = GestorSesion.getInstancia().getUsuarioActual();
        if (usuarioLogueado != null && lblUserName != null) {
            lblUserName.setText(usuarioLogueado.getNombre() + " " + usuarioLogueado.getPrimerApellido());
        }

        refreshBadges();
        showDespacho(); // Módulo activo por defecto al abrir
    }

    private void setupClock() {
        if (lblFecha == null || lblHora == null) return;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        clockTimeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblFecha.setText(dateFormatter.format(now).toUpperCase());
            lblHora.setText(timeFormatter.format(now));
        }), new KeyFrame(Duration.seconds(1)));

        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    /**
     * Calcula los pendientes del Almacenista.
     * PUBLIC para que los sub-módulos puedan llamarlo al terminar una acción.
     */
    public void refreshBadges() {
        long pendientesDespacho = ventaDao.listarTodos().stream()
                .filter(v -> v.getEstado().equalsIgnoreCase("COMPLETADA"))
                .count();

        long pendientesRecepcion = ordenDao.listarTodos().stream()
                .filter(o -> o.getEstado().equalsIgnoreCase("EMITIDA"))
                .count();

        lblCountDespacho.setText(String.valueOf(pendientesDespacho));
        lblCountRecepcion.setText(String.valueOf(pendientesRecepcion));
    }

    @FXML
    private void showDespacho() {
        ajustarEstiloActivo(cardDespacho, "card-active-despacho");
        quitarEstiloActivo(cardRecepcion, "card-active-recepcion");
        loadSubModule("/views/almacenista/DespachoView.fxml");
    }

    @FXML
    private void showRecepcion() {
        ajustarEstiloActivo(cardRecepcion, "card-active-recepcion");
        quitarEstiloActivo(cardDespacho, "card-active-despacho");
        loadSubModule("/views/almacenista/RecepcionView.fxml");
    }

    private void ajustarEstiloActivo(HBox card, String cssClass) {
        if (!card.getStyleClass().contains(cssClass)) {
            card.getStyleClass().add(cssClass);
        }
    }

    private void quitarEstiloActivo(HBox card, String cssClass) {
        card.getStyleClass().remove(cssClass);
    }

    private void loadSubModule(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) throw new IOException("No se encontró el FXML: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(resource);
            Parent module = loader.load();

            contentArea.getChildren().setAll(module);
            // Usamos tu utilidad para hacer una transición de aparición suave
            UIComponents.fadeIn(module, 300);

        } catch (IOException e) {
            UIComponents.showNotification("Error al cargar módulo de almacén.", "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Usamos la utilidad de NavigationTools que ya recibe el botón y el reloj
        NavigationTools.manejarLogout(btnLogout, clockTimeline);
    }
}