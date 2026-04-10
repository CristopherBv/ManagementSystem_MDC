package gestionmats.dao;

import gestionmats.models.DetalleVenta;
import java.util.List;

public interface IDetalleVentaDAO extends IDAO<DetalleVenta> {
    // Este método es exclusivo de los Detalles de Venta, por eso va aquí y no en el padre
    List<DetalleVenta> obtenerPorVentaId(String idVenta);
}