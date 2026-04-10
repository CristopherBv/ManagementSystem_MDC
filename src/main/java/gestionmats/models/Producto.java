package gestionmats.models;

/**
 * Record que representa la entidad Producto.
 * Al ser un record de Java 17, los getters se llaman igual que el atributo (ej. producto.nombre())
 */

public record Producto(
        String id,
        String nombre,
        String descripcion,
        double precio,
        int stock,
        String categoria,
        String unidad,
        int minStock,
        double descuento,
        String imageUrl,
        String createdAt,
        String updatedAt
) {}
