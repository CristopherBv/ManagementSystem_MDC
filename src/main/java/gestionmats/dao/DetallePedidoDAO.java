package gestionmats.dao;

import gestionmats.models.DetallePedido;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO extends AbstractCsvDAO<DetallePedido> implements IDetallePedidoDAO {
    private static DetallePedidoDAO instancia;
    private DetallePedidoDAO() { super("detalles_pedido.csv"); }

    public static DetallePedidoDAO getInstancia() {
        if (instancia == null) instancia = new DetallePedidoDAO();
        return instancia;
    }

    @Override
    public List<DetallePedido> obtenerPorPedidoId(String idPedido) {
        List<DetallePedido> filtrados = new ArrayList<>();
        for (DetallePedido d : getAll()) {
            if (d.idPedido().equals(idPedido)) filtrados.add(d);
        }
        return filtrados;
    }

    @Override
    protected DetallePedido mapearDeCSV(String[] datos) {
        return new DetallePedido(datos[0], datos[1], datos[2], Integer.parseInt(datos[3]),
                Double.parseDouble(datos[4]), Double.parseDouble(datos[5]), Double.parseDouble(datos[6]));
    }

    @Override
    protected String mapearACSV(DetallePedido d) {
        return String.join(SEPARADOR, d.idPedido(), d.productId(), d.productName(),
                String.valueOf(d.quantity()), String.valueOf(d.unitPrice()),
                String.valueOf(d.discount()), String.valueOf(d.subtotal()));
    }

    @Override
    protected String obtenerId(DetallePedido d) { return d.idPedido() + "_" + d.productId(); }
}