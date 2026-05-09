package gestionmats.controllers.gerente;

import gestionmats.dao.OrdenCompraDaoCsv;
import gestionmats.model.OrdenCompra;
import gestionmats.utils.UIComponents;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class GerentePedidosController implements Initializable {

    @FXML private TableView<OrdenCompra> tablePedidos;
    @FXML private TableColumn<OrdenCompra, Integer> colFolio;
    @FXML private TableColumn<OrdenCompra, String> colFecha, colProveedor, colEstado;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private Label lblTotal;
    @FXML private Button btnNuevoPedido, btnVerDetalle;

    private OrdenCompraDaoCsv ordenDao;
    private ObservableList<OrdenCompra> allItems;
    private FilteredList<OrdenCompra> filteredItems;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ordenDao = new OrdenCompraDaoCsv();
        setupStyles();
        setupTable();
        setupFilters();
        loadData();
    }

    private void setupStyles() {
        UIComponents.applyButtonAdd(btnNuevoPedido);
        UIComponents.applyButtonEdit(btnVerDetalle);
    }

    private void setupTable() {
        // Mapeo de columnas
        colFolio.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getIdOrden()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(sdf.format(d.getValue().getFechaEmision())));

        // Extraemos el nombre del proveedor desde el objeto anidado
        colProveedor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProveedor() != null ? d.getValue().getProveedor().getNombreProveedor() : "Proveedor Desconocido"
        ));

        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));

        // Darle color dinámico a los estados
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "EMITIDA"    -> setStyle("-fx-text-fill: #60a5fa; -fx-font-weight: bold;"); // Azul
                        case "SURTIDA"    -> setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;"); // Verde
                        case "INCOMPLETA" -> setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;"); // Naranja
                        case "CANCELADA"  -> setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;"); // Rojo
                        default           -> setStyle("-fx-text-fill: #9ca3af;");
                    }
                }
            }
        });
    }

    private void loadData() {
        allItems = FXCollections.observableArrayList(ordenDao.listarTodos());
        filteredItems = new FilteredList<>(allItems, p -> true);
        tablePedidos.setItems(filteredItems);

        filteredItems.addListener((javafx.collections.ListChangeListener<OrdenCompra>) c ->
                lblTotal.setText("Total: " + filteredItems.size() + " pedidos"));

        // Set inicial del texto
        lblTotal.setText("Total: " + filteredItems.size() + " pedidos");
    }

    private void setupFilters() {
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Todos", "EMITIDA", "SURTIDA", "INCOMPLETA", "CANCELADA"
        ));
        cmbEstado.getSelectionModel().selectFirst();

        // Listeners para refrescar la tabla cuando escribes o cambias el combo
        txtSearch.textProperty().addListener((obs, old, newVal) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, old, newVal) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        String search = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String estado = cmbEstado.getValue();

        filteredItems.setPredicate(o -> {
            boolean matchSearch = search.isEmpty() ||
                    String.valueOf(o.getIdOrden()).contains(search) ||
                    (o.getProveedor() != null && o.getProveedor().getNombreProveedor().toLowerCase().contains(search));

            boolean matchEstado = estado == null || estado.equals("Todos") || o.getEstado().equals(estado);

            return matchSearch && matchEstado;
        });
    }

    @FXML private void handleNuevoPedido() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/gerente/GerenteNuevoPedidoView.fxml"));
            javafx.scene.Parent root = loader.load();

            GerenteNuevoPedidoController controller = loader.getController();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Generar Nueva Orden");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (controller.isOrdenGenerada()) {
                loadData(); // ¡AQUÍ ESTÁ LA MAGIA! Recarga la tabla con el nuevo pedido
                UIComponents.showNotification("Orden emitida y guardada en CSV.", "success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIComponents.showNotification("Error al abrir el carrito.", "error");
        }
    }
    /**
     * SI, esto no debería ir aqui pero: Estoy_cansado_jefe.jpg
     * Así que ignoremos esto xd
     * */
    @FXML private void handleVerDetalle() {
        OrdenCompra selected = tablePedidos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UIComponents.showNotification("Seleccione un pedido para ver su desglose.", "warn");
            return;
        }

        // 1. Crear una tabla visual desde código (sin necesidad de FXML extra)
        TableView<gestionmats.model.DetalleOrden> tablaDetalles = new TableView<>();
        tablaDetalles.setItems(FXCollections.observableArrayList(selected.getDetalles()));
        tablaDetalles.setPrefHeight(250);
        tablaDetalles.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 2. Crear las columnas
        TableColumn<gestionmats.model.DetalleOrden, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProducto().getNombre()));

        TableColumn<gestionmats.model.DetalleOrden, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getCantidadEsperada()));

        TableColumn<gestionmats.model.DetalleOrden, String> colPrecio = new TableColumn<>("Precio Unit.");
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().getProducto().getPrecioVenta())));

        TableColumn<gestionmats.model.DetalleOrden, String> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%.2f", d.getValue().calcularSubtotal())));

        tablaDetalles.getColumns().addAll(colProd, colCant, colPrecio, colSub);

        // 3. Calcular el Gran Total
        double granTotal = selected.getDetalles().stream().mapToDouble(gestionmats.model.DetalleOrden::calcularSubtotal).sum();
        Label lblTotal = new Label(String.format("Total del Pedido: $%.2f", granTotal));
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #F58220;"); // Letras naranjas y grandes

        // 4. Armar el contenido de la ventana
        javafx.scene.layout.VBox contenido = new javafx.scene.layout.VBox(15, tablaDetalles, lblTotal);
        contenido.setStyle("-fx-padding: 20; -fx-background-color: #111318;");
        contenido.setPrefWidth(500);

        // 5. Mostrar la ventana emergente
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Desglose del Pedido");
        dialog.setHeaderText("Folio: " + selected.getIdOrden() + " | Proveedor: " + selected.getProveedor().getNombreProveedor());
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Aplicamos el dark mode al DialogPane para que no desentone
        dialog.getDialogPane().setStyle("-fx-background-color: #111318; -fx-base: #111318;");

        dialog.showAndWait();
    }
}