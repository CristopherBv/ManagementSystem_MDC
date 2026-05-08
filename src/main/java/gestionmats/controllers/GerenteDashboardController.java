package gestionmats.controllers;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.Producto;
import gestionmats.model.EstadoProducto;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GerenteDashboardController implements Initializable {

    @FXML private Label lblTotalProductos, lblAlertas, lblVentasHoy, lblClientes, lblProveedores, lblPedidos;
    @FXML private PieChart chartInventario;
    @FXML private BarChart<String, Number> chartVentas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosReales();
        cargarDatosSimulados();
    }

    private void cargarDatosReales() {
        ProductoDaoCsv dao = new ProductoDaoCsv();
        List<Producto> productos = dao.listarTodos();

        lblTotalProductos.setText(String.valueOf(productos.size()));

        long alertas = productos.stream()
                .filter(p -> p.getEstado() == EstadoProducto.BAJO || p.getEstado() == EstadoProducto.CRITICO)
                .count();
        lblAlertas.setText(String.valueOf(alertas));

        // Gráfica de pastel mejorada
        long ok = productos.stream()
                .filter(p -> p.getEstado() == EstadoProducto.OK || p.getEstado() == EstadoProducto.LLENO)
                .count();

        chartInventario.getData().add(new PieChart.Data("Stock Sano", ok));
        chartInventario.getData().add(new PieChart.Data("Requiere Resurtido", alertas));
    }

    private void cargarDatosSimulados() {
        // TODO: Conectar con ClienteDaoCsv
        lblClientes.setText("48");

        // TODO: Conectar con ProveedorDaoCsv
        lblProveedores.setText("12");

        // TODO: Conectar con OrdenCompraDao
        lblPedidos.setText("5");

        // TODO: Implementar lógica de ingresos diarios desde VentaDaoCsv
        lblVentasHoy.setText("$14,250.00");

        // Simulación de gráfica de barras
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Lun", 12000));
        series.getData().add(new XYChart.Data<>("Mar", 15000));
        series.getData().add(new XYChart.Data<>("Mie", 11000));
        series.getData().add(new XYChart.Data<>("Jue", 18000));
        series.getData().add(new XYChart.Data<>("Vie", 14250));
        chartVentas.getData().add(series);
    }
}