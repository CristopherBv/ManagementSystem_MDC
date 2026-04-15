package gestionmats.controllers;

import gestionmats.utils.AlertUtils;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
// import gestionmats.dao.UsuarioDAO; // Descomenta cuando tengas tu DAO

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private VBox loginContainer;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPasswordHidden;
    @FXML private TextField txtPasswordVisible;
    @FXML private ToggleButton btnTogglePass;
    @FXML private ImageView imgEye;
    @FXML private Button btnLogin;

    private final String PATH_OJO_ABIERTO = "/images/ojoAbierto.png";
    private final String PATH_OJO_CERRADO = "/images/ojoCerrado.png";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPasswordVisibility();
        playEntryAnimation();
    }

    private void playEntryAnimation() {
        FadeTransition fade = new FadeTransition(Duration.millis(800), loginContainer);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(800), loginContainer);
        translate.setFromY(40);
        translate.setToY(0);

        fade.play();
        translate.play();
    }

    private void setupPasswordVisibility() {
        // Vinculamos el texto de ambos campos para que tengan lo mismo
        txtPasswordVisible.textProperty().bindBidirectional(txtPasswordHidden.textProperty());
    }

    @FXML
    private void togglePasswordVisibility() {
        boolean isVisible = btnTogglePass.isSelected();

        txtPasswordVisible.setVisible(isVisible);
        txtPasswordVisible.setManaged(isVisible);
        txtPasswordHidden.setVisible(!isVisible);
        txtPasswordHidden.setManaged(!isVisible);

        updateEyeIcon(isVisible ? PATH_OJO_ABIERTO : PATH_OJO_CERRADO);
    }

    private void updateEyeIcon(String path) {
        try {
            imgEye.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + path);
        }
    }

    @FXML
    private void handleLogin() {
        String user = txtUser.getText();
        String pass = txtPasswordHidden.getText();

        // Lógica simulada. Reemplazar con: if (UsuarioDAO.login(user, pass))
        if (user.equals("admin") && pass.equals("1234")) {
            loadMainDashboard();
        } else {
            // Animación de error
            shakeNode(txtUser);
            shakeNode(txtPasswordVisible.isVisible() ? txtPasswordVisible : txtPasswordHidden);
        }
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setByX(8f);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();

        // Opcional: Poner el borde rojo temporalmente usando CSS inline solo para el error
        node.setStyle("-fx-border-color: #e74c3c;");
        tt.setOnFinished(e -> node.setStyle("")); // Limpia el estilo al terminar
    }

    private void loadMainDashboard() {//Nombre Provisional/NO FINAL
        try {
            // 1. Buscamos el recurso
            URL dashboardUrl = getClass().getResource("/views/Dashboard.fxml");

            // 2. Validamos explícitamente en lugar de depender del NullPointerException
            if (dashboardUrl == null) {
                throw new IOException("El archivo de vista 'Dashboard.fxml' no se encuentra en el directorio /views/.");
            }

            // 3. Cargamos la vista
            Parent root = FXMLLoader.load(dashboardUrl);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SDG MDC - Panel de Control");
            stage.centerOnScreen();

        } catch (IOException e) {
            AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo cargar el panel principal.", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "Ocurrió un error inesperado al navegar.", e.getMessage());
            e.printStackTrace();
        }
    }

}
