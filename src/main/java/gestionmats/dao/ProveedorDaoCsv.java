package gestionmats.dao;

import gestionmats.model.Proveedor;

public class ProveedorDaoCsv extends AbstractDaoCsv<Proveedor> {

    public ProveedorDaoCsv() {
        super("dataBase/Proveedores.csv");
    }

    @Override
    protected Proveedor mapearDeCsv(String linea) {
        String[] datos = linea.split(",");
        try {
            int id = Integer.parseInt(datos[0].trim());
            String nombre = datos[1].trim();
            String telefono = datos[2].trim();
            return new Proveedor(id, nombre, telefono);
        } catch (Exception e) {
            System.err.println("Error al parsear el proveedor: " + linea);
            return null;
        }
    }

    @Override
    protected String mapearACsv(Proveedor p) {
        return p.getIdProveedor() + "," + p.getNombreProveedor() + "," + p.getNumeroTelefonico();
    }

    @Override
    protected String obtenerId(Proveedor entidad) {
        return String.valueOf(entidad.getIdProveedor());
    }

    // --- MÉTODOS EXTRA PARA EL CONTROLADOR ---

    public int generarSiguienteId() {
        int maxId = 0;
        for (Proveedor p : this.listarTodos()) {
            if (p.getIdProveedor() > maxId) {
                maxId = p.getIdProveedor();
            }
        }
        return maxId + 1; // Si no hay ninguno, devolverá 1
    }

    public boolean existeProveedorPorNombre(String nombre) {
        for (Proveedor p : this.listarTodos()) {
            if (p.getNombreProveedor().equalsIgnoreCase(nombre.trim())) {
                return true;
            }
        }
        return false;
    }
}