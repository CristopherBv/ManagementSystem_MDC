package gestionmats.controllers.almacenista;

import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.DetalleOrden;
import gestionmats.model.OrdenCompra;
import gestionmats.model.Producto;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import java.net.URL;
import java.util.ResourceBundle;

public class RecepcionController implements Initializable {

    @FXML private TextField txtIdOrden;
    @FXML private TableView<DetalleOrden> tablaRecepcion;
    @FXML private TableColumn<DetalleOrden, String> colProducto;
    @FXML private TableColumn<DetalleOrden, Integer> colEsperado;
    @FXML private TableColumn<DetalleOrden, Integer> colRecibido;

    private OrdenCompra ordenActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración de columnas
        colProducto.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto().getNombre()));

        colEsperado.setCellValueFactory(new PropertyValueFactory<>("cantidadEsperada"));

        colRecibido.setCellValueFactory(new PropertyValueFactory<>("cantidadRecibida"));
        colRecibido.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        colRecibido.setOnEditCommit(event -> {
            event.getRowValue().setCantidadRecibida(event.getNewValue());
        });

        tablaRecepcion.setEditable(true);
    }

    @FXML
    private void handleBuscarOrden() {
        String idStr = txtIdOrden.getText().trim();
        if (idStr.isEmpty()) return;

        OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();
        for (OrdenCompra oc : ordenDao.listarTodos()) {
            if (String.valueOf(oc.getIdOrden()).equals(idStr)) {
                this.ordenActual = oc;
                // Cargamos los detalles de la orden a la tabla
                ObservableList<DetalleOrden> items = FXCollections.observableArrayList(oc.getDetalles());
                tablaRecepcion.setItems(items);
                return;
            }
        }
        UIComponents.showNotification("No se encontró la Orden de Compra: " + idStr, "error");
    }

    @FXML
    private void handleRegistrarEntrada() {
        if (ordenActual == null || tablaRecepcion.getItems().isEmpty()) return;

        ProductoDaoCsv productoDao = new ProductoDaoCsv();
        OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();
        boolean hayFaltantes = false;

        for (DetalleOrden item : tablaRecepcion.getItems()) {
            Producto p = productoDao.buscarPorId(item.getProducto().getIdProducto());
            if (p != null) {
                // Sumar stock
                int nuevoStock = p.getStockActual() + item.getCantidadRecibida();
                p.setStockActual(nuevoStock);
                productoDao.actualizar(p);
            }

            if (item.getCantidadRecibida() < item.getCantidadEsperada()) {
                hayFaltantes = true;
            }
        }

        // Notificar discrepancia vía estado
        ordenActual.setEstado(hayFaltantes ? "RECIBIDO_CON_FALTANTES" : "RECIBIDO_COMPLETO");
        ordenDao.actualizar(ordenActual);

        UIComponents.showNotification("Entrada registrada. Estado: " + ordenActual.getEstado(), "success");
        limpiar();
    }

    private void limpiar() {
        tablaRecepcion.getItems().clear();
        txtIdOrden.clear();
        ordenActual = null;
    }
}