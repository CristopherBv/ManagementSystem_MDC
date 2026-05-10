package gestionmats.strategy;

public class DescuentoPorPuntos implements EstrategiaDescuento {
    private double puntosCliente;
    private static final double VALOR_PUNTO = 0.01;

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