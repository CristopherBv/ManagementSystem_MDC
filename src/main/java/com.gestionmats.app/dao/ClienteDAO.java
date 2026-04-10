package com.gestionmats.app.dao;

import com.gestionmats.app.models.Cliente;

public class ClienteDAO extends AbstractCsvDAO<Cliente> implements IClienteDAO {

    private static ClienteDAO instancia;

    private ClienteDAO() {
        super("clientes.csv");
    }

    public static ClienteDAO getInstancia() {
        if (instancia == null) {
            instancia = new ClienteDAO();
        }
        return instancia;
    }

    @Override
    protected Cliente mapearDeCSV(String[] datos) {
        return new Cliente(
                datos[0],                           // id
                datos[1],                           // nombre
                datos[2],                           // apellidos
                datos[3],                           // email
                datos[4],                           // telefono
                datos[5],                           // direccion
                Integer.parseInt(datos[6]),         // puntosLealtad
                Double.parseDouble(datos[7]),       // porcentajeDescuento
                datos[8]                            // createdAt
        );
    }

    @Override
    protected String mapearACSV(Cliente c) {
        return String.join(SEPARADOR,
                c.id(),
                c.nombre(),
                c.apellidos(),
                c.email(),
                c.telefono(),
                c.direccion(),
                String.valueOf(c.puntosLealtad()),
                String.valueOf(c.porcentajeDescuento()),
                c.createdAt()
        );
    }

    @Override
    protected String obtenerId(Cliente c) {
        return c.id();
    }
}
