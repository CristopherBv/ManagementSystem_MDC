package gestionmats.controllers;

import gestionmats.dao.ProductoDaoCsv;
import gestionmats.model.Producto;
import gestionmats.utils.UIComponents;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GerenteProductoFormController implements Initializable {

    @FXML private TextField txtId, txtNombre, txtMarca, txtUnidad, txtPrecio, txtDescuento, txtStockActual, txtStockMaximo;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private Button btnGuardar, btnCancelar;

    private ProductoDaoCsv dao;
    private boolean guardadoExitoso = false; // Bandera para avisar si debemos recargar la tabla

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new ProductoDaoCsv();

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Cemento y concreto", "Acero y metales", "Madera y derivados",
                "Impermeabilizantes", "Herramientas", "Tubería y plomería"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        // Autogenerar y bloquear el ID
        txtId.setText(dao.generarSiguienteId());

        UIComponents.applyButtonAdd(btnGuardar);
        UIComponents.applyButtonGhost(btnCancelar);
    }

    @FXML private void handleGuardar() {
        // 1. Validar campos vacíos
        if (txtNombre.getText().trim().isEmpty() || txtMarca.getText().trim().isEmpty() ||
                txtUnidad.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty() ||
                txtStockActual.getText().trim().isEmpty() || txtStockMaximo.getText().trim().isEmpty()) {
            UIComponents.showNotification("Complete todos los campos obligatorios.", "warn");
            return;
        }

        // 2. Validar que no estemos duplicando
        if (dao.existeProductoPorNombreExacto(txtNombre.getText())) {
            UIComponents.showNotification("Ya existe un material registrado con ese nombre.", "error");
            return;
        }

        try {
            // 3. Validar números
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            double descuento = txtDescuento.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtDescuento.getText().trim());
            int stockActual = Integer.parseInt(txtStockActual.getText().trim());
            int stockMax = Integer.parseInt(txtStockMaximo.getText().trim());

            if (stockActual > stockMax) {
                UIComponents.showNotification("El stock actual no puede ser mayor al stock máximo.", "warn");
                return;
            }

            // 4. Crear el objeto
            Producto nuevoProducto = new Producto(
                    txtId.getText(),
                    txtNombre.getText().trim(),
                    txtMarca.getText().trim(),
                    cmbCategoria.getValue(),
                    precio,
                    stockMax,
                    stockActual,
                    txtUnidad.getText().trim(),
                    descuento
            );

            // 5. Guardar físicamente
            if (dao.guardar(nuevoProducto)) {
                guardadoExitoso = true;
                cerrarVentana();
            } else {
                UIComponents.showNotification("Error al escribir en la base de datos.", "error");
            }

        } catch (NumberFormatException e) {
            UIComponents.showNotification("Revise que el precio y los stocks sean números válidos.", "error");
        }
    }

    @FXML private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Método que usará el controlador principal para saber si debe actualizar la tabla
    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}