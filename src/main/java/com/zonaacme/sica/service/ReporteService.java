package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Visita;

import java.time.LocalDateTime;
import java.util.List;

/** Reportes de permanencia, historial y auditoría. */
public interface ReporteService {

    List<Visita> personasDentro(Sesion sesion);

    List<Visita> historialFiltrado(Sesion sesion, String texto);

    List<BitacoraAuditoria> bitacoraFiltrada(Sesion sesion, String accion, Integer usuarioId,
                                             LocalDateTime desde, LocalDateTime hasta);
}
