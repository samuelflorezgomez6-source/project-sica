package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;

import java.util.Optional;

/** Regla 3: invitado pre-registrado por un funcionario (visita ya en estado Aprobado). */
public class PreRegistradoStrategy implements IngresoStrategy {

    @Override
    public ResultadoIngreso evaluar(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta) {
        if (solicitud.getFlujo() != TipoFlujoVisita.PRE_REGISTRADA) {
            return null;
        }
        return ResultadoIngreso.permitido(EstadoVisita.APROBADO,
                "Invitado pre-registrado: acceso autorizado. El guarda puede realizar el check-in.");
    }
}
