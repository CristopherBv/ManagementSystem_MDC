package gestionmats.model;

// Asumo que tienes este Enum creado en el paquete model según tu diagrama
// import gestionmats.model.TipoOperacion;

public class Vendedor extends Usuario {

    public Vendedor(int idUsuario, String primerApellido, String segundoApellido, String nombre, String username, String password) {
        super(idUsuario, primerApellido, segundoApellido, nombre, username, password, RolUsuario.VENDEDOR);
    }

    /**
     * Concreta una venta, afectando el inventario temporalmente hasta que almacén confirme.
     */
    public void registrarVenta() {
        // TODO: Lógica para armar la venta y enviarla al VentaDaoCsv
        System.out.println("Vendedor " + this.getUsername() + " registrando venta...");
    }

    /**
     * Solicita al sistema validar el PIN de un Gerente para realizar una acción restringida.
     */
    public boolean solicitarAutorizacion(String pinGerente, TipoOperacion tipo) {
        // TODO: Aquí se conectaría con la clase ServiciosAutorizacion que dejamos comentada
        System.out.println("Vendedor " + this.getUsername() + " solicitando autorización para: " + tipo);
        return false;
    }
}