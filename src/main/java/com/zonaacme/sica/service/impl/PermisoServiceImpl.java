package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.PermisoDao;
import com.zonaacme.sica.dao.RolDao;
import com.zonaacme.sica.model.entity.Permiso;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.PermisoService;

import java.util.List;

/** RBAC dinámico con validación de permiso y auditoría en la asignación. */
public class PermisoServiceImpl implements PermisoService {

    private final PermisoDao permisoDao;
    private final RolDao rolDao;
    private final AuditoriaService auditoriaService;

    public PermisoServiceImpl(PermisoDao permisoDao, RolDao rolDao,
                              AuditoriaService auditoriaService) {
        this.permisoDao = permisoDao;
        this.rolDao = rolDao;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public List<Rol> listarRoles() {
        return rolDao.findAll();
    }

    @Override
    public List<Permiso> listarPermisos() {
        return permisoDao.findAll();
    }

    @Override
    public List<Permiso> permisosDelRol(int rolId) {
        return rolDao.findPermisos(rolId);
    }

    @Override
    public void reemplazarPermisos(Sesion sesion, int rolId, List<Integer> permisoIds) {
        sesion.requerir("gestionar_roles_permisos");
        rolDao.reemplazarPermisos(rolId, permisoIds);
        auditoriaService.registrar(
                AccionAuditoria.CAMBIO_ROL_PERMISOS, sesion, "rol_permisos", rolId,
                "Permisos asignados al rol #" + rolId + ": " + permisoIds.size());
    }
}
