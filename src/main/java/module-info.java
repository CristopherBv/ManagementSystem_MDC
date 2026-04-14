module gestionmats {
    requires javafx.controls;
    requires javafx.fxml;

    // Permitir que JavaFX acceda a los archivos FXML y CSS en resources
    opens gestionmats to javafx.fxml;

    // CRUCIAL: Abre el paquete de controladores para que el FXMLLoader
    // pueda inyectar los @FXML de LoginController y otros.
    opens gestionmats.controllers to javafx.fxml;

    // Exporta el modelo para que las celdas de las tablas puedan
    // leer los atributos de tus clases de datos (Producto, Material, etc.)
    //exports gestionmats.model; aun no hay nada xd

    // Exporta el paquete principal para que el launcher funcione
    exports gestionmats;
}