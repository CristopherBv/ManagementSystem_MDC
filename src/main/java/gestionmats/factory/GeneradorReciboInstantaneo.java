package gestionmats.factory;

import gestionmats.model.DetalleVenta;
import gestionmats.model.Venta;
import gestionmats.strategy.PagoEfectivo;
import java.text.SimpleDateFormat;

public class GeneradorReciboInstantaneo extends GeneradorRecibo {

    public GeneradorReciboInstantaneo(Venta venta) {
        super(venta);
    }

    @Override
    public String generarRecibo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════\n");
        sb.append("         TICKET DE VENTA - INSTANTANEA         \n");
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("Recibo #: ").append(idRecibo).append("\n");
        sb.append("Fecha: ").append(sdf.format(fechaEmision)).append("\n");
        sb.append("───────────────────────────────────────────────\n");
        sb.append("PRODUCTOS:\n");

        for (DetalleVenta detalle : venta.getDetalles()) {
            sb.append(String.format("  %d x %-25s $%.2f\n",
                    detalle.getCantidad(),
                    detalle.getNombreProducto(),
                    detalle.getPrecioUnitario()));
        }

        sb.append("───────────────────────────────────────────────\n");
        sb.append(String.format("SUBTOTAL: $%.2f\n", venta.getSubtotal()));

        // Mostrar descuento por puntos si existe
        double descuentoPuntos = venta.getDescuentoPorPuntos();
        if (descuentoPuntos > 0) {
            sb.append(String.format("DESCUENTO POR PUNTOS: -$%.2f\n", descuentoPuntos));
        }

        // Mostrar descuento porcentual si existe
        if (venta.getDescuento() > 0) {
            sb.append(String.format("DESCUENTO POR PROMOCION: %.1f%%\n", venta.getDescuento()));
        }

        sb.append(String.format("TOTAL: $%.2f\n", venta.getTotal()));
        sb.append("MÉTODO DE PAGO: ").append(venta.getMetodoPago()).append("\n");

        // Mostrar cambio si el pago fue en efectivo
        if (venta.getEstrategiaPago() instanceof PagoEfectivo) {
            PagoEfectivo pagoEfectivo = (PagoEfectivo) venta.getEstrategiaPago();
            double cambio = pagoEfectivo.calcularCambio(venta.getTotal());
            sb.append("EFECTIVO RECIBIDO: $").append(String.format("%.2f", pagoEfectivo.getMontoRecibido())).append("\n");
            sb.append("CAMBIO: $").append(String.format("%.2f", cambio)).append("\n");
        }

        sb.append("───────────────────────────────────────────────\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("═══════════════════════════════════════════════\n");

        return sb.toString();
    }
}