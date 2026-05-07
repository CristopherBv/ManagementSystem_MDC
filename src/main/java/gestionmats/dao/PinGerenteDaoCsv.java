package gestionmats.dao;

import gestionmats.utils.CsvUtils;
import java.util.List;

public class PinGerenteDaoCsv {

    private String ruta = "dataBase/pines_gerente.csv";

    public PinGerenteDaoCsv() {
        // Constructor vacío
    }

    /**
     * Valida si el PIN coincide con EL PIN de CUALQUIER gerente registrado.
     * Se usa cuando el Vendedor pide autorización y el Gerente escribe su PIN.
     */
    public boolean validarCualquierPin(String pin) {
        List<String> lineas = CsvUtils.leerArchivo(ruta);
        for (String linea : lineas) {
            String[] datos = linea.split(",");
            if (datos.length >= 2) {
                try {
                    String pinGuardado = datos[1].trim();
                    if (pinGuardado.equals(pin)) {
                        return true;
                    }
                } catch (Exception e) {
                    // ignorar líneas mal formateadas
                }
            }
        }
        return false;
    }

    /**
     * Valida si el PIN ingresado corresponde al ID del Gerente.
     */
    public boolean validarPin(int idUsuario, String pin) {
        List<String> lineas = CsvUtils.leerArchivo(ruta);

        for (String linea : lineas) {
            String[] datos = linea.split(",");
            if (datos.length >= 2) {
                try {
                    int id = Integer.parseInt(datos[0].trim());
                    String pinGuardado = datos[1].trim();
                    if (id == idUsuario && pinGuardado.equals(pin)) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar líneas mal formateadas
                }
            }
        }
        return false;
    }

    /**
     * Guarda o actualiza el PIN de un Gerente.
     */
    public boolean guardarPin(int idUsuario, String pin) {
        List<String> lineas = CsvUtils.leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] datos = lineas.get(i).split(",");
            if (datos.length > 0) {
                try {
                    int id = Integer.parseInt(datos[0].trim());
                    if (id == idUsuario) {
                        lineas.set(i, idUsuario + "," + pin);
                        CsvUtils.escribirArchivo(ruta, lineas);
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            }
        }

        CsvUtils.agregarLinea(ruta, String.valueOf(idUsuario), pin);
        return true;
    }

    /**
     * Actualiza el PIN de un Gerente (alias de guardarPin).
     */
    public boolean actualizarPin(int idUsuario, String pinNuevo) {
        return guardarPin(idUsuario, pinNuevo);
    }
}