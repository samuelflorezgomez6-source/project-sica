package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.RolDao;
import com.zonaacme.sica.dao.UsuarioDao;
import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Usuario;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.UsuarioService;
import com.zonaacme.sica.util.PasswordHasher;
import com.zonaacme.sica.util.ValidationUtil;

import java.util.List;

/** CRUD de usuarios: valida permiso RBAC antes de cada acción y audita el resultado. */
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioDao usuarioDao;
    private final RolDao rolDao;
    private final AuditoriaService auditoriaService;

    public UsuarioServiceImpl(UsuarioDao usuarioDao, RolDao rolDao,
                              AuditoriaService auditoriaService) {
        this.usuarioDao = usuarioDao;
        this.rolDao = rolDao;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public List<Usuario> listar(Sesion sesion) {
        sesion.requerir("ver_usuarios");
        return usuarioDao.findAll();
    }

    @Override
    public List<Rol> listarRoles(Sesion sesion) {
        sesion.requerir("ver_usuarios");
        return rolDao.findAll();
    }

    @Override
    public int crear(Sesion sesion, Usuario usuario, String passwordPlano) {
        sesion.requerir("crear_usuario");
        ValidationUtil.requerir(usuario.getNombre(), "nombre");
        ValidationUtil.validarEmail(usuario.getEmail());
        ValidationUtil.requerirId(usuario.getRolId(), "rol");
        validarRolExiste(usuario.getRolId());
        ValidationUtil.requerir(passwordPlano, "contraseña");

        if (usuarioDao.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ValidacionException("Ya existe un usuario con ese email.");
        }

        int id = usuarioDao.insertar(usuario, PasswordHasher.hash(passwordPlano));
        auditoriaService.registrar(AccionAuditoria.CREACION_USUARIO, sesion, "usuarios", id,
                "Usuario creado: " + usuario.getEmail() + " (rol #" + usuario.getRolId() + ")");
        return id;
    }

    @Override
    public int actualizar(Sesion sesion, Usuario usuario, String passwordPlanoNuevo) {
        sesion.requerir("editar_usuario");
        ValidationUtil.requerirId(usuario.getId(), "usuario");
        ValidationUtil.validarEmail(usuario.getEmail());
        validarRolExiste(usuario.getRolId());

        String hash = passwordPlanoNuevo == null || passwordPlanoNuevo.isBlank()
                ? null : PasswordHasher.hash(passwordPlanoNuevo);
        int filas = usuarioDao.actualizar(usuario, hash);
        auditoriaService.registrar(AccionAuditoria.ACTUALIZACION_USUARIO, sesion, "usuarios",
                usuario.getId(), "Usuario actualizado: " + usuario.getEmail());
        return filas;
    }

    @Override
    public int eliminar(Sesion sesion, int usuarioId) {
        sesion.requerir("eliminar_usuario");
        if (AuditoriaServiceImpl.idDe(sesion) != null
                && AuditoriaServiceImpl.idDe(sesion) == usuarioId) {
            throw new ValidacionException("No puede eliminar su propia cuenta.");
        }
        int filas = usuarioDao.eliminar(usuarioId);
        auditoriaService.registrar(AccionAuditoria.ELIMINACION_USUARIO, sesion, "usuarios",
                usuarioId, "Usuario eliminado #" + usuarioId);
        return filas;
    }

    @Override
    public void cambiarEstado(Sesion sesion, int usuarioId, boolean activo) {
        sesion.requerir("editar_usuario");
        usuarioDao.cambiarEstado(usuarioId, activo);
        auditoriaService.registrar(AccionAuditoria.ACTUALIZACION_USUARIO, sesion, "usuarios",
                usuarioId, activo ? "Usuario activado" : "Usuario desactivado");
    }

    private void validarRolExiste(int rolId) {
        if (rolDao.findById(rolId).isEmpty()) {
            throw new ValidacionException("El rol seleccionado no existe.");
        }
    }
}
