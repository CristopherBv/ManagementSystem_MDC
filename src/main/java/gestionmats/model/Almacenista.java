package gestionmats.model;

public class Almacenista extends Usuario {

    public Almacenista(int idUsuario, String primerApellido, String segundoApellido, String nombre, String username, String password) {
        super(idUsuario, primerApellido, segundoApellido, nombre, username, password, RolUsuario.ALMACENISTA);
    }

    /**
     * Verifica físicamente que la mercancía de una orden de compra llegó y actualiza el stock real.
     */
    public void confirmarEntradaStock(int idOrden) {
        // TODO: Buscar la orden, actualizar estado a RECIBIDA y sumar al inventario
        System.out.println("Almacenista " + this.getUsername() + " confirmando entrada de la orden #" + idOrden);
    }

    /**
     * Verifica que los materiales de una venta se entregaron al cliente y resta el stock definitivamente.
     */
    public void confirmarSalidaStock(int idVenta) {
        // TODO: Buscar la venta y descontar las cantidades del ProductoDaoCsv
        System.out.println("Almacenista " + this.getUsername() + " confirmando salida de la venta #" + idVenta);
    }
}