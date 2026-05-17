package gestionmats.controllers.almacenista;

import gestionmats.dao.DetalleOrdenDaoCsv;
import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.DetalleOrden;
import gestionmats.model.OrdenCompra;
import gestionmats.model.Producto;
import gestionmats.utils.UIComponents;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RecepcionController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private ListView<OrdenCompra> listOrdenes;
    @FXML private Label lblOrdenSeleccionada, lblProveedor, lblEstado;
    @FXML private TableView<DetalleOrden> tablaRecepcion;
    @FXML private TableColumn<DetalleOrden, String> colProducto;
    @FXML private TableColumn<DetalleOrden, Integer> colEsperado, colRecibido;
    @FXML private Button btnConfirmar;

    private OrdenCompraDaoCsv ordenDao = new OrdenCompraDaoCsv();
    private DetalleOrdenDaoCsv detalleDao = new DetalleOrdenDaoCsv();
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();

    private ObservableList<OrdenCompra> ordenesPendientes;
    private FilteredList<OrdenCompra> ordenesFiltradas;
    private OrdenCompra ordenActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        cargarListaOrdenes();
        setupListSelection();
        setupBuscador();
    }

    private void cargarListaOrdenes() {
        // Traemos solo las órdenes pendientes que vienen en camino
        List<OrdenCompra> emitidas = ordenDao.listarTodos().stream()
                .filter(o -> o.getEstado().equalsIgnoreCase("EMITIDA"))
                .toList();

        ordenesPendientes = FXCollections.observableArrayList(emitidas);
        ordenesFiltradas = new FilteredList<>(ordenesPendientes, o -> true);
        listOrdenes.setItems(ordenesFiltradas);

        // Diseñar las Tarjetas Cuadradas del Carrusel
        listOrdenes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(OrdenCompra orden, boolean empty) {
                super.updateItem(orden, empty);
                if (empty || orden == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    VBox card = new VBox(5);
                    card.setPrefWidth(180);
                    card.setAlignment(Pos.CENTER);
                    card.setStyle("-fx-padding: 10; -fx-background-color: #22252e; -fx-background-radius: 8; -fx-border-color: #313543; -fx-border-radius: 8; -fx-cursor: hand;");

                    Label lblId = new Label("Orden #" + orden.getIdOrden());
                    lblId.setStyle("-fx-font-weight: bold; -fx-text-fill: #F58220; -fx-font-size: 14px;");

                    String provName = orden.getProveedor() != null ? orden.getProveedor().getNombreProveedor() : "Prov. Desconocido";
                    Label lblProv = new Label(provName);
                    lblProv.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 11px;");

                    card.getChildren().addAll(lblId, lblProv);
                    setGraphic(card);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0 10 0 0;");
                }
            }
        });
    }

    private void setupTable() {
        colProducto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProducto().getNombre()));
        colEsperado.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCantidadEsperada()));

        // ==============================================================
        // INGENIERÍA DE UX MEJORADA: Conteo Táctil, Editable y con Límites
        // ==============================================================
        colRecibido.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    DetalleOrden det = getTableRow().getItem();

                    HBox box = new HBox(8);
                    box.setAlignment(Pos.CENTER);

                    // Botón Menos (Rojo)
                    Button btnMenos = new Button("-");
                    btnMenos.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-min-width: 35px; -fx-cursor: hand;");

                    // Campo Editable (Reemplaza al Label)
                    TextField txtCant = new TextField(String.valueOf(det.getCantidadRecibida()));
                    txtCant.setAlignment(Pos.CENTER);
                    txtCant.setStyle("-fx-background-color: #2a2d36; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-pref-width: 55px; -fx-border-color: #374151; -fx-border-radius: 4; -fx-background-radius: 4;");

                    // Botón Más (Verde)
                    Button btnMas = new Button("+");
                    btnMas.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-min-width: 35px; -fx-cursor: hand;");

                    // Botón MAX (Azul) para llenar todo de golpe
                    Button btnMax = new Button("MAX");
                    btnMax.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-min-width: 45px; -fx-cursor: hand;");

                    // Función para actualizar colores (Naranja = incompleto, Verde = completo)
                    Runnable updateColor = () -> {
                        if (det.getCantidadRecibida() < det.getCantidadEsperada()) {
                            txtCant.setStyle(txtCant.getStyle().replaceAll("-fx-text-fill: [^;]+;", "-fx-text-fill: #fbbf24;"));
                        } else {
                            txtCant.setStyle(txtCant.getStyle().replaceAll("-fx-text-fill: [^;]+;", "-fx-text-fill: #4ade80;"));
                        }
                    };

                    // Lógica Botón Menos
                    btnMenos.setOnAction(e -> {
                        if (det.getCantidadRecibida() > 0) {
                            det.setCantidadRecibida(det.getCantidadRecibida() - 1);
                            txtCant.setText(String.valueOf(det.getCantidadRecibida()));
                            updateColor.run();
                        }
                    });

                    // Lógica Botón Más (Con tope máximo)
                    btnMas.setOnAction(e -> {
                        if (det.getCantidadRecibida() < det.getCantidadEsperada()) {
                            det.setCantidadRecibida(det.getCantidadRecibida() + 1);
                            txtCant.setText(String.valueOf(det.getCantidadRecibida()));
                            updateColor.run();
                        }
                    });

                    // Lógica Botón MAX
                    btnMax.setOnAction(e -> {
                        det.setCantidadRecibida(det.getCantidadEsperada());
                        txtCant.setText(String.valueOf(det.getCantidadRecibida()));
                        updateColor.run();
                    });

                    // Lógica al Escribir directamente (Validación en tiempo real)
                    txtCant.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal.matches("\\d*")) {
                            txtCant.setText(newVal.replaceAll("[^\\d]", "")); // Evita letras
                            return;
                        }
                        if (!newVal.isEmpty()) {
                            int valorIngresado = Integer.parseInt(newVal);

                            // Topamos el valor a la cantidad esperada
                            if (valorIngresado > det.getCantidadEsperada()) {
                                valorIngresado = det.getCantidadEsperada();
                                txtCant.setText(String.valueOf(valorIngresado));
                            }

                            det.setCantidadRecibida(valorIngresado);
                            updateColor.run();
                        } else {
                            det.setCantidadRecibida(0);
                            updateColor.run();
                        }
                    });

                    updateColor.run(); // Pintar estado inicial
                    box.getChildren().addAll(btnMenos, txtCant, btnMas, btnMax);
                    setGraphic(box);
                }
            }
        });
    }
    private void setupBuscador() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> {
            ordenesFiltradas.setPredicate(orden -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return String.valueOf(orden.getIdOrden()).contains(newVal.trim());
            });
        });
    }
    private void setupListSelection() {
        listOrdenes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDetalleOrden(newSelection);
            }
        });
    }

    private void cargarDetalleOrden(OrdenCompra orden) {
        this.ordenActual = orden;
        lblOrdenSeleccionada.setText("Recepción de Orden #" + orden.getIdOrden());
        lblEstado.setText("LISTO PARA CONTEO");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-background-color: rgba(245, 130, 32, 0.2); -fx-text-fill: #F58220;");

        String provName = orden.getProveedor() != null ? orden.getProveedor().getNombreProveedor() : "Desconocido";
        lblProveedor.setText("Proveedor: " + provName);

        // Usamos el DAO nuevo para sacar la lista que corresponde
        List<DetalleOrden> detalles = detalleDao.buscarPorIdOrden(orden.getIdOrden());

        // Iniciamos el conteo en 0 o lo que ya estuviera guardado
        tablaRecepcion.setItems(FXCollections.observableArrayList(detalles));
        btnConfirmar.setDisable(detalles.isEmpty());
    }

    @FXML
    private void handleRegistrarEntrada() {
        if (ordenActual == null || tablaRecepcion.getItems().isEmpty()) return;

        boolean confirm = UIComponents.showConfirmDialog("Ingresar al Almacén", "¿Confirmas que el conteo físico es correcto y deseas actualizar el inventario?");
        if (!confirm) return;

        boolean hayFaltantes = false;
        List<DetalleOrden> detalles = tablaRecepcion.getItems();

        for (DetalleOrden item : detalles) {

            // 1. Sumar al Stock Real (Catálogo)
            Producto p = productoDao.buscarPorId(item.getProducto().getIdProducto());
            if (p != null && item.getCantidadRecibida() > 0) {
                int nuevoStock = p.getStockActual() + item.getCantidadRecibida();
                p.setStockActual(nuevoStock);
                productoDao.actualizar(p);
            }

            // 2. Guardar en el Detalle de Orden para que quede registro
            detalleDao.actualizarRecepcion(ordenActual.getIdOrden(), item.getProducto().getIdProducto(), item.getCantidadRecibida());

            // 3. Revisar discrepancias (Requisito RF-07)
            if (item.getCantidadRecibida() < item.getCantidadEsperada()) {
                hayFaltantes = true;
            }
        }

        // 4. Cambiar estado principal de la orden
        ordenActual.setEstado(hayFaltantes ? "INCOMPLETA" : "SURTIDA");
        ordenDao.actualizar(ordenActual);

        // Notificar el éxito (El Gerente verá esto reflejado en su Dashboard inmediatamente)
        UIComponents.showNotification("Entrada registrada. Estado de orden: " + ordenActual.getEstado(), "success");

        limpiarPanelInferior();
        cargarListaOrdenes();
    }

    private void limpiarPanelInferior() {
        ordenActual = null;
        lblOrdenSeleccionada.setText("Seleccione una orden del carrusel");
        lblProveedor.setText("-- Esperando camión --");
        lblEstado.setText("--");
        lblEstado.setStyle("-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-background-color: #374151; -fx-text-fill: #9ca3af;");
        tablaRecepcion.getItems().clear();
        btnConfirmar.setDisable(true);
    }
}