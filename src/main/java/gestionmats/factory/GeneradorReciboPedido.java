package gestionmats.factory;

import gestionmats.model.DetalleVenta;
import gestionmats.model.Venta;
import java.text.SimpleDateFormat;

public class GeneradorReciboPedido extends GeneradorRecibo {

    private int ordenRef;
    private String fechaEntregaEstimada;

    public GeneradorReciboPedido(Venta venta, int ordenRef, String fechaEntregaEstimada) {
        super(venta);
        this.ordenRef = ordenRef;
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    @Override
    public String generarRecibo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════\n");
        sb.append("         COMPROBANTE DE PEDIDO                 \n");
        sb.append("═══════════════════════════════════════════════\n");
        sb.append("Pedido #: ").append(idRecibo).append("\n");
        sb.append("Referencia de orden: ").append(ordenRef).append("\n");
        sb.append("Fecha: ").append(sdf.format(fechaEmision)).append("\n");
        sb.append("Fecha entrega estimada: ").append(fechaEntregaEstimada).append("\n");
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
        sb.append(String.format("DESCUENTO: %.1f%%\n", venta.getDescuento()));
        sb.append(String.format("TOTAL: $%.2f\n", venta.getTotal()));
        sb.append("───────────────────────────────────────────────\n");
        sb.append("El pedido será procesado en 24 hrs.\n");
        sb.append("═══════════════════════════════════════════════\n");

        return sb.toString();
    }
}