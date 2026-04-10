package com.gestionmats.app.dao;

import com.gestionmats.app.models.Rol; //ENUM DE ROLES
import com.gestionmats.app.models.Usuario;
import java.util.List;

public class UsuarioDAO extends AbstractCsvDAO<Usuario> implements IUsuarioDAO {

    private static UsuarioDAO instancia;

    private UsuarioDAO() {
        super("usuarios.csv");
    }

    @Override
    public synchronized Usuario create(Usuario usuario) {
        // 1. Validación exclusiva de Usuarios: Que el username no se repita
        List<Usuario> todos = getAll();
        for (Usuario u : todos) {
            if (u.username().equalsIgnoreCase(usuario.username())) {
                throw new IllegalArgumentException("Error: El nombre de usuario '" + usuario.username() + "' ya está ocupado.");
            }
        }

        // 2. Si pasa la prueba, le pasamos la bolita al Padre para que valide el ID y lo guarde en el CSV
        return super.create(usuario);
    }

    public static UsuarioDAO getInstancia() {
        if (instancia == null) { instancia = new UsuarioDAO(); }
        return instancia;
    }

    // --- LA LÓGICA DEL LOGIN ---
    @Override
    public Usuario autenticar(String username, String password) {
        List<Usuario> todos = getAll();
        for (Usuario u : todos) {
            // Compara usuario y contraseña. Si coinciden, devuelve el objeto Usuario completo
            if (u.username().equals(username) && u.password().equals(password)) {
                return u;
            }
        }
        return null; // Si no lo encuentra o la contraseña está mal, devuelve nulo
    }

    // --- MAPEOS (Igual que siempre) ---
    @Override
    protected Usuario mapearDeCSV(String[] datos) {
        return new Usuario(
                datos[0], datos[1], datos[2], datos[3],
                Rol.valueOf(datos[4].toUpperCase()) // Traduce de texto a Enum
        );
    }

    @Override
    protected String mapearACSV(Usuario u) {
        return String.join(SEPARADOR,
                u.id(), u.nombre(), u.username(), u.password(),
                u.rol().name() // Traduce de Enum a texto para el CSV
        );
    }

    @Override
    protected String obtenerId(Usuario u) { return u.id(); }
}