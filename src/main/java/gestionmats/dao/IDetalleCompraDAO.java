package gestionmats.dao;
import gestionmats.models.DetalleCompra;
import java.util.List;

public interface IDetalleCompraDAO extends IDAO<DetalleCompra> {
    List<DetalleCompra> obtenerPorCompraId(String idCompra);
}
