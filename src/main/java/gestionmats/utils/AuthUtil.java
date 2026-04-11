package gestionmats.utils;

import gestionmats.dao.UsuarioDAO;
import gestionmats.models.Usuario;
import java.util.Optional;

public class AuthUtil {

    private static final UsuarioDAO usuarioDAO = UsuarioDAO.getInstancia();

    /**
     * Lógica central de autenticación.
     */
    public static Optional<Usuario> intentarLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        Optional<Usuario> optUsuario = usuarioDAO.buscarPorUsername(username);

        if (optUsuario.isPresent() && optUsuario.get().password().equals(password)) {
            return optUsuario;
        }

        return Optional.empty();
    }
}
