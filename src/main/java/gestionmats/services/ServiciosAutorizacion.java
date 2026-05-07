package gestionmats.services;

import gestionmats.dao.PinGerenteDaoCsv;
import gestionmats.model.TipoOperacion;

public class ServiciosAutorizacion {

    private ServiciosAutorizacion() {}

    /**
     * Valida si el PIN ingresado corresponde a CUALQUIER Gerente registrado.
     * No depende de quién esté logueado — el Gerente escribe su PIN
     * físicamente en la pantalla del Vendedor.
     */
    public static boolean autorizarOperacion(String pin, TipoOperacion tipo) {
        if (pin == null || pin.trim().isEmpty()) {
            System.out.println("Autorización denegada: PIN vacío.");
            return false;
        }

        PinGerenteDaoCsv pinDao = new PinGerenteDaoCsv();
        boolean autorizado = pinDao.validarCualquierPin(pin);

        if (autorizado) {
            System.out.println("Operación [" + tipo + "] autorizada.");
        } else {
            System.out.println("Autorización denegada para [" + tipo + "]. PIN inválido.");
        }
        return autorizado;
    }
}