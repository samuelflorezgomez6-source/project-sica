package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;

import java.util.Optional;

/** Regla 4: invitado no anunciado, requiere aprobación en tiempo real. */
public class NoAnunciadoStrategy implements IngresoStrategy {

    @Override
    public ResultadoIngreso evaluar(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta) {
        if (solicitud.getFlujo() != TipoFlujoVisita.NO_ANUNCIADA) {
            return null;
        }
        return ResultadoIngreso.pendiente(EstadoVisita.PENDIENTE,
                "Solicitud enviada al funcionario de empresa. Esperando aprobación.");
    }
}
