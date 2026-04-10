package com.gestionmats.app;

import com.gestionmats.app.dao.IProductoDAO;
import com.gestionmats.app.dao.ProductoDAO;
import com.gestionmats.app.models.Producto;

public class PruebaUno {
    public static void main(String[] args) {
        // Obtenemos la instancia del DAO usando Singleton
        IProductoDAO dao = ProductoDAO.getInstancia();

        // Creamos un producto de prueba
        Producto nuevoCemento = new Producto(
                "P-009", "Cemento Cruz Azulito Pro", "Bulto de 800kg",
                210.50, 900, "Materiales Básicos", "Bulto",
                60, 0.0, "img/cemento.png", "2026-04-08", "2026-04-08"
        );
        // Lo guardamos en el CSV
        dao.create(nuevoCemento);

        // Lo leemos para confirmar
        System.out.println("Productos en la base de datos CSV:");
        for (Producto p : dao.getAll()) {
            System.out.println("- " + p.nombre() + " | Stock: " + p.stock() + " | Precio: $" + p.precio());
        }
    }
}