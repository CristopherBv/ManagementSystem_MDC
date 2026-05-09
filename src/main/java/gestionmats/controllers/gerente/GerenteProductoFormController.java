package gestionmats.controllers.gerente;

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
    private boolean guardadoExitoso = false;

    // Si esta variable es nula, estamos Creando. Si tiene datos, estamos Editando.
    private Producto productoEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new ProductoDaoCsv();

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "Cemento y concreto", "Acero y metales", "Madera y derivados",
                "Impermeabilizantes", "Herramientas", "Tubería y plomería"
        ));
        cmbCategoria.getSelectionModel().selectFirst();

        // Por defecto, asumimos que es una creación
        txtId.setText(dao.generarSiguienteId());

        UIComponents.applyButtonAdd(btnGuardar);
        UIComponents.applyButtonGhost(btnCancelar);
    }

    /**
     * MÉTODO NUEVO: Lo llama el inventario cuando queremos editar un material.
     */
    public void cargarProductoParaEdicion(Producto p) {
        this.productoEdicion = p; // Guardamos la referencia

        txtId.setText(p.getIdProducto());
        txtNombre.setText(p.getNombre());
        txtMarca.setText(p.getMarca());
        cmbCategoria.setValue(p.getCategoria());
        txtUnidad.setText(p.getUnidadMedida());
        txtPrecio.setText(String.valueOf(p.getPrecioVenta()));
        txtDescuento.setText(String.valueOf(p.getDescuento()));
        txtStockActual.setText(String.valueOf(p.getStockActual()));
        txtStockMaximo.setText(String.valueOf(p.getStockMaximo()));

        // BLOQUEO DE SEGURIDAD (Tú lo pediste)
        txtStockActual.setDisable(true);
        btnGuardar.setText("Actualizar Material");
    }

    @FXML private void handleGuardar() {
        if (txtNombre.getText().trim().isEmpty() || txtMarca.getText().trim().isEmpty() ||
                txtUnidad.getText().trim().isEmpty() || txtPrecio.getText().trim().isEmpty() ||
                txtStockActual.getText().trim().isEmpty() || txtStockMaximo.getText().trim().isEmpty()) {
            UIComponents.showNotification("Complete todos los campos obligatorios.", "warn");
            return;
        }

        // Validar duplicados SOLO si estamos creando un producto nuevo,
        // o si estamos editando pero le cambiamos el nombre.
        boolean nombreModificado = (productoEdicion == null) || !productoEdicion.getNombre().equalsIgnoreCase(txtNombre.getText().trim());

        if (nombreModificado && dao.existeProductoPorNombreExacto(txtNombre.getText())) {
            UIComponents.showNotification("Ya existe un material registrado con ese nombre.", "error");
            return;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            double descuento = txtDescuento.getText().trim().isEmpty() ? 0.0 : Double.parseDouble(txtDescuento.getText().trim());
            int stockActual = Integer.parseInt(txtStockActual.getText().trim());
            int stockMax = Integer.parseInt(txtStockMaximo.getText().trim());

            if (stockActual > stockMax) {
                UIComponents.showNotification("El stock actual no puede ser mayor al stock máximo.", "warn");
                return;
            }

            Producto productoProcesado = new Producto(
                    txtId.getText(), txtNombre.getText().trim(), txtMarca.getText().trim(),
                    cmbCategoria.getValue(), precio, stockMax, stockActual, txtUnidad.getText().trim(), descuento
            );

            boolean exito;
            if (productoEdicion == null) {
                exito = dao.guardar(productoProcesado); // CREAR
            } else {
                exito = dao.actualizar(productoProcesado); // EDITAR
            }

            if (exito) {
                guardadoExitoso = true;
                cerrarVentana();
            } else {
                UIComponents.showNotification("Error al escribir en la base de datos.", "error");
            }

        } catch (NumberFormatException e) {
            UIComponents.showNotification("Revise que el precio y los stocks sean números válidos.", "error");
        }
    }

    @FXML private void handleCancelar() { cerrarVentana(); }
    private void cerrarVentana() { ((Stage) btnCancelar.getScene().getWindow()).close(); }
    public boolean isGuardadoExitoso() { return guardadoExitoso; }
}