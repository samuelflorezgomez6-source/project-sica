package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.pattern.observer.VisitaSubject;
import com.zonaacme.sica.pattern.strategy.ResultadoIngreso;
import com.zonaacme.sica.pattern.strategy.SolicitudIngreso;

import java.util.List;

/**
 * Operaciones de control de acceso. Extiende {@link VisitaSubject} para que
 * las ventanas abiertas reciban los cambios en tiempo real (PATRÓN OBSERVER).
 */
public interface VisitaService extends VisitaSubject {

    List<Visita> pendientes(Sesion sesion);

    List<Visita> dentro(Sesion sesion);

    List<Visita> historial(Sesion sesion);

    List<Visita> porPersona(Sesion sesion, int personaId);

    ResultadoIngreso registrarIngreso(Sesion sesion, SolicitudIngreso solicitud);

    void checkIn(Sesion sesion, int visitaId, String placa);

    void aprobar(Sesion sesion, int visitaId);

    void rechazar(Sesion sesion, int visitaId);

    void checkOut(Sesion sesion, int visitaId);
}
