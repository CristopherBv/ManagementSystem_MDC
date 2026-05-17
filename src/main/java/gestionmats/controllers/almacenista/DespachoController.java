package gestionmats.controllers.almacenista;

import gestionmats.dao.ClienteDaoCsv;
import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.model.Cliente;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;
import gestionmats.utils.UIComponents;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DespachoController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private ListView<Venta> listTickets;
    @FXML private Label lblTicketSeleccionado, lblCliente, lblEstado;
    @FXML private TableView<DetalleVenta> tablaMateriales;
    @FXML private TableColumn<DetalleVenta, String> colMaterial;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private Button btnConfirmar;

    private VentaDaoCsv ventaDao = new VentaDaoCsv();
    private DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();
    private ClienteDaoCsv clienteDao = new ClienteDaoCsv();

    private ObservableList<Venta> ventasPendientes;
    private FilteredList<Venta> ventasFiltradas;
    private Venta ventaActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        cargarListaTickets();
        setupListSelection();
        setupBuscador();
    }

    private void setupTable() {
        colMaterial.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreProducto()));
        colCantidad.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCantidad()));
    }

    private void cargarListaTickets() {
        List<Venta> pendientes = ventaDao.listarTodos().stream()
                .filter(v -> v.getEstado().equalsIgnoreCase("COMPLETADA"))
                .toList();

        ventasPendientes = FXCollections.observableArrayList(pendientes);
        ventasFiltradas = new FilteredList<>(ventasPendientes, v -> true);
        listTickets.setItems(ventasFiltradas);

        // Diseñar cómo se ve cada Ticket (formato tarjeta cuadrada para carrusel)
        listTickets.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Venta venta, boolean empty) {
                super.updateItem(venta, empty);
                if (empty || venta == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    VBox card = new VBox(5);
                    card.setPrefWidth(160); // Ancho fijo para que no se estiren
                    card.setAlignment(Pos.CENTER);
                    card.setStyle("-fx-padding: 10; -fx-background-color: #22252e; -fx-background-radius: 8; -fx-border-color: #313543; -fx-border-radius: 8; -fx-cursor: hand;");

                    Label lblId = new Label("Ticket #" + venta.getIdVenta());
                    lblId.setStyle("-fx-font-weight: bold; -fx-text-fill: #60a5fa; -fx-font-size: 14px;");

                    Label lblFecha = new Label(venta.getFechaHora());
                    lblFecha.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

                    card.getChildren().addAll(lblId, lblFecha);
                    setGraphic(card);
                    // Margen derecho para separar las tarjetas
                    setStyle("-fx-background-color: transparent; -fx-padding: 0 10 0 0;");
                }
            }
        });
    }

    private void setupBuscador() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            ventasFiltradas.setPredicate(venta -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return String.valueOf(venta.getIdVenta()).contains(newVal.trim());
            });
        });
    }

    private void setupListSelection() {
        listTickets.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDetalleVenta(newSelection);
            }
        });
    }

    private void cargarDetalleVenta(Venta venta) {
        this.ventaActual = venta;
        lblTicketSeleccionado.setText("Despachando Ticket #" + venta.getIdVenta());
        lblEstado.setText("LISTO PARA ENTREGA");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-background-color: rgba(96, 165, 250, 0.2); -fx-text-fill: #60a5fa;");

        String nombreCliente = "Cliente General";
        if (venta.getIdCliente() > 0) {
            Cliente c = clienteDao.buscarPorId(venta.getIdCliente());
            if (c != null) nombreCliente = c.getNombre() + " (ID: " + c.getIdCliente() + ")";
        }
        lblCliente.setText("Cliente: " + nombreCliente);

        List<DetalleVenta> detalles = detalleDao.listarPorIdVenta(venta.getIdVenta());
        tablaMateriales.setItems(FXCollections.observableArrayList(detalles));
        btnConfirmar.setDisable(detalles.isEmpty());
    }

    @FXML
    private void handleConfirmarEntrega() {
        if (ventaActual == null || tablaMateriales.getItems().isEmpty()) return;

        boolean confirm = UIComponents.showConfirmDialog("Confirmar Despacho", "¿El cliente ya recibió físicamente el material del Ticket #" + ventaActual.getIdVenta() + "?");
        if (!confirm) return;

        List<DetalleVenta> detalles = tablaMateriales.getItems();
        for (DetalleVenta dv : detalles) {
            Producto p = productoDao.buscarPorId(dv.getIdProducto());
            if (p != null) {
                int stockNuevo = p.getStockActual() - dv.getCantidad();
                p.setStockActual(stockNuevo);
                productoDao.actualizar(p);
            }
        }

        ventaActual.setEstado("SURTIDO");
        ventaDao.actualizar(ventaActual);

        UIComponents.showNotification("¡Ticket despachado! El inventario ha sido actualizado.", "success");
        limpiarPanelInferior();
        cargarListaTickets();
    }

    private void limpiarPanelInferior() {
        ventaActual = null;
        lblTicketSeleccionado.setText("Seleccione un ticket del carrusel");
        lblCliente.setText("-- Esperando selección --");
        lblEstado.setText("--");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-background-color: #374151; -fx-text-fill: #9ca3af;");
        tablaMateriales.getItems().clear();
        btnConfirmar.setDisable(true);
    }
}