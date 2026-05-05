package gestionmats.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static final String SEPARADOR = ",";

    /**
     * Lee un archivo CSV y devuelve una lista con todas sus líneas.
     */
    public static List<String> leerArchivo(String ruta) {
        List<String> lineas = new ArrayList<>();
        // El bloque try-with-resources asegura que el archivo se cierre automáticamente
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al acceder a la base de datos CSV: " + ruta);
            e.printStackTrace();
        }
        return lineas;
    }

    // TODO: Implementar los métodos de sobrescribirArchivo, agregarLinea y eliminarLinea
    // cuando el equipo comience con la creación/edición de datos.
}