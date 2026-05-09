package gestionmats.dao;

import gestionmats.model.*;
import gestionmats.utils.CsvUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrdenCompraDaoCsv extends AbstractDaoCsv<OrdenCompra> {

    private ProveedorDaoCsv proveedorDao = new ProveedorDaoCsv();
    private ProductoDaoCsv productoDao = new ProductoDaoCsv();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String rutaDetalles = "dataBase/DetallesOrden.csv";

    public OrdenCompraDaoCsv() {
        super("dataBase/OrdenesCompra.csv");
    }

    @Override
    protected OrdenCompra mapearDeCsv(String linea) {
        String[] datos = linea.split(",");
        try {
            int idOrden = Integer.parseInt(datos[0]);
            Date fecha = sdf.parse(datos[1]);
            int idProveedor = Integer.parseInt(datos[2]);
            String estado = datos[3];

            // Buscamos al proveedor en su propio DAO
            Proveedor prov = proveedorDao.buscarPorId(idProveedor);

            OrdenCompra orden = new OrdenCompra(idOrden, prov);
            orden.setFechaEmision(fecha);
            orden.setEstado(estado);

            // Cargamos la lista de materiales que pertenecen a esta orden
            cargarDetalles(orden);

            return orden;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected String mapearACsv(OrdenCompra orden) {
        return orden.getIdOrden() + "," + sdf.format(orden.getFechaEmision()) + "," +
                orden.getProveedor().getIdProveedor() + "," + orden.getEstado();
    }

    @Override
    protected String obtenerId(OrdenCompra orden) {
        return String.valueOf(orden.getIdOrden());
    }

    @Override
    public boolean guardar(OrdenCompra orden) {
        // 1. Guardamos la "cabecera" de la orden
        boolean guardado = super.guardar(orden);
        // 2. Si tuvo éxito, guardamos sus detalles
        if (guardado) {
            guardarDetalles(orden);
        }
        return guardado;
    }

    private void cargarDetalles(OrdenCompra orden) {
        List<String> lineas = CsvUtils.leerArchivo(rutaDetalles);
        for (int i = 1; i < lineas.size(); i++) { // saltamos el encabezado
            String[] datos = lineas.get(i).split(",");
            if (datos.length >= 4 && Integer.parseInt(datos[0]) == orden.getIdOrden()) {
                Producto p = productoDao.buscarPorId(datos[1]);
                int cantEsp = Integer.parseInt(datos[2]);
                int cantRec = Integer.parseInt(datos[3]);
                orden.addDetalleOrden(new DetalleOrden(p, cantEsp, cantRec));
            }
        }
    }

    private void guardarDetalles(OrdenCompra orden) {
        for (DetalleOrden det : orden.getDetalles()) {
            String linea = orden.getIdOrden() + "," + det.getProducto().getIdProducto() + "," +
                    det.getCantidadEsperada() + "," + det.getCantidadRecibida();
            CsvUtils.agregarLinea(rutaDetalles, linea.split(","));
        }
    }

    public int generarSiguienteId() {
        int maxId = 0;
        for (OrdenCompra o : this.listarTodos()) {
            if (o.getIdOrden() > maxId) maxId = o.getIdOrden();
        }
        return maxId + 1;
    }
}