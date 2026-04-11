package gestionmats.controllers;

import gestionmats.dao.*;
import gestionmats.models.*;
import gestionmats.utils.NavigationUtils;
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

    private final PedidoDAO pedidoDAO = PedidoDAO.getInstancia();
    private final CompraDAO compraDAO = CompraDAO.getInstancia();
    private final ProductoDAO productoDAO = ProductoDAO.getInstancia();
    private final MovimientoInventarioDAO movimientoDAO = MovimientoInventarioDAO.getInstancia();

    private Pedido pedidoActual = null;
    private Compra compraActual = null;
    private Usuario usuarioLogueado;

    public void initData(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.lblUsuario.setText("Sesión activa: " + usuario.nombre());
    }

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
            Optional<Pedido> res = pedidoDAO.getAll().stream().filter(p -> p.id().equals(id)).findFirst();
            if (res.isPresent()) {
                pedidoActual = res.get();
                mostrarDetallesPedido();
            } else {
                mostrarAlerta("No encontrado", "El pedido no existe o no está pendiente.");
            }
        } else {
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
        btnConfirmar.setStyle("-fx-background-color: #0a0a0a; -fx-text-fill: white; -fx-cursor: hand;");
        btnConfirmar.setOnAction(e -> procesarDespacho());
        vboxResults.getChildren().add(btnConfirmar);
    }

    private void procesarDespacho() {
        for (DetallePedido item : pedidoActual.items()) {
            Optional<Producto> pOpt = productoDAO.getAll().stream()
                    .filter(p -> p.id().equals(item.productId())).findFirst();

            if (pOpt.isPresent()) {
                Producto p = pOpt.get();
                Producto actualizado = new Producto(
                        p.id(), p.nombre(), p.descripcion(), p.precio(),
                        p.stock() - item.quantity(),
                        p.categoria(), p.unidad(), p.minStock(), p.descuento(),
                        p.imageUrl(), p.createdAt(), LocalDateTime.now().toString()
                );
                productoDAO.update(p.id(), actualizado);

                MovimientoInventario mov = new MovimientoInventario(
                        UUID.randomUUID().toString(),
                        p.id(), p.nombre(), -item.quantity(),
                        TipoMovimiento.SALE,
                        "Despacho Pedido: " + pedidoActual.id(),
                        LocalDateTime.now().toString()
                );
                movimientoDAO.create(mov);
            }
        }

        Pedido pedidoDespachado = new Pedido(
                pedidoActual.id(), pedidoActual.customerId(), pedidoActual.customerName(),
                pedidoActual.items(), pedidoActual.subtotal(), pedidoActual.discountAmount(),
                pedidoActual.total(), EstadoPedido.DISPATCHED,
                pedidoActual.deliveryMethod(), pedidoActual.createdAt(), pedidoActual.createdBy()
        );
        pedidoDAO.update(pedidoActual.id(), pedidoDespachado);

        mostrarAlerta("Éxito", "El pedido ha sido despachado y el stock actualizado.");
        vboxResults.getChildren().clear();
        txtSearchId.clear();
    }

    private void mostrarDetallesCompra() {
        vboxResults.getChildren().add(new Label("Proveedor: " + compraActual.supplierName()));
        vboxResults.getChildren().add(new Label("Total Orden: $" + compraActual.total()));

        Button btnConfirmar = new Button("Confirmar Recepción Completa");
        btnConfirmar.setStyle("-fx-background-color: #0a0a0a; -fx-text-fill: white; -fx-cursor: hand;");
        btnConfirmar.setOnAction(e -> procesarRecepcion());
        vboxResults.getChildren().add(btnConfirmar);
    }

    private void procesarRecepcion() {
        for (DetalleCompra item : compraActual.items()) {
            Optional<Producto> pOpt = productoDAO.getAll().stream()
                    .filter(p -> p.id().equals(item.productId())).findFirst();

            if (pOpt.isPresent()) {
                Producto p = pOpt.get();
                Producto actualizado = new Producto(
                        p.id(), p.nombre(), p.descripcion(), p.precio(),
                        p.stock() + item.quantity(),
                        p.categoria(), p.unidad(), p.minStock(), p.descuento(),
                        p.imageUrl(), p.createdAt(), LocalDateTime.now().toString()
                );
                productoDAO.update(p.id(), actualizado);

                MovimientoInventario mov = new MovimientoInventario(
                        UUID.randomUUID().toString(),
                        p.id(), p.nombre(),
                        item.quantity(),
                        TipoMovimiento.PURCHASE,
                        "Recepción Compra: " + compraActual.id(),
                        LocalDateTime.now().toString()
                );
                movimientoDAO.create(mov);
            }
        }

        Compra compraRecibida = new Compra(
                compraActual.id(), compraActual.supplierId(), compraActual.supplierName(),
                compraActual.items(), compraActual.total(),
                EstadoCompra.RECEIVED,
                compraActual.createdAt(), compraActual.createdBy(),
                "Todo recibido correctamente"
        );
        compraDAO.update(compraActual.id(), compraRecibida);

        mostrarAlerta("Éxito", "Inventario actualizado. El material ha ingresado al almacén.");
        vboxResults.getChildren().clear();
        txtSearchId.clear();
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML public void handleLogout() {
        NavigationUtils.irALogin(lblUsuario);
    }
}