package gestionmats;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Cargamos la vista del login
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/views/login.fxml"));

        // Creamos la escena (el contenido de la ventana)
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Sistema de Gestión - Constructora");
        stage.setScene(scene);
        stage.setResizable(false); // Por ahora que sea fija
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}