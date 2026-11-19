package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Usuario;

import java.util.List;

/** Gestión de usuarios del sistema (Superusuario). */
public interface UsuarioService {

    List<Usuario> listar(Sesion sesion);

    List<Rol> listarRoles(Sesion sesion);

    int crear(Sesion sesion, Usuario usuario, String passwordPlano);

    int actualizar(Sesion sesion, Usuario usuario, String passwordPlanoNuevo);

    int eliminar(Sesion sesion, int usuarioId);

    void cambiarEstado(Sesion sesion, int usuarioId, boolean activo);
}
