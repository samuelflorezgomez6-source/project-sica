package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Permiso;
import com.zonaacme.sica.model.entity.Rol;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de roles y su asignación de permisos. */
public interface RolDao {

    List<Rol> findAll();

    Optional<Rol> findById(int id);

    List<Permiso> findPermisos(int rolId);

    /** Reemplaza todos los permisos del rol por la lista indicada (transaccional). */
    void reemplazarPermisos(int rolId, List<Integer> permisoIds);
}
