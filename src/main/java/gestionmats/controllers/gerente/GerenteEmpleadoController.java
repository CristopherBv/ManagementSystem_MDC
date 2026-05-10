package gestionmats.controllers.gerente;

import gestionmats.dao.UsuarioDaoCsv;
import gestionmats.model.*;
import gestionmats.utils.UIComponents;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteEmpleadoController implements Initializable {

    @FXML private TableView<Usuario> tableEmpleados;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colNombreCompleto, colUsername, colRol;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotal;
    @FXML private Button btnAgregar, btnEditar, btnEliminar;

    private UsuarioDaoCsv usuarioDao;
    private ObservableList<Usuario> allItems;
    private FilteredList<Usuario> filteredItems;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDao = new UsuarioDaoCsv();
        setupStyles();
        setupTable();
        loadData();
        setupFilters();
    }

    private void setupStyles() {
        UIComponents.applyButtonAdd(btnAgregar);
        UIComponents.applyButtonEdit(btnEditar);
        UIComponents.applyButtonDelete(btnEliminar);
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));

        // Unimos el nombre con los apellidos para la tabla
        colNombreCompleto.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNombre() + " " + d.getValue().getPrimerApellido() + " " + d.getValue().getSegundoApellido()
        ));

        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        colRol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRol().name()));
        colRol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    // Colores por rol para fácil identificación
                    if (item.equals("GERENTE")) setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;"); // Rojo
                    else if (item.equals("ALMACENISTA")) setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;"); // Amarillo
                    else setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold;"); // Verde (Vendedor)
                }
            }
        });
    }

    private void loadData() {
        allItems = FXCollections.observableArrayList(usuarioDao.listarTodos());
        filteredItems = new FilteredList<>(allItems, p -> true);
        tableEmpleados.setItems(filteredItems);
        filteredItems.addListener((javafx.collections.ListChangeListener<Usuario>) c ->
                lblTotal.setText("Total: " + filteredItems.size() + " empleados"));
        lblTotal.setText("Total: " + filteredItems.size() + " empleados");
    }

    private void setupFilters() {
        txtSearch.textProperty().addListener((obs, old, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            filteredItems.setPredicate(u -> filter.isEmpty() ||
                    u.getNombre().toLowerCase().contains(filter) ||
                    u.getUsername().toLowerCase().contains(filter)
            );
        });
    }

    @FXML private void handleNuevoEmpleado() { abrirDialogoEmpleado(null); }

    @FXML private void handleEditarEmpleado() {
        Usuario sel = tableEmpleados.getSelectionModel().getSelectedItem();
        if (sel == null) UIComponents.showNotification("Seleccione un empleado", "warn");
        else abrirDialogoEmpleado(sel);
    }

    private void abrirDialogoEmpleado(Usuario uExistente) {
        Dialog<Usuario> dialog = new Dialog<>();
        boolean isEdicion = (uExistente != null);

        dialog.setTitle(isEdicion ? "Editar Empleado" : "Nuevo Empleado");
        dialog.setHeaderText(isEdicion ? "Editando: " + uExistente.getUsername() : "Registrar nuevo empleado");

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: #111318; -fx-base: #111318;");

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15); grid.setPadding(new Insets(20));

        TextField txtNombre = new TextField(isEdicion ? uExistente.getNombre() : "");
        TextField txtAp1 = new TextField(isEdicion ? uExistente.getPrimerApellido() : "");
        TextField txtAp2 = new TextField(isEdicion ? uExistente.getSegundoApellido() : "");
        TextField txtUser = new TextField(isEdicion ? uExistente.getUsername() : "");
        PasswordField txtPass = new PasswordField(); // Usamos PasswordField para ocultar
        if (isEdicion) txtPass.setText(uExistente.getPassword());

        ComboBox<String> cmbRol = new ComboBox<>(FXCollections.observableArrayList("VENDEDOR", "ALMACENISTA", "GERENTE"));
        cmbRol.setValue(isEdicion ? uExistente.getRol().name() : "VENDEDOR");

        // Estilos
        String inputStyle = "-fx-background-color: rgba(255,255,255,0.05); -fx-text-fill: white;";
        txtNombre.setStyle(inputStyle); txtAp1.setStyle(inputStyle); txtAp2.setStyle(inputStyle);
        txtUser.setStyle(inputStyle); txtPass.setStyle(inputStyle); cmbRol.setStyle(inputStyle);

        grid.addRow(0, new Label("Nombre(s):"), txtNombre);
        grid.addRow(1, new Label("Primer Apellido:"), txtAp1);
        grid.addRow(2, new Label("Segundo Apellido:"), txtAp2);
        grid.addRow(3, new Label("Usuario (Login):"), txtUser);
        grid.addRow(4, new Label("Contraseña:"), txtPass);
        grid.addRow(5, new Label("Puesto/Rol:"), cmbRol);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(bt -> {
            if (bt != btnGuardar) return null;

            // Validaciones rápidas
            if(txtNombre.getText().trim().isEmpty() || txtUser.getText().trim().isEmpty() || txtPass.getText().trim().isEmpty()) {
                UIComponents.showNotification("Nombre, Usuario y Contraseña son obligatorios.", "error");
                return null;
            }

            // Validar que no roben el username
            if (!isEdicion || !uExistente.getUsername().equalsIgnoreCase(txtUser.getText().trim())) {
                if (usuarioDao.existeUsername(txtUser.getText().trim())) {
                    UIComponents.showNotification("Ese nombre de usuario ya está en uso.", "error");
                    return null;
                }
            }

            RolUsuario rolEnum = RolUsuario.valueOf(cmbRol.getValue());
            int idFinal = isEdicion ? uExistente.getIdUsuario() : usuarioDao.generarSiguienteId();

            // Instanciar la clase hija correspondiente
            Usuario nuevoU;
            if (rolEnum == RolUsuario.GERENTE) {
                nuevoU = new Gerente(idFinal, txtAp1.getText().trim(), txtAp2.getText().trim(), txtNombre.getText().trim(), txtUser.getText().trim(), txtPass.getText().trim());
            } else if (rolEnum == RolUsuario.ALMACENISTA) {
                nuevoU = new Almacenista(idFinal, txtAp1.getText().trim(), txtAp2.getText().trim(), txtNombre.getText().trim(), txtUser.getText().trim(), txtPass.getText().trim());
            } else {
                nuevoU = new Vendedor(idFinal, txtAp1.getText().trim(), txtAp2.getText().trim(), txtNombre.getText().trim(), txtUser.getText().trim(), txtPass.getText().trim());
            }

            return nuevoU;
        });

        dialog.showAndWait().ifPresent(u -> {
            if (isEdicion) usuarioDao.actualizar(u);
            else usuarioDao.guardar(u);
            loadData();
            UIComponents.showNotification(isEdicion ? "Empleado actualizado." : "Empleado registrado.", "success");
        });
    }

    @FXML private void handleEliminarEmpleado() {
        Usuario sel = tableEmpleados.getSelectionModel().getSelectedItem();
        if (sel == null) { UIComponents.showNotification("Seleccione un empleado", "warn"); return; }

        // Regla de negocio: No dejar que el gerente se borre a sí mismo accidentalmente
        if (sel.getIdUsuario() == gestionmats.services.GestorSesion.getInstancia().getUsuarioActual().getIdUsuario()) {
            UIComponents.showNotification("No puedes eliminar tu propio usuario activo.", "error");
            return;
        }

        if (UIComponents.showConfirmDialog("Eliminar empleado", "¿Eliminar acceso al sistema para " + sel.getUsername() + "?")) {
            usuarioDao.eliminar(sel.getIdUsuario());
            loadData();
            UIComponents.showNotification("Acceso revocado.", "success");
        }
    }
}
