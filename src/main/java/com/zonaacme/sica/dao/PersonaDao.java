package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Persona;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de personas. */
public interface PersonaDao {

    List<Persona> findAll();

    Optional<Persona> findById(int id);

    Optional<Persona> findByDocumento(String documento);

    List<Persona> findByEmpresa(int empresaId);

    int insertar(Persona persona);

    int actualizar(Persona persona);

    int eliminar(int id);

    int cambiarEstadoAcceso(int personaId, int estadoId);
}
