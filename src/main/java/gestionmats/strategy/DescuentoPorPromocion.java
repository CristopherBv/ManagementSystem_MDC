package gestionmats.strategy;

public class DescuentoPorPromocion implements EstrategiaDescuento {
    private double porcentaje;
    private String nombrePromocion;

    public DescuentoPorPromocion(double porcentaje, String nombrePromocion) {
        this.porcentaje = porcentaje;
        this.nombrePromocion = nombrePromocion;
    }

    @Override
    public double calcularDescuento(double subtotal) {
        return subtotal * (porcentaje / 100);
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public String getNombrePromocion() {
        return nombrePromocion;
    }
}