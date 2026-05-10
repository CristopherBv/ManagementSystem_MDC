package gestionmats.strategy;

public interface EstrategiaPago {
    boolean procesarPago(double monto);
    String getNombreMetodo();
}