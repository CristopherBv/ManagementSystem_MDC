package gestionmats.controllers;

import gestionmats.utils.UIComponents;
import gestionmats.utils.NavigationTools;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
 * KPIs, reloj en tiempo real y tabla de inventario.
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

    // ── INVENTARIO ─────────────────────────────────────────
    @FXML private TextField  txtSearch;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private TableView<MaterialItem> tableInventario;
    @FXML private TableColumn<MaterialItem, String>  colId;
    @FXML private TableColumn<MaterialItem, String>  colNombre;
    @FXML private TableColumn<MaterialItem, String>  colCategoria;
    @FXML private TableColumn<MaterialItem, Integer> colCantidad;
    @FXML private TableColumn<MaterialItem, String>  colUnidad;
    @FXML private TableColumn<MaterialItem, Integer> colStock;
    @FXML private TableColumn<MaterialItem, String>  colEstado;
    @FXML private Label lblTotal;
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnExportar;
    @FXML private Button btnActualizar;

    // ── ESTADO INTERNO ─────────────────────────────────────
    private ObservableList<MaterialItem> allItems;
    private FilteredList<MaterialItem>   filteredItems;
    private Timeline clockTimeline;

    /** Navegación activa actual */
    private Button activeNavBtn;

    // ══════════════════════════════════════════════════════
    // INITIALIZE
    // ══════════════════════════════════════════════════════
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupClock();
        setupNavButtons();
        setupTableColumns();
        setupSearchFilter();
        setupToolbarButtons();
        loadDemoData();
        updateKPIs();

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
            // Capitalizar primera letra
            lblFecha.setText(Character.toUpperCase(fecha.charAt(0)) + fecha.substring(1));
            lblHora.setText(now.format(fmtHora));
        };

        tick.run(); // ejecutar inmediatamente
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(30), e -> tick.run()));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    // ══════════════════════════════════════════════════════
    // NAVEGACIÓN LATERAL
    // ══════════════════════════════════════════════════════
    private void setupNavButtons() {
        // Aplicar hover animado a todos los botones nav
        UIComponents.applyNavHover(navInventario);
        UIComponents.applyNavHover(navPedidos);
        UIComponents.applyNavHover(navProveedores);
        UIComponents.applyNavHover(navReportes);
        UIComponents.applyNavHover(navConfig);
        UIComponents.applyButtonPulse(btnLogout);
    }

    /** Cambia la vista activa y actualiza el estado del botón nav */
    private void switchView(Button navBtn, VBox view, String title, String subtitle) {
        // Quitar active del anterior
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().remove("nav-btn-active");
        }
        navBtn.getStyleClass().add("nav-btn-active");
        activeNavBtn = navBtn;

        // Ocultar todas las vistas
        viewInventario.setVisible(false);  viewInventario.setManaged(false);
        viewPedidos.setVisible(false);     viewPedidos.setManaged(false);
        viewProveedores.setVisible(false); viewProveedores.setManaged(false);
        viewReportes.setVisible(false);    viewReportes.setManaged(false);
        viewConfig.setVisible(false);      viewConfig.setManaged(false);

        // Mostrar la seleccionada con fade-in
        view.setVisible(true);
        view.setManaged(true);
        UIComponents.fadeIn(view, 200);

        lblPageTitle.setText(title);
        lblPageSub.setText(subtitle);
    }

    @FXML private void showInventario() {
        switchView(navInventario, viewInventario,
                "Inventario", "Control y seguimiento de materiales");
    }

    @FXML private void showPedidos() {
        switchView(navPedidos, viewPedidos,
                "Pedidos", "Gestión de pedidos activos");
    }

    @FXML private void showProveedores() {
        switchView(navProveedores, viewProveedores,
                "Proveedores", "Directorio de proveedores");
    }

    @FXML private void showReportes() {
        switchView(navReportes, viewReportes,
                "Reportes", "Informes y estadísticas");
    }

    @FXML private void showConfig() {
        switchView(navConfig, viewConfig,
                "Configuración", "Parámetros del sistema");
    }

    // ══════════════════════════════════════════════════════
    // TABLA DE INVENTARIO
    // ══════════════════════════════════════════════════════
    private void setupTableColumns() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));
        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCategoria()));
        colCantidad.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCantidad()).asObject());
        colUnidad.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getUnidad()));
        colStock.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStockMin()).asObject());

        // Columna Estado con colores
        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEstado()));
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
                    case "OK"      -> setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;");
                    case "BAJO"    -> setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;");
                    case "CRÍTICO" -> setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");
                    default        -> setStyle("-fx-text-fill: #9ca3af;");
                }
            }
        });

        // Categorías para el ComboBox
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

        filteredItems.addListener((javafx.collections.ListChangeListener<MaterialItem>) c ->
                lblTotal.setText("Total: " + filteredItems.size() + " registros"));
    }

    private void applyFilters() {
        String search  = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String cat     = cmbCategoria.getValue();

        filteredItems.setPredicate(item -> {
            boolean matchSearch = search.isEmpty()
                    || item.getNombre().toLowerCase().contains(search)
                    || item.getId().toLowerCase().contains(search);
            boolean matchCat = cat == null || cat.equals("Todas")
                    || item.getCategoria().equals(cat);
            return matchSearch && matchCat;
        });
    }

    // ══════════════════════════════════════════════════════
    // BOTONES DE ACCIÓN
    // ══════════════════════════════════════════════════════
    private void setupToolbarButtons() {
        UIComponents.applyButtonAdd(btnAgregar);
        UIComponents.applyButtonEdit(btnEditar);
        UIComponents.applyButtonDelete(btnEliminar);
        UIComponents.applyButtonGhost(btnExportar);
        UIComponents.applyButtonGhost(btnActualizar);
    }

    @FXML private void handleAgregar() {
        UIComponents.showNotification("Funcionalidad: Agregar material", "info");
        // TODO: Abrir diálogo de creación
    }

    @FXML private void handleEditar() {
        MaterialItem selected = tableInventario.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponents.showNotification("Seleccione un material para editar.", "warn");
            return;
        }
        UIComponents.showNotification("Editando: " + selected.getNombre(), "info");
        // TODO: Abrir diálogo de edición con datos del item
    }

    @FXML private void handleEliminar() {
        MaterialItem selected = tableInventario.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponents.showNotification("Seleccione un material para eliminar.", "warn");
            return;
        }
        boolean confirmed = UIComponents.showConfirmDialog(
                "Eliminar material",
                "¿Está seguro de eliminar \"" + selected.getNombre() + "\"?\nEsta acción no se puede deshacer."
        );
        if (confirmed) {
            allItems.remove(selected);
            updateKPIs();
            UIComponents.showNotification("Material eliminado correctamente.", "success");
        }
    }

    @FXML private void handleExportar() {
        UIComponents.showNotification("Exportando inventario...", "info");
        // TODO: Implementar exportación CSV/Excel
    }

    @FXML private void handleActualizar() {
        loadDemoData();
        updateKPIs();
        UIComponents.showNotification("Datos actualizados.", "success");
    }

    @FXML private void handleLogout() {
        // Llamamos a nuestro método universal pasándole el botón y el reloj
        NavigationTools.manejarLogout(btnLogout, clockTimeline);
    }

    // ══════════════════════════════════════════════════════
    // DATOS Y KPIs
    // ══════════════════════════════════════════════════════
    private void loadDemoData() {
        allItems.setAll(
            new MaterialItem("M-001", "Cemento Portland",      "Cemento y concreto",  240, "Sacos",   50,  "OK"),
            new MaterialItem("M-002", "Varilla 3/8\" Grado 40","Acero y metales",     180, "Barras",  40,  "OK"),
            new MaterialItem("M-003", "Block de 15x20x40",     "Cemento y concreto",   80, "Piezas",  100, "BAJO"),
            new MaterialItem("M-004", "Madera pino 2x4",       "Madera y derivados",   12, "Tablas",  20,  "CRÍTICO"),
            new MaterialItem("M-005", "Impermeabilizante 19L", "Impermeabilizantes",   35, "Cubetas", 15,  "OK"),
            new MaterialItem("M-006", "Tubo PVC 4\" hidráulico","Tubería y plomería",  60, "Piezas",  20,  "OK"),
            new MaterialItem("M-007", "Arena fina (m³)",        "Cemento y concreto",   8, "m³",      10,  "BAJO"),
            new MaterialItem("M-008", "Grava 3/4\"",            "Cemento y concreto",  15, "m³",      10,  "OK")
        );
    }

    private void updateKPIs() {
        int total    = allItems.size();
        long bajos   = allItems.stream().filter(i -> !i.getEstado().equals("OK")).count();

        kpiStock.setText(String.valueOf(total));
        kpiAlertas.setText(String.valueOf(bajos));
        // Pedidos y proveedores serían de BD — poner valores de ejemplo:
        kpiPedidos.setText("7");
        kpiProveedores.setText("12");
    }

    // ══════════════════════════════════════════════════════
    // MODELO INTERNO
    // ══════════════════════════════════════════════════════
    public static class MaterialItem {
        private final String  id, nombre, categoria, unidad, estado;
        private final int     cantidad, stockMin;

        public MaterialItem(String id, String nombre, String categoria,
                            int cantidad, String unidad, int stockMin, String estado) {
            this.id = id; this.nombre = nombre; this.categoria = categoria;
            this.cantidad = cantidad; this.unidad = unidad;
            this.stockMin = stockMin; this.estado = estado;
        }
        public String  getId()        { return id; }
        public String  getNombre()    { return nombre; }
        public String  getCategoria() { return categoria; }
        public int     getCantidad()  { return cantidad; }
        public String  getUnidad()    { return unidad; }
        public int     getStockMin()  { return stockMin; }
        public String  getEstado()    { return estado; }
    }
}
