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
            System.out.println("Operación exitosa, el banco aprobó la transacción por $" + monto);
            return true;
        }
        System.out.println("Operación fallida, el banco rechazó la transacción.");
        return false;
    }

    @Override
    public String getNombreMetodo() {
        return "TARJETA_CREDITO";
    }
}