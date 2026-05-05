package gestionmats.dao;

import gestionmats.model.*;

public class UsuarioDaoCsv extends AbstractDaoCsv<Usuario> {

    public UsuarioDaoCsv() {
        // Le pasamos la ruta relativa de donde guardaste tu CSV
        super("dataBase/Usuarios.csv");
    }

    /**
     * Convierte una línea de texto del CSV en un Objeto Usuario real.
     */
    @Override
    protected Usuario mapearDeCsv(String linea) {
        // Tu CsvUtils marca que el separador es ","
        String[] datos = linea.split(",");

        // Asumiendo que tu CSV tiene este orden:
        // idUsuario, primerApellido, segundoApellido, nombre, username, password, rol
        try {
            int id = Integer.parseInt(datos[0].trim());
            String primerApellido = datos[1].trim();
            String segundoApellido = datos[2].trim();
            String nombre = datos[3].trim();
            String username = datos[4].trim();
            String password = datos[5].trim();
            RolUsuario rol = RolUsuario.valueOf(datos[6].trim().toUpperCase());

            // Dependiendo del rol en el CSV, instanciamos la clase hija correcta
            switch (rol) {
                case GERENTE:
                    return new Gerente(id, primerApellido, segundoApellido, nombre, username, password);
                case VENDEDOR:
                    return new Vendedor(id, primerApellido, segundoApellido, nombre, username, password);
                case ALMACENISTA:
                    return new Almacenista(id, primerApellido, segundoApellido, nombre, username, password);
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error al parsear la línea del CSV: " + linea);
            return null;
        }
    }

    /**
     * Convierte un Objeto Usuario en una línea de texto para el CSV.
     */
    @Override
    protected String mapearACsv(Usuario u) {
        return u.getIdUsuario() + "," +
                u.getPrimerApellido() + "," +
                u.getSegundoApellido() + "," +
                u.getNombre() + "," +
                u.getUsername() + "," +
                u.getPassword() + "," +
                u.getRol().name();
    }

    /**
     * Método específico para el Login.
     * Busca en la lista completa de usuarios aquel que coincida con el username dado.
     */
    public Usuario buscarPorUsername(String username) {
        // listarTodos() hereda de AbstractDaoCsv y devuelve todos los usuarios
        for (Usuario u : this.listarTodos()) {
            if (u.getUsername().equals(username)) {
                return u; // Retorna el usuario si hace match
            }
        }
        return null; // Si termina el ciclo y no lo encontró, regresa nulo
    }
}