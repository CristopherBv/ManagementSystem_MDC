package gestionmats.model;

public class Gerente extends Usuario {

    // El constructor no pide el RolUsuario. Lo inyecta directamente al "super".
    public Gerente(int idUsuario, String primerApellido, String segundoApellido, String nombre, String username, String password) {
        super(idUsuario, primerApellido, segundoApellido, nombre, username, password, RolUsuario.GERENTE);
    }

    /**
     * Inicia el proceso de creación de una nueva orden de compra a un proveedor.
     */
    public void generarOrdenCompra() {
        // TODO: Lógica para generar la orden de compra
        System.out.println("Gerente " + this.getUsername() + " generando orden de compra...");
    }

    /**
     * Extrae información del sistema para visualización de KPIs o exportación.
     */
    public void obtenerReportes() {
        // TODO: Lógica para recopilar datos de DAOs y armar reportes
        System.out.println("Gerente " + this.getUsername() + " obteniendo reportes...");
    }
}