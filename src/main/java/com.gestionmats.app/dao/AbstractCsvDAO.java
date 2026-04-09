package com.gestionmats.app.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCsvDAO<T> {

    protected final String rutaArchivo;
    protected final String SEPARADOR = ",";

    // El constructor verifica que el archivo exista
    public AbstractCsvDAO(String nombreArchivo) {
        this.rutaArchivo = nombreArchivo;
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al crear el archivo " + nombreArchivo + ": " + e.getMessage());
            }
        }
    }

    // =================================================================
    // MÉTODOS ABSTRACTOS: Cada DAO específico debe enseñar cómo hacer esto
    // =================================================================
    protected abstract T mapearDeCSV(String[] datos);
    protected abstract String mapearACSV(T entidad);
    protected abstract String obtenerId(T entidad);

    // =================================================================
    // MÉTODOS UNIVERSALES (El principio DRY en acción)
    // =================================================================

    public List<T> getAll() {
        List<T> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split(SEPARADOR);
                lista.add(mapearDeCSV(datos)); // Delega la conversión al hijo
            }
        } catch (IOException e) {
            System.err.println("Error al leer " + rutaArchivo + ": " + e.getMessage());
        }
        return lista;
    }

    public void saveAll(List<T> entidades) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (T entidad : entidades) {
                bw.write(mapearACSV(entidad)); // Delega la conversión al hijo
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en " + rutaArchivo + ": " + e.getMessage());
        }
    }

    public T create(T entidad) {
        List<T> lista = getAll();
        lista.add(entidad);
        saveAll(lista);
        return entidad;
    }

    public boolean delete(String id) {
        List<T> lista = getAll();
        // Usa el método obtenerId para saber cuál borrar, sin importar qué clase sea
        boolean removido = lista.removeIf(entidad -> obtenerId(entidad).equals(id));
        if (removido) {
            saveAll(lista);
        }
        return removido;
    }
}
