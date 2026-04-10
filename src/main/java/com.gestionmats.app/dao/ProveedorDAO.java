package com.gestionmats.app.dao;

import com.gestionmats.app.models.Proveedor;

public class ProveedorDAO extends AbstractCsvDAO<Proveedor>{
    // --- Singleton ---
    private static ProveedorDAO instancia;

    private ProveedorDAO() {
        super("proveedores.csv");
    }

    public static ProveedorDAO getInstancia() {
        if (instancia == null) {
            instancia = new ProveedorDAO();
        }
        return instancia;
    }

    // --- Métodos abstractos obligatorios ---

    @Override
    protected Proveedor mapearDeCSV(String[] datos) {
        return new Proveedor(
                datos[0],  // id
                datos[1],  // nombre
                datos[2],  // email
                datos[3],  // telefono
                datos[4],  // direccion
                datos[5]   // createdAt
        );
    }

    @Override
    protected String mapearACSV(Proveedor proveedor) {
        return String.join(SEPARADOR,
                proveedor.id(),
                proveedor.nombre(),
                proveedor.email(),
                proveedor.telefono(),
                proveedor.direccion(),
                proveedor.createdAt()
        );
    }

    @Override
    protected String obtenerId(Proveedor proveedor) {
        return proveedor.id();
    }
}
