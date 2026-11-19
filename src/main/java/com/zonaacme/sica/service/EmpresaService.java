package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Empresa;
import com.zonaacme.sica.model.entity.Sesion;

import java.util.List;

/** Gestión de empresas del complejo. */
public interface EmpresaService {

    List<Empresa> listar(Sesion sesion);

    int crear(Sesion sesion, Empresa empresa);

    int actualizar(Sesion sesion, Empresa empresa);

    int eliminar(Sesion sesion, int empresaId);
}
