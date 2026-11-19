package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.PermisoDao;
import com.zonaacme.sica.dao.UsuarioDao;
import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Usuario;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.util.PasswordHasher;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.AuthService;

import java.util.Optional;
import java.util.Set;

/** Autenticación: verifica BCrypt y carga permisos desde la BD (RBAC dinámico). */
public class AuthServiceImpl implements AuthService {

    private final UsuarioDao usuarioDao;
    private final PermisoDao permisoDao;
    private final AuditoriaService auditoriaService;

    public AuthServiceImpl(UsuarioDao usuarioDao, PermisoDao permisoDao,
                           AuditoriaService auditoriaService) {
        this.usuarioDao = usuarioDao;
        this.permisoDao = permisoDao;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public Sesion login(String email, String password) {
        Optional<Usuario> encontrado = usuarioDao.findByEmail(email == null ? "" : email.trim());
        Usuario usuario = encontrado.orElse(null);

        boolean credencialesValidas = usuario != null
                && usuario.isActivo()
                && PasswordHasher.verificar(password, usuario.getPassword());

        if (!credencialesValidas) {
            auditoriaService.registrar(
                    AccionAuditoria.LOGIN_FALLIDO,
                    null,
                    "usuarios",
                    usuario == null ? null : usuario.getId(),
                    "Intento de acceso con email: " + email);
            throw new ValidacionException("Credenciales inválidas o usuario inactivo.");
        }

        Set<String> permisos = permisoDao.findNombresPorRol(usuario.getRolId());
        usuario.setPermisos(permisos);
        Sesion sesion = new Sesion(usuario, permisos);

        auditoriaService.registrar(
                AccionAuditoria.LOGIN_EXITOSO,
                sesion,
                "usuarios",
                usuario.getId(),
                "Rol: " + usuario.getNombreRol() + " | permisos cargados: " + permisos.size());

        return sesion;
    }
}
