package gestionmats.models;

public record Cliente(
        String id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        String direccion,
        int puntosLealtad,
        double porcentajeDescuento,
        String createdAt
) {}