package gestionmats.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Venta {
    private int idVenta;
    private int idCliente;        // Cliente que compra (id=0 para cliente genérico)
    private int idVendedor;       // Usuario que realizó la venta
    private String fechaHora;     // Formato: "yyyy-MM-dd HH:mm:ss"
    private String tipoVenta;     // "INSTANTANEA" o "PEDIDO"
    private String metodoPago;    // "EFECTIVO", "TARJETA", "CREDITO"
    private double subtotal;
    private double descuento;      // Porcentaje aplicado (ej: 10.0 = 10%)
    private double total;
    private String estado;         // "COMPLETADA", "PENDIENTE", "CANCELADA"

    // Constructor completo
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
    }

    // Constructor para crear una venta nueva (sin ID aún)
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

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", idCliente=" + idCliente +
                ", idVendedor=" + idVendedor +
                ", fechaHora='" + fechaHora + '\'' +
                ", total=" + total +
                '}';
    }
}