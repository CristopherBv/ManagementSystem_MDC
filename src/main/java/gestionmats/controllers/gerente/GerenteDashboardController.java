package gestionmats.controllers.gerente;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.ClienteDaoCsv;
import gestionmats.dao.ProveedorDaoCsv;
import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.model.Producto;
import gestionmats.model.EstadoProducto;
import gestionmats.model.OrdenCompra;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import gestionmats.utils.UIComponents;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GerenteDashboardController implements Initializable {

    @FXML private Label lblTotalProductos, lblAlertas, lblVentasHoy, lblClientes, lblProveedores, lblPedidos;
    @FXML private VBox boxProductos, boxAlertas, boxVentas, boxClientes, boxProveedores, boxPedidos;
    @FXML private PieChart chartInventario;
    @FXML private BarChart<String, Number> chartVentas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosReales();
        cargarDatosSimulados();
        setupAnimations();
    }

    private void setupAnimations() {
        // Aplicamos el efecto de escala que configuramos en UIComponents
        UIComponents.applyHoverScale(boxProductos, 1.03);
        UIComponents.applyHoverScale(boxAlertas, 1.03);
        UIComponents.applyHoverScale(boxVentas, 1.03);
        UIComponents.applyHoverScale(boxClientes, 1.03);
        UIComponents.applyHoverScale(boxProveedores, 1.03);
        UIComponents.applyHoverScale(boxPedidos, 1.03);
    }

    private void cargarDatosReales() {
        // 1. Instanciamos todos los DAOs necesarios
        ProductoDaoCsv productoDao = new ProductoDaoCsv();
        ClienteDaoCsv clienteDao = new ClienteDaoCsv();
        ProveedorDaoCsv proveedorDao = new ProveedorDaoCsv();
        OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();

        // --- SECCIÓN PRODUCTOS ---
        List<Producto> productos = productoDao.listarTodos();
        lblTotalProductos.setText(String.valueOf(productos.size()));

        long alertas = productos.stream()
                .filter(p -> p.getEstado() == EstadoProducto.BAJO || p.getEstado() == EstadoProducto.CRITICO)
                .count();
        lblAlertas.setText(String.valueOf(alertas));

        // --- SECCIÓN CLIENTES ---
        // Conectado con el DAO que me pasaste
        int totalClientes = clienteDao.listarTodos().size();
        lblClientes.setText(String.valueOf(totalClientes));

        // --- SECCIÓN PROVEEDORES ---
        int totalProveedores = proveedorDao.listarTodos().size();
        lblProveedores.setText(String.valueOf(totalProveedores));

        // --- SECCIÓN PEDIDOS PENDIENTES ---
        // Filtramos para contar solo los que no han sido surtidos ni cancelados
        long pedidosPendientes = ordenDao.listarTodos().stream()
                .filter(o -> o.getEstado().equals("EMITIDA") || o.getEstado().equals("INCOMPLETA"))
                .count();
        lblPedidos.setText(String.valueOf(pedidosPendientes));

        // --- GRÁFICA DE INVENTARIO ---
        chartInventario.getData().clear();
        long ok = productos.stream()
                .filter(p -> p.getEstado() == EstadoProducto.OK || p.getEstado() == EstadoProducto.LLENO)
                .count();

        chartInventario.getData().add(new PieChart.Data("Stock Sano", ok));
        chartInventario.getData().add(new PieChart.Data("Requiere Resurtido", alertas));
    }

    private void cargarDatosSimulados() {
        // TODO: Implementar lógica de ingresos diarios reales desde VentaDaoCsv cuando el equipo de Ventas termine
        lblVentasHoy.setText("$14,250.00");

        // Simulación de gráfica de barras (Ventas de la semana)
        chartVentas.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Lun", 12000));
        series.getData().add(new XYChart.Data<>("Mar", 15000));
        series.getData().add(new XYChart.Data<>("Mie", 11000));
        series.getData().add(new XYChart.Data<>("Jue", 18000));
        series.getData().add(new XYChart.Data<>("Vie", 14250));
        chartVentas.getData().add(series);
    }
}