package gestionmats;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/Login.fxml")));

        // Cargamos la escena. El CSS ya está vinculado en el archivo FXML.
        Scene scene = new Scene(root);

        primaryStage.setTitle("SDG MDC - Autenticación");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Es buena práctica bloquear el resize en el login
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}