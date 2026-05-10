package gestionmats.strategy;

public class PagoTarjetaCredito implements EstrategiaPago {
    private String numeroTarjeta;

    public PagoTarjetaCredito(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    private boolean validarTarjeta() {
        // Validación básica: número no vacío y longitud mínima
        return numeroTarjeta != null && numeroTarjeta.trim().length() >= 15;
    }

    @Override
    public boolean procesarPago(double monto) {
        if (validarTarjeta()) {
            System.out.println("Pago con tarjeta de crédito por $" + monto + " autorizado.");
            return true;
        }
        System.out.println("Tarjeta de crédito inválida.");
        return false;
    }

    @Override
    public String getNombreMetodo() {
        return "TARJETA_CREDITO";
    }
}