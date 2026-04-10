package gestionmats.controllers;

import gestionmats.dao.*;
import gestionmats.models.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class WarehouseController {

    @FXML private Label lblUsuario, lblSearchTitle;
    @FXML private TextField txtSearchId;
    @FXML private ToggleButton btnTabSalidas;
    @FXML private VBox vboxResults;

    // Inyectamos todos los DAOs necesarios
    private final PedidoDAO pedidoDAO = PedidoDAO.getInstancia();
    private final CompraDAO compraDAO = CompraDAO.getInstancia();
    private final ProductoDAO productoDAO = ProductoDAO.getInstancia();
    private final MovimientoInventarioDAO movimientoDAO = MovimientoInventarioDAO.getInstancia();

    private Pedido pedidoActual = null;
    private Compra compraActual = null;

    @FXML
    public void switchTab() {
        vboxResults.getChildren().clear();
        txtSearchId.clear();
        if (btnTabSalidas.isSelected()) {
            lblSearchTitle.setText("Buscar Pedido para Despacho");
            txtSearchId.setPromptText("Ej: P-001...");
        } else {
            lblSearchTitle.setText("Buscar Orden de Compra");
            txtSearchId.setPromptText("Ej: C-001...");
        }
    }

    @FXML
    public void handleSearch() {
        String id = txtSearchId.getText().trim();
        vboxResults.getChildren().clear();

        if (btnTabSalidas.isSelected()) {
            // Lógica de Salidas (Despacho)
            Optional<Pedido> res = pedidoDAO.getAll().stream().filter(p -> p.id().equals(id)).findFirst();
            if (res.isPresent()) {
                pedidoActual = res.get();
                mostrarDetallesPedido();
            } else {
                mostrarAlerta("No encontrado", "El pedido no existe o no está pendiente.");
            }
        } else {
            // Lógica de Entradas (Recepción)
            Optional<Compra> res = compraDAO.getAll().stream().filter(c -> c.id().equals(id)).findFirst();
            if (res.isPresent()) {
                compraActual = res.get();
                mostrarDetallesCompra();
            } else {
                mostrarAlerta("No encontrado", "La orden de compra no existe.");
            }
        }
    }

    private void mostrarDetallesPedido() {
        vboxResults.getChildren().add(new Label("Pedido de: " + pedidoActual.customerName()));
        vboxResults.getChildren().add(new Label("Total: $" + pedidoActual.total()));

        Button btnConfirmar = new Button("Confirmar Despacho");
        btnConfirmar.setOnAction(e -> procesarDespacho());
        vboxResults.getChildren().add(btnConfirmar);
    }

    private void procesarDespacho() {
        // 1. Afectar Stock de cada producto
        for (DetallePedido item : pedidoActual.items()) {
            Optional<Producto> pOpt = productoDAO.getAll().stream()
                    .filter(p -> p.id().equals(item.productId())).findFirst();

            if (pOpt.isPresent()) {
                Producto p = pOpt.get();
                // IMPORTANTE: Aquí restamos stock
                Producto actualizado = new Producto(
                        p.id(), p.nombre(), p.descripcion(), p.precio(),
                        p.stock() - item.quantity(), // <--- Resta
                        p.categoria(), p.unidad(), p.minStock(), p.descuento(),
                        p.imageUrl(), p.createdAt(), LocalDateTime.now().toString()
                );
                productoDAO.update(p.id(), actualizado);

                // 2. Registrar el movimiento en el historial
                MovimientoInventario mov = new MovimientoInventario(
                        UUID.randomUUID().toString(),
                        p.id(), p.nombre(), -item.quantity(), // Negativo porque es salida
                        TipoMovimiento.SALE,
                        "Despacho Pedido: " + pedidoActual.id(),
                        LocalDateTime.now().toString()
                );
                movimientoDAO.create(mov);
            }
        }

        // 3. Cambiar status del pedido
        Pedido pedidoDespachado = new Pedido(
                pedidoActual.id(), pedidoActual.customerId(), pedidoActual.customerName(),
                pedidoActual.items(), pedidoActual.subtotal(), pedidoActual.discountAmount(),
                pedidoActual.total(), EstadoPedido.DISPATCHED, // <--- Cambio de estado
                pedidoActual.deliveryMethod(), pedidoActual.createdAt(), pedidoActual.createdBy()
        );
        pedidoDAO.update(pedidoActual.id(), pedidoDespachado);

        mostrarAlerta("Éxito", "El pedido ha sido despachado y el stock actualizado.");
        vboxResults.getChildren().clear();
    }

    // (Aquí iría mostrarDetallesCompra y procesarRecepcion siguiendo la misma lógica)

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    //METODO PROVISIONAL PERO ERRONEO
    @FXML
    public void handleLogout() {
        try {
            // 1. Preparamos la carga de la vista de Login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // 2. Obtenemos la ventana actual (Stage) usando el label del usuario
            Stage stage = (Stage) lblUsuario.getScene().getWindow();

            // 3. Cambiamos la escena y reseteamos el título
            stage.setTitle("Inicio de Sesión - Constructora");
            stage.setScene(scene);
            stage.centerOnScreen(); // Para que el login vuelva a quedar centrado

        } catch (IOException e) {
            // Si algo falla al cargar el FXML, avisamos al usuario
            mostrarAlerta("Error de Navegación", "No se pudo cerrar la sesión correctamente.");
            e.printStackTrace();
        }
    }
    private void mostrarDetallesCompra() {
        // Mostramos info básica del proveedor y el total
        vboxResults.getChildren().add(new Label("Proveedor: " + compraActual.supplierName()));
        vboxResults.getChildren().add(new Label("Total Orden: $" + compraActual.total()));

        // Botón para confirmar que llegó todo el material
        Button btnConfirmar = new Button("Confirmar Recepción Completa");
        btnConfirmar.setStyle("-fx-background-color: #0a0a0a; -fx-text-fill: white; -fx-cursor: hand;");
        btnConfirmar.setOnAction(e -> procesarRecepcion());
        vboxResults.getChildren().add(btnConfirmar);
    }

    private void procesarRecepcion() {
        // 1. Afectar Stock: En recepción, el stock SUMA
        for (DetalleCompra item : compraActual.items()) {
            Optional<Producto> pOpt = productoDAO.getAll().stream()
                    .filter(p -> p.id().equals(item.productId())).findFirst();

            if (pOpt.isPresent()) {
                Producto p = pOpt.get();
                // Creamos la versión actualizada del producto con más stock
                Producto actualizado = new Producto(
                        p.id(), p.nombre(), p.descripcion(), p.precio(),
                        p.stock() + item.quantity(), // <--- AQUÍ SE SUMA
                        p.categoria(), p.unidad(), p.minStock(), p.descuento(),
                        p.imageUrl(), p.createdAt(), LocalDateTime.now().toString()
                );
                productoDAO.update(p.id(), actualizado);

                // 2. Registrar el movimiento (Auditoría)
                MovimientoInventario mov = new MovimientoInventario(
                        UUID.randomUUID().toString(),
                        p.id(), p.nombre(),
                        item.quantity(), // Positivo porque es entrada
                        TipoMovimiento.PURCHASE,
                        "Recepción Compra: " + compraActual.id(),
                        LocalDateTime.now().toString()
                );
                movimientoDAO.create(mov);
            }
        }

        // 3. Cambiar status de la Compra a RECEIVED
        Compra compraRecibida = new Compra(
                compraActual.id(), compraActual.supplierId(), compraActual.supplierName(),
                compraActual.items(), compraActual.total(),
                EstadoCompra.RECEIVED, // Estado final
                compraActual.createdAt(), compraActual.createdBy(),
                "Todo recibido correctamente" // Nota de discrepancia vacía
        );
        compraDAO.update(compraActual.id(), compraRecibida);

        mostrarAlerta("Éxito", "Inventario actualizado. El material ha ingresado al almacén.");
        vboxResults.getChildren().clear();
        txtSearchId.clear();
    }
}
