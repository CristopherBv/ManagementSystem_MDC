package gestionmats.utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static final String SEPARADOR = ",";

    /**
     * Lee un archivo CSV y devuelve una lista con todas sus líneas.
     */
    public static List<String> leerArchivo(String ruta) {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(ruta);

        // Si el archivo no existe, lo creamos vacío
        if (!archivo.exists()) {
            try {
                archivo.getParentFile().mkdirs();  // Crea directorios si no existen
                archivo.createNewFile();           // Crea el archivo vacío
            } catch (IOException e) {
                System.err.println("Error al crear el archivo: " + ruta);
                e.printStackTrace();
                return lineas;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSV: " + ruta);
            e.printStackTrace();
        }
        return lineas;
    }

    /**
     * Escribe una lista de líneas completa en un archivo CSV (sobrescribe).
     */
    public static void escribirArchivo(String ruta, List<String> lineas) {
        File archivo = new File(ruta);
        try {
            archivo.getParentFile().mkdirs();  // Crea directorios si no existen
        } catch (Exception e) {
            System.err.println("Error al crear directorios para: " + ruta);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo CSV: " + ruta);
            e.printStackTrace();
        }
    }

    /**
     * Agrega una nueva línea al final del archivo CSV.
     */
    public static void agregarLinea(String ruta, String... datos) {
        // Unir los datos en una sola línea separada por comas
        String nuevaLinea = String.join(SEPARADOR, datos);

        // Leer todas las líneas existentes
        List<String> lineas = leerArchivo(ruta);

        // Si el archivo está vacío, agregar la línea; si no, agregar al final
        if (lineas.isEmpty()) {
            lineas.add(nuevaLinea);
        } else if (!lineas.get(lineas.size() - 1).isEmpty()) {
            lineas.add(nuevaLinea);
        } else {
            lineas.set(lineas.size() - 1, nuevaLinea);
        }

        // Escribir todo de vuelta
        escribirArchivo(ruta, lineas);
    }

    /**
     * Reemplaza una línea específica del archivo CSV por su ID (primer campo).
     */
    public static boolean reemplazarLinea(String ruta, String id, String... nuevosDatos) {
        List<String> lineas = leerArchivo(ruta);
        boolean reemplazado = false;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (linea.isEmpty()) continue;

            // Extraer el ID (primer campo antes de la coma)
            String idActual = linea.split(SEPARADOR)[0];
            if (idActual.equals(id)) {
                lineas.set(i, String.join(SEPARADOR, nuevosDatos));
                reemplazado = true;
                break;
            }
        }

        if (reemplazado) {
            escribirArchivo(ruta, lineas);
        }
        return reemplazado;
    }

    /**
     * Elimina una línea del archivo CSV por su ID (primer campo).
     */
    public static boolean eliminarLinea(String ruta, String id) {
        List<String> lineas = leerArchivo(ruta);
        boolean eliminado = false;

        for (int i = 0; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (linea.isEmpty()) continue;

            String idActual = linea.split(SEPARADOR)[0];
            if (idActual.equals(id)) {
                lineas.remove(i);
                eliminado = true;
                break;
            }
        }

        if (eliminado) {
            escribirArchivo(ruta, lineas);
        }
        return eliminado;
    }

    /**
     * Elimina una línea por su posición (índice) en el archivo.
     */
    public static boolean eliminarLineaPorIndice(String ruta, int indice) {
        List<String> lineas = leerArchivo(ruta);
        if (indice < 0 || indice >= lineas.size()) {
            return false;
        }
        lineas.remove(indice);
        escribirArchivo(ruta, lineas);
        return true;
    }

    /**
     * Versión sobrecargada para eliminar por ID (int).
     */
    public static boolean eliminarLinea(String ruta, int id) {
        return eliminarLinea(ruta, String.valueOf(id));
    }

    /**
     * Versión sobrecargada para reemplazar por ID (int).
     */
    public static boolean reemplazarLinea(String ruta, int id, String... nuevosDatos) {
        return reemplazarLinea(ruta, String.valueOf(id), nuevosDatos);
    }
}