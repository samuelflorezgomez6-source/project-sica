package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;

import java.util.Optional;

/** Regla 1 (prioridad máxima): una persona con acceso prohibido no ingresa. */
public class PersonaBloqueadaStrategy implements IngresoStrategy {

    @Override
    public ResultadoIngreso evaluar(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta) {
        if (solicitud.getPersona().estaBloqueada()) {
            return ResultadoIngreso.denegado(
                    "Acceso denegado: " + solicitud.getPersona().getNombre()
                    + " tiene prohibición de ingreso vigente.");
        }
        return null;
    }
}
