package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.EmpresaDao;
import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Empresa;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.EmpresaService;
import com.zonaacme.sica.util.ValidationUtil;

import java.util.List;

/** CRUD de empresas con RBAC y auditoría. */
public class EmpresaServiceImpl implements EmpresaService {

    private final EmpresaDao empresaDao;
    private final AuditoriaService auditoriaService;

    public EmpresaServiceImpl(EmpresaDao empresaDao, AuditoriaService auditoriaService) {
        this.empresaDao = empresaDao;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public List<Empresa> listar(Sesion sesion) {
        sesion.requerir("ver_empresas");
        return empresaDao.findAll();
    }

    @Override
    public int crear(Sesion sesion, Empresa empresa) {
        sesion.requerir("crear_empresa");
        validar(empresa);
        int id = empresaDao.insertar(empresa);
        auditoriaService.registrar(AccionAuditoria.CREACION_EMPRESA, sesion, "empresas", id,
                "Empresa creada: " + empresa.getNombre());
        return id;
    }

    @Override
    public int actualizar(Sesion sesion, Empresa empresa) {
        sesion.requerir("editar_empresa");
        ValidationUtil.requerirId(empresa.getId(), "empresa");
        validar(empresa);
        int filas = empresaDao.actualizar(empresa);
        auditoriaService.registrar(AccionAuditoria.ACTUALIZACION_EMPRESA, sesion, "empresas",
                empresa.getId(), "Empresa actualizada: " + empresa.getNombre());
        return filas;
    }

    @Override
    public int eliminar(Sesion sesion, int empresaId) {
        sesion.requerir("eliminar_empresa");
        int filas = empresaDao.eliminar(empresaId);
        auditoriaService.registrar(AccionAuditoria.ELIMINACION_EMPRESA, sesion, "empresas",
                empresaId, "Empresa eliminada #" + empresaId);
        return filas;
    }

    private void validar(Empresa empresa) {
        if (empresa == null) {
            throw new ValidacionException("Debe completar los datos de la empresa.");
        }
        ValidationUtil.requerir(empresa.getNombre(), "nombre de la empresa");
    }
}
