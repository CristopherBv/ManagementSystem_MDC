package com.gestionmats.app.dao;

import com.gestionmats.app.models.Usuario;
import com.gestionmats.app.models.Rol;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

public class UsuarioDAO extends AbstractCsvDAO<Usuario> implements IUsuarioDAO {

    private static UsuarioDAO instancia;

    private UsuarioDAO() { super("usuarios.csv"); }

    public static UsuarioDAO getInstancia() {
        if (instancia == null) { instancia = new UsuarioDAO(); }
        return instancia;
    }

    // --- EL BUSCADOR OPTIMIZADO (Sin cargar toda la RAM) ---
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] datos = linea.split(SEPARADOR);
                // El username está en el índice 2
                if (datos[2].equals(username)) {
                    // Usamos el método del Padre para no repetir cómo se construye el objeto
                    return Optional.of(mapearDeCSV(datos));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al buscar usuario en CSV", e);
        }
        return Optional.empty(); // Si termina el archivo y no lo encontró
    }

    @Override
    public synchronized Usuario create(Usuario usuario) {
        // Validamos que el username no se repita usando el nuevo método ultra rápido
        if (buscarPorUsername(usuario.username()).isPresent()) {
            throw new IllegalArgumentException("Error: El nombre de usuario '" + usuario.username() + "' ya está ocupado.");
        }
        return super.create(usuario);
    }

    @Override
    protected Usuario mapearDeCSV(String[] datos) {
        return new Usuario(datos[0], datos[1], datos[2], datos[3], Rol.valueOf(datos[4].toUpperCase()));
    }

    @Override
    protected String mapearACSV(Usuario u) {
        return String.join(SEPARADOR, u.id(), u.nombre(), u.username(), u.password(), u.rol().name());
    }

    @Override
    protected String obtenerId(Usuario u) { return u.id(); }
}