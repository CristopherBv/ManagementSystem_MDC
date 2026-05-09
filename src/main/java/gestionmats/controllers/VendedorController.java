package gestionmats.controllers;

import gestionmats.dao.ClienteDaoCsv;
import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.model.Cliente;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;
import gestionmats.services.GestorSesion;
import gestionmats.utils.NavigationTools;
import gestionmats.utils.UIComponents;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class VendedorController implements Initializable {

    // ── SIDEBAR / TOPBAR ──────────────────────────────────────────────
    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSub;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblVendedorNombre;
    @FXML private Button btnLogout;

    // ── NAVEGACIÓN ────────────────────────────────────────────────────
    @FXML private Button navVentas;
    @FXML private Button navHistorial;
    @FXML private Button navClientes;
    @FXML private VBox viewVentas;
    @FXML private VBox viewHistorial;
    @FXML private VBox viewClientes;

    // ── VISTA VENTAS: productos ───────────────────────────────────────
    @FXML private TextField txtSearchProducto;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colIdProducto;
    @FXML private TableColumn<Producto, String> colNombreProducto;
    @FXML private TableColumn<Producto, String> colCategoriaProducto;
    @FXML private TableColumn<Producto, Integer> colStockProducto;
    @FXML private TableColumn<Producto, Double> colPrecioProducto;

    // ── VISTA VENTAS: carrito ─────────────────────────────────────────
    @FXML private TableView<DetalleVenta> tableCarrito;
    @FXML private TableColumn<DetalleVenta, String> colIdProdCarrito;
    @FXML private TableColumn<DetalleVenta, String> colNombreCarrito;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidadCarrito;
    @FXML private TableColumn<DetalleVenta, Double> colPrecioCarrito;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotalCarrito;
    @FXML private TableColumn<DetalleVenta, Double> colTotalCarrito;

    // ── VISTA VENTAS: totales y controles ─────────────────────────────
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private ComboBox<String> cmbTipoVenta;
    @FXML private Button btnAgregarCarrito;
    @FXML private Button btnEliminarDelCarrito;
    @FXML private Button btnAplicarDescuento;
    @FXML private Button btnFinalizarVenta;

    // ── VISTA VENTAS: cliente seleccionado ────────────────────────────
    @FXML private Label lblClienteSeleccionado;
    @FXML private Label lblPuntosCliente;
    @FXML private Button btnSeleccionarCliente;
    @FXML private Button btnLimpiarCliente;
    @FXML private Button btnCanjearPuntos; // NUEVO BOTÓN

    // ── VISTA HISTORIAL ───────────────────────────────────────────────
    @FXML private TextField txtBuscarVenta;
    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private TableView<Venta> tableHistorial;
    @FXML private TableColumn<Venta, Integer> colHisId;
    @FXML private TableColumn<Venta, String> colHisFecha;
    @FXML private TableColumn<Venta, String> colHisTipo;
    @FXML private TableColumn<Venta, String> colHisMetodo;
    @FXML private TableColumn<Venta, Double> colHisTotal;
    @FXML private TableColumn<Venta, String> colHisEstado;
    @FXML private TableColumn<Venta, String> colHisCliente;
    @FXML private Button btnVerDetalleVenta;
    @FXML private Label lblFooterHistorial;

    // ── VISTA CLIENTES ────────────────────────────────────────────────
    @FXML private TextField txtBuscarCliente;
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Integer> colCliId;
    @FXML private TableColumn<Cliente, String> colCliNombre;
    @FXML private TableColumn<Cliente, String> colCliTelefono;
    @FXML private TableColumn<Cliente, String> colCliEmail;
    @FXML private TableColumn<Cliente, Double> colCliPuntos;
    @FXML private TableColumn<Cliente, String> colCliPreferencias;
    @FXML private Button btnNuevoCliente;
    @FXML private Button btnEditarCliente;
    @FXML private Button btnEliminarCliente;
    @FXML private Label lblFooterClientes;

    // ── DAOs ──────────────────────────────────────────────────────────
    private ProductoDaoCsv productoDao;
    private VentaDaoCsv ventaDao;
    private DetalleVentaDaoCsv detalleDao;
    private ClienteDaoCsv clienteDao;

    // ── ESTADO INTERNO ────────────────────────────────────────────────
    private ObservableList<Producto> todosProductos;
    private FilteredList<Producto> productosFiltrados;
    private ObservableList<DetalleVenta> carrito;
    private ObservableList<Venta> historialVentas;
    private FilteredList<Venta> historialFiltrado;
    private ObservableList<Cliente> todosClientes;
    private FilteredList<Cliente> clientesFiltrados;

    private double descuentoGlobal = 0.0;
    private double descuentoPorPuntos = 0.0; // Descuento generado por puntos canjeados
    private int puntosUsados = 0; // Puntos que se van a restar del cliente
    private Cliente clienteActual = null;
    private Timeline clockTimeline;
    private Button activeNavBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productoDao = new ProductoDaoCsv();
        ventaDao = new VentaDaoCsv();
        detalleDao = new DetalleVentaDaoCsv();
        clienteDao = new ClienteDaoCsv();

        setupClock();
        setupVendedorInfo();
        setupTablaProductos();
        setupTablaCarrito();
        setupTablaHistorial();
        setupTablaClientes();
        setupCombos();
        cargarProductos();
        cargarHistorial();
        cargarClientes();
        setupSearchFilter();
        actualizarInfoCliente();

        activeNavBtn = navVentas;
        mostrarVista(viewVentas, navVentas, "Punto de Venta", "Registro de ventas y pedidos");
        viewHistorial.setVisible(false);
        viewHistorial.setManaged(false);
        viewClientes.setVisible(false);
        viewClientes.setManaged(false);
    }

    // ==================== MÉTODOS EXISTENTES (SIN CAMBIOS ESTRUCTURALES) ====================
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
    }

    private void mostrarVista(VBox vistaActiva, Button navActivo, String titulo, String subtitulo) {
        if (activeNavBtn != null) activeNavBtn.getStyleClass().remove("nav-btn-active");
        navActivo.getStyleClass().add("nav-btn-active");
        activeNavBtn = navActivo;

        viewVentas.setVisible(false); viewVentas.setManaged(false);
        viewHistorial.setVisible(false); viewHistorial.setManaged(false);
        viewClientes.setVisible(false); viewClientes.setManaged(false);

        vistaActiva.setVisible(true); vistaActiva.setManaged(true);
        UIComponents.fadeIn(vistaActiva, 180);

        lblPageTitle.setText(titulo);
        lblPageSub.setText(subtitulo);
    }

    @FXML private void showVentas() {
        mostrarVista(viewVentas, navVentas, "Punto de Venta", "Registro de ventas y pedidos");
    }
    @FXML private void showHistorial() { cargarHistorial(); mostrarVista(viewHistorial, navHistorial, "Historial de Ventas", "Consulta de todas las ventas registradas"); }
    @FXML private void showClientes() { cargarClientes(); mostrarVista(viewClientes, navClientes, "Clientes", "Gestión de clientes y programa de lealtad"); }

    private void setupTablaProductos() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoriaProducto.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colStockProducto.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colPrecioProducto.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colPrecioProducto.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$ %.2f", item));
            }
        });
        todosProductos = FXCollections.observableArrayList();
        productosFiltrados = new FilteredList<>(todosProductos, p -> true);
        tableProductos.setItems(productosFiltrados);
    }
    private void cargarProductos() { todosProductos.setAll(productoDao.listarTodos()); }
    private void setupSearchFilter() {
        txtSearchProducto.textProperty().addListener((obs, o, n) -> applyFilters());
        cmbCategoria.valueProperty().addListener((obs, o, n) -> applyFilters());
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

    // ==================== CARRITO ====================
    private void setupTablaCarrito() {
        colIdProdCarrito.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreCarrito.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidadCarrito.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioCarrito.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotalCarrito.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colTotalCarrito.setCellValueFactory(new PropertyValueFactory<>("total"));
        for (TableColumn<DetalleVenta, Double> col : List.of(colPrecioCarrito, colSubtotalCarrito, colTotalCarrito)) {
            col.setCellFactory(c -> new TableCell<>() {
                @Override protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : String.format("$ %.2f", item));
                }
            });
        }
        carrito = FXCollections.observableArrayList();
        tableCarrito.setItems(carrito);
        carrito.addListener((javafx.collections.ListChangeListener<DetalleVenta>) c -> actualizarTotales());
    }

    private void setupCombos() {
        cmbCategoria.setItems(FXCollections.observableArrayList("Todas", "Cemento y concreto", "Acero y metales", "Madera y derivados", "Impermeabilizantes", "Herramientas", "Tubería y plomería"));
        cmbCategoria.getSelectionModel().selectFirst();
        cmbMetodoPago.setItems(FXCollections.observableArrayList("EFECTIVO", "TARJETA", "CREDITO"));
        cmbMetodoPago.getSelectionModel().selectFirst();
        cmbTipoVenta.setItems(FXCollections.observableArrayList("INSTANTANEA", "PEDIDO"));
        cmbTipoVenta.getSelectionModel().selectFirst();
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "COMPLETADA", "PENDIENTE", "CANCELADA"));
        cmbFiltroEstado.getSelectionModel().selectFirst();
        cmbFiltroEstado.valueProperty().addListener((obs, o, n) -> applyHistorialFilter());
    }

    @FXML private void handleAgregarAlCarrito() {
        Producto sel = tableProductos.getSelectionModel().getSelectedItem();
        if (sel == null) { UIComponents.showNotification("Seleccione un producto", "warn"); return; }
        if (sel.getStockActual() <= 0) { UIComponents.showNotification("Producto sin stock", "error"); return; }

        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Cantidad");
        dialog.setHeaderText("Producto: " + sel.getNombre());
        dialog.setContentText("Cantidad:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int cantidad = Integer.parseInt(result.get().trim());
                if (cantidad <= 0 || cantidad > sel.getStockActual()) { UIComponents.showNotification("Cantidad inválida o stock insuficiente", "warn"); return; }
                for (DetalleVenta item : carrito) {
                    if (item.getIdProducto().equals(sel.getIdProducto())) {
                        item.setCantidad(item.getCantidad() + cantidad);
                        item.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
                        item.setTotal(item.getSubtotal() * (1 - item.getDescuentoAplicado() / 100));
                        tableCarrito.refresh(); actualizarTotales();
                        UIComponents.showNotification("Cantidad actualizada", "success");
                        return;
                    }
                }
                carrito.add(new DetalleVenta(0, sel.getIdProducto(), sel.getNombre(), cantidad, sel.getPrecioVenta(), 0.0));
                UIComponents.showNotification("Producto agregado", "success");
            } catch (NumberFormatException e) { UIComponents.showNotification("Cantidad inválida", "error"); }
        }
    }

    @FXML private void handleEliminarDelCarrito() {
        DetalleVenta sel = tableCarrito.getSelectionModel().getSelectedItem();
        if (sel == null) { UIComponents.showNotification("Seleccione un item del carrito", "warn"); return; }
        carrito.remove(sel); actualizarTotales();
        UIComponents.showNotification("Item eliminado", "success");
    }

    @FXML private void handleAplicarDescuento() {
        TextInputDialog pinDialog = new TextInputDialog();
        pinDialog.setTitle("Autorización de Gerente");
        pinDialog.setHeaderText("Se requiere PIN del Gerente para aplicar descuento manual");
        pinDialog.setContentText("PIN:");
        Optional<String> pinResult = pinDialog.showAndWait();
        if (pinResult.isEmpty()) return;
        if (!"1234".equals(pinResult.get())) { UIComponents.showNotification("PIN incorrecto", "error"); return; }

        TextInputDialog descDialog = new TextInputDialog(String.valueOf((int) descuentoGlobal));
        descDialog.setTitle("Descuento manual");
        descDialog.setHeaderText("Autorizado. Ingrese el porcentaje.");
        descDialog.setContentText("Descuento (0–100):");
        descDialog.showAndWait().ifPresent(input -> {
            try {
                double desc = Double.parseDouble(input.trim());
                if (desc < 0 || desc > 100) { UIComponents.showNotification("Valor entre 0 y 100", "warn"); return; }
                descuentoGlobal = desc;
                actualizarTotales();
                UIComponents.showNotification("Descuento del " + desc + "% aplicado", "success");
            } catch (NumberFormatException e) { UIComponents.showNotification("Valor inválido", "error"); }
        });
    }

    private void actualizarTotales() {
        double subtotal = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        double total = subtotal * (1 - descuentoGlobal / 100);
        total = total - descuentoPorPuntos; // Aplicar descuento por puntos
        if (total < 0) total = 0;
        lblSubtotal.setText(String.format("$ %.2f", subtotal));
        lblDescuento.setText(String.format("%.1f%%", descuentoGlobal));
        lblTotal.setText(String.format("$ %.2f", total));
    }

    // ==================== CLIENTE EN VENTA (CON CANJE DE PUNTOS) ====================
    @FXML private void handleSeleccionarCliente() {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Cliente");
        dialog.setHeaderText("Busca al cliente por nombre o teléfono");

        ButtonType btnSeleccionar = new ButtonType("Seleccionar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSeleccionar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: #16181f;");

        VBox content = new VBox(10); content.setPadding(new Insets(12));
        TextField busqueda = new TextField(); busqueda.setPromptText("Nombre o teléfono...");

        TableView<Cliente> tabla = new TableView<>(); tabla.setPrefHeight(220);
        TableColumn<Cliente, String> cNombre = new TableColumn<>("Nombre"); cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre")); cNombre.setPrefWidth(180);
        TableColumn<Cliente, String> cTel = new TableColumn<>("Teléfono"); cTel.setCellValueFactory(new PropertyValueFactory<>("numeroTelefonico")); cTel.setPrefWidth(120);
        TableColumn<Cliente, String> cEmail = new TableColumn<>("Email"); cEmail.setCellValueFactory(new PropertyValueFactory<>("correoElectronico")); cEmail.setPrefWidth(150);
        TableColumn<Cliente, Double> cPuntos = new TableColumn<>("Puntos"); cPuntos.setCellValueFactory(new PropertyValueFactory<>("puntosLealtad")); cPuntos.setPrefWidth(80);
        cPuntos.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Double item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : String.format("%.0f", item)); } });
        tabla.getColumns().addAll(cNombre, cTel, cEmail, cPuntos);

        ObservableList<Cliente> listaDialog = FXCollections.observableArrayList(clienteDao.listarTodos());
        FilteredList<Cliente> filtradoDialog = new FilteredList<>(listaDialog, c -> true);
        tabla.setItems(filtradoDialog);
        busqueda.textProperty().addListener((obs, o, n) -> {
            String txt = n == null ? "" : n.toLowerCase().trim();
            filtradoDialog.setPredicate(c -> txt.isEmpty() || c.getNombre().toLowerCase().contains(txt) || c.getNumeroTelefonico().contains(txt));
        });

        content.getChildren().addAll(busqueda, tabla);
        dialog.getDialogPane().setContent(content);
        javafx.scene.Node btnOk = dialog.getDialogPane().lookupButton(btnSeleccionar);
        btnOk.setDisable(true);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> btnOk.setDisable(n == null));
        dialog.setResultConverter(bt -> bt == btnSeleccionar ? tabla.getSelectionModel().getSelectedItem() : null);

        dialog.showAndWait().ifPresent(c -> {
            clienteActual = c;
            actualizarInfoCliente();
            UIComponents.showNotification("Cliente seleccionado: " + c.getNombre(), "success");
        });
    }

    @FXML private void handleLimpiarCliente() {
        clienteActual = null;
        descuentoGlobal = 0.0;
        puntosUsados = 0;
        descuentoPorPuntos = 0.0;
        actualizarTotales();
        actualizarInfoCliente();
    }

    private void actualizarInfoCliente() {
        if (clienteActual == null) {
            lblClienteSeleccionado.setText("Sin cliente (venta general)");
            lblPuntosCliente.setText("");
            btnCanjearPuntos.setDisable(true);
        } else {
            lblClienteSeleccionado.setText(clienteActual.getNombre());
            lblPuntosCliente.setText("Puntos: " + String.format("%.0f", clienteActual.getPuntosLealtad()));
            btnCanjearPuntos.setDisable(false);
        }
    }

    @FXML
    private void handleCanjearPuntos() {
        if (clienteActual == null) {
            UIComponents.showNotification("No hay cliente seleccionado", "warn");
            return;
        }
        if (carrito.isEmpty()) {
            UIComponents.showNotification("El carrito está vacío", "warn");
            return;
        }

        double totalActual = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        totalActual = totalActual * (1 - descuentoGlobal / 100);

        double puntosDisponibles = clienteActual.getPuntosLealtad();
        double valorEnPesos = puntosDisponibles * 0.01; // 1 punto = $0.01 MXN

        // Crear diálogo para elegir puntos
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Canjear Puntos");
        dialog.setHeaderText("Cliente: " + clienteActual.getNombre() + "\nPuntos disponibles: " + (int) puntosDisponibles);
        dialog.setContentText("Ingrese la cantidad de puntos a canjear:\n(1 punto = $0.01 MXN | Valor máximo en pesos: $" + String.format("%.2f", Math.min(valorEnPesos, totalActual)) + ")");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int puntos = Integer.parseInt(result.get().trim());
                if (puntos <= 0) {
                    UIComponents.showNotification("Debe ingresar una cantidad mayor a 0", "warn");
                    return;
                }
                if (puntos > puntosDisponibles) {
                    UIComponents.showNotification("No tiene suficientes puntos. Máximo: " + (int) puntosDisponibles, "warn");
                    return;
                }

                double valorDescuento = puntos * 0.01;
                if (valorDescuento > totalActual) {
                    UIComponents.showNotification("El valor del descuento no puede superar el total de la compra.", "warn");
                    return;
                }

                puntosUsados = puntos;
                descuentoPorPuntos = valorDescuento;
                actualizarTotales();

                UIComponents.showNotification("Se canjearon " + puntos + " puntos por $" + String.format("%.2f", valorDescuento), "success");
            } catch (NumberFormatException e) {
                UIComponents.showNotification("Cantidad inválida", "error");
            }
        }
    }

    // ==================== FINALIZAR VENTA (con descuento de puntos) ====================
    @FXML
    private void handleFinalizarVenta() {
        if (carrito.isEmpty()) {
            UIComponents.showNotification("Carrito vacío", "warn");
            return;
        }

        String msgCliente = clienteActual != null ? "Cliente: " + clienteActual.getNombre() : "Venta general (sin cliente)";
        if (!UIComponents.showConfirmDialog("Confirmar venta", msgCliente + "\n¿Desea finalizar la venta?")) return;

        var usuario = GestorSesion.getInstancia().getUsuarioActual();
        int idVendedor = usuario != null ? usuario.getIdUsuario() : 1;
        int idCliente = clienteActual != null ? clienteActual.getIdCliente() : 0;

        double subtotal = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        double total = Double.parseDouble(lblTotal.getText().replace("$ ", ""));

        int nuevoIdVenta = ventaDao.obtenerUltimoId() + 1;
        Venta venta = new Venta(nuevoIdVenta, idCliente, idVendedor,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                cmbTipoVenta.getValue(), cmbMetodoPago.getValue(),
                subtotal, descuentoGlobal, total, "COMPLETADA");

        if (!ventaDao.guardar(venta)) {
            UIComponents.showNotification("Error al guardar la venta", "error");
            return;
        }

        int nuevoIdDetalle = detalleDao.obtenerUltimoId() + 1;
        for (DetalleVenta item : carrito) {
            item.setIdVenta(nuevoIdVenta);
            item.setIdDetalle(nuevoIdDetalle++);
            detalleDao.guardar(item);
            Producto p = productoDao.buscarPorId(item.getIdProducto());
            if (p != null) {
                p.setStockActual(p.getStockActual() - item.getCantidad());
                productoDao.actualizar(p);
            }
        }

        // Actualizar puntos del cliente
        if (clienteActual != null) {
            double puntosGanados = total * 0.01;
            double puntosFinales = clienteActual.getPuntosLealtad() - puntosUsados + puntosGanados;
            clienteActual.setPuntosLealtad(puntosFinales);
            clienteDao.actualizar(clienteActual);
            UIComponents.showNotification("Cliente " + clienteActual.getNombre() +
                    " usó " + puntosUsados + " puntos, ganó " + String.format("%.0f", puntosGanados) +
                    " nuevos puntos. Saldo actual: " + String.format("%.0f", puntosFinales), "success");
        }

        carrito.clear();
        descuentoGlobal = 0;
        puntosUsados = 0;
        descuentoPorPuntos = 0.0;
        clienteActual = null;
        actualizarTotales();
        actualizarInfoCliente();
        cargarProductos();
        UIComponents.showNotification("Venta #" + nuevoIdVenta + " completada", "success");
    }

    // ==================== HISTORIAL (SIN CAMBIOS) ====================
    private void setupTablaHistorial() {
        colHisId.setCellValueFactory(new PropertyValueFactory<>("idVenta"));
        colHisFecha.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colHisTipo.setCellValueFactory(new PropertyValueFactory<>("tipoVenta"));
        colHisMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colHisTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colHisEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colHisCliente.setCellValueFactory(data -> {
            int idCli = data.getValue().getIdCliente();
            if (idCli == 0) return new SimpleStringProperty("General");
            Cliente c = clienteDao.buscarPorId(idCli);
            return new SimpleStringProperty(c != null ? c.getNombre() : "ID:" + idCli);
        });
        colHisTotal.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Double item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : String.format("$ %.2f", item)); } });
        historialVentas = FXCollections.observableArrayList(); historialFiltrado = new FilteredList<>(historialVentas, v -> true); tableHistorial.setItems(historialFiltrado);
        historialFiltrado.addListener((javafx.collections.ListChangeListener<Venta>) c -> lblFooterHistorial.setText("Mostrando " + historialFiltrado.size() + " ventas"));
        txtBuscarVenta.textProperty().addListener((obs, o, n) -> applyHistorialFilter());
    }
    private void cargarHistorial() { historialVentas.setAll(ventaDao.listarTodos()); FXCollections.sort(historialVentas, (a, b) -> Integer.compare(b.getIdVenta(), a.getIdVenta())); }
    private void applyHistorialFilter() {
        String search = txtBuscarVenta.getText() == null ? "" : txtBuscarVenta.getText().toLowerCase().trim();
        String estado = cmbFiltroEstado.getValue();
        historialFiltrado.setPredicate(v -> {
            boolean matchSearch = search.isEmpty() || String.valueOf(v.getIdVenta()).contains(search) || v.getFechaHora().toLowerCase().contains(search);
            boolean matchEstado = estado == null || estado.equals("Todos") || v.getEstado().equals(estado);
            return matchSearch && matchEstado;
        });
    }
    @FXML private void handleVerDetalleVenta() {
        Venta sel = tableHistorial.getSelectionModel().getSelectedItem();
        if (sel == null) { UIComponents.showNotification("Seleccione una venta", "warn"); return; }
        List<DetalleVenta> detalles = detalleDao.listarPorIdVenta(sel.getIdVenta());
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle de Venta #" + sel.getIdVenta());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setStyle("-fx-background-color: #16181f;");
        VBox content = new VBox(10); content.setPadding(new Insets(14)); content.setPrefWidth(580);
        GridPane grid = new GridPane(); grid.setHgap(20); grid.setVgap(6);
        String nombreCliente = "General";
        if (sel.getIdCliente() != 0) { Cliente c = clienteDao.buscarPorId(sel.getIdCliente()); if (c != null) nombreCliente = c.getNombre(); }
        grid.add(new Label("Cliente:"), 0,0); grid.add(new Label(nombreCliente), 1,0);
        grid.add(new Label("Estado:"), 0,1); grid.add(new Label(sel.getEstado()), 1,1);
        grid.add(new Label("Tipo:"), 0,2); grid.add(new Label(sel.getTipoVenta()), 1,2);
        grid.add(new Label("Método:"), 0,3); grid.add(new Label(sel.getMetodoPago()), 1,3);
        grid.add(new Label("Subtotal:"), 2,0); grid.add(new Label(String.format("$ %.2f", sel.getSubtotal())), 3,0);
        grid.add(new Label("Descuento:"), 2,1); grid.add(new Label(String.format("%.1f%%", sel.getDescuento())), 3,1);
        grid.add(new Label("Total:"), 2,2); grid.add(new Label(String.format("$ %.2f", sel.getTotal())), 3,2);
        TableView<DetalleVenta> tablaDetalle = new TableView<>(); tablaDetalle.setPrefHeight(200);
        TableColumn<DetalleVenta, String> dNombre = new TableColumn<>("Producto"); dNombre.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        TableColumn<DetalleVenta, Integer> dCantidad = new TableColumn<>("Cant."); dCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        TableColumn<DetalleVenta, Double> dPrecio = new TableColumn<>("Precio"); dPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        TableColumn<DetalleVenta, Double> dTotal = new TableColumn<>("Total"); dTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        dPrecio.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Double item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : String.format("$ %.2f", item)); } });
        dTotal.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Double item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : String.format("$ %.2f", item)); } });
        tablaDetalle.getColumns().addAll(dNombre, dCantidad, dPrecio, dTotal);
        tablaDetalle.setItems(FXCollections.observableArrayList(detalles));
        content.getChildren().addAll(grid, new Separator(), tablaDetalle);
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    // ==================== CLIENTES CRUD ====================
    private void setupTablaClientes() {
        colCliId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colCliNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCliTelefono.setCellValueFactory(new PropertyValueFactory<>("numeroTelefonico"));
        colCliEmail.setCellValueFactory(new PropertyValueFactory<>("correoElectronico"));
        colCliPuntos.setCellValueFactory(new PropertyValueFactory<>("puntosLealtad"));
        colCliPreferencias.setCellValueFactory(new PropertyValueFactory<>("preferencias"));
        colCliPuntos.setCellFactory(col -> new TableCell<>() { @Override protected void updateItem(Double item, boolean empty) { super.updateItem(item, empty); setText(empty || item == null ? null : String.format("%.0f", item)); } });
        todosClientes = FXCollections.observableArrayList();
        clientesFiltrados = new FilteredList<>(todosClientes, c -> true);
        tableClientes.setItems(clientesFiltrados);
        clientesFiltrados.addListener((javafx.collections.ListChangeListener<Cliente>) c -> lblFooterClientes.setText("Total: " + clientesFiltrados.size() + " clientes"));
        txtBuscarCliente.textProperty().addListener((obs, o, n) -> {
            String txt = n == null ? "" : n.toLowerCase().trim();
            clientesFiltrados.setPredicate(c -> txt.isEmpty() || c.getNombre().toLowerCase().contains(txt) || c.getNumeroTelefonico().contains(txt));
        });
    }
    private void cargarClientes() { todosClientes.setAll(clienteDao.listarTodos()); }
    @FXML private void handleNuevoCliente() {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Cliente"); dialog.setHeaderText("Registrar nuevo cliente");
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: #16181f;");
        GridPane grid = new GridPane(); grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(16));
        TextField txtNombre = new TextField(); txtNombre.setPromptText("Nombre(s)");
        TextField txtTelefono = new TextField(); txtTelefono.setPromptText("Teléfono");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email (opcional)");
        TextField txtDireccion = new TextField(); txtDireccion.setPromptText("Dirección (opcional)");
        TextField txtPreferencias = new TextField(); txtPreferencias.setPromptText("Preferencias (opcional)");
        grid.addRow(0, new Label("Nombre:"), txtNombre); grid.addRow(1, new Label("Teléfono:"), txtTelefono);
        grid.addRow(2, new Label("Email:"), txtEmail); grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label("Preferencias:"), txtPreferencias);
        dialog.getDialogPane().setContent(grid);
        javafx.scene.Node btnOk = dialog.getDialogPane().lookupButton(btnGuardar);
        btnOk.setDisable(true);
        txtNombre.textProperty().addListener((obs, o, n) -> btnOk.setDisable(n.trim().isEmpty() || txtTelefono.getText().trim().isEmpty()));
        txtTelefono.textProperty().addListener((obs, o, n) -> btnOk.setDisable(txtNombre.getText().trim().isEmpty() || n.trim().isEmpty()));
        dialog.setResultConverter(bt -> {
            if (bt != btnGuardar) return null;
            int nuevoId = clienteDao.obtenerUltimoId() + 1;
            return new Cliente(nuevoId, txtNombre.getText().trim(), txtEmail.getText().trim(), txtTelefono.getText().trim(), txtDireccion.getText().trim(), 0.0, txtPreferencias.getText().trim());
        });
        dialog.showAndWait().ifPresent(c -> { clienteDao.guardar(c); cargarClientes(); UIComponents.showNotification("Cliente registrado", "success"); });
    }
    @FXML private void handleEditarCliente() {
        Cliente sel = tableClientes.getSelectionModel().getSelectedItem();
        if (sel == null) { UIComponents.showNotification("Seleccione un cliente para editar", "warn"); return; }
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle("Editar Cliente"); dialog.setHeaderText("Editando: " + sel.getNombre());
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: #16181f;");
        GridPane grid = new GridPane(); grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(16));
        TextField txtNombre = new TextField(sel.getNombre()); TextField txtTelefono = new TextField(sel.getNumeroTelefonico());
        TextField txtEmail = new TextField(sel.getCorreoElectronico()); TextField txtDireccion = new TextField(sel.getDireccion());
        TextField txtPreferencias = new TextField(sel.getPreferencias());
        grid.addRow(0, new Label("Nombre:"), txtNombre); grid.addRow(1, new Label("Teléfono:"), txtTelefono);
        grid.addRow(2, new Label("Email:"), txtEmail); grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label("Preferencias:"), txtPreferencias);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(bt -> {
            if (bt != btnGuardar) return null;
            sel.setNombre(txtNombre.getText().trim()); sel.setNumeroTelefonico(txtTelefono.getText().trim());
            sel.setCorreoElectronico(txtEmail.getText().trim()); sel.setDireccion(txtDireccion.getText().trim());
            sel.setPreferencias(txtPreferencias.getText().trim());
            return sel;
        });
        dialog.showAndWait().ifPresent(c -> { clienteDao.actualizar(c); cargarClientes(); UIComponents.showNotification("Cliente actualizado", "success"); });
    }
    @FXML private void handleEliminarCliente() {
        Cliente sel = tableClientes.getSelectionModel().getSelectedItem();
        if (sel == null) { UIComponents.showNotification("Seleccione un cliente para eliminar", "warn"); return; }
        boolean confirm = UIComponents.showConfirmDialog("Eliminar cliente", "¿Eliminar a " + sel.getNombre() + "?\nLas ventas históricas no se borrarán.");
        if (confirm) { clienteDao.eliminar(sel.getIdCliente()); cargarClientes(); UIComponents.showNotification("Cliente eliminado", "success"); }
    }

    // ==================== LOGOUT ====================
    @FXML private void handleLogout() { NavigationTools.manejarLogout(btnLogout, clockTimeline); }
}