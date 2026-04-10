package com.gestionmats.app.dao;
import com.gestionmats.app.models.Usuario;

public interface IUsuarioDAO extends IDAO<Usuario> {
    // Método exclusivo para el Login
    Usuario autenticar(String username, String password);
}
