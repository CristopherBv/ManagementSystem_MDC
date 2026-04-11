package gestionmats.controllers;

import gestionmats.dao.ProductoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PosController {

    @FXML private TextField txtProductoId, txtProductoNombre, txtCantidad;
    @FXML private Label lblTotal;
    @FXML private TableView<DetalleTemporal> tablaVenta;
    @FXML private TableColumn<DetalleTemporal, String> colDescripcion, colUnidad;
    @FXML private TableColumn<DetalleTemporal, Double> colPrecio, colImporte, colCantidad;

    private final ProductoDAO productoDAO = ProductoDAO.getInstancia();
    private ObservableList<DetalleTemporal> itemsVenta = FXCollections.observableArrayList();
    private double totalVenta = 0.0;

    @FXML
    public void initialize() {
        // Configuramos las columnas de la tabla
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));

        tablaVenta.setItems(itemsVenta);
    }

    @FXML
    public void handleAgregarProducto() {
        String id = txtProductoId.getText().trim();
        double cant = Double.parseDouble(txtCantidad.getText());

        productoDAO.getAll().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .ifPresentOrElse(p -> {
                    double importe = p.precio() * cant;
                    itemsVenta.add(new DetalleTemporal(p.nombre(), p.precio(), cant, p.unidad(), importe));
                    actualizarTotal();
                    limpiarCampos();
                }, () -> mostrarAlerta("Error", "Producto no encontrado"));
    }

    private void actualizarTotal() {
        totalVenta = itemsVenta.stream().mapToDouble(DetalleTemporal::getImporte).sum();
        lblTotal.setText(String.format("$%.2f", totalVenta));
    }

    private void limpiarCampos() {
        txtProductoId.clear();
        txtProductoNombre.clear();
        txtCantidad.setText("1.0");
        txtProductoId.requestFocus();
    }

    @FXML
    public void handleCobrar() {
        if (itemsVenta.isEmpty()) return;
        // Aquí conectaremos con el VentaDAO que hicimos antes
        System.out.println("Procesando cobro de: " + totalVenta);
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Clase interna para la tabla (no necesita ser un archivo aparte si solo se usa aquí)
    public static class DetalleTemporal {
        private String descripcion;
        private double precioUnitario;
        private double cantidad;
        private String unidad;
        private double importe;

        public DetalleTemporal(String d, double p, double c, String u, double i) {
            this.descripcion = d; this.precioUnitario = p; this.cantidad = c; this.unidad = u; this.importe = i;
        }
        public String getDescripcion() { return descripcion; }
        public double getPrecioUnitario() { return precioUnitario; }
        public double getCantidad() { return cantidad; }
        public String getUnidad() { return unidad; }
        public double getImporte() { return importe; }
    }
}
