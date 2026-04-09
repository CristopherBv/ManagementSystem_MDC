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
                datos[0], //id
                datos[1], //nombre
                datos[2], //descripcion
                Double.parseDouble(datos[3]), //precio
                Integer.parseInt(datos[4]), //stock
                datos[5], //categoría
                datos[6], //unidad
                Integer.parseInt(datos[7]), //stock minimo
                Double.parseDouble(datos[8]), //descuento
                datos[9], //imagen
                datos[10], //createdAt
                datos[11] //updatedAt
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