package gestionmats.controllers;

import gestionmats.model.RolUsuario;
import gestionmats.services.GestorSesion;
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

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
// import gestionmats.dao.UsuarioDAO; // Descomenta cuando tengas tu DAO


public class LoginController implements Initializable {

    @FXML private VBox loginContainer;
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPasswordHidden;
    @FXML private TextField txtPasswordVisible;
    @FXML private ToggleButton btnTogglePass;
    @FXML private ImageView imgEye;
    @FXML private Button btnLogin;

    private final String PATH_OJO_ABIERTO = "/images/ojoAbierto_Naranja.png";
    private final String PATH_OJO_CERRADO = "/images/ojoCerrado_Naranja.png";

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

        // 1. Usamos el Singleton para validar en el CSV
        boolean loginExitoso = GestorSesion.getInstancia().iniciarSesion(user, pass);

        if (loginExitoso) {
            // 2. Obtenemos el rol del usuario que acaba de iniciar sesión
            RolUsuario rol = GestorSesion.getInstancia().getRolActual();

            // 3. Redirigimos según el rol
            switch (rol) {
                case GERENTE:
                    loadGerenteView();
                    break;
                case VENDEDOR:
                    loadVendedorView();
                    break;
                case ALMACENISTA:
                    loadAlmacenistaView();
                    break;
                default:
                    AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error de Acceso", "Rol no reconocido", "Consulte con el administrador de sistemas.");
                    break;
            }
        } else {
            // Animación de error si falla el login
            shakeNode(txtUser);
            shakeNode(txtPasswordVisible.isVisible() ? txtPasswordVisible : txtPasswordHidden);

            // Opcional: mostrar la etiqueta de error si tienes una (ej. lblError.setVisible(true))
            /*if (errorBox != null) {
                errorBox.setVisible(true);
                errorBox.setManaged(true);
            }*/
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

    private void loadGerenteView() {//Nombre Provisional/NO FINAL
        try {
            // 1. Buscamos el recurso
            URL gerenteUrl = getClass().getResource("/views/GerenteView.fxml");

            // 2. Validamos explícitamente en lugar de depender del NullPointerException
            if (gerenteUrl == null) {
                throw new IOException("El archivo de vista 'GerenteView.fxml' no se encuentra en el directorio /views/.");
            }

            // 3. Cargamos la vista
            Parent root = FXMLLoader.load(gerenteUrl);
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

    private void loadVendedorView() {
        // TODO: Mismo código que loadGerenteView pero apuntando a "/views/VendedorView.fxml"
        try {
            // 1. Buscamos el recurso
            URL vendedorUrl = getClass().getResource("/views/VendedorView.fxml");

            // 2. Validamos explícitamente en lugar de depender del NullPointerException
            if (vendedorUrl== null) {
                throw new IOException("El archivo de vista 'VendedorView.fxml' no se encuentra en el directorio /views/.");
            }

            // 3. Cargamos la vista
            Parent root = FXMLLoader.load(vendedorUrl);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SDG MDC - Punto de Venta");
            stage.centerOnScreen();

        } catch (IOException e) {
            AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo cargar el de vendedor.", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertUtils.mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "Ocurrió un error inesperado al navegar.", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAlmacenistaView() {
        // TODO: Mismo código que loadGerenteView pero apuntando a "/views/AlmacenistaView.fxml"
        System.out.println("Cargando vista del Almacenista...");
        AlertUtils.mostrarAlerta(Alert.AlertType.INFORMATION, "Redirección", "Vista Almacenista", "Aquí iría la interfaz de Entradas/Salidas");
    }


}
