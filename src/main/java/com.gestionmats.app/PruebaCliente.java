package com.gestionmats.app;

import com.gestionmats.app.dao.ClienteDAO;
import com.gestionmats.app.models.Cliente;

public class PruebaCliente {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE CLIENTEDAO ===\n");

        // Obtener la instancia del DAO
        ClienteDAO dao = ClienteDAO.getInstancia();

        // Crear cliente de prueba
        Cliente cliente1 = new Cliente(
                "C-001",
                "Juan",
                "Perez",
                "juan.perez@mail.com",
                "555-1234",
                "Calle Principal 123",
                100,
                5.0,
                "2026-04-09"
        );

        // Guardar cliente
        dao.create(cliente1);
        System.out.println("Cliente guardado: " + cliente1.nombre() + " " + cliente1.apellidos());

        // Crear segundo cliente
        Cliente cliente2 = new Cliente(
                "C-002",
                "Ana",
                "Lopez",
                "ana.lopez@mail.com",
                "555-5678",
                "Av. Secundaria 456",
                250,
                10.0,
                "2026-04-09"
        );

        dao.create(cliente2);
        System.out.println("Cliente guardado: " + cliente2.nombre() + " " + cliente2.apellidos());

        // Leer todos los clientes
        System.out.println("\n=== LISTA DE CLIENTES ===");
        for (Cliente c : dao.getAll()) {
            System.out.println("- " + c.nombre() + " " + c.apellidos() +
                    " | Email: " + c.email() +
                    " | Puntos: " + c.puntosLealtad());
        }
    }
}
