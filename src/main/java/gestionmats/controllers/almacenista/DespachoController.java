package gestionmats.controllers.almacenista;

import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import gestionmats.model.Venta;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DespachoController {

    @FXML private TextField txtIdPedido;
    @FXML private TableView<DetalleVenta> tablaMaterialesPedido;
    @FXML private TableColumn<DetalleVenta, String> colMaterial;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;

    private Venta ventaActual; // Guarda la venta encontrada para procesarla después

    @FXML
    public void initialize() {
        // Configuramos las columnas para que lean los atributos de DetalleVenta
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
    }

    @FXML
    private void handleBuscarPedido() {
        String idStr = txtIdPedido.getText().trim();
        if (idStr.isEmpty()) return;

        try {
            int idBusqueda = Integer.parseInt(idStr);
            VentaDaoCsv ventaDao = new VentaDaoCsv();
            Venta encontrado = null;

            // Búsqueda manual en la lista del CSV
            for (Venta v : ventaDao.listarTodos()) {
                if (v.getIdVenta() == idBusqueda) {
                    encontrado = v;
                    break;
                }
            }

            if (encontrado != null) {
                if ("SURTIDO".equalsIgnoreCase(encontrado.getEstado())) {
                    UIComponents.showNotification("Aviso: El pedido " + idBusqueda + " ya fue entregado anteriormente.", "error");
                    limpiarVista();
                } else {
                    this.ventaActual = encontrado;
                    cargarDetallesVenta(idBusqueda);
                }
            } else {
                UIComponents.showNotification("No se encontró ninguna venta con el ID: " + idBusqueda, "error");
                limpiarVista();
            }

        } catch (NumberFormatException e) {
            UIComponents.showNotification("Por favor, ingrese un ID numérico válido.", "error");
        }
    }

    private void cargarDetallesVenta(int idVenta) {
        DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
        List<DetalleVenta> lista = detalleDao.listarPorIdVenta(idVenta);

        ObservableList<DetalleVenta> details = FXCollections.observableArrayList(lista);
        tablaMaterialesPedido.setItems(details);
    }

    @FXML
    private void handleConfirmarEntrega() {

        if (ventaActual == null) {
            UIComponents.showNotification("Debe buscar un ticket válido primero.", "error");
            return;
        }

        ProductoDaoCsv productoDao = new ProductoDaoCsv();
        VentaDaoCsv ventaDao = new VentaDaoCsv();
        DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();

        // 1. Obtener lo que se vendió
        List<DetalleVenta> detalles = detalleDao.listarPorIdVenta(ventaActual.getIdVenta());

        // 2. Descontar del inventario (CSV Productos)
        for (DetalleVenta dv : detalles) {
            Producto p = productoDao.buscarPorId(dv.getIdProducto());
            if (p != null) {
                int stockNuevo = p.getStockActual() - dv.getCantidad();
                p.setStockActual(stockNuevo);
                productoDao.actualizar(p); // Reescribe la línea en el CSV
            }
        }


        // 3. Actualizar estado de la venta
        ventaActual.setEstado("SURTIDO");
        ventaDao.actualizar(ventaActual);

        UIComponents.showNotification("Despacho completado con éxito. Stock actualizado.", "success");
        limpiarVista();
    }

    private void limpiarVista() {
        this.ventaActual = null;
        tablaMaterialesPedido.getItems().clear();
        txtIdPedido.clear();
    }
}