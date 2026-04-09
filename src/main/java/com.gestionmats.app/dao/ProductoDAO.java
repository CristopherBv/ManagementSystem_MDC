package com.gestionmats.app.dao;

import com.gestionmats.app.models.Producto;

public class ProductoDAO extends AbstractCsvDAO<Producto> {

    private static ProductoDAO instancia;

    // Le pasamos al Padre el nombre del archivo
    private ProductoDAO() {
        super("productos.csv");
    }

    public static ProductoDAO getInstancia() {
        if (instancia == null) {
            instancia = new ProductoDAO();
        }
        return instancia;
    }

    @Override
    protected Producto mapearDeCSV(String[] datos) {
        return new Producto(
                datos[0], datos[1], datos[2],
                Double.parseDouble(datos[3]),
                Integer.parseInt(datos[4]),
                datos[5], datos[6],
                Integer.parseInt(datos[7]),
                Double.parseDouble(datos[8]),
                datos[9], datos[10], datos[11]
        );
    }

    @Override
    protected String mapearACSV(Producto p) {
        return String.join(SEPARADOR,
                p.id(), p.nombre(), p.descripcion(),
                String.valueOf(p.precio()), String.valueOf(p.stock()),
                p.categoria(), p.unidad(), String.valueOf(p.minStock()),
                String.valueOf(p.descuento()), p.imageUrl(),
                p.createdAt(), p.updatedAt()
        );
    }

    @Override
    protected String obtenerId(Producto p) {
        return p.id();
    }
}