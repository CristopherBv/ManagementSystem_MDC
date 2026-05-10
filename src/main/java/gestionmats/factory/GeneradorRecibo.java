package gestionmats.factory;

import gestionmats.model.Venta;
import java.util.Date;

public abstract class GeneradorRecibo {
    protected int idRecibo;
    protected Date fechaEmision;
    protected double total;
    protected Venta venta;

    public GeneradorRecibo(Venta venta) {
        this.venta = venta;
        this.idRecibo = generarIdUnico();
        this.fechaEmision = new Date();
        this.total = venta.getTotal();
    }

    private int generarIdUnico() {
        return (int) (System.currentTimeMillis() % 1000000);
    }

    public abstract String generarRecibo();

    public void imprimir() {
        System.out.println(generarRecibo());
    }

    public double getTotal() {
        return total;
    }

    public int getIdRecibo() {
        return idRecibo;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }
}