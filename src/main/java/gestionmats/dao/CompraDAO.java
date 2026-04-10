package gestionmats.dao;

import gestionmats.models.Compra;
import gestionmats.models.DetalleCompra;
import gestionmats.models.EstadoCompra;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO extends AbstractCsvDAO<Compra> implements ICompraDAO {

    private static CompraDAO instancia;
    private final DetalleCompraDAO detalleDAO;

    private CompraDAO() {
        super("compras.csv");
        this.detalleDAO = DetalleCompraDAO.getInstancia();
    }

    public static CompraDAO getInstancia() {
        if (instancia == null) {
            instancia = new CompraDAO();
        }
        return instancia;
    }

    @Override
    public synchronized Compra create(Compra compra) {
        // 1. Guardar el encabezado de la compra
        super.create(compra);

        // 2. Guardar cada uno de sus detalles
        for (DetalleCompra item : compra.items()) {
            detalleDAO.create(item);
        }
        return compra;
    }

    @Override
    public List<Compra> getAll() {
        List<Compra> comprasSinDetalles = super.getAll();
        List<Compra> comprasCompletas = new ArrayList<>();

        for (Compra c : comprasSinDetalles) {
            // Buscamos sus productos en el otro archivo
            List<DetalleCompra> susItems = detalleDAO.obtenerPorCompraId(c.id());

            // Reconstruimos el Record con la lista llena
            comprasCompletas.add(new Compra(
                    c.id(), c.supplierId(), c.supplierName(), susItems,
                    c.total(), c.status(), c.createdAt(), c.createdBy(), c.discrepancies()
            ));
        }
        return comprasCompletas;
    }

    @Override
    protected Compra mapearDeCSV(String[] datos) {
        return new Compra(
                datos[0], // id
                datos[1], // supplierId
                datos[2], // supplierName
                new ArrayList<>(), // Lista vacía (se llena en getAll)
                Double.parseDouble(datos[3]), // total
                EstadoCompra.valueOf(datos[4].toUpperCase()), // status (Enum)
                datos[5], // createdAt
                datos[6], // createdBy
                datos.length > 7 ? datos[7] : "" // discrepancies (opcional)
        );
    }

    @Override
    protected String mapearACSV(Compra c) {
        return String.join(SEPARADOR,
                c.id(),
                c.supplierId(),
                c.supplierName(),
                String.valueOf(c.total()),
                c.status().name(), // Guardamos el nombre del Enum
                c.createdAt(),
                c.createdBy(),
                c.discrepancies() != null ? c.discrepancies() : ""
        );
    }

    @Override
    protected String obtenerId(Compra c) {
        return c.id();
    }
}