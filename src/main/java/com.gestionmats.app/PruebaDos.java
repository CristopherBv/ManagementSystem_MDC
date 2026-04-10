package com.gestionmats.app;

import com.gestionmats.app.dao.ProveedorDAO;
import com.gestionmats.app.models.Proveedor;

public class PruebaDos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ProveedorDAO dao = ProveedorDAO.getInstancia();

        // ─── 1. CREAR proveedores ────────────────────────────────────────────

        Proveedor p1 = new Proveedor(
                "PROV-001", "Cementos del Sur S.A.",
                "ventas@cementosdelsur.com", "999-111-2233",
                "Calle Industria 45, Mérida", "2026-04-09"
        );
        Proveedor p2 = new Proveedor(
                "PROV-002", "Materiales Hernández",
                "contacto@mathernandez.mx", "999-444-5566",
                "Av. Periférico 120, Mérida", "2026-04-09"
        );
        Proveedor p3 = new Proveedor(
                "PROV-003", "Ferretera Yucatán",
                "info@ferreyuc.com", "999-777-8899",
                "Calle 60 #300, Centro, Mérida", "2026-04-09"
        );

        dao.create(p1);
        dao.create(p2);
        dao.create(p3);

        // ─── 2. LEER todos ───────────────────────────────────────────────────
        System.out.println("\n=== LEER TODOS ===");
        for (Proveedor p : dao.getAll()) {
            System.out.printf("- [%s] %s | Tel: %s | Email: %s%n",
                    p.id(), p.nombre(), p.telefono(), p.email());
        }

        // ─── 3. ELIMINAR uno ─────────────────────────────────────────────────
        System.out.println("\n=== ELIMINAR PROV-002 ===");
        boolean eliminado = dao.delete("PROV-002");
        System.out.println(eliminado
                ? "Proveedor eliminado correctamente."
                : "No se encontró el proveedor.");

        // ─── 4. VERIFICAR eliminación ────────────────────────────────────────
        System.out.println("\n=== LISTA FINAL ===");
        for (Proveedor p : dao.getAll()) {
            System.out.printf("- [%s] %s | Dirección: %s%n",
                    p.id(), p.nombre(), p.direccion());
        }
    }

}