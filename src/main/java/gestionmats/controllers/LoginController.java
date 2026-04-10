package gestionmats.controllers;

import gestionmats.dao.UsuarioDAO;
import gestionmats.models.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        // 1. Buscamos al usuario usando el archivista (DAO)
        Optional<Usuario> optUsuario = usuarioDAO.buscarPorUsername(user);

        // 2. El guardia (Controlador) toma la decisión
        if (optUsuario.isPresent()) {
            Usuario u = optUsuario.get();

            if (u.password().equals(pass)) {
                lblError.setText(""); // Limpiamos errores
                System.out.println("Login exitoso. Bienvenido: " + u.nombre() + " (" + u.rol() + ")");

                // Aquí es donde después cambiaremos a la pantalla principal
                mostrarAlerta("Éxito", "Bienvenido " + u.nombre() + "\nRol: " + u.rol());
            } else {
                lblError.setText("Contraseña incorrecta.");
            }
        } else {
            lblError.setText("El usuario no existe.");
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