package gestionmats.models;
import java.util.List;

public record Compra(
        String id,
        String supplierId,
        String supplierName,
        List<DetalleCompra> items,
        double total,
        EstadoCompra status, // Usando el Enum
        String createdAt,
        String createdBy,
        String discrepancies
) {}