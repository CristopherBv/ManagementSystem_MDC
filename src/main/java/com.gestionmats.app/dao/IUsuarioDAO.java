package com.gestionmats.app.dao;
import com.gestionmats.app.models.Usuario;
import java.util.Optional;

public interface IUsuarioDAO extends IDAO<Usuario> {
    // Solo buscamos por nombre de usuario, devolvemos un Optional (puede existir o no)
    Optional<Usuario> buscarPorUsername(String username);
}
