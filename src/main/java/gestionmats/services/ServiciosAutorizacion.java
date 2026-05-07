package gestionmats.services;

import gestionmats.dao.PinGerenteDaoCsv;
import gestionmats.model.TipoOperacion;
import gestionmats.model.Usuario;
import gestionmats.model.Gerente;

/**
 * Servicio para autorizar operaciones restringidas (ej. aplicar descuentos, cancelar tickets).
 */
public class ServiciosAutorizacion {

    private ServiciosAutorizacion() {
        // Clase utilitaria, no instanciar
    }

    /**
     * Verifica si el PIN ingresado corresponde a un Gerente autorizado
     * para realizar la operación solicitada.
     */
    public static boolean autorizarOperacion(String pin, TipoOperacion tipo) {
        // Obtener el usuario actual de la sesión
        Usuario usuarioActual = GestorSesion.getInstancia().getUsuarioActual();

        // Verificar que sea un Gerente
        if (usuarioActual == null || !(usuarioActual instanceof Gerente)) {
            System.err.println("Error: No hay un Gerente logueado para autorizar la operación.");
            return false;
        }

        int idGerente = usuarioActual.getIdUsuario();

        // Validar el PIN usando PinGerenteDaoCsv
        PinGerenteDaoCsv pinDao = new PinGerenteDaoCsv();
        boolean pinValido = pinDao.validarPin(idGerente, pin);

        if (pinValido) {
            System.out.println("Operación [" + tipo + "] autorizada correctamente por Gerente ID: " + idGerente);
            return true;
        }

        System.out.println("Autorización denegada para la operación [" + tipo + "]. PIN inválido.");
        return false;
    }
}