package gestionmats.models;
import java.util.List;

public record Pedido(
        String id,
        String customerId,
        String customerName,
        List<DetallePedido> items,
        double subtotal,
        double discountAmount,
        double total,
        EstadoPedido status, // Usando el Enum
        String deliveryMethod,
        String createdAt,
        String createdBy
) {}
