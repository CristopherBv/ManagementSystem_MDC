package gestionmats.services;

import gestionmats.dao.UsuarioDaoCsv;
import gestionmats.model.Usuario;
import gestionmats.model.RolUsuario;

/**
 * Clase Singleton para manejar la sesión global del usuario en el sistema.
 */
public class GestorSesion {

    // 1. Atributo estático que guardará la única instancia de la clase
    private static GestorSesion instancia;

    // 2. Referencia al usuario que tiene la sesión activa
    private Usuario usuarioActual;

    // 3. Constructor privado para evitar que otras clases hagan "new GestorSesion()"
    private GestorSesion() {
        this.usuarioActual = null;
    }

    // 4. Método global para obtener la instancia (se crea solo la primera vez que se llama)
    public static GestorSesion getInstancia() {
        if (instancia == null) {
            instancia = new GestorSesion();
        }
        return instancia;
    }

    /**
     * Valida las credenciales contra la base de datos (CSV) e inicia la sesión.
     */
    public boolean iniciarSesion(String username, String password) {
        // Instanciamos el DAO para buscar en el CSV
        UsuarioDaoCsv dao = new UsuarioDaoCsv();
        Usuario usuarioEncontrado = dao.buscarPorUsername(username);

        // Si el usuario existe y la contraseña es correcta, lo asignamos a la sesión
        if (usuarioEncontrado != null && usuarioEncontrado.getPassword().equals(password)) {
            this.usuarioActual = usuarioEncontrado;
            return true; // Login exitoso
        }

        return false; // Credenciales incorrectas o usuario no encontrado
    }

    /**
     * Limpia la sesión actual (se llama al presionar el botón "Cerrar Sesión").
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Método auxiliar para obtener rápidamente el rol del usuario en sesión.
     */
    public RolUsuario getRolActual() {
        if (usuarioActual != null) {
            return usuarioActual.getRol();
        }
        return null;
    }
}
