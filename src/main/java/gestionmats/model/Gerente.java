package gestionmats.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Gerente extends Usuario {

    public Gerente(int idUsuario, String primerApellido, String segundoApellido, String nombre, String username, String password) {
        super(idUsuario, primerApellido, segundoApellido, nombre, username, password, RolUsuario.GERENTE);
    }

    /**
     * Registra en la bitácora del modelo que el gerente autorizó una orden.
     * La lógica real de guardado ocurre en OrdenCompraDaoCsv.
     */
    public void generarOrdenCompra(int idOrden, String proveedor) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // Aquí en el futuro podrías guardar esto en un "Bitacora.txt"
        System.out.println("[BITÁCORA] El gerente " + this.getUsername() +
                " generó la Orden #" + idOrden + " para " + proveedor + " el " + fecha);
    }

    /**
     * Registra la solicitud de un reporte de inteligencia de negocio.
     * La lógica de cálculo ocurre en GerenteReporteController.
     */
    public void obtenerReportes(String tipoReporte) {
        System.out.println("[BITÁCORA] El gerente " + this.getUsername() +
                " solicitó el reporte: " + tipoReporte);
    }
}