package gestionmats.models;

public record DetalleCompra(
        String idCompra,
        String productId,
        String productName,
        int quantity,
        double unitPrice,
        double discount,
        double subtotal
) {}