package gestionmats.controllers;

import gestionmats.models.Rol;
import gestionmats.models.Usuario;
import gestionmats.utils.NavigationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class MainLayoutController {

    @FXML private Label lblUsuario;
    @FXML private Text txtVistaActual;
    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard, btnProductos, btnHistorial, btnClientes;

    private Usuario usuarioLogueado;

    public void initData(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.lblUsuario.setText("Sesión activa: " + usuario.nombre());

        if (usuario.rol() == Rol.VENDEDOR) {
            btnDashboard.setVisible(false);
            btnDashboard.setManaged(false);
            btnProductos.setVisible(false);
            btnProductos.setManaged(false);
            mostrarPOS();
        } else {
            mostrarDashboard();
        }
    }

    @FXML public void mostrarPOS() {
        txtVistaActual.setText("Punto de Venta");
        NavigationUtils.cargarVista(contentArea, "/views/pos.fxml");
    }

    @FXML public void mostrarDashboard() {
        txtVistaActual.setText("Panel Principal");
        NavigationUtils.mostrarEnConstruccion(contentArea, "Panel Principal");
    }

    @FXML public void mostrarProductos() {
        txtVistaActual.setText("Catálogo de Productos");
        NavigationUtils.mostrarEnConstruccion(contentArea, "Productos");
    }

    @FXML public void mostrarHistorial() {
        txtVistaActual.setText("Historial de Ventas y Pedidos");
        NavigationUtils.mostrarEnConstruccion(contentArea, "Historial");
    }

    @FXML public void mostrarClientes() {
        txtVistaActual.setText("Directorio de Clientes");
        NavigationUtils.mostrarEnConstruccion(contentArea, "Clientes");
    }

    @FXML public void handleLogout() {
        NavigationUtils.irALogin(lblUsuario);
    }
}