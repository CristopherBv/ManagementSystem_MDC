package gestionmats.controllers;

import gestionmats.dao.UsuarioDAO;
import gestionmats.models.Rol;
import gestionmats.models.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();

    @FXML
    public void handleLogin() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Por favor, llene todos los campos.");
            return;
        }

        Optional<Usuario> optUsuario = usuarioDAO.buscarPorUsername(user);

        if (optUsuario.isPresent()) {
            Usuario u = optUsuario.get();

            if (u.password().equals(pass)) {
                dirigirAPantallaSegunRol(u);
            } else {
                lblError.setText("Contraseña incorrecta.");
            }
        } else {
            lblError.setText("El usuario no existe.");
        }
    }

    private void dirigirAPantallaSegunRol(Usuario u) {
        try {
            String fxmlPath = "";
            String titulo = "";

            if (u.rol() == Rol.ALMACENISTA) {
                fxmlPath = "/views/almacen.fxml";
                titulo = "Panel de Control - Almacén";
            } else if (u.rol() == Rol.GERENTE) {
                // fxmlPath = "/views/gerente.fxml";
                titulo = "Panel de Administración - Gerencia";
            } else {
                // fxmlPath = "/views/ventas.fxml";
                titulo = "Punto de Venta";
            }

            if (fxmlPath.isEmpty()) {
                mostrarAlerta("Próximamente", "La pantalla de " + u.rol() + " aún está en desarrollo.");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());

            // --- INYECCIÓN DEL USUARIO ---
            if (u.rol() == Rol.ALMACENISTA) {
                WarehouseController controller = fxmlLoader.getController();
                controller.initData(u);
            }

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setTitle(titulo + " - " + u.nombre());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            lblError.setText("Error al cargar la pantalla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}