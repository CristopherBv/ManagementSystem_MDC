package com.gestionmats.app.models;

public record DetalleVenta(
        String idVenta,    // "foreign key" que lo conecta con la Venta
        String productId,
        String productName,
        int quantity,
        double unitPrice,
        double discount,
        double subtotal
) {}
