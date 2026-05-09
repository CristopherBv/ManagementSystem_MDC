package gestionmats.controllers.gerente;

import gestionmats.model.OrdenCompra;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GerentePedidosController implements Initializable {

    @FXML private TableView<OrdenCompra> tablePedidos;
    @FXML private TableColumn<OrdenCompra, Integer> colFolio;
    @FXML private TableColumn<OrdenCompra, String> colFecha, colProveedor, colEstado;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblTotal;
    @FXML private Button btnNuevoPedido, btnVerDetalle;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupStyles();
        setupFilters();
        // TODO: setupTable() y loadData() cuando tengamos el DAO listo
    }

    private void setupStyles() {
        UIComponents.applyButtonAdd(btnNuevoPedido);
        UIComponents.applyButtonEdit(btnVerDetalle);
    }

    private void setupFilters() {
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Todos", "EMITIDA", "SURTIDA", "INCOMPLETA", "CANCELADA"
        ));
        cmbEstado.getSelectionModel().selectFirst();
    }

    @FXML private void handleNuevoPedido() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/gerente/GerenteNuevoPedidoView.fxml"));
            javafx.scene.Parent root = loader.load();

            GerenteNuevoPedidoController controller = loader.getController();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Generar Nueva Orden");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isOrdenGenerada()) {
                // Aquí en el futuro llamaremos a loadData() para recargar la tabla principal
                UIComponents.showNotification("Orden emitida y guardada en CSV.", "success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIComponents.showNotification("Error al abrir el carrito.", "error");
        }
    }

    @FXML private void handleVerDetalle() {
        UIComponents.showNotification("Seleccione un pedido para ver sus materiales", "warn");
    }
}