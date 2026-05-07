package gestionmats.dao;

import gestionmats.utils.CsvUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDaoCsv<T> implements Dao<T> {

    protected String ruta;

    public AbstractDaoCsv(String ruta) {
        this.ruta = ruta;
    }

    // Métodos abstractos que cada DAO concreto debe implementar
    protected abstract String mapearACsv(T entidad);
    protected abstract T mapearDeCsv(String linea);
    protected abstract String obtenerId(T entidad);

    // ========== CRUD IMPLEMENTADO ==========

    @Override
    public boolean guardar(T entidad) {
        String linea = mapearACsv(entidad);
        CsvUtils.agregarLinea(this.ruta, linea.split(","));
        return true;
    }

    @Override
    public T buscarPorId(int id) {
        for (T entidad : listarTodos()) {
            String idEntidad = obtenerId(entidad);
            // Comparar como String (funciona con IDs numéricos y alfanuméricos)
            if (idEntidad.equals(String.valueOf(id))) {
                return entidad;
            }
        }
        return null;
    }

    @Override
    public List<T> listarTodos() {
        List<T> lista = new ArrayList<>();
        List<String> lineas = CsvUtils.leerArchivo(this.ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            // Saltar encabezado si existe (primera línea con "id")
            if (i == 0 && linea.toLowerCase().startsWith("id")) {
                continue;
            }
            if (linea != null && !linea.trim().isEmpty()) {
                T entidad = mapearDeCsv(linea);
                if (entidad != null) {
                    lista.add(entidad);
                }
            }
        }
        return lista;
    }

    @Override
    public boolean actualizar(T entidad) {
        String id = obtenerId(entidad);
        String nuevaLinea = mapearACsv(entidad);
        return CsvUtils.reemplazarLinea(this.ruta, id, nuevaLinea.split(","));
    }

    @Override
    public boolean eliminar(int id) {
        return CsvUtils.eliminarLinea(this.ruta, String.valueOf(id));
    }
}