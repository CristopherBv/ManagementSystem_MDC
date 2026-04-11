package gestionmats.utils;

import gestionmats.dao.UsuarioDAO;
import gestionmats.models.Usuario;
import java.util.Optional;

public class AuthUtil {

    private final UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();
    private static AuthUtil instancia;

    private AuthUtil() {}

    public static AuthUtil getInstancia() {
        if (instancia == null) {
            instancia = new AuthUtil();
        }
        return instancia;
    }

    /**
     * Valida las credenciales y devuelve el objeto Usuario si es exitoso.
     */
    public Optional<Usuario> login(String username, String password) {
        Optional<Usuario> optUsuario = usuarioDAO.buscarPorUsername(username);

        if (optUsuario.isPresent() && optUsuario.get().password().equals(password)) {
            return optUsuario;
        }

        return Optional.empty();
    }
}
