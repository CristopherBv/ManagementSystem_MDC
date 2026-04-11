package gestionmats.controllers;

import gestionmats.models.Rol;
import gestionmats.models.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;

public class MainLayoutController {

    @FXML private Label lblUsuario;
    @FXML private Text txtVistaActual;
    @FXML private StackPane contentArea;

    // Botones del Sidebar
    @FXML private Button btnPOS;
    @FXML private Button btnDashboard;
    @FXML private Button btnProductos;
    @FXML private Button btnHistorial;
    @FXML private Button btnClientes;

    private Usuario usuarioLogueado;

    /**
     * Se llama desde LoginController después de validar la contraseña.
     */
    public void initData(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.lblUsuario.setText("Sesión activa: " + usuario.nombre());

        // LÓGICA DE ACCESOS (RBAC en el Menú Lateral)
        if (usuario.rol() == Rol.VENDEDOR) {
            // El Vendedor no ve el Dashboard gerencial ni el CRUD completo de productos
            btnDashboard.setVisible(false);
            btnDashboard.setManaged(false); // Quita el espacio en blanco que dejaría

            btnProductos.setVisible(false);
            btnProductos.setManaged(false);

            // Por defecto, le cargamos el Punto de Venta
            mostrarPOS();
        } else if (usuario.rol() == Rol.GERENTE) {
            // El Gerente ve todo. Por defecto, le cargamos su Panel Principal
            mostrarDashboard();
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN (Cambian el centro de la pantalla) ---

    @FXML
    public void mostrarPOS() {
        txtVistaActual.setText("Punto de Venta");
        // cargarVista("/views/pos.fxml"); // Descomentar cuando crees pos.fxml
        mostrarEnConstruccion("Punto de Venta");
    }

    @FXML
    public void mostrarDashboard() {
        txtVistaActual.setText("Panel Principal");
        // cargarVista("/views/dashboard.fxml"); // Descomentar cuando crees dashboard.fxml
        mostrarEnConstruccion("Panel Principal");
    }

    @FXML
    public void mostrarProductos() {
        txtVistaActual.setText("Catálogo de Productos");
        // cargarVista("/views/productos.fxml");
        mostrarEnConstruccion("Productos");
    }

    @FXML
    public void mostrarHistorial() {
        txtVistaActual.setText("Historial de Ventas y Pedidos");
        // cargarVista("/views/historial.fxml");
        mostrarEnConstruccion("Historial");
    }

    @FXML
    public void mostrarClientes() {
        txtVistaActual.setText("Directorio de Clientes");
        // cargarVista("/views/clientes.fxml");
        mostrarEnConstruccion("Clientes");
    }

    // --- LÓGICA INTERNA DEL CASCARÓN ---

    /**
     * Este método inyecta un archivo FXML dentro del StackPane central.
     */
    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node vista = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarEnConstruccion("Error al cargar la vista");
        }
    }

    private void mostrarEnConstruccion(String modulo) {
        contentArea.getChildren().clear();
        Label lblConstruccion = new Label(modulo + "\n(Módulo en construcción)");
        lblConstruccion.setStyle("-fx-font-size: 24px; -fx-text-fill: #a3a3a3; -fx-text-alignment: center;");
        contentArea.getChildren().add(lblConstruccion);
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) lblUsuario.getScene().getWindow();
            stage.setTitle("Inicio de Sesión - Constructora");
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}