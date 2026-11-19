package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;

import java.util.Optional;

/** Regla 5: trabajador con carnet olvidado, aprobación puntual para ese día. */
public class CarnetOlvidadoStrategy implements IngresoStrategy {

    @Override
    public ResultadoIngreso evaluar(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta) {
        if (solicitud.getFlujo() != TipoFlujoVisita.CARNET_OLVIDADO) {
            return null;
        }
        return ResultadoIngreso.pendiente(EstadoVisita.PENDIENTE_OLVIDO,
                "Pase temporal solicitado: el funcionario debe aprobar el ingreso de hoy.");
    }
}
