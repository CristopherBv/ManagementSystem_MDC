package gestionmats.dao;

import gestionmats.model.Cliente;
import gestionmats.utils.CsvUtils;
import java.util.ArrayList;
import java.util.List;

public class ClienteDaoCsv implements Dao<Cliente> {

    private final String ruta = "dataBase/clientes.csv";

    @Override
    public boolean guardar(Cliente cliente) {
        String linea = mapearACsv(cliente);
        // FIX: Agregamos -1 para que no borre las columnas vacías al final
        CsvUtils.agregarLinea(ruta, linea.split(",", -1));
        return true;
    }

    @Override
    public Cliente buscarPorId(int id) {
        for (Cliente c : listarTodos()) {
            if (c.getIdCliente() == id) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        List<String> lineas = CsvUtils.leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (i == 0 && linea.toLowerCase().startsWith("id")) continue;
            if (linea == null || linea.trim().isEmpty()) continue;

            // FIX: El "-1" obliga a Java a contar los espacios vacíos al final de la línea
            String[] datos = linea.split(",", -1);
            try {
                Cliente c = new Cliente();
                // FIX: Usamos el method seguro getDato para evitar excepciones de índice
                c.setIdCliente(Integer.parseInt(getDato(datos, 0, "0")));
                c.setNombre(getDato(datos, 1, "Desconocido"));
                c.setCorreoElectronico(getDato(datos, 2, ""));
                c.setNumeroTelefonico(getDato(datos, 3, ""));
                c.setDireccion(getDato(datos, 4, ""));
                c.setPuntosLealtad(Double.parseDouble(getDato(datos, 5, "0.0")));
                c.setPreferencias(getDato(datos, 6, ""));

                clientes.add(c);
            } catch (Exception e) {
                System.err.println("Error al parsear cliente: " + linea);
            }
        }
        return clientes;
    }

    @Override
    public boolean actualizar(Cliente cliente) {
        String id = String.valueOf(cliente.getIdCliente());
        String nuevaLinea = mapearACsv(cliente);
        // FIX: Agregamos -1 para mantener columnas vacías
        return CsvUtils.reemplazarLinea(ruta, id, nuevaLinea.split(",", -1));
    }

    @Override
    public boolean eliminar(int id) {
        return CsvUtils.eliminarLinea(ruta, String.valueOf(id));
    }

    private String mapearACsv(Cliente c) {
        // FIX: Prevenimos que se escriba la palabra "null" en el CSV si un campo está vacío
        return c.getIdCliente() + "," +
                (c.getNombre() != null ? c.getNombre() : "") + "," +
                (c.getCorreoElectronico() != null ? c.getCorreoElectronico() : "") + "," +
                (c.getNumeroTelefonico() != null ? c.getNumeroTelefonico() : "") + "," +
                (c.getDireccion() != null ? c.getDireccion() : "") + "," +
                c.getPuntosLealtad() + "," +
                (c.getPreferencias() != null ? c.getPreferencias() : "");
    }

    // =====================================================================
    // NUEVO METHODO AUXILIAR: Para leer arreglos de forma segura
    // =====================================================================
    private String getDato(String[] datos, int index, String defecto) {
        return (index < datos.length && datos[index] != null && !datos[index].trim().isEmpty())
                ? datos[index].trim() : defecto;
    }

    public int obtenerUltimoId() {
        int max = 0;
        for (Cliente c : listarTodos()) {
            if (c.getIdCliente() > max) max = c.getIdCliente();
        }
        return max;
    }

    /**
     * Acumula puntos al cliente después de una venta
     * @param idCliente ID del cliente
     * @param montoVenta Monto total de la venta
     */
    public void acumularPuntos(int idCliente, double montoVenta) {
        Cliente c = buscarPorId(idCliente);
        if (c != null) {
            c.acumularPuntos(montoVenta);
            actualizar(c);
        }
    }
}