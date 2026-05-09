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
        CsvUtils.agregarLinea(ruta, linea.split(","));
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

            String[] datos = linea.split(",");
            try {
                Cliente c = new Cliente();
                c.setIdCliente(Integer.parseInt(datos[0].trim()));
                c.setNombre(datos[1].trim());
                c.setCorreoElectronico(datos[2].trim());
                c.setNumeroTelefonico(datos[3].trim());
                c.setDireccion(datos[4].trim());
                c.setPuntosLealtad(Double.parseDouble(datos[5].trim()));
                c.setPreferencias(datos[6].trim());
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
        return CsvUtils.reemplazarLinea(ruta, id, nuevaLinea.split(","));
    }

    @Override
    public boolean eliminar(int id) {
        return CsvUtils.eliminarLinea(ruta, String.valueOf(id));
    }

    private String mapearACsv(Cliente c) {
        return c.getIdCliente() + "," +
                c.getNombre() + "," +
                c.getCorreoElectronico() + "," +
                c.getNumeroTelefonico() + "," +
                c.getDireccion() + "," +
                c.getPuntosLealtad() + "," +
                c.getPreferencias();
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
     */
    public void acumularPuntos(int idCliente, double montoVenta) {
        Cliente c = buscarPorId(idCliente);
        if (c != null) {
            c.acumularPuntos(montoVenta);
            actualizar(c);
        }
    }
}