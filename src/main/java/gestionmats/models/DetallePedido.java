package gestionmats.models;

public record DetallePedido(
        String idPedido,
        String productId,
        String productName,
        int quantity,
        double unitPrice,
        double discount,
        double subtotal
) {}
