package com.gestionmats.app.dao;

import java.util.List;

public interface IDAO<T> {
    List<T> getAll();
    T create(T entidad);
    boolean update(String id, T entidad);
    boolean delete(String id);
}
