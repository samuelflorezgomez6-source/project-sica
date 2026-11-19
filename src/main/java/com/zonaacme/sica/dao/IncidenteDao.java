package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Incidente;

import java.util.List;

/** Contrato de persistencia de incidentes. */
public interface IncidenteDao {

    List<Incidente> findAll();

    List<Incidente> findByVisita(int visitaId);

    int insertar(Incidente incidente);
}
