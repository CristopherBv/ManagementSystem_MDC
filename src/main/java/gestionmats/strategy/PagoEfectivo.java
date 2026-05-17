package gestionmats.strategy;

public class PagoEfectivo implements EstrategiaPago {
    private double montoRecibido;

    public PagoEfectivo(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public double calcularCambio(double monto) {
        return montoRecibido - monto;
    }

    @Override
    public boolean procesarPago(double monto) {
        if (montoRecibido >= monto) {
            System.out.println("Pago en efectivo aprobado. Cambio: $" + calcularCambio(monto));
            return true;
        }
        System.out.println("Monto insuficiente. Faltan: $" + (monto - montoRecibido));
        return false;
    }

    @Override
    public String getNombreMetodo() {
        return "EFECTIVO";
    }
}