package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Permiso;

import java.util.List;
import java.util.Set;

/** Contrato de persistencia de permisos (RBAC dinámico). */
public interface PermisoDao {

    List<Permiso> findAll();

    /** Nombres de permisos asignados a un rol. Se consultan en la BD al iniciar sesión. */
    Set<String> findNombresPorRol(int rolId);
}
