package gestionmats.model;

import gestionmats.dao.DetalleVentaDaoCsv;
import gestionmats.dao.VentaDaoCsv;
import gestionmats.services.ServiciosAutorizacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Vendedor extends Usuario {

    public Vendedor(int idUsuario, String primerApellido, String segundoApellido,
                    String nombre, String username, String password) {
        super(idUsuario, primerApellido, segundoApellido, nombre, username, password, RolUsuario.VENDEDOR);
    }

    /**
     * Concreta una venta, afectando el inventario temporalmente hasta que almacén confirme.
     * Recibe el carrito de compras, el método de pago, el tipo de venta y el descuento aplicado.
     */
    public boolean registrarVenta(List<DetalleVenta> carrito, String metodoPago,
                                  String tipoVenta, double descuento) {
        if (carrito == null || carrito.isEmpty()) {
            System.err.println("Error: Carrito vacío, no se puede registrar la venta.");
            return false;
        }

        // Calcular subtotal y total
        double subtotal = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        double total = subtotal * (1 - descuento / 100);

        // Crear la venta
        VentaDaoCsv ventaDao = new VentaDaoCsv();
        int nuevoIdVenta = ventaDao.obtenerUltimoId() + 1;

        Venta venta = new Venta(
                nuevoIdVenta,
                0, // Cliente genérico (id=0)
                this.getIdUsuario(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                tipoVenta,
                metodoPago,
                subtotal,
                descuento,
                total,
                "COMPLETADA"
        );

        // Guardar la venta en el CSV
        if (!ventaDao.guardar(venta)) {
            System.err.println("Error al guardar la venta en el CSV.");
            return false;
        }

        // Guardar los detalles de la venta
        DetalleVentaDaoCsv detalleDao = new DetalleVentaDaoCsv();
        int nuevoIdDetalle = detalleDao.obtenerUltimoId() + 1;
        for (DetalleVenta item : carrito) {
            item.setIdVenta(nuevoIdVenta);
            item.setIdDetalle(nuevoIdDetalle++);
            detalleDao.guardar(item);
        }

        System.out.println("Venta registrada exitosamente por " + this.getUsername() + " - Total: $" + total);
        return true;
    }

    /**
     * Solicita al sistema validar el PIN de un Gerente para realizar una acción restringida.
     */
    public boolean solicitarAutorizacion(String pinGerente, TipoOperacion tipo) {
        return ServiciosAutorizacion.autorizarOperacion(pinGerente, tipo);
    }
}