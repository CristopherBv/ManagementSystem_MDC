package gestionmats.dao;

import gestionmats.models.DetalleCompra;
import java.util.ArrayList;
import java.util.List;

public class DetalleCompraDAO extends AbstractCsvDAO<DetalleCompra> implements IDetalleCompraDAO {

    private static DetalleCompraDAO instancia;

    private DetalleCompraDAO() {
        super("detalles_compra.csv");
    }

    public static DetalleCompraDAO getInstancia() {
        if (instancia == null) {
            instancia = new DetalleCompraDAO();
        }
        return instancia;
    }

    @Override
    public List<DetalleCompra> obtenerPorCompraId(String idCompra) {
        List<DetalleCompra> filtrados = new ArrayList<>();
        for (DetalleCompra d : getAll()) {
            if (d.idCompra().equals(idCompra)) {
                filtrados.add(d);
            }
        }
        return filtrados;
    }

    @Override
    protected DetalleCompra mapearDeCSV(String[] datos) {
        return new DetalleCompra(
                datos[0], // idCompra
                datos[1], // productId
                datos[2], // productName
                Integer.parseInt(datos[3]), // quantity
                Double.parseDouble(datos[4]), // unitPrice
                Double.parseDouble(datos[5]), // discount
                Double.parseDouble(datos[6])  // subtotal
        );
    }

    @Override
    protected String mapearACSV(DetalleCompra d) {
        return String.join(SEPARADOR,
                d.idCompra(),
                d.productId(),
                d.productName(),
                String.valueOf(d.quantity()),
                String.valueOf(d.unitPrice()),
                String.valueOf(d.discount()),
                String.valueOf(d.subtotal())
        );
    }

    @Override
    protected String obtenerId(DetalleCompra d) {
        // Llave compuesta para evitar duplicados del mismo producto en la misma compra
        return d.idCompra() + "_" + d.productId();
    }
}