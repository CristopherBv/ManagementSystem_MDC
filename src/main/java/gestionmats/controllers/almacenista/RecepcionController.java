package gestionmats.controllers.almacenista;

import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.OrdenCompra;
import gestionmats.model.DetalleOrden;
import gestionmats.model.Producto;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RecepcionController implements Initializable {

    @FXML private TextField txtBusqueda;
    @FXML private TableView<OrdenCompra> tablaRecepcion;
    @FXML private TableColumn<OrdenCompra, String> colOrden;
    @FXML private TableColumn<OrdenCompra, String> colProveedor;
    @FXML private TableColumn<OrdenCompra, String> colMateriales;
    @FXML private TableColumn<OrdenCompra, Void> colAccion;

    private ObservableList<OrdenCompra> todasLasOrdenesPendientes = FXCollections.observableArrayList();
    private ObservableList<OrdenCompra> ordenesFiltradas = FXCollections.observableArrayList();

    // Este mapa nos servirá para vincular cada DetalleOrden con su cuadro de texto en la pantalla
    private final Map<DetalleOrden, TextField> camposCantidadRecibida = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colOrden.setCellValueFactory(cellData ->
                new SimpleStringProperty("OC-" + cellData.getValue().getIdOrden()));

        colProveedor.setCellValueFactory(cellData ->
                new SimpleStringProperty("Proveedor ID: " + cellData.getValue().getProveedor().getIdProveedor()));

        cargarOrdenesPendientes();
        configurarColumnaMateriales();
        agregarBotonRecepcion();

        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarOrdenes(newValue);
        });
    }

    private void cargarOrdenesPendientes() {
        OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();
        todasLasOrdenesPendientes.setAll(
                ordenDao.listarTodos().stream()
                        .filter(o -> o.getEstado() != null && "EMITIDA".equalsIgnoreCase(o.getEstado().trim()))
                        .collect(Collectors.toList())
        );
        ordenesFiltradas.setAll(todasLasOrdenesPendientes);
        tablaRecepcion.setItems(ordenesFiltradas);
    }

    private void filtrarOrdenes(String texto) {
        if (texto == null || texto.isEmpty()) {
            ordenesFiltradas.setAll(todasLasOrdenesPendientes);
        } else {
            String query = texto.toLowerCase().trim();
            ordenesFiltradas.setAll(
                    todasLasOrdenesPendientes.stream()
                            .filter(o -> String.valueOf(o.getIdOrden()).contains(query) ||
                                    String.valueOf(o.getProveedor().getIdProveedor()).contains(query))
                            .collect(Collectors.toList())
            );
        }
    }

    private void configurarColumnaMateriales() {
        colMateriales.setCellValueFactory(cellData -> new SimpleStringProperty(""));
        colMateriales.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OrdenCompra, String> call(TableColumn<OrdenCompra, String> param) {
                return new TableCell<>() {
                    private final VBox contenedorFilas = new VBox(8); // Contenedor vertical con espacio

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getItems().get(getIndex()).getDetalles() == null) {
                            setGraphic(null);
                        } else {
                            OrdenCompra ordenFila = getTableView().getItems().get(getIndex());
                            contenedorFilas.getChildren().clear();

                            // Generamos un renglón interactivo por cada material de la orden
                            for (DetalleOrden d : ordenFila.getDetalles()) {
                                HBox filaMaterial = new HBox(15);
                                filaMaterial.setAlignment(Pos.CENTER_LEFT);

                                // Texto informativo de la expectativa (Forzado a color oscuro para que se vea sobre el fondo blanco de la tabla)
                                Label lblInfo = new Label("Esperados: " + d.getCantidadEsperada() + " pz - " + d.getProducto().getNombre());
                                lblInfo.setStyle("-fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 13px;");

                                // Campo de texto para capturar la realidad
                                TextField txtRecibido = new TextField();
                                txtRecibido.setPromptText("Cant. Real");
                                txtRecibido.setPrefWidth(80);
                                txtRecibido.setText(String.valueOf(d.getCantidadEsperada()));
                                // Le ponemos un borde gris sutil y fondo blanco para que resalte como un input real
                                txtRecibido.setStyle(
                                        "-fx-text-fill: #333333; " +
                                                "-fx-background-color: #ffffff; " +
                                                "-fx-border-color: #cccccc; " +
                                                "-fx-border-radius: 3; " +
                                                "-fx-background-radius: 3;"
                                );

                                // Restricción para que solo acepte números enteros
                                txtRecibido.textProperty().addListener((obs, oldVal, newVal) -> {
                                    if (!newVal.matches("\\d*")) {
                                        txtRecibido.setText(newVal.replaceAll("[^\\d]", ""));
                                    }
                                });

                                // Guardamos la referencia en nuestro mapa para poder leerlo al dar clic en "Recibir"
                                camposCantidadRecibida.put(d, txtRecibido);

                                filaMaterial.getChildren().addAll(lblInfo, txtRecibido);
                                contenedorFilas.getChildren().add(filaMaterial);
                            }
                            setGraphic(contenedorFilas);
                        }
                    }
                };
            }
        });
    }

    private void agregarBotonRecepcion() {
        Callback<TableColumn<OrdenCompra, Void>, TableCell<OrdenCompra, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<OrdenCompra, Void> call(final TableColumn<OrdenCompra, Void> param) {
                return new TableCell<>() {
                    private final Button btnConfirmar = new Button("📥 Recibir");
                    {
                        btnConfirmar.getStyleClass().add("btn-table-action");
                        btnConfirmar.setOnAction(event -> {
                            OrdenCompra ordenSeleccionada = getTableView().getItems().get(getIndex());
                            procesarRecepcion(ordenSeleccionada);
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

    private void procesarRecepcion(OrdenCompra orden) {
        ProductoDaoCsv productoDao = new ProductoDaoCsv();
        OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();
        boolean hayFaltantes = false;

        // RF-07: Procesamos renglón por renglón comparando la realidad con la expectativa
        for (DetalleOrden item : orden.getDetalles()) {
            // Buscamos el TextField correspondiente a este renglón en nuestro mapa
            TextField txtRecibido = camposCantidadRecibida.get(item);
            int cantidadRealRecibida = item.getCantidadEsperada(); // Por defecto lo esperado

            if (txtRecibido != null && !txtRecibido.getText().trim().isEmpty()) {
                try {
                    cantidadRealRecibida = Integer.parseInt(txtRecibido.getText().trim());
                } catch (NumberFormatException e) {
                    // Si hay error de parseo, se queda el valor esperado por seguridad
                }
            }

            // Guardamos el valor real en el modelo del detalle
            item.setCantidadRecibida(cantidadRealRecibida);

            // Sumamos ÚNICAMENTE lo que llegó en la realidad al stock del producto
            Producto p = productoDao.buscarPorId(item.getProducto().getIdProducto());
            if (p != null) {
                int nuevoStock = p.getStockActual() + cantidadRealRecibida;
                p.setStockActual(nuevoStock);
                productoDao.actualizar(p); // Persistencia en Productos.csv
            }

            // Si llegó menos de lo esperado, se marca la bandera de discrepancia
            if (cantidadRealRecibida < item.getCantidadEsperada()) {
                hayFaltantes = true;
            }
        }

        // RF-07: Evaluamos el estado final de la Orden de Compra según las cantidades ingresadas
        if (hayFaltantes) {
            orden.setEstado("RECIBIDO_CON_FALTANTES");
            System.out.println("Alerta de Almacén: Orden recibida con discrepancias.");
        } else {
            orden.setEstado("RECIBIDO_COMPLETO");
        }

        // Guardamos los cambios de la orden en el CSV
        ordenDao.actualizar(orden);

        // Limpieza de memoria del mapa para este pedido y reactividad visual
        for (DetalleOrden item : orden.getDetalles()) {
            camposCantidadRecibida.remove(item);
        }
        todasLasOrdenesPendientes.remove(orden);
        ordenesFiltradas.remove(orden);

        System.out.println("Orden de Compra #" + orden.getIdOrden() + " guardada exitosamente como: " + orden.getEstado());
    }
}