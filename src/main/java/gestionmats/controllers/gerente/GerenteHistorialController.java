package gestionmats.controllers.gerente;

import gestionmats.dao.ClienteDaoCsv;
import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.model.Cliente;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Venta;
import gestionmats.utils.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GerenteHistorialController implements Initializable {

    @FXML private TableView<Venta> tableHistorial;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colFecha, colTipo, colMetodo, colEstado, colCliente;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TextField txtBuscarVenta;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private Label lblFooter;
    @FXML private Button btnVerDetalle;

    private VentaDaoCsv ventaDao = new VentaDaoCsv();
    private DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
    private ClienteDaoCsv clienteDao = new ClienteDaoCsv();

    private ObservableList<Venta> allVentas;
    private FilteredList<Venta> filteredVentas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupCombos();
        loadData();
        setupFilters();
        UIComponents.applyButtonEdit(btnVerDetalle);
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoVenta"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Formato de moneda para el total
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$ %.2f", item));
            }
        });

        // Lógica para mostrar nombre del cliente en lugar de ID
        colCliente.setCellValueFactory(data -> {
            int idCli = data.getValue().getIdCliente();
            if (idCli == 0) return new SimpleStringProperty("Venta General");
            Cliente c = clienteDao.buscarPorId(idCli);
            return new SimpleStringProperty(c != null ? c.getNombre() : "ID: " + idCli);
        });
    }

    private void setupCombos() {
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "COMPLETADA", "PENDIENTE", "CANCELADA"));
        cmbFiltroEstado.getSelectionModel().selectFirst();
        cmbFiltroEstado.valueProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void loadData() {
        allVentas = FXCollections.observableArrayList(ventaDao.listarTodos());
        // Ordenar por ID descendente (más recientes primero)
        FXCollections.sort(allVentas, (a, b) -> Integer.compare(b.getIdVenta(), a.getIdVenta()));

        filteredVentas = new FilteredList<>(allVentas, v -> true);
        tableHistorial.setItems(filteredVentas);
        actualizarFooter();
    }

    private void setupFilters() {
        txtBuscarVenta.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void applyFilters() {
        String search = txtBuscarVenta.getText().toLowerCase().trim();
        String estado = cmbFiltroEstado.getValue();

        filteredVentas.setPredicate(v -> {
            boolean matchesSearch = search.isEmpty() ||
                    String.valueOf(v.getIdVenta()).contains(search) ||
                    v.getFechaHora().toLowerCase().contains(search);
            boolean matchesEstado = estado.equals("Todos") || v.getEstado().equals(estado);

            return matchesSearch && matchesEstado;
        });
        actualizarFooter();
    }

    private void actualizarFooter() {
        lblFooter.setText("Mostrando " + filteredVentas.size() + " registros de venta");
    }

    @FXML
    private void handleVerDetalleVenta() {
        Venta sel = tableHistorial.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UIComponents.showNotification("Seleccione una venta de la lista", "warn");
            return;
        }

        // Recuperar los detalles de la venta
        List<DetalleVenta> detalles = detalleDao.listarPorIdVenta(sel.getIdVenta());

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle de Venta #" + sel.getIdVenta());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: #111318; -fx-base: #111318;");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);

        // Información de cabecera
        GridPane grid = new GridPane();
        grid.setHgap(25); grid.setVgap(8);

        String cliente = (sel.getIdCliente() == 0) ? "General" :
                (clienteDao.buscarPorId(sel.getIdCliente()) != null ? clienteDao.buscarPorId(sel.getIdCliente()).getNombre() : "N/A");

        grid.addRow(0, new Label("Cliente:"), new Label(cliente));
        grid.addRow(1, new Label("Vendedor ID:"), new Label(String.valueOf(sel.getIdVendedor())));
        grid.addRow(2, new Label("Método Pago:"), new Label(sel.getMetodoPago()));
        grid.addRow(0, new Label("Subtotal:"), new Label(String.format("$ %.2f", sel.getSubtotal())), new Label("Total:"), new Label(String.format("$ %.2f", sel.getTotal())));

        // Tabla de productos en el detalle
        TableView<DetalleVenta> tablaItems = new TableView<>(FXCollections.observableArrayList(detalles));
        tablaItems.setPrefHeight(250);

        TableColumn<DetalleVenta, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProd.setPrefWidth(250);

        TableColumn<DetalleVenta, Integer> colCant = new TableColumn<>("Cant.");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        TableColumn<DetalleVenta, Double> colPrec = new TableColumn<>("Precio Unit.");
        colPrec.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));

        TableColumn<DetalleVenta, Double> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(new PropertyValueFactory<>("total"));

        tablaItems.getColumns().addAll(colProd, colCant, colPrec, colSub);

        content.getChildren().addAll(new Label("RESUMEN DE VENTA"), grid, new Separator(), new Label("ARTÍCULOS"), tablaItems);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
}