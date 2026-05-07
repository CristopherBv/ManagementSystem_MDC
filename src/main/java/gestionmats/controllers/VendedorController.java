package gestionmats.controllers;

import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;
import gestionmats.services.GestorSesion;
import gestionmats.utils.NavigationTools;
import gestionmats.utils.UIComponents;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class VendedorController implements Initializable {

    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSub;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblVendedorNombre;
    @FXML private Button btnLogout;

    @FXML private TextField txtSearchProducto;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colIdProducto;
    @FXML private TableColumn<Producto, String> colNombreProducto;
    @FXML private TableColumn<Producto, String> colCategoriaProducto;
    @FXML private TableColumn<Producto, Integer> colStockProducto;
    @FXML private TableColumn<Producto, Double> colPrecioProducto;

    @FXML private TableView<DetalleVenta> tableCarrito;
    @FXML private TableColumn<DetalleVenta, String> colIdProdCarrito;
    @FXML private TableColumn<DetalleVenta, String> colNombreCarrito;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidadCarrito;
    @FXML private TableColumn<DetalleVenta, Double> colPrecioCarrito;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotalCarrito;
    @FXML private TableColumn<DetalleVenta, Double> colTotalCarrito;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private ComboBox<String> cmbTipoVenta;

    @FXML private Button btnAgregarCarrito;
    @FXML private Button btnEliminarDelCarrito;
    @FXML private Button btnAplicarDescuento;
    @FXML private Button btnFinalizarVenta;

    @FXML private Button navVentas;
    @FXML private Button navPedidos;
    @FXML private Button navClientes;
    @FXML private VBox viewVentas;
    @FXML private VBox viewPedidos;
    @FXML private VBox viewClientes;

    private ProductoDaoCsv productoDao;
    private VentaDaoCsv ventaDao;
    private DetalleVentaDaoCsv detalleDao;

    private ObservableList<Producto> todosProductos;
    private FilteredList<Producto> productosFiltrados;
    private ObservableList<DetalleVenta> carrito;
    private double descuentoGlobal = 0.0;
    private Timeline clockTimeline;
    private Button activeNavBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productoDao = new ProductoDaoCsv();
        ventaDao = new VentaDaoCsv();
        detalleDao = new DetalleVentaDaoCsv();

        setupClock();
        setupVendedorInfo();
        setupTablaProductos();
        setupTablaCarrito();
        setupCombos();
        cargarProductos();
        setupSearchFilter();
        setupNavegacion();

        activeNavBtn = navVentas;
        viewVentas.setVisible(true);
        viewVentas.setManaged(true);
        viewPedidos.setVisible(false);
        viewPedidos.setManaged(false);
        viewClientes.setVisible(false);
        viewClientes.setManaged(false);
    }

    private void setupClock() {
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", new java.util.Locale("es", "MX"));
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
        Runnable tick = () -> {
            LocalDateTime now = LocalDateTime.now();
            lblFecha.setText(now.format(fmtFecha));
            lblHora.setText(now.format(fmtHora));
        };
        tick.run();
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> tick.run()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    private void setupVendedorInfo() {
        var usuario = GestorSesion.getInstancia().getUsuarioActual();
        if (usuario != null) {
            lblVendedorNombre.setText(usuario.getNombre() + " " + usuario.getPrimerApellido());
        }
        lblPageTitle.setText("Punto de Venta");
        lblPageSub.setText("Registro de ventas y pedidos");
    }

    private void setupTablaProductos() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoriaProducto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStockProducto.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colPrecioProducto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$ %.2f", item));
            }
        });
        todosProductos = FXCollections.observableArrayList();
        productosFiltrados = new FilteredList<>(todosProductos, p -> true);
        tableProductos.setItems(productosFiltrados);
    }

    private void setupTablaCarrito() {
        colIdProdCarrito.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreCarrito.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidadCarrito.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioCarrito.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotalCarrito.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colTotalCarrito.setCellValueFactory(new PropertyValueFactory<>("total"));

        colPrecioCarrito.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$ %.2f", item));
            }
        });
        colSubtotalCarrito.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$ %.2f", item));
            }
        });
        colTotalCarrito.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$ %.2f", item));
            }
        });

        carrito = FXCollections.observableArrayList();
        tableCarrito.setItems(carrito);
        // ✅ CORREGIDO: usar cast explícito para evitar ambigüedad
        carrito.addListener((javafx.collections.ListChangeListener<DetalleVenta>) c -> actualizarTotales());
    }

    private void setupCombos() {
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Cemento y concreto", "Acero y metales",
                "Madera y derivados", "Impermeabilizantes", "Herramientas", "Tubería y plomería"));
        cmbCategoria.getSelectionModel().selectFirst();
        cmbMetodoPago.setItems(FXCollections.observableArrayList("EFECTIVO", "TARJETA", "CREDITO"));
        cmbMetodoPago.getSelectionModel().selectFirst();
        cmbTipoVenta.setItems(FXCollections.observableArrayList("INSTANTANEA", "PEDIDO"));
        cmbTipoVenta.getSelectionModel().selectFirst();
    }

    private void cargarProductos() {
        todosProductos.setAll(productoDao.listarTodos());
    }

    private void setupSearchFilter() {
        txtSearchProducto.textProperty().addListener((obs, old, newVal) -> applyFilters());
        cmbCategoria.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String search = txtSearchProducto.getText() == null ? "" : txtSearchProducto.getText().toLowerCase().trim();
        String cat = cmbCategoria.getValue();
        productosFiltrados.setPredicate(item -> {
            boolean matchSearch = search.isEmpty() || item.getNombre().toLowerCase().contains(search) || item.getIdProducto().toLowerCase().contains(search);
            boolean matchCat = cat == null || cat.equals("Todas") || item.getCategoria().equals(cat);
            return matchSearch && matchCat;
        });
    }

    private void setupNavegacion() {
        // ✅ CORREGIDO: usar setOnAction (JavaFX), no setOnClickListener
        navVentas.setOnAction(e -> showVentas());
        navPedidos.setOnAction(e -> showPedidos());
        navClientes.setOnAction(e -> showClientes());
    }

    @FXML
    private void showVentas() {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("nav-btn-active");
        navVentas.getStyleClass().add("nav-btn-active");
        activeNavBtn = navVentas;

        viewVentas.setVisible(true);
        viewVentas.setManaged(true);
        viewPedidos.setVisible(false);
        viewPedidos.setManaged(false);
        viewClientes.setVisible(false);
        viewClientes.setManaged(false);

        lblPageTitle.setText("Punto de Venta");
        lblPageSub.setText("Registro de ventas y pedidos");
    }

    @FXML
    private void showPedidos() {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("nav-btn-active");
        navPedidos.getStyleClass().add("nav-btn-active");
        activeNavBtn = navPedidos;

        viewVentas.setVisible(false);
        viewVentas.setManaged(false);
        viewPedidos.setVisible(true);
        viewPedidos.setManaged(true);
        viewClientes.setVisible(false);
        viewClientes.setManaged(false);

        lblPageTitle.setText("Mis Pedidos");
        lblPageSub.setText("Historial de pedidos realizados");
    }

    @FXML
    private void showClientes() {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("nav-btn-active");
        navClientes.getStyleClass().add("nav-btn-active");
        activeNavBtn = navClientes;

        viewVentas.setVisible(false);
        viewVentas.setManaged(false);
        viewPedidos.setVisible(false);
        viewPedidos.setManaged(false);
        viewClientes.setVisible(true);
        viewClientes.setManaged(true);

        lblPageTitle.setText("Clientes");
        lblPageSub.setText("Gestión de clientes");
    }

    @FXML
    private void handleAgregarAlCarrito() {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UIComponents.showNotification("Seleccione un producto", "warn");
            return;
        }
        if (seleccionado.getStockActual() <= 0) {
            UIComponents.showNotification("Producto sin stock", "error");
            return;
        }
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Producto: " + seleccionado.getNombre());
        dialog.setContentText("Cantidad:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int cantidad = Integer.parseInt(result.get());
                if (cantidad <= 0 || cantidad > seleccionado.getStockActual()) {
                    UIComponents.showNotification("Cantidad inválida o stock insuficiente", "warn");
                    return;
                }
                for (DetalleVenta item : carrito) {
                    if (item.getIdProducto().equals(seleccionado.getIdProducto())) {
                        item.setCantidad(item.getCantidad() + cantidad);
                        item.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
                        item.setTotal(item.getSubtotal() * (1 - item.getDescuentoAplicado() / 100));
                        tableCarrito.refresh();
                        actualizarTotales();
                        UIComponents.showNotification("Cantidad actualizada", "success");
                        return;
                    }
                }
                DetalleVenta nuevo = new DetalleVenta(0, seleccionado.getIdProducto(), seleccionado.getNombre(),
                        cantidad, seleccionado.getPrecioVenta(), 0.0);
                carrito.add(nuevo);
                UIComponents.showNotification("Producto agregado", "success");
            } catch (NumberFormatException e) {
                UIComponents.showNotification("Cantidad inválida", "error");
            }
        }
    }

    @FXML
    private void handleEliminarDelCarrito() {
        DetalleVenta seleccionado = tableCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            UIComponents.showNotification("Seleccione un item del carrito", "warn");
            return;
        }
        carrito.remove(seleccionado);
        actualizarTotales();
        UIComponents.showNotification("Item eliminado", "success");
    }

    @FXML
    private void handleAplicarDescuento() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Autorización de Gerente");
        dialog.setHeaderText("Se requiere PIN del Gerente");
        dialog.setContentText("Ingrese el PIN de autorización:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && "1234".equals(result.get())) {
            TextInputDialog descDialog = new TextInputDialog("0");
            descDialog.setTitle("Descuento");
            descDialog.setHeaderText("Aplicar descuento");
            descDialog.setContentText("Porcentaje de descuento (%):");
            Optional<String> descResult = descDialog.showAndWait();
            if (descResult.isPresent()) {
                try {
                    double desc = Double.parseDouble(descResult.get());
                    if (desc < 0 || desc > 100) {
                        UIComponents.showNotification("Descuento entre 0 y 100", "warn");
                        return;
                    }
                    descuentoGlobal = desc;
                    actualizarTotales();
                    UIComponents.showNotification("Descuento del " + desc + "% aplicado", "success");
                } catch (NumberFormatException e) {
                    UIComponents.showNotification("Descuento inválido", "error");
                }
            }
        } else if (result.isPresent()) {
            UIComponents.showNotification("PIN incorrecto", "error");
        }
    }

    private void actualizarTotales() {
        double subtotal = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        double total = subtotal * (1 - descuentoGlobal / 100);
        lblSubtotal.setText(String.format("$ %.2f", subtotal));
        lblDescuento.setText(String.format("%.1f%%", descuentoGlobal));
        lblTotal.setText(String.format("$ %.2f", total));
    }

    @FXML
    private void handleFinalizarVenta() {
        if (carrito.isEmpty()) {
            UIComponents.showNotification("Carrito vacío", "warn");
            return;
        }
        boolean confirmed = UIComponents.showConfirmDialog("Confirmar venta", "¿Desea finalizar la venta?");
        if (!confirmed) return;

        var usuario = GestorSesion.getInstancia().getUsuarioActual();
        int idVendedor = usuario != null ? usuario.getIdUsuario() : 1;

        double subtotal = Double.parseDouble(lblSubtotal.getText().replace("$ ", ""));
        double total = Double.parseDouble(lblTotal.getText().replace("$ ", ""));

        int nuevoIdVenta = ventaDao.obtenerUltimoId() + 1;
        Venta venta = new Venta(nuevoIdVenta, 0, idVendedor,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                cmbTipoVenta.getValue(), cmbMetodoPago.getValue(), subtotal, descuentoGlobal, total, "COMPLETADA");

        if (ventaDao.guardar(venta)) {
            int nuevoIdDetalle = detalleDao.obtenerUltimoId() + 1;
            for (DetalleVenta item : carrito) {
                item.setIdVenta(nuevoIdVenta);
                item.setIdDetalle(nuevoIdDetalle++);
                detalleDao.guardar(item);
                Producto producto = productoDao.buscarPorId(item.getIdProducto());
                if (producto != null) {
                    producto.setStockActual(producto.getStockActual() - item.getCantidad());
                    productoDao.actualizar(producto);
                }
            }
            carrito.clear();
            descuentoGlobal = 0;
            actualizarTotales();
            cargarProductos();
            UIComponents.showNotification("Venta completada con éxito", "success");
        } else {
            UIComponents.showNotification("Error al guardar la venta", "error");
        }
    }

    @FXML
    private void handleLogout() {
        boolean confirmed = UIComponents.showConfirmDialog("Cerrar sesión", "¿Desea cerrar la sesión?");
        if (confirmed) {
            GestorSesion.getInstancia().cerrarSesion();
            NavigationTools.navigateTo(btnLogout, "/views/Login.fxml", "SDG MDC - Autenticación");
        }
    }
}