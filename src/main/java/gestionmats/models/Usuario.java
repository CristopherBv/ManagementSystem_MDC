package gestionmats.models;

public record Usuario(
        String id,
        String nombre,
        String username,
        String password, // Nota de Senior: En el mundo real esto iría encriptado (Hash), pero para el CSV escolar en texto plano está bien.
        Rol rol       // "GERENTE", "VENDEDOR", o "ALMACENISTA"
) {}
