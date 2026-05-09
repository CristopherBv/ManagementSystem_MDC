package gestionmats.controllers.gerente;

import gestionmats.dao.ProveedorDaoCsv;
import gestionmats.model.Proveedor;
import gestionmats.utils.UIComponents;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteProveedorFormController implements Initializable {

    @FXML private TextField txtId, txtNombre, txtTelefono;
    @FXML private Button btnGuardar, btnCancelar;

    private ProveedorDaoCsv dao;
    private boolean guardadoExitoso = false;
    private Proveedor proveedorEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new ProveedorDaoCsv();
        txtId.setText(String.valueOf(dao.generarSiguienteId())); // ID automático

        UIComponents.applyButtonAdd(btnGuardar);
        UIComponents.applyButtonGhost(btnCancelar);
    }

    public void cargarProveedorParaEdicion(Proveedor p) {
        this.proveedorEdicion = p;
        txtId.setText(String.valueOf(p.getIdProveedor()));
        txtNombre.setText(p.getNombreProveedor());
        txtTelefono.setText(p.getNumeroTelefonico());
        btnGuardar.setText("Actualizar Datos");
    }

    @FXML private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        String tel = txtTelefono.getText().trim();

        if (nombre.isEmpty() || tel.isEmpty()) {
            UIComponents.showNotification("Por favor, llene todos los campos.", "warn");
            return;
        }

        // Validar nombre duplicado (si es nuevo o cambió el nombre)
        boolean nombreModificado = (proveedorEdicion == null) || !proveedorEdicion.getNombreProveedor().equalsIgnoreCase(nombre);
        if (nombreModificado && dao.existeProveedorPorNombre(nombre)) {
            UIComponents.showNotification("Ya existe un proveedor con ese nombre.", "error");
            return;
        }

        try {
            Proveedor p = new Proveedor(Integer.parseInt(txtId.getText()), nombre, tel);
            boolean exito = (proveedorEdicion == null) ? dao.guardar(p) : dao.actualizar(p);

            if (exito) {
                guardadoExitoso = true;
                cerrarVentana();
            } else {
                UIComponents.showNotification("Error al guardar en el archivo CSV.", "error");
            }
        } catch (Exception e) {
            UIComponents.showNotification("Error de formato en los datos.", "error");
        }
    }

    @FXML private void handleCancelar() { cerrarVentana(); }
    private void cerrarVentana() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
    public boolean isGuardadoExitoso() { return guardadoExitoso; }
}
