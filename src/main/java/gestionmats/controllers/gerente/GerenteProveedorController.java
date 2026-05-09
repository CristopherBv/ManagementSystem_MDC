package gestionmats.controllers.gerente;

import gestionmats.dao.ProveedorDaoCsv;
import gestionmats.model.Proveedor;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteProveedorController implements Initializable {

    @FXML private TableView<Proveedor> tableProveedores;
    @FXML private TableColumn<Proveedor, String> colNombre, colTelefono;
    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotal;
    @FXML private Button btnAgregar, btnEditar, btnEliminar;

    private ObservableList<Proveedor> allItems;
    private FilteredList<Proveedor> filteredItems;
    private ProveedorDaoCsv proveedorDao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        proveedorDao = new ProveedorDaoCsv();
        setupStyles();
        setupTable();
        setupFilters();
        loadData();
    }

    private void setupStyles() {
        UIComponents.applyButtonAdd(btnAgregar);
        UIComponents.applyButtonEdit(btnEditar);
        UIComponents.applyButtonDelete(btnEliminar);
    }

    private void setupTable() {
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getIdProveedor()));
        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNombreProveedor()));
        colTelefono.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNumeroTelefonico()));
    }

    private void setupFilters() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            filteredItems.setPredicate(p -> filter.isEmpty() ||
                    p.getNombreProveedor().toLowerCase().contains(filter) ||
                    String.valueOf(p.getIdProveedor()).contains(filter)
            );
        });
    }

    private void loadData() {
        allItems = FXCollections.observableArrayList(proveedorDao.listarTodos());
        filteredItems = new FilteredList<>(allItems, p -> true);
        tableProveedores.setItems(filteredItems);

        filteredItems.addListener((javafx.collections.ListChangeListener<Proveedor>) c ->
                lblTotal.setText("Total: " + filteredItems.size() + " proveedores"));
        lblTotal.setText("Total: " + filteredItems.size() + " proveedores");
    }

    @FXML private void handleEliminar() {
        Proveedor selected = tableProveedores.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean confirmado = UIComponents.showConfirmDialog(
                    "Eliminar Proveedor",
                    "¿Eliminar permanentemente a '" + selected.getNombreProveedor() + "'?"
            );

            if (confirmado) {
                if (proveedorDao.eliminar(selected.getIdProveedor())) {
                    allItems.remove(selected);
                    UIComponents.showNotification("Proveedor eliminado.", "success");
                } else {
                    UIComponents.showNotification("Error en base de datos.", "error");
                }
            }
        } else {
            UIComponents.showNotification("Seleccione un proveedor.", "warn");
        }
    }

    @FXML private void handleAgregar() {
        // TODO: Mismo mecanismo que hicimos con GerenteProductoFormView
        UIComponents.showNotification("Abrir formulario nuevo proveedor", "info");
    }

    @FXML private void handleEditar() {
        // TODO: Mismo mecanismo de edición
        UIComponents.showNotification("Abrir formulario editar proveedor", "info");
    }
}