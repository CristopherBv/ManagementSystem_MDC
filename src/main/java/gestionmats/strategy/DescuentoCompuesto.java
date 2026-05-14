package gestionmats.strategy;

import java.util.ArrayList;
import java.util.List;

public class DescuentoCompuesto implements EstrategiaDescuento {
    private List<EstrategiaDescuento> estrategias = new ArrayList<>();

    public void agregarDescuento(EstrategiaDescuento estrategia) {
        if (estrategia != null && !(estrategia instanceof SinDescuento)) {
            this.estrategias.add(estrategia);
        }
    }

    public void limpiar() {
        this.estrategias.clear();
    }

    /**
     * Calcula el descuento total aplicando TODOS los descuentos
     * sobre el SUBTOTAL ORIGINAL (no secuencialmente)
     */
    @Override
    public double calcularDescuento(double subtotal) {
        double totalDescuento = 0;
        for (EstrategiaDescuento e : estrategias) {
            totalDescuento += e.calcularDescuento(subtotal);
        }
        // No permitir que el descuento supere el subtotal
        return Math.min(totalDescuento, subtotal);
    }

    public double getPorcentajeTotal() {
        double porcentaje = 0;
        for (EstrategiaDescuento e : estrategias) {
            if (e instanceof DescuentoPorPromocion) {
                porcentaje += ((DescuentoPorPromocion) e).getPorcentaje();
            }
        }
        return porcentaje;
    }

    public double getPuntosUsados() {
        double puntos = 0;
        for (EstrategiaDescuento e : estrategias) {
            if (e instanceof DescuentoPorPuntos) {
                puntos += ((DescuentoPorPuntos) e).getPuntosUsados();
            }
        }
        return puntos;
    }

    public double getDescuentoPuntosPesos(double subtotal) {
        double descuento = 0;
        for (EstrategiaDescuento e : estrategias) {
            if (e instanceof DescuentoPorPuntos) {
                descuento += e.calcularDescuento(subtotal);
            }
        }
        return descuento;
    }

    public boolean tieneDescuentoManual() {
        for (EstrategiaDescuento e : estrategias) {
            if (e instanceof DescuentoPorPromocion) {
                return true;
            }
        }
        return false;
    }

    public boolean tieneDescuentoPorPuntos() {
        for (EstrategiaDescuento e : estrategias) {
            if (e instanceof DescuentoPorPuntos) {
                return true;
            }
        }
        return false;
    }
}