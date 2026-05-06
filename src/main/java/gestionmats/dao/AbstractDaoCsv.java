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

        // Llamada real a tu herramienta de lectura
        List<String> lineas = gestionmats.utils.CsvUtils.leerArchivo(this.ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);

            // Saltamos la fila 0 si detectamos que es un encabezado (idUsuario o idProducto)
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