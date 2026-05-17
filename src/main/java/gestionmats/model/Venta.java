package gestionmats.model;

import gestionmats.strategy.EstrategiaPago;
import gestionmats.strategy.DescuentoCompuesto;
import gestionmats.strategy.DescuentoPorPromocion;
import gestionmats.strategy.DescuentoPorPuntos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int idVenta;
    private int idCliente;
    private int idVendedor;
    private String fechaHora;
    private String tipoVenta;
    private String metodoPago;
    private double subtotal;
    private double descuento;
    private double total;
    private String estado;

    private List<DetalleVenta> detalles;
    private EstrategiaPago estrategiaPago;
    private DescuentoCompuesto estrategiaDescuento;

    public Venta(int idVenta, int idCliente, int idVendedor, String fechaHora,
                 String tipoVenta, String metodoPago, double subtotal,
                 double descuento, double total, String estado) {
        this.idVenta = idVenta;
        this.idCliente = idCliente;
        this.idVendedor = idVendedor;
        this.fechaHora = fechaHora;
        this.tipoVenta = tipoVenta;
        this.metodoPago = metodoPago;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.estado = estado;
        this.detalles = new ArrayList<>();
        this.estrategiaDescuento = new DescuentoCompuesto();
    }

    public Venta(int idCliente, int idVendedor, String tipoVenta,
                 String metodoPago, double subtotal, double descuento, double total) {
        this.idCliente = idCliente;
        this.idVendedor = idVendedor;
        this.fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.tipoVenta = tipoVenta;
        this.metodoPago = metodoPago;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.total = total;
        this.estado = "COMPLETADA";
        this.detalles = new ArrayList<>();
        this.estrategiaDescuento = new DescuentoCompuesto();
    }

    // Getters y Setters
    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdVendedor() { return idVendedor; }
    public void setIdVendedor(int idVendedor) { this.idVendedor = idVendedor; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getTipoVenta() { return tipoVenta; }
    public void setTipoVenta(String tipoVenta) { this.tipoVenta = tipoVenta; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDescuento() { return descuento; }
    public void setDescuento(double descuento) { this.descuento = descuento; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    public void agregarDetalle(DetalleVenta detalle) { this.detalles.add(detalle); }

    public EstrategiaPago getEstrategiaPago() { return estrategiaPago; }
    public void setEstrategiaPago(EstrategiaPago estrategiaPago) { this.estrategiaPago = estrategiaPago; }

    public DescuentoCompuesto getEstrategiaDescuento() { return estrategiaDescuento; }
    public void setEstrategiaDescuento(DescuentoCompuesto estrategiaDescuento) {
        this.estrategiaDescuento = estrategiaDescuento;
    }

    public double getDescuentoPorPuntos() {
        if (estrategiaDescuento != null && estrategiaDescuento.tieneDescuentoPorPuntos()) {
            double subtotalTemp = this.subtotal;
            double descuentoManual = 0;
            if (estrategiaDescuento.tieneDescuentoManual()) {
                descuentoManual = new DescuentoPorPromocion(estrategiaDescuento.getPorcentajeTotal(), "").calcularDescuento(subtotalTemp);
            }
            return estrategiaDescuento.calcularDescuento(subtotalTemp) - descuentoManual;
        }
        return 0;
    }

    public double getDescuentoManualPesos() {
        if (estrategiaDescuento != null && estrategiaDescuento.tieneDescuentoManual()) {
            return new DescuentoPorPromocion(estrategiaDescuento.getPorcentajeTotal(), "").calcularDescuento(this.subtotal);
        }
        return 0;
    }

    public boolean procesarPago() {
        if (estrategiaPago != null) {
            return estrategiaPago.procesarPago(this.total);
        }
        System.out.println("No se ha definido una estrategia de pago.");
        return false;
    }

    @Override
    public String toString() {
        return "Venta #" + idVenta + " - Total: $" + total;
    }
}