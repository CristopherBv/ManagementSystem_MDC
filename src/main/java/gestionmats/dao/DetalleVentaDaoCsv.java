package gestionmats.dao;

import gestionmats.model.DetalleVenta;

public class DetalleVentaDaoCsv extends AbstractDaoCsv<DetalleVenta> {

    public DetalleVentaDaoCsv() {
        super("dataBase/detalleVenta.csv");
    }

    @Override
    protected DetalleVenta mapearDeCsv(String linea) {
        String[] datos = linea.split(",");
        try {
            // Formato CSV esperado:
            // idDetalle,idVenta,idProducto,nombreProducto,cantidad,precioUnitario,descuentoAplicado,subtotal,total
            int idDetalle = Integer.parseInt(datos[0].trim());
            int idVenta = Integer.parseInt(datos[1].trim());
            String idProducto = datos[2].trim();
            String nombreProducto = datos[3].trim();
            int cantidad = Integer.parseInt(datos[4].trim());
            double precioUnitario = Double.parseDouble(datos[5].trim());
            double descuentoAplicado = Double.parseDouble(datos[6].trim());
            double subtotal = Double.parseDouble(datos[7].trim());
            double total = Double.parseDouble(datos[8].trim());

            return new DetalleVenta(idDetalle, idVenta, idProducto, nombreProducto,
                    cantidad, precioUnitario, descuentoAplicado, subtotal, total);
        } catch (Exception e) {
            System.err.println("Error al parsear DetalleVenta en línea: " + linea);
            return null;
        }
    }

    @Override
    protected String mapearACsv(DetalleVenta d) {
        return d.getIdDetalle() + "," +
                d.getIdVenta() + "," +
                d.getIdProducto() + "," +
                d.getNombreProducto() + "," +
                d.getCantidad() + "," +
                d.getPrecioUnitario() + "," +
                d.getDescuentoAplicado() + "," +
                d.getSubtotal() + "," +
                d.getTotal();
    }

    /**
     * Obtiene el último ID usado (para auto-incrementar)
     */
    public int obtenerUltimoId() {
        int maxId = 0;
        for (DetalleVenta d : this.listarTodos()) {
            if (d.getIdDetalle() > maxId) {
                maxId = d.getIdDetalle();
            }
        }
        return maxId;
    }

    /**
     * Lista todos los detalles de una venta específica
     */
    public java.util.List<DetalleVenta> listarPorIdVenta(int idVenta) {
        java.util.List<DetalleVenta> resultado = new java.util.ArrayList<>();
        for (DetalleVenta d : this.listarTodos()) {
            if (d.getIdVenta() == idVenta) {
                resultado.add(d);
            }
        }
        return resultado;
    }

    @Override
    protected String obtenerId(DetalleVenta entidad) {
        return String.valueOf(entidad.getIdDetalle());
    }
}