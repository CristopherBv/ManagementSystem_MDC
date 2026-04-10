package gestionmats.models;

public record MovimientoInventario(
        String id,
        String productId,
        String productName,
        int quantity,
        TipoMovimiento type, // Usando el Enum
        String reference,
        String createdAt
) {}