package gestionmats.controllers;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.EstadoProducto;
import gestionmats.model.Producto;
import gestionmats.utils.UIComponents;
import gestionmats.utils.NavigationTools;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * GerenteController
 * Maneja la vista principal del gerente: navegación lateral,
 * KPIs, reloj en tiempo real y tabla de inventario integrada con CSV.
 */
public class GerenteController implements Initializable {

    // ── SIDEBAR / NAV ──────────────────────────────────────
    @FXML private Button navInventario;
    @FXML private Button navPedidos;
    @FXML private Button navProveedores;
    @FXML private Button navReportes;
    @FXML private Button navConfig;
    @FXML private Button btnLogout;
    @FXML private Label  lblUserName;

    // ── TOPBAR ─────────────────────────────────────────────
    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSub;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;

    // ── KPIs ───────────────────────────────────────────────
    @FXML private Label kpiStock;
    @FXML private Label kpiStockSub;
    @FXML private Label kpiPedidos;
    @FXML private Label kpiPedidosSub;
    @FXML private Label kpiProveedores;
    @FXML private Label kpiProveedoresSub;
    @FXML private Label kpiAlertas;
    @FXML private Label kpiAlertasSub;

    // ── VISTAS DINÁMICAS ───────────────────────────────────
    @FXML private VBox viewInventario;
    @FXML private VBox viewPedidos;
    @FXML private VBox viewProveedores;
    @FXML private VBox viewReportes;
    @FXML private VBox viewConfig;

    // ── INVENTARIO (AHORA USANDO EL MODELO PRODUCTO) ───────
    @FXML private TextField  txtSearch;
    @FXML private ComboBox<String> cmbCategoria;

    @FXML private TableView<Producto> tableInventario;
    @FXML private TableColumn<Producto, String>  colId;
    @FXML private TableColumn<Producto, String>  colNombre;
    @FXML private TableColumn<Producto, String>  colCategoria;
    @FXML private TableColumn<Producto, Integer> colCantidad; // Stock Actual
    @FXML private TableColumn<Producto, String>  colUnidad;
    @FXML private TableColumn<Producto, Integer> colStock;    // Stock Mínimo (Calculado)
    @FXML private TableColumn<Producto, String>  colEstado;   // Estado (Dinámico)

    @FXML private Label lblTotal;
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnExportar;
    @FXML private Button btnActualizar;

    // ── ESTADO INTERNO ─────────────────────────────────────
    private ObservableList<Producto> allItems;
    private FilteredList<Producto>   filteredItems;
    private Timeline clockTimeline;
    private Button activeNavBtn;

    // Nuestro DAO para la persistencia real
    private ProductoDaoCsv productoDao;

    // ══════════════════════════════════════════════════════
    // INITIALIZE
    // ══════════════════════════════════════════════════════
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializamos el DAO
        productoDao = new ProductoDaoCsv();

        setupClock();
        setupNavButtons();
        setupTableColumns();
        setupSearchFilter();
        setupToolbarButtons();

        // Cargamos los datos reales del CSV en lugar de los hardcodeados
        actualizarTablaDesdeCsv();

