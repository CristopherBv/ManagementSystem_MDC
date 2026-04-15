package gestionmats.utils;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class AlertUtils {

    /**
     * Muestra una alerta estandarizada para todo el sistema SDG_MDC.
     */
    public static void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);

        // Quitamos el borde por defecto del sistema operativo para que se vea más moderno
        alerta.initStyle(StageStyle.UTILITY);

        // TODO a futuro: alerta.getDialogPane().getStylesheets().add(...ruta_al_css...);

        alerta.showAndWait();
    }
}