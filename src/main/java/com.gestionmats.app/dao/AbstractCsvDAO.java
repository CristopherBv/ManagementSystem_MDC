package com.gestionmats.app.dao;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCsvDAO<T> {

    protected final String rutaArchivo;
    protected final String SEPARADOR = ",";

    public AbstractCsvDAO(String nombreArchivo) {
        this.rutaArchivo = nombreArchivo;
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error crítico al crear el archivo base: " + nombreArchivo, e);
            }
        }
    }

    protected abstract T mapearDeCSV(String[] datos);
    protected abstract String mapearACSV(T entidad);
    protected abstract String obtenerId(T entidad);

    // =========================================================
    // MÉTODOS PÚBLICOS (El CRUD)
    // =========================================================

    public synchronized List<T> getAll() {
        List<T> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split(SEPARADOR);
                lista.add(mapearDeCSV(datos));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo " + rutaArchivo, e);
        }
        return lista;
    }

    public synchronized T create(T entidad) {
        String nuevoId = obtenerId(entidad);

        // VALIDACIÓN: Evitar IDs duplicados en el CSV
        List<T> existentes = getAll();
        for (T e : existentes) {
            if (obtenerId(e).equals(nuevoId)) {
                throw new IllegalArgumentException("No se puede guardar: El ID '" + nuevoId + "' ya existe en el sistema.");
            }
        }

        // Si pasa la validación, añadimos al final (Append)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            bw.write(mapearACSV(entidad));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error al añadir un nuevo registro en " + rutaArchivo, e);
        }
        return entidad;
    }

    public synchronized boolean update(String id, T entidadActualizada) {
        List<T> lista = getAll();
        boolean actualizado = false;

        for (int i = 0; i < lista.size(); i++) {
            if (obtenerId(lista.get(i)).equals(id)) {
                lista.set(i, entidadActualizada);
                actualizado = true;
                break;
            }
        }

        if (actualizado) {
            saveAll(lista); // Llama al método protegido para reescribir
        }
        return actualizado;
    }

    public synchronized boolean delete(String id) {
        List<T> lista = getAll();
        boolean removido = lista.removeIf(entidad -> obtenerId(entidad).equals(id));
        if (removido) {
            saveAll(lista);
        }
        return removido;
    }

    // =========================================================
    // MÉTODOS INTERNOS (Ocultos a la interfaz gráfica)
    // =========================================================

    // Cambiado de 'public' a 'protected'. Solo el DAO y sus hijos pueden usarlo.
    protected synchronized void saveAll(List<T> entidades) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            for (T entidad : entidades) {
                bw.write(mapearACSV(entidad));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la lista completa en " + rutaArchivo, e);
        }
    }
}