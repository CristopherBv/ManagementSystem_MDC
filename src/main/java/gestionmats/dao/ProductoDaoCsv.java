package gestionmats.dao;

import gestionmats.model.Producto;

public class ProductoDaoCsv extends AbstractDaoCsv<Producto> {

    public ProductoDaoCsv() {
        // Apuntamos a la base de datos fuera del src
        super("dataBase/Productos.csv");
    }

    @Override
    protected Producto mapearDeCsv(String linea) {
        // El separador definido en tu CsvUtils es ","
        String[] datos = linea.split(",");

        try {
            // El orden estricto del CSV será:
            // idProducto, nombre, marca, categoria, precioVenta, stockMaximo, stockActual, unidadMedida, descuento
            String idProducto = datos[0].trim();
            String nombre = datos[1].trim();
            String marca = datos[2].trim();
            String categoria = datos[3].trim();
            double precioVenta = Double.parseDouble(datos[4].trim());
            int stockMaximo = Integer.parseInt(datos[5].trim());
            int stockActual = Integer.parseInt(datos[6].trim());
            String unidadMedida = datos[7].trim();
            double descuento = Double.parseDouble(datos[8].trim());

            return new Producto(idProducto, nombre, marca, categoria, precioVenta,
                    stockMaximo, stockActual, unidadMedida, descuento);
        } catch (Exception e) {
            System.err.println("Error al parsear el producto en la línea: " + linea);
            return null;
        }
    }

    @Override
    protected String mapearACsv(Producto p) {
        // Convertimos el objeto de vuelta a un String separado por comas
        return p.getIdProducto() + "," +
                p.getNombre() + "," +
                p.getMarca() + "," +
                p.getCategoria() + "," +
                p.getPrecioVenta() + "," +
                p.getStockMaximo() + "," +
                p.getStockActual() + "," +
                p.getUnidadMedida() + "," +
                p.getDescuento();
    }

    /**
     * Méthod específico para buscar productos por nombre,
     * ideal para usarlo más adelante en la barra de búsqueda de la UI.
     */
    public Producto buscarPorNombre(String nombre) {
        for (Producto p : this.listarTodos()) {
            if (p.getNombre().toLowerCase().contains(nombre.toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    @Override
    protected String obtenerId(Producto entidad) {
        return entidad.getIdProducto();
    }

    /**
     * Busca un producto por su ID (ej: "M-001")
     */
    public Producto buscarPorId(String idProducto) {
        for (Producto p : this.listarTodos()) {
            if (p.getIdProducto().equals(idProducto)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Elimina un producto por su ID alfanumérico (ej: "M-001")
     */
    public boolean eliminar(String idProducto) {
        return gestionmats.utils.CsvUtils.eliminarLinea(this.ruta, idProducto);
    }

    /**
     * Genera automáticamente el siguiente ID disponible (ej. si existe M008, devuelve M009).
     */
    public String generarSiguienteId() {
        int maxNumero = 0;
        for (Producto p : this.listarTodos()) {
            try {
                // Quitamos la "M" y convertimos el resto a número ("M008" -> 8)
                int numeroActual = Integer.parseInt(p.getIdProducto().replace("M", ""));
                if (numeroActual > maxNumero) {
                    maxNumero = numeroActual;
                }
            } catch (NumberFormatException e) {
                // Ignorar si hay algún ID con formato extraño
            }
        }
        // Retornamos el máximo + 1 con formato de 3 dígitos (ej. M009)
        return String.format("M%03d", maxNumero + 1);
    }

    /**
     * Verifica si ya existe un producto con exactamente el mismo nombre (ignorando mayúsculas).
     */
    public boolean existeProductoPorNombreExacto(String nombreNuevo) {
        for (Producto p : this.listarTodos()) {
            if (p.getNombre().equalsIgnoreCase(nombreNuevo.trim())) {
                return true;
            }
        }
        return false;
    }

}