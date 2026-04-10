package gestionmats.dao;

import gestionmats.models.MovimientoInventario;
import java.util.List;

public interface IMovimientoInventarioDAO extends IDAO<MovimientoInventario> {
    // Podríamos añadir un método para filtrar por producto si fuera necesario
    List<MovimientoInventario> obtenerPorProducto(String productId);
}
