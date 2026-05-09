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
        // Aquí abriremos la ventana del "Carrito de Compras"
        UIComponents.showNotification("Módulo de Creación de Pedidos en construcción", "info");
    }

    @FXML private void handleVerDetalle() {
        UIComponents.showNotification("Seleccione un pedido para ver sus materiales", "warn");
    }
}