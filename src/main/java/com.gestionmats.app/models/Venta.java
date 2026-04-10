package com.gestionmats.app.models;
import java.util.List;

public record Venta(
        String id,
        String orderId,
        String customerId,
        String customerName,
        List<DetalleVenta> items, // Aquí vive la lista de productos
        double subtotal,
        double discountAmount,
        double total,
        String paymentMethod,
        String createdAt,
        String createdBy
) {}