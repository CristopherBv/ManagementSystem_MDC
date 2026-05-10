package gestionmats.strategy;

public class PagoTarjetaDebito implements EstrategiaPago {
    private double saldoDisponible;

    public PagoTarjetaDebito(double saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    private boolean verificarSaldo(double monto) {
        return saldoDisponible >= monto;
    }

    @Override
    public boolean procesarPago(double monto) {
        if (verificarSaldo(monto)) {
            System.out.println("Pago con tarjeta de débito por $" + monto + " autorizado.");
            System.out.println("Saldo restante: $" + (saldoDisponible - monto));
            return true;
        }
        System.out.println("Saldo insuficiente para pago con débito.");
        return false;
    }

    @Override
    public String getNombreMetodo() {
        return "TARJETA_DEBITO";
    }
}