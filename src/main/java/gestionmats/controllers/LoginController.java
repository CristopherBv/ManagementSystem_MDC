package gestionmats.controllers;

import gestionmats.models.Rol;
import gestionmats.models.Usuario;
import gestionmats.utils.AlertUtils;
import gestionmats.utils.AuthUtil;
import gestionmats.utils.NavigationUtils;
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

    @FXML
    public void handleLogin() {
        Optional<Usuario> optUsuario = AuthUtil.intentarLogin(txtUsername.getText(), txtPassword.getText());

        if (optUsuario.isPresent()) {
            dirigirAPantallaSegunRol(optUsuario.get());
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
        }
    }

    private void dirigirAPantallaSegunRol(Usuario u) {
        try {
            String fxmlPath = "";
            String titulo = "";

            if (u.rol() == Rol.ALMACENISTA) {
                fxmlPath = "/views/almacen.fxml";
                titulo = "Panel de Control - Almacén";
            } else if (u.rol() == Rol.GERENTE || u.rol() == Rol.VENDEDOR) {
                fxmlPath = "/views/main_layout.fxml";
                titulo = (u.rol() == Rol.GERENTE) ? "Panel de Administración - Gerencia" : "Punto de Venta";
            }

            if (fxmlPath.isEmpty()) {
                AlertUtils.mostrarInformacion("Próximamente", "Módulo no disponible. Bv");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());

            if (u.rol() == Rol.ALMACENISTA) {
                WarehouseController controller = fxmlLoader.getController();
                controller.initData(u);
            } else {
                MainLayoutController mainController = fxmlLoader.getController();
                mainController.initData(u);
            }

            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setTitle(titulo + " - " + u.nombre());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            lblError.setText("Error al cargar la interfaz.");
            e.printStackTrace();
        }
    }
}