        // Vista activa por defecto
        activeNavBtn = navInventario;
    }

    // ══════════════════════════════════════════════════════
    // RELOJ EN TIEMPO REAL
    // ══════════════════════════════════════════════════════
    private void setupClock() {
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy",
                new java.util.Locale("es", "MX"));
        DateTimeFormatter fmtHora  = DateTimeFormatter.ofPattern("HH:mm");

        Runnable tick = () -> {
            LocalDateTime now = LocalDateTime.now();
            String fecha = now.format(fmtFecha);
            lblFecha.setText(Character.toUpperCase(fecha.charAt(0)) + fecha.substring(1));
            lblHora.setText(now.format(fmtHora));
        };

        tick.run();
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> tick.run()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    // ══════════════════════════════════════════════════════
    // NAVEGACIÓN LATERAL
    // ══════════════════════════════════════════════════════
    private void setupNavButtons() {
        UIComponents.applyNavHover(navInventario);
        UIComponents.applyNavHover(navPedidos);
        UIComponents.applyNavHover(navProveedores);
        UIComponents.applyNavHover(navReportes);
        UIComponents.applyNavHover(navConfig);
        UIComponents.applyButtonPulse(btnLogout);
    }

    private void switchView(Button navBtn, VBox view, String title, String subtitle) {
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().remove("nav-btn-active");
        }
        navBtn.getStyleClass().add("nav-btn-active");
        activeNavBtn = navBtn;

        viewInventario.setVisible(false);  viewInventario.setManaged(false);
        viewPedidos.setVisible(false);     viewPedidos.setManaged(false);
        viewProveedores.setVisible(false); viewProveedores.setManaged(false);
        viewReportes.setVisible(false);    viewReportes.setManaged(false);
        viewConfig.setVisible(false);      viewConfig.setManaged(false);

        view.setVisible(true);
        view.setManaged(true);
        UIComponents.fadeIn(view, 200);

        lblPageTitle.setText(title);
        lblPageSub.setText(subtitle);
    }

    @FXML private void showInventario() {
        switchView(navInventario, viewInventario, "Inventario", "Control y seguimiento de materiales");
    }
    @FXML private void showPedidos() {
        switchView(navPedidos, viewPedidos, "Pedidos", "Gestión de pedidos activos");
    }
    @FXML private void showProveedores() {
        switchView(navProveedores, viewProveedores, "Proveedores", "Directorio de proveedores");
    }
    @FXML private void showReportes() {
        switchView(navReportes, viewReportes, "Reportes", "Informes y estadísticas");
    }
    @FXML private void showConfig() {
        switchView(navConfig, viewConfig, "Configuración", "Parámetros del sistema");
    }

    // ══════════════════════════════════════════════════════
    // TABLA DE INVENTARIO CON DATOS REALES
    // ══════════════════════════════════════════════════════
    private void setupTableColumns() {
        // Mapeo directo a los atributos de Producto
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getIdProducto()));
        colNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria()));
        colCantidad.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStockActual()));
        colUnidad.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUnidadMedida()));

        // Atributo calculado al vuelo en el modelo
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().calcularStockMinimo()));

        // Atributo de Estado dinámico evaluado al vuelo
        colEstado.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado().name()));

        // Lógica de colores para el estado
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null); setStyle("");
                    return;
                }
                setText(estado);
                switch (estado) {
                    case "LLENO"   -> setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;"); // Celeste
                    case "OK"      -> setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;"); // Verde
                    case "BAJO"    -> setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;"); // Naranja
                    case "CRITICO" -> setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;"); // Rojo
                    default        -> setStyle("-fx-text-fill: #9ca3af;");
                }
            }
        });

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Cemento y concreto", "Acero y metales",
                "Madera y derivados", "Impermeabilizantes",
                "Herramientas", "Tubería y plomería"
        ));
        cmbCategoria.getSelectionModel().selectFirst();
    }

    private void setupSearchFilter() {
        allItems = FXCollections.observableArrayList();
        filteredItems = new FilteredList<>(allItems, p -> true);
        tableInventario.setItems(filteredItems);

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        cmbCategoria.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        filteredItems.addListener((javafx.collections.ListChangeListener<Producto>) c ->
                lblTotal.setText("Total: " + filteredItems.size() + " registros"));
    }

    private void applyFilters() {
        String search = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String cat    = cmbCategoria.getValue();

        filteredItems.setPredicate(item -> {
            boolean matchSearch = search.isEmpty()
                    || item.getNombre().toLowerCase().contains(search)
                    || item.getIdProducto().toLowerCase().contains(search);
            boolean matchCat = cat == null || cat.equals("Todas")
                    || item.getCategoria().equals(cat);
            return matchSearch && matchCat;
        });
    }

    // ══════════════════════════════════════════════════════
    // BOTONES DE ACCIÓN Y PERSISTENCIA
    // ══════════════════════════════════════════════════════
    private void setupToolbarButtons() {
        UIComponents.applyButtonAdd(btnAgregar);
        UIComponents.applyButtonEdit(btnEditar);
        UIComponents.applyButtonDelete(btnEliminar);
        UIComponents.applyButtonGhost(btnExportar);
        UIComponents.applyButtonGhost(btnActualizar);
    }

    private void actualizarTablaDesdeCsv() {
        // Leemos todo desde tu base de datos física usando el DAO
        allItems.setAll(productoDao.listarTodos());
        updateKPIs();
    }

    @FXML private void handleAgregar() {
        UIComponents.showNotification("Funcionalidad: Agregar material (En desarrollo)", "info");
        // TODO: Abrir diálogo que construya un Producto y llame a productoDao.guardar(p)
    }

    @FXML private void handleEditar() {
        Producto selected = tableInventario.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponents.showNotification("Seleccione un material para editar.", "warn");
            return;
        }
        UIComponents.showNotification("Editando: " + selected.getNombre(), "info");
        // TODO: Abrir diálogo, modificar Producto y llamar a productoDao.actualizar(p)
    }

    @FXML private void handleEliminar() {
        Producto selected = tableInventario.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponents.showNotification("Seleccione un material para eliminar.", "warn");
            return;
        }

        boolean confirmed = UIComponents.showConfirmDialog(
                "Eliminar material",
                "¿Está seguro de eliminar \"" + selected.getNombre() + "\"?\nEsta acción no se puede deshacer."
        );

        if (confirmed) {
            // Nota: El equipo debe implementar el método de borrado físico en CsvUtils
            // productoDao.eliminar(selected.getIdProducto());

            // Por ahora lo borramos visualmente de la tabla
            allItems.remove(selected);
            updateKPIs();
            UIComponents.showNotification("Material eliminado de la vista temporalmente.", "success");
        }
    }

    @FXML private void handleExportar() {
        UIComponents.showNotification("Exportando inventario...", "info");
        // TODO: Implementar exportación CSV/Excel
    }

    @FXML private void handleActualizar() {
        actualizarTablaDesdeCsv();
        UIComponents.showNotification("Datos recargados desde la base de datos.", "success");
    }

    @FXML private void handleLogout() {
        NavigationTools.manejarLogout(btnLogout, clockTimeline);
    }

    // ══════════════════════════════════════════════════════
    // ACTUALIZACIÓN DE KPIs CON LÓGICA DE NEGOCIO REAL
    // ══════════════════════════════════════════════════════
    private void updateKPIs() {
        int total = allItems.size();

        // Usamos nuestro modelo real para contar cuántos productos están en alerta
        long bajosOCriticos = allItems.stream()
                .filter(i -> i.getEstado() == EstadoProducto.BAJO || i.getEstado() == EstadoProducto.CRITICO)
                .count();

        kpiStock.setText(String.valueOf(total));
        kpiAlertas.setText(String.valueOf(bajosOCriticos));

        // Pedidos y proveedores (pendientes de implementar sus DAOs)
        kpiPedidos.setText("7");
        kpiProveedores.setText("12");
    }
}
