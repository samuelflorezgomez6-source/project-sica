package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Incidente;
import com.zonaacme.sica.model.entity.Sesion;

import java.util.List;

/** Registro y consulta de incidentes asociados a una visita. */
public interface IncidenteService {

    List<Incidente> listar(Sesion sesion);

    int registrar(Sesion sesion, Incidente incidente);
}
