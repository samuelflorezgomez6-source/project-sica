package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Permiso;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Sesion;

import java.util.List;

/** RBAC dinámico: roles y permisos se leen de la base de datos. */
public interface PermisoService {

    List<Rol> listarRoles();

    List<Permiso> listarPermisos();

    List<Permiso> permisosDelRol(int rolId);

    void reemplazarPermisos(Sesion sesion, int rolId, List<Integer> permisoIds);
}
