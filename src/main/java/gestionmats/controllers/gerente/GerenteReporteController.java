package gestionmats.controllers.gerente;

import gestionmats.dao.*;
import gestionmats.model.*;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
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
    @FXML private Button btnGenerarTxt;

    private VentaDaoCsv ventaDao = new VentaDaoCsv();
    private DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar filtros: por defecto los últimos 30 días
        dpFin.setValue(LocalDate.now());
        dpInicio.setValue(LocalDate.now().minusDays(30));

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Cemento y concreto", "Acero y metales", "Madera y derivados", "Herramientas"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        UIComponents.applyButtonAdd(btnGenerarTxt);

        // Listeners para actualizar todo cuando cambie un filtro
        dpInicio.valueProperty().addListener((o, old, n) -> procesarDatos());
        dpFin.valueProperty().addListener((o, old, n) -> procesarDatos());
        cmbCategoria.valueProperty().addListener((o, old, n) -> procesarDatos());

        procesarDatos();
    }

    private void procesarDatos() {
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();
        String catFiltro = cmbCategoria.getValue();

        List<Venta> ventasFiltradas = ventaDao.listarTodos().stream()
                .filter(v -> !v.getEstado().equals("CANCELADA"))
                .filter(v -> {
                    LocalDate fechaV = LocalDateTime.parse(v.getFechaHora(), fmt).toLocalDate();
                    return (fechaV.isAfter(inicio) || fechaV.isEqual(inicio)) &&
                            (fechaV.isBefore(fin) || fechaV.isEqual(fin));
                })
                .collect(Collectors.toList());

        calcularMetricas(ventasFiltradas, catFiltro);
        actualizarGrafica(ventasFiltradas);
    }

    private void calcularMetricas(List<Venta> ventas, String catFiltro) {
        Map<String, Integer> conteoProductos = new HashMap<>();
        Map<String, Double> ingresosPorCat = new HashMap<>();
        double totalGlobal = 0;

        for (Venta v : ventas) {
            List<DetalleVenta> detalles = detalleDao.listarPorIdVenta(v.getIdVenta());
            for (DetalleVenta det : detalles) {
                Producto p = productoDao.buscarPorId(det.getIdProducto());
                if (p == null) continue;

                // Filtro de categoría
                if (!catFiltro.equals("Todas") && !p.getCategoria().equals(catFiltro)) continue;

                conteoProductos.put(p.getNombre(), conteoProductos.getOrDefault(p.getNombre(), 0) + det.getCantidad());
                ingresosPorCat.put(p.getCategoria(), ingresosPorCat.getOrDefault(p.getCategoria(), 0.0) + det.getTotal());
                totalGlobal += det.getTotal();
            }
        }

        // Obtener el más vendido
        String masVendido = conteoProductos.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + " uds)")
                .orElse("N/A");

        // Obtener categoría líder
        String mejorCat = ingresosPorCat.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        lblMasVendido.setText(masVendido);
        lblMejorCategoria.setText(mejorCat);
        lblUtilidadTotal.setText(String.format("$%,.2f", totalGlobal));
    }

    private void actualizarGrafica(List<Venta> ventas) {
        chartTendencia.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Agrupar ventas por fecha para la línea de tendencia
        Map<LocalDate, Double> ventasPorFecha = ventas.stream()
                .collect(Collectors.groupingBy(
                        v -> LocalDateTime.parse(v.getFechaHora(), fmt).toLocalDate(),
                        TreeMap::new,
                        Collectors.summingDouble(Venta::getTotal)
                ));

        ventasPorFecha.forEach((fecha, total) -> {
            series.getData().add(new XYChart.Data<>(fecha.toString(), total));
        });

        chartTendencia.getData().add(series);
    }

    @FXML private void handleExportarReporte() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Ventas");
        fileChooser.setInitialFileName("Reporte_Gerencia_" + LocalDate.now() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto Plano (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(btnGenerarTxt.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("==================================================");
                writer.println("   REPORTE DE INTELIGENCIA DE NEGOCIO - SDG_MDC   ");
                writer.println("==================================================");
                writer.println("Periodo: " + dpInicio.getValue() + " al " + dpFin.getValue());
                writer.println("Categoría filtrada: " + cmbCategoria.getValue());
                writer.println("Generado el: " + LocalDateTime.now());
                writer.println("--------------------------------------------------");
                writer.println("RESUMEN GENERAL:");
                writer.println("- Ingresos Totales: " + lblUtilidadTotal.getText());
                writer.println("- Producto Estrella: " + lblMasVendido.getText());
                writer.println("- Categoría Dominante: " + lblMejorCategoria.getText());
                writer.println("--------------------------------------------------");
                writer.println("Este reporte es de uso exclusivo para la Gerencia.");
                UIComponents.showNotification("Reporte guardado con éxito.", "success");
            } catch (Exception e) {
                UIComponents.showNotification("Error al guardar el archivo.", "error");
            }
        }
    }
}
