package gestionmats.controllers.gerente;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.ClienteDaoCsv;
import gestionmats.dao.ProveedorDaoCsv;
import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.dao.UsuarioDaoCsv;
import gestionmats.model.Producto;
import gestionmats.model.EstadoProducto;
import gestionmats.model.OrdenCompra;
import gestionmats.model.Venta;
import gestionmats.model.Usuario;
import gestionmats.model.RolUsuario;
import gestionmats.utils.UIComponents;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class GerenteDashboardController implements Initializable {

    @FXML private Label lblTotalProductos, lblAlertas, lblVentasHoy, lblClientes, lblProveedores, lblPedidos;
    @FXML private Label lblVendedores, lblAlmacenistas, lblTotalEmpleados;
    @FXML private VBox boxProductos, boxAlertas, boxVentas, boxClientes, boxProveedores, boxPedidos;
    @FXML private VBox boxVendedores, boxAlmacenistas, boxEmpleadosTotal;

    @FXML private PieChart chartInventario;
    @FXML private BarChart<String, Number> chartVentas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosReales();
        setupAnimations();
    }

    private void setupAnimations() {
        // Efectos de hover para TODAS las 9 tarjetas
        UIComponents.applyHoverScale(boxProductos, 1.03);
        UIComponents.applyHoverScale(boxAlertas, 1.03);
        UIComponents.applyHoverScale(boxVentas, 1.03);
        UIComponents.applyHoverScale(boxClientes, 1.03);
        UIComponents.applyHoverScale(boxProveedores, 1.03);
        UIComponents.applyHoverScale(boxPedidos, 1.03);
        UIComponents.applyHoverScale(boxVendedores, 1.03);
        UIComponents.applyHoverScale(boxAlmacenistas, 1.03);
        UIComponents.applyHoverScale(boxEmpleadosTotal, 1.03);
    }

    private void cargarDatosReales() {
        ProductoDaoCsv productoDao = new ProductoDaoCsv();
        ClienteDaoCsv clienteDao = new ClienteDaoCsv();
        ProveedorDaoCsv proveedorDao = new ProveedorDaoCsv();
        OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();
        UsuarioDaoCsv usuarioDao = new UsuarioDaoCsv();
        VentaDaoCsv ventaDao = new VentaDaoCsv();

        // --- PRODUCTOS ---
        List<Producto> productos = productoDao.listarTodos();
        lblTotalProductos.setText(String.valueOf(productos.size()));

        long alertas = productos.stream()
                .filter(p -> p.getEstado() == EstadoProducto.BAJO || p.getEstado() == EstadoProducto.CRITICO)
                .count();
        lblAlertas.setText(String.valueOf(alertas));

        // --- CLIENTES (Omitiendo al Cliente 0) ---
        int totalClientes = clienteDao.listarTodos().size();
        lblClientes.setText(String.valueOf(Math.max(0, totalClientes - 1)));

        // --- PROVEEDORES ---
        lblProveedores.setText(String.valueOf(proveedorDao.listarTodos().size()));

        // --- PEDIDOS PENDIENTES ---
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

        // --- EMPLEADOS POR ROL ---
        long totalVendedores = 0;
        long totalAlmacenistas = 0;
        List<Usuario> listaUsuarios = usuarioDao.listarTodos();

        for (Usuario u : listaUsuarios) {
            if (u.getRol() == RolUsuario.VENDEDOR) totalVendedores++;
            if (u.getRol() == RolUsuario.ALMACENISTA) totalAlmacenistas++;
        }

        lblVendedores.setText(String.valueOf(totalVendedores));
        lblAlmacenistas.setText(String.valueOf(totalAlmacenistas));
        lblTotalEmpleados.setText(String.valueOf(listaUsuarios.size()));

        // --- VENTAS REALES Y GRÁFICA ---
        List<Venta> todasLasVentas = ventaDao.listarTodos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate hoy = LocalDate.now();

        double ingresosHoy = 0.0;
        double[] ventasPorDia = new double[7]; // Índice 0=Lunes, 6=Domingo

        for (Venta v : todasLasVentas) {
            if (v.getEstado().equals("CANCELADA")) continue;

            try {
                LocalDateTime fechaVenta = LocalDateTime.parse(v.getFechaHora(), formatter);

                // Ventas de hoy
                if (fechaVenta.toLocalDate().equals(hoy)) {
                    ingresosHoy += v.getTotal();
                }

                // Gráfica de 7 días
                if (fechaVenta.toLocalDate().isAfter(hoy.minusDays(7)) || fechaVenta.toLocalDate().equals(hoy)) {
                    int diaSemana = fechaVenta.getDayOfWeek().getValue() - 1;
                    ventasPorDia[diaSemana] += v.getTotal();
                }
            } catch (Exception e) {
                System.err.println("Error al parsear fecha de venta: " + v.getFechaHora());
            }
        }

        lblVentasHoy.setText(String.format("$%,.2f", ingresosHoy));

        chartVentas.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos ($)");

        String[] nombresDias = {"Lun", "Mar", "Mie", "Jue", "Vie", "Sáb", "Dom"};
        for (int i = 0; i < 7; i++) {
            series.getData().add(new XYChart.Data<>(nombresDias[i], ventasPorDia[i]));
        }
        chartVentas.getData().add(series);
    }
}