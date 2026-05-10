package gestionmats.controllers.gerente;

import gestionmats.services.GestorSesion;
import gestionmats.model.Usuario;
import gestionmats.utils.UIComponents;
import gestionmats.utils.NavigationTools;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GerenteController implements Initializable {

    @FXML private Label lblPageTitle, lblPageSub, lblFecha, lblHora, lblUserName; // <-- Agregado lblUserName
    @FXML private Button navDashboard, navEmpleados, navInventario, navPedidos, navClientes, navProveedores, btnLogout;
    @FXML private StackPane contentArea;

    private Timeline clockTimeline;
    private Button activeNavBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupClock();
        setupNavButtons();

        // SINGLETON AQUÍ
        Usuario usuarioLogueado = GestorSesion.getInstancia().getUsuarioActual();
        if (usuarioLogueado != null) {
            // Actualizamos el nombre en la barra lateral izquierda
            lblUserName.setText(usuarioLogueado.getNombre() + " " + usuarioLogueado.getPrimerApellido());
        }

        // Carga el Dashboard por defecto al iniciar sesión
        showDashboard();
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
        UIComponents.applyNavHover(navDashboard);
        UIComponents.applyNavHover(navEmpleados);
        UIComponents.applyNavHover(navInventario);
        UIComponents.applyNavHover(navPedidos);
        UIComponents.applyNavHover(navClientes);
        UIComponents.applyNavHover(navProveedores);
        UIComponents.applyButtonPulse(btnLogout);
    }

    /**
     * MOTOR DE INYECCIÓN: Carga un FXML dentro del StackPane central.
     */
    private void loadModule(String fxmlPath, Button navBtn, String title, String sub) {
        try {
            // Estética de botones
            if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("nav-btn-active");
            navBtn.getStyleClass().add("nav-btn-active");
            activeNavBtn = navBtn;

            // Carga del archivo
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent module = loader.load();

            // Inyección en la UI
            contentArea.getChildren().setAll(module);
            UIComponents.fadeIn(module, 300);

            lblPageTitle.setText(title);
            lblPageSub.setText(sub);

        } catch (IOException e) {
            UIComponents.showNotification("Error al cargar módulo: " + fxmlPath, "error");
            e.printStackTrace();
        }
    }
    @FXML private void showDashboard() {
        String nombre = "Usuario";
        Usuario u = GestorSesion.getInstancia().getUsuarioActual();
        if (u != null) {
            nombre = u.getNombre();
        }

        loadModule("/views/gerente/GerenteDashboardView.fxml", navDashboard, "Panel de Control", "Bienvenido, " + nombre);
    }

    @FXML private void showInventario() {
        loadModule("/views/gerente/GerenteInventarioView.fxml", navInventario, "Inventario", "Gestión de stock físico");
    }

    @FXML private void showEmpleados() {
        loadModule("/views/gerente/GerenteEmpleadoView.fxml", navEmpleados, "Administración de Personal", "Gestión de accesos y roles del sistema");
    }

    @FXML private void showClientes() {
        loadModule("/views/gerente/GerenteClienteView.fxml", navClientes, "Cartera de Clientes", "Gestión de clientes y programa de lealtad");
    }

    @FXML private void showPedidos() {
        loadModule("/views/gerente/GerentePedidosView.fxml", navPedidos, "Historial de Pedidos", "Gestión de abastecimiento y órdenes de compra");
    }
     @FXML private void showProveedores() {
        loadModule("/views/gerente/GerenteProveedorView.fxml", navProveedores, "Directorio de Proveedores", "Gestión de empresas suministradoras");
    }

    @FXML private void handleLogout() {
        NavigationTools.manejarLogout(btnLogout, clockTimeline);
    }
}
