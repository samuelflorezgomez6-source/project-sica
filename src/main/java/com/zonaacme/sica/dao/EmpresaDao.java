package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Empresa;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de empresas. */
public interface EmpresaDao {

    List<Empresa> findAll();

    Optional<Empresa> findById(int id);

    int insertar(Empresa empresa);

    int actualizar(Empresa empresa);

    int eliminar(int id);
}
