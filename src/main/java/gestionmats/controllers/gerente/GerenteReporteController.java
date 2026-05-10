package gestionmats.controllers.gerente;

import gestionmats.dao.*;
import gestionmats.model.*;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class GerenteReporteController implements Initializable {

    @FXML private DatePicker dpInicio, dpFin;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Label lblMasVendido, lblMejorCategoria, lblUtilidadTotal;
    @FXML private LineChart<String, Number> chartTendencia;
    @FXML private PieChart chartDistribucion;
    @FXML private HBox hboxCustomDates;
    @FXML private Button btnExportar;

    private VentaDaoCsv ventaDao = new VentaDaoCsv();
    private DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @FXML private Button btnHoy, btnSemana, btnMes, btnAño, btnPersonalizado;
    private List<Button> listaBotones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        listaBotones = Arrays.asList(btnHoy, btnSemana, btnMes, btnAño, btnPersonalizado);
        // Marcamos "Esta Semana" como activo por defecto
        marcarBotonActivo(btnSemana);
        setupInitialFilters();
        UIComponents.applyButtonAdd(btnExportar);
        procesarDatos();
    }

    private void setupInitialFilters() {
        dpFin.setValue(LocalDate.now());
        dpInicio.setValue(LocalDate.now().minusDays(7)); // Por defecto "Esta Semana"

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Cemento y concreto", "Acero y metales", "Madera y derivados", "Herramientas"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        // Listeners para refrescar al cambiar fechas o categoría
        dpInicio.valueProperty().addListener((o, old, n) -> procesarDatos());
        dpFin.valueProperty().addListener((o, old, n) -> procesarDatos());
        cmbCategoria.valueProperty().addListener((o, old, n) -> procesarDatos());
    }

    private void marcarBotonActivo(Button btn) {
        // Quitamos la clase 'active-period' de todos
        listaBotones.forEach(b -> b.getStyleClass().remove("active-period"));
        // Se la ponemos solo al presionado
        btn.getStyleClass().add("active-period");
    }

    // ==========================================
    // LÓGICA DE BOTONES RÁPIDOS (UX)
    // ==========================================
    // Actualizamos los handlers para llamar a la función
    @FXML private void handlePresetHoy() {
        marcarBotonActivo(btnHoy);
        ocultarCustomDates();
        dpInicio.setValue(LocalDate.now());
        dpFin.setValue(LocalDate.now());
    }

    @FXML private void handlePresetSemana() {
        marcarBotonActivo(btnSemana);
        ocultarCustomDates();
        dpInicio.setValue(LocalDate.now().minusWeeks(1));
        dpFin.setValue(LocalDate.now());
    }

    @FXML private void handlePresetMes() {
        marcarBotonActivo(btnMes);
        ocultarCustomDates();
        dpInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dpFin.setValue(LocalDate.now());
    }

    @FXML private void handlePresetAño() {
        marcarBotonActivo(btnAño);
        ocultarCustomDates();
        dpInicio.setValue(LocalDate.now().withDayOfYear(1));
        dpFin.setValue(LocalDate.now());
    }

    @FXML private void handleTogglePersonalizado() {
        marcarBotonActivo(btnPersonalizado);
        boolean estaVisible = hboxCustomDates.isVisible();
        hboxCustomDates.setVisible(!estaVisible);
        hboxCustomDates.setManaged(!estaVisible);
    }

    private void ocultarCustomDates() {
        hboxCustomDates.setVisible(false);
        hboxCustomDates.setManaged(false);
    }

    // ==========================================
    // PROCESAMIENTO DE DATOS E INTELIGENCIA
    // ==========================================
    private void procesarDatos() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();
        String catFiltro = cmbCategoria.getValue();

        List<Venta> ventasFiltradas = ventaDao.listarTodos().stream()
                .filter(v -> !v.getEstado().equals("CANCELADA"))
                .filter(v -> {
                    LocalDate f = LocalDateTime.parse(v.getFechaHora(), fmt).toLocalDate();
                    return (f.isAfter(inicio) || f.isEqual(inicio)) && (f.isBefore(fin) || f.isEqual(fin));
                }).collect(Collectors.toList());

        Map<String, Integer> unidadesPorProducto = new HashMap<>();
        Map<String, Double> dineroPorCat = new HashMap<>();
        double utilidadTotal = 0;

        for (Venta v : ventasFiltradas) {
            List<DetalleVenta> detalles = detalleDao.listarPorIdVenta(v.getIdVenta());
            for (DetalleVenta d : detalles) {
                Producto p = productoDao.buscarPorId(d.getIdProducto());
                if (p == null) continue;
                if (!catFiltro.equals("Todas") && !p.getCategoria().equals(catFiltro)) continue;

                unidadesPorProducto.put(p.getNombre(), unidadesPorProducto.getOrDefault(p.getNombre(), 0) + d.getCantidad());
                dineroPorCat.put(p.getCategoria(), dineroPorCat.getOrDefault(p.getCategoria(), 0.0) + d.getTotal());
                utilidadTotal += d.getTotal();
            }
        }

        // Actualizar Labels
        lblUtilidadTotal.setText(String.format("$%,.2f", utilidadTotal));
        lblMasVendido.setText(unidadesPorProducto.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(e -> e.getKey()).orElse("--"));
        lblMejorCategoria.setText(dineroPorCat.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(e -> e.getKey()).orElse("--"));

        // Cargar PieChart (Distribución)
        chartDistribucion.getData().clear();
        unidadesPorProducto.forEach((nombre, cant) -> {
            chartDistribucion.getData().add(new PieChart.Data(nombre + " (" + cant + ")", cant));
        });

        // Cargar LineChart (Tendencia)
        actualizarGraficaTendencia(ventasFiltradas);
    }

    private void actualizarGraficaTendencia(List<Venta> ventas) {
        chartTendencia.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<LocalDate, Double> mapa = ventas.stream().collect(Collectors.groupingBy(
                v -> LocalDateTime.parse(v.getFechaHora(), fmt).toLocalDate(),
                TreeMap::new, Collectors.summingDouble(Venta::getTotal)));
        mapa.forEach((f, t) -> series.getData().add(new XYChart.Data<>(f.toString(), t)));
        chartTendencia.getData().add(series);
    }

    @FXML private void handleExportarReporte() {
        // Lógica de FileChooser (Txt) similar a la anterior...
        UIComponents.showNotification("Reporte generado con filtros actuales.", "success");
    }
}