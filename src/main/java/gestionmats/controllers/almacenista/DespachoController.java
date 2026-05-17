package gestionmats.controllers.almacenista;

import gestionmats.dao.VentaDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.Venta;
import gestionmats.model.DetalleVenta;
import gestionmats.model.Producto;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DespachoController implements Initializable {

    @FXML private TextField txtBusqueda;
    @FXML private TableView<Venta> tablaDespacho;
    @FXML private TableColumn<Venta, String> colTicket;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, String> colDetalles;
    @FXML private TableColumn<Venta, Void> colAccion;

    private ObservableList<Venta> todasLasVentasPendientes = FXCollections.observableArrayList();
    private ObservableList<Venta> ventasFiltradas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Configurar columna del ID de la Venta
        colTicket.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getIdVenta())));

        // CORRECCIÓN: Mostrar el ID del cliente asociado que viene en el modelo Venta
        colCliente.setCellValueFactory(cellData ->
                new SimpleStringProperty("ID Cliente: " + cellData.getValue().getIdCliente()));

        // CORRECCIÓN: Mapear la lista de Detalles a un string legible ("2x Cemento, 5x Varilla")
        colDetalles.setCellValueFactory(cellData -> {
            List<DetalleVenta> items = cellData.getValue().getDetalles();
            if (items == null || items.isEmpty()) {
                return new SimpleStringProperty("Sin productos asignados");
            }
            String resumen = items.stream()
                    .map(d -> d.getCantidad() + "x " + d.getNombreProducto()) // <-- CAMBIADO AQUÍ
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(resumen);
        });;

        // 2. Cargar los datos de los CSVs
        cargarVentasPendientes();

        // 3. Insertar botones de "✔ Entregar" en cada fila
        agregarBotonDespacho();

        // 4. Configurar el filtro en tiempo real al escribir en la barra de búsqueda
        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarVentas(newValue);
        });
    }

    private void cargarVentasPendientes() {
        VentaDaoCsv ventaDao = new VentaDaoCsv();
        // NECESITAS ESTE DAO para ir a buscar los renglones de cada ticket
        gestionmats.dao.DetalleVentaDaoCsv detalleDao = new gestionmats.dao.DetalleVentaDaoCsv();

        List<Venta> pendientes = ventaDao.listarTodos().stream()
                .filter(v -> !"SURTIDO".equals(v.getEstado()))
                .collect(Collectors.toList());

        // ¡EL PASO CLAVE!: Por cada venta, buscamos sus renglones y se los asignamos
        for (Venta v : pendientes) {
            List<DetalleVenta> susDetalles = detalleDao.listarTodos().stream()
                    .filter(d -> d.getIdVenta() == v.getIdVenta())
                    .collect(Collectors.toList());
            v.setDetalles(susDetalles);
        }

        todasLasVentasPendientes.setAll(pendientes);
        ventasFiltradas.setAll(todasLasVentasPendientes);
        tablaDespacho.setItems(ventasFiltradas);
    }

    private void filtrarVentas(String texto) {
        if (texto == null || texto.isEmpty()) {
            ventasFiltradas.setAll(todasLasVentasPendientes);
        } else {
            String query = texto.toLowerCase().trim();
            ventasFiltradas.setAll(
                    todasLasVentasPendientes.stream()
                            .filter(v -> String.valueOf(v.getIdVenta()).contains(query) ||
                                    String.valueOf(v.getIdCliente()).contains(query))
                            .collect(Collectors.toList())
            );
        }
    }

    private void agregarBotonDespacho() {
        Callback<TableColumn<Venta, Void>, TableCell<Venta, Void>> cellFactory = new Callback<> () {
            @Override
            public TableCell<Venta, Void> call(final TableColumn<Venta, Void> param) {
                return new TableCell<>() {
                    private final Button btnConfirmar = new Button("Entregar");

                    {
                        btnConfirmar.getStyleClass().add("btn-table-action");
                        btnConfirmar.setOnAction(event -> {
                            Venta ventaSeleccionada = getTableView().getItems().get(getIndex());
                            procesarDespacho(ventaSeleccionada);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnConfirmar);
                        }
                    }
                };
            }
        };
        colAccion.setCellFactory(cellFactory);
    }

    private void procesarDespacho(Venta venta) {
        ProductoDaoCsv productoDao = new ProductoDaoCsv();
        VentaDaoCsv ventaDao = new VentaDaoCsv();

        // RF-07: Descontar stock físico del inventario
        for (DetalleVenta item : venta.getDetalles()) {
            // CORRECCIÓN: item.getIdProducto() nos da el String directo (ej: "M001")
            Producto p = productoDao.buscarPorId(item.getIdProducto()); // <-- CAMBIADO AQUÍ
            if (p != null) {
                int nuevoStock = p.getStockActual() - item.getCantidad();
                p.setStockActual(Math.max(0, nuevoStock));
                productoDao.actualizar(p);
            }
        }

        // Cambiamos el estado de la venta
        venta.setEstado("SURTIDO");
        ventaDao.actualizar(venta);

        // Reactividad en la UI
        todasLasVentasPendientes.remove(venta);
        ventasFiltradas.remove(venta);

        System.out.println("Venta #" + venta.getIdVenta() + " despachada y removida de la vista.");
    }
}