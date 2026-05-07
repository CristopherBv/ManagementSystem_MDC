package gestionmats.dao;

import gestionmats.model.Venta;

public class VentaDaoCsv extends AbstractDaoCsv<Venta> {

    public VentaDaoCsv() {
        super("dataBase/ventas.csv");
    }

    @Override
    protected Venta mapearDeCsv(String linea) {
        String[] datos = linea.split(",");
        try {
            // Formato CSV esperado:
            // idVenta,idCliente,idVendedor,fechaHora,tipoVenta,metodoPago,subtotal,descuento,total,estado
            int idVenta = Integer.parseInt(datos[0].trim());
            int idCliente = Integer.parseInt(datos[1].trim());
            int idVendedor = Integer.parseInt(datos[2].trim());
            String fechaHora = datos[3].trim();
            String tipoVenta = datos[4].trim();
            String metodoPago = datos[5].trim();
            double subtotal = Double.parseDouble(datos[6].trim());
            double descuento = Double.parseDouble(datos[7].trim());
            double total = Double.parseDouble(datos[8].trim());
            String estado = datos[9].trim();

            return new Venta(idVenta, idCliente, idVendedor, fechaHora,
                    tipoVenta, metodoPago, subtotal, descuento, total, estado);
        } catch (Exception e) {
            System.err.println("Error al parsear Venta en línea: " + linea);
            return null;
        }
    }

    @Override
    protected String mapearACsv(Venta v) {
        return v.getIdVenta() + "," +
                v.getIdCliente() + "," +
                v.getIdVendedor() + "," +
                v.getFechaHora() + "," +
                v.getTipoVenta() + "," +
                v.getMetodoPago() + "," +
                v.getSubtotal() + "," +
                v.getDescuento() + "," +
                v.getTotal() + "," +
                v.getEstado();
    }

    /**
     * Obtiene el último ID usado (para auto-incrementar)
     */
    public int obtenerUltimoId() {
        int maxId = 0;
        for (Venta v : this.listarTodos()) {
            if (v.getIdVenta() > maxId) {
                maxId = v.getIdVenta();
            }
        }
        return maxId;
    }

    /**
     * Lista las ventas de un cliente específico
     */
    public java.util.List<Venta> listarPorCliente(int idCliente) {
        java.util.List<Venta> resultado = new java.util.ArrayList<>();
        for (Venta v : this.listarTodos()) {
            if (v.getIdCliente() == idCliente) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    /**
     * Lista las ventas por rango de fechas
     */
    public java.util.List<Venta> listarPorFecha(String fechaInicio, String fechaFin) {
        java.util.List<Venta> resultado = new java.util.ArrayList<>();
        for (Venta v : this.listarTodos()) {
            String fecha = v.getFechaHora().split(" ")[0]; // Solo YYYY-MM-DD
            if (fecha.compareTo(fechaInicio) >= 0 && fecha.compareTo(fechaFin) <= 0) {
                resultado.add(v);
            }
        }
        return resultado;
    }

    @Override
    protected String obtenerId(Venta entidad) {
        return String.valueOf(entidad.getIdVenta());
    }
}