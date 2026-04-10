package gestionmats.dao;

import gestionmats.models.MovimientoInventario;
import gestionmats.models.TipoMovimiento;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO extends AbstractCsvDAO<MovimientoInventario> implements IMovimientoInventarioDAO {

    private static MovimientoInventarioDAO instancia;

    private MovimientoInventarioDAO() {
        super("movimientos_inventario.csv");
    }

    public static MovimientoInventarioDAO getInstancia() {
        if (instancia == null) {
            instancia = new MovimientoInventarioDAO();
        }
        return instancia;
    }

    @Override
    public List<MovimientoInventario> obtenerPorProducto(String productId) {
        List<MovimientoInventario> filtrados = new ArrayList<>();
        for (MovimientoInventario m : getAll()) {
            if (m.productId().equals(productId)) {
                filtrados.add(m);
            }
        }
        return filtrados;
    }

    @Override
    protected MovimientoInventario mapearDeCSV(String[] datos) {
        return new MovimientoInventario(
                datos[0], // id
                datos[1], // productId
                datos[2], // productName
                Integer.parseInt(datos[3]), // quantity
                TipoMovimiento.valueOf(datos[4].toUpperCase()), // type (Enum)
                datos[5], // reference
                datos[6]  // createdAt
        );
    }

    @Override
    protected String mapearACSV(MovimientoInventario m) {
        return String.join(SEPARADOR,
                m.id(),
                m.productId(),
                m.productName(),
                String.valueOf(m.quantity()),
                m.type().name(), // Guardamos el nombre del Enum
                m.reference(),
                m.createdAt()
        );
    }

    @Override
    protected String obtenerId(MovimientoInventario m) {
        return m.id();
    }
}