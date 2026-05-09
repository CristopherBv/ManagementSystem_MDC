package gestionmats.controllers;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.Producto;
import gestionmats.model.EstadoProducto;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteInventarioController implements Initializable {

    @FXML private TableView<Producto> tableInventario;
    @FXML private TableColumn<Producto, String> colId, colNombre, colMarca, colUnidad, colEstado;
    @FXML private TableColumn<Producto, Integer> colCantidad, colStock;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Label kpiStock, kpiAlertas, lblTotal;
    @FXML private Button btnAgregar, btnEditar, btnEliminar, btnExportar, btnActualizar;

    private ObservableList<Producto> allItems;
    private FilteredList<Producto> filteredItems;
    private ProductoDaoCsv productoDao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productoDao = new ProductoDaoCsv();
        setupTable();
        setupFilters();
        loadData();
        setupStyles();
    }

    private void setupStyles() {
        UIComponents.applyButtonAdd(btnAgregar);
        UIComponents.applyButtonEdit(btnEditar);
        UIComponents.applyButtonDelete(btnEliminar);
        UIComponents.applyButtonGhost(btnExportar);
        UIComponents.applyButtonGhost(btnActualizar);
    }

    private void setupTable() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIdProducto()));
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colMarca.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMarca()));
        colCantidad.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getStockActual()));
        colUnidad.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUnidadMedida()));
        colStock.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().calcularStockMinimo()));
        colEstado.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEstado().name()));

        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    switch (item) {
                        case "LLENO"   -> setStyle("-fx-text-fill: #38bdf8; -fx-font-weight: bold;"); // Celeste
                        case "OK"      -> setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;"); // Verde
                        case "BAJO"    -> setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;"); // Naranja
                        case "CRITICO" -> setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;"); // Rojo
                        default        -> setStyle("-fx-text-fill: #9ca3af;");
                    }
                }
            }
        });
    }

    private void setupFilters() {
        // Llenamos el ComboBox con las categorías
        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Todas", "Cemento y concreto", "Acero y metales",
                "Madera y derivados", "Impermeabilizantes",
                "Herramientas", "Tubería y plomería"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        // Listeners para los filtros
        txtSearch.textProperty().addListener((obs, old, newVal) -> applyFilters());
        cmbCategoria.valueProperty().addListener((obs, old, newVal) -> applyFilters());
    }

    private void loadData() {
        allItems = FXCollections.observableArrayList(productoDao.listarTodos());
        filteredItems = new FilteredList<>(allItems, p -> true);
        tableInventario.setItems(filteredItems);

        filteredItems.addListener((javafx.collections.ListChangeListener<Producto>) c ->
                lblTotal.setText("Total: " + filteredItems.size() + " registros"));

        // Forzamos el texto inicial
        lblTotal.setText("Total: " + filteredItems.size() + " registros");
        updateKPIs();
    }

    private void applyFilters() {
        String search = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String cat = cmbCategoria.getValue();

        filteredItems.setPredicate(p -> {
            boolean matchSearch = search.isEmpty() || p.getNombre().toLowerCase().contains(search) || p.getIdProducto().toLowerCase().contains(search);
            boolean matchCat = cat == null || cat.equals("Todas") || p.getCategoria().equals(cat);
            return matchSearch && matchCat;
        });
    }

    private void updateKPIs() {
        kpiStock.setText(String.valueOf(allItems.size()));
        long alertas = allItems.stream().filter(p -> p.getEstado() == EstadoProducto.BAJO || p.getEstado() == EstadoProducto.CRITICO).count();
        kpiAlertas.setText(String.valueOf(alertas));
    }


    @FXML private void handleAgregar() {
        try {
            // Cargamos la vista del formulario
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/GerenteProductoFormView.fxml"));
            javafx.scene.Parent root = loader.load();

            // Obtenemos el controlador de esa vista
            GerenteProductoFormController formController = loader.getController();

            // Creamos la nueva ventana (Stage)
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Registrar Nuevo Material");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);

            // Hacer que sea MODAL (bloquea la ventana de inventario hasta que se cierre)
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait(); // El código se detiene aquí hasta que el usuario cierra el pop-up

            // Cuando se cierra, le preguntamos al controlador si logró guardar
            if (formController.isGuardadoExitoso()) {
                handleActualizar(); // Tu método que ya recarga la tabla desde el CSV y actualiza KPIs
                UIComponents.showNotification("Material agregado exitosamente.", "success");
            }

        } catch (Exception e) {
            e.printStackTrace();
            UIComponents.showNotification("Error al abrir el formulario.", "error");
        }
    }

    @FXML private void handleEditar() { UIComponents.showNotification("Editar Producto", "info"); }
    @FXML private void handleExportar() { UIComponents.showNotification("Exportando...", "info"); }

    @FXML private void handleEliminar() {
        Producto selected = tableInventario.getSelectionModel().getSelectedItem();

        if (selected != null) {
            // 1. Pedimos confirmación al usuario
            boolean confirmado = UIComponents.showConfirmDialog(
                    "Eliminar Material",
                    "¿Estás seguro de que deseas eliminar permanentemente '" + selected.getNombre() + "'?\nEsta acción no se puede deshacer."
            );

            if (confirmado) {
                // 2. Borramos físicamente del CSV usando el DAO (pasando el String "M-XXX")
                boolean borradoFisico = productoDao.eliminar(selected.getIdProducto());

                if (borradoFisico) {
                    // 3. Si tuvo éxito en el CSV, lo quitamos de la pantalla
                    allItems.remove(selected);
                    updateKPIs();
                    UIComponents.showNotification("Material eliminado permanentemente.", "success");
                } else {
                    UIComponents.showNotification("Error interno al intentar modificar la base de datos.", "error");
                }
            }
        } else {
            UIComponents.showNotification("Seleccione un material primero.", "warn");
        }
    }

    @FXML private void handleActualizar() {
        loadData();
        UIComponents.showNotification("Datos actualizados.", "success");
    }
}
