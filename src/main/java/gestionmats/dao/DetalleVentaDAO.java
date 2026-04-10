package gestionmats.dao;

import gestionmats.models.DetalleVenta;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAO extends AbstractCsvDAO<DetalleVenta> implements IDetalleVentaDAO {

    private static DetalleVentaDAO instancia;

    private DetalleVentaDAO() { super("detalles_venta.csv"); }

    public static DetalleVentaDAO getInstancia() {
        if (instancia == null) { instancia = new DetalleVentaDAO(); }
        return instancia;
    }

    // EL MÉTODO CLAVE PARA LOS REPORTES
    public List<DetalleVenta> obtenerPorVentaId(String idVenta) {
        List<DetalleVenta> todos = getAll();
        List<DetalleVenta> filtrados = new ArrayList<>();
        for (DetalleVenta d : todos) {
            if (d.idVenta().equals(idVenta)) {
                filtrados.add(d);
            }
        }
        return filtrados;
    }

    @Override
    protected DetalleVenta mapearDeCSV(String[] datos) {
        return new DetalleVenta(
                datos[0], datos[1], datos[2], Integer.parseInt(datos[3]),
                Double.parseDouble(datos[4]), Double.parseDouble(datos[5]), Double.parseDouble(datos[6])
        );
    }

    @Override
    protected String mapearACSV(DetalleVenta d) {
        return String.join(SEPARADOR, d.idVenta(), d.productId(), d.productName(),
                String.valueOf(d.quantity()), String.valueOf(d.unitPrice()),
                String.valueOf(d.discount()), String.valueOf(d.subtotal())
        );
    }

    // En los detalles, la llave primaria suele ser compuesta, pero para el CSV podemos usar el IdVenta + ProductId
    @Override
    protected String obtenerId(DetalleVenta d) {
        return d.idVenta() + "_" + d.productId();
    }
}