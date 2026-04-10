package gestionmats.dao;

import gestionmats.models.Venta;
import gestionmats.models.DetalleVenta;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO extends AbstractCsvDAO<Venta> implements IVentaDAO {

    private static VentaDAO instancia;
    private DetalleVentaDAO detalleDAO; // Conexión al otro archivo

    private VentaDAO() {
        super("ventas.csv");
        this.detalleDAO = DetalleVentaDAO.getInstancia();
    }

    public static VentaDAO getInstancia() {
        if (instancia == null) { instancia = new VentaDAO(); }
        return instancia;
    }

    // ---SOBREESCRIBIMOS LOS MÉTODOS DEL PADRE ---

    @Override
    public synchronized Venta create(Venta venta) {
        // 1. Guardar el encabezado en ventas.csv (usando el método del padre)
        super.create(venta);

        // 2. Guardar cada producto en detalles_venta.csv
        for (DetalleVenta item : venta.items()) {
            detalleDAO.create(item);
        }

        return venta;
    }

    @Override
    public synchronized List<Venta> getAll() {
        // 1. Obtenemos las ventas básicas del CSV
        List<Venta> ventasSinDetalles = super.getAll();
        List<Venta> ventasCompletas = new ArrayList<>();

        // 2. Por cada venta, vamos a buscar sus productos al otro CSV
        for (Venta v : ventasSinDetalles) {
            List<DetalleVenta> susProductos = detalleDAO.obtenerPorVentaId(v.id());

            // Creamos una nueva Venta (porque los Records son inmutables) agregándole su lista
            Venta ventaEnsamblada = new Venta(
                    v.id(), v.orderId(), v.customerId(), v.customerName(),
                    susProductos, // <- ¡Aquí le inyectamos los detalles!
                    v.subtotal(), v.discountAmount(), v.total(),
                    v.paymentMethod(), v.createdAt(), v.createdBy()
            );
            ventasCompletas.add(ventaEnsamblada);
        }
        return ventasCompletas;
    }

    // --- MAPEOS DE CSV (Nota: Omitimos la lista al leer/escribir este archivo específico) ---

    @Override
    protected Venta mapearDeCSV(String[] datos) {
        // Inicializamos con una lista vacía. El getAll() se encargará de llenarla después.
        return new Venta(
                datos[0], datos[1], datos[2], datos[3], new ArrayList<>(),
                Double.parseDouble(datos[4]), Double.parseDouble(datos[5]), Double.parseDouble(datos[6]),
                datos[7], datos[8], datos[9]
        );
    }

    @Override
    protected String mapearACSV(Venta v) {
        // Solo guardamos los datos del encabezado. Nada de Listas ni JSON aquí.
        return String.join(SEPARADOR,
                v.id(), v.orderId(), v.customerId(), v.customerName(),
                String.valueOf(v.subtotal()), String.valueOf(v.discountAmount()), String.valueOf(v.total()),
                v.paymentMethod(), v.createdAt(), v.createdBy()
        );
    }

    @Override
    protected String obtenerId(Venta v) { return v.id(); }
}
