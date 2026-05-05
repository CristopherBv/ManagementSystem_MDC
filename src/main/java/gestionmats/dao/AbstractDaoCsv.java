package gestionmats.dao;

import java.util.ArrayList;
import java.util.List;
import gestionmats.utils.CsvUtils; // Descomenta esto cuando tengas tu CsvUtils

public abstract class AbstractDaoCsv<T> implements Dao<T> {

    protected String ruta;

    public AbstractDaoCsv(String ruta) {
        this.ruta = ruta;
    }

    // Métodos abstractos que cada DAO concreto debe implementar
    protected abstract String mapearACsv(T entidad);
    protected abstract T mapearDeCsv(String linea);

    @Override
    public List<T> listarTodos() {
        List<T> lista = new ArrayList<>();

        // Aquí usamos tu CsvUtils (según el diagrama) para obtener las líneas
        List<String> lineas = CsvUtils.leerArchivo(this.ruta);

        // Simulación temporal para que no te marque error si aún no tienes CsvUtils:
        //List<String> lineas = new ArrayList<>();

        // Empezamos asumiendo que la primera línea puede ser el encabezado,
        // dependiendo de cómo estructures tu CSV.
        for (String linea : lineas) {
            // Saltamos encabezados o líneas vacías
            if (linea != null && !linea.trim().isEmpty() && !linea.startsWith("id")) {
                T entidad = mapearDeCsv(linea);
                if (entidad != null) {
                    lista.add(entidad);
                }
            }
        }
        return lista;
    }

    // Dejo las firmas de los demás métodos del CRUD listos para
    // que después los llenen usando CsvUtils
    @Override
    public boolean guardar(T entidad) {
        // TODO: Usar CsvUtils.agregarLinea(ruta, mapearACsv(entidad))
        return false;
    }

    @Override
    public T buscarPorId(int id) {
        // TODO: Lógica para buscar un ID específico
        return null;
    }

    @Override
    public boolean actualizar(T entidad) {
        // TODO: Usar CsvUtils.reemplazarLinea(...)
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        // TODO: Usar CsvUtils.eliminarLinea(...)
        return false;
    }
}