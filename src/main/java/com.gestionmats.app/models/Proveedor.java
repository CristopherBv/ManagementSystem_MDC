package com.gestionmats.app.models;

public record Proveedor(
        String id,
        String nombre,
        String email,
        String telefono,
        String direccion,
        String createdAt
) {}