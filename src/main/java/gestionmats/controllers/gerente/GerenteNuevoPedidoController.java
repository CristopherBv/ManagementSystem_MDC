package gestionmats.controllers.gerente;

import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.ProveedorDaoCsv;
import gestionmats.model.DetalleOrden;
import gestionmats.model.OrdenCompra;
import gestionmats.model.Producto;
import gestionmats.model.Proveedor;
import gestionmats.utils.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteNuevoPedidoController implements Initializable {

    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad;
    @FXML private TableView<DetalleOrden> tableDetalles;
    @FXML private TableColumn<DetalleOrden, String> colMaterial, colCosto;
    @FXML private TableColumn<DetalleOrden, Integer> colCantidad;
    @FXML private Label lblTotal;
    @FXML private Button btnAgregar, btnEmitir, btnCancelar;

    private ObservableList<DetalleOrden> detallesCarrito;
    private ProveedorDaoCsv proveedorDao = new ProveedorDaoCsv();
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();
    private OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();

    private boolean ordenGenerada = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        detallesCarrito = FXCollections.observableArrayList();
        tableDetalles.setItems(detallesCarrito);

        cargarCombos();
        setupTable();

        UIComponents.applyButtonAdd(btnEmitir);
        UIComponents.applyButtonEdit(btnAgregar);
        UIComponents.applyButtonGhost(btnCancelar);
    }

    private void cargarCombos() {
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedorDao.listarTodos()));
        cmbProveedor.setConverter(new StringConverter<>() {
            @Override public String toString(Proveedor p) { return p == null ? "" : p.getNombreProveedor(); }
            @Override public Proveedor fromString(String s) { return null; }
        });

        cmbProducto.setItems(FXCollections.observableArrayList(productoDao.listarTodos()));
        cmbProducto.setConverter(new StringConverter<>() {
            @Override public String toString(Producto p) { return p == null ? "" : p.getNombre(); }
            @Override public Producto fromString(String s) { return null; }
        });
    }

    private void setupTable() {
        colMaterial.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProducto().getNombre()));
        colCantidad.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getCantidadEsperada()));
        colCosto.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calcularSubtotal())));
    }

    @FXML private void handleAgregarDetalle() {
        Producto p = cmbProducto.getValue();
        if (p == null) {
            UIComponents.showNotification("Seleccione un material.", "warn");
            return;
        }
        try {
            int cant = Integer.parseInt(txtCantidad.getText().trim());
            if (cant <= 0) throw new NumberFormatException();

            detallesCarrito.add(new DetalleOrden(p, cant, 0)); // cantidad recibida inicia en 0
            txtCantidad.clear();
            actualizarTotal();
        } catch (NumberFormatException e) {
            UIComponents.showNotification("Ingrese una cantidad válida mayor a 0.", "error");
        }
    }

    private void actualizarTotal() {
        double total = detallesCarrito.stream().mapToDouble(DetalleOrden::calcularSubtotal).sum();
        lblTotal.setText(String.format("$%.2f", total));
    }

    @FXML private void handleEmitirOrden() {
        Proveedor prov = cmbProveedor.getValue();
        if (prov == null) {
            UIComponents.showNotification("Debe seleccionar un proveedor.", "warn");
            return;
        }
        if (detallesCarrito.isEmpty()) {
            UIComponents.showNotification("El pedido está vacío.", "warn");
            return;
        }

        // Creamos la orden general y le añadimos los materiales del carrito
        OrdenCompra nuevaOrden = new OrdenCompra(ordenDao.generarSiguienteId(), prov);
        for (DetalleOrden det : detallesCarrito) {
            nuevaOrden.addDetalleOrden(det);
        }

        // El Super-DAO se encarga de guardar todo
        if (ordenDao.guardar(nuevaOrden)) {
            ordenGenerada = true;
            cerrarVentana();
        } else {
            UIComponents.showNotification("Error al guardar la orden.", "error");
        }
    }

    @FXML private void handleCancelar() { cerrarVentana(); }
    private void cerrarVentana() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
    public boolean isOrdenGenerada() { return ordenGenerada; }
}