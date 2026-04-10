package gestionmats.dao;

import gestionmats.models.Pedido;
import gestionmats.models.DetallePedido;
import gestionmats.models.EstadoPedido;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO extends AbstractCsvDAO<Pedido> implements IPedidoDAO {
    private static PedidoDAO instancia;
    private final DetallePedidoDAO detalleDAO;

    private PedidoDAO() {
        super("pedidos.csv");
        this.detalleDAO = DetallePedidoDAO.getInstancia();
    }

    public static PedidoDAO getInstancia() {
        if (instancia == null) instancia = new PedidoDAO();
        return instancia;
    }

    @Override
    public synchronized Pedido create(Pedido pedido) {
        super.create(pedido);
        for (DetallePedido item : pedido.items()) {
            detalleDAO.create(item);
        }
        return pedido;
    }

    @Override
    public List<Pedido> getAll() {
        List<Pedido> pedidos = new ArrayList<>();
        for (Pedido p : super.getAll()) {
            List<DetallePedido> items = detalleDAO.obtenerPorPedidoId(p.id());
            pedidos.add(new Pedido(p.id(), p.customerId(), p.customerName(), items, p.subtotal(),
                    p.discountAmount(), p.total(), p.status(), p.deliveryMethod(),
                    p.createdAt(), p.createdBy()));
        }
        return pedidos;
    }

    @Override
    protected Pedido mapearDeCSV(String[] datos) {
        return new Pedido(datos[0], datos[1], datos[2], new ArrayList<>(), Double.parseDouble(datos[3]),
                Double.parseDouble(datos[4]), Double.parseDouble(datos[5]),
                EstadoPedido.valueOf(datos[6]), datos[7], datos[8], datos[9]);
    }

    @Override
    protected String mapearACSV(Pedido p) {
        return String.join(SEPARADOR, p.id(), p.customerId(), p.customerName(),
                String.valueOf(p.subtotal()), String.valueOf(p.discountAmount()),
                String.valueOf(p.total()), p.status().name(), p.deliveryMethod(),
                p.createdAt(), p.createdBy());
    }

    @Override
    protected String obtenerId(Pedido p) { return p.id(); }
}