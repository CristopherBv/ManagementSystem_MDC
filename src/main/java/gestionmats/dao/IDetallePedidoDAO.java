package gestionmats.dao;
import gestionmats.models.DetallePedido;
import java.util.List;

public interface IDetallePedidoDAO extends IDAO<DetallePedido> {
    List<DetallePedido> obtenerPorPedidoId(String idPedido);
}