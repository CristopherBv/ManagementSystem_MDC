package gestionmats.strategy;

public class DescuentoPorPuntos implements EstrategiaDescuento {
    private double puntosCliente;
    private static final double VALOR_PUNTO = 0.05;  // 1 punto = $0.05 MXN

    public DescuentoPorPuntos(double puntosCliente) {
        this.puntosCliente = puntosCliente;
    }

    @Override
    public double calcularDescuento(double subtotal) {
        double descuentoMaximo = puntosCliente * VALOR_PUNTO;
        return Math.min(descuentoMaximo, subtotal);
    }

    public double getPuntosUsados() {
        return puntosCliente;
    }
}