package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;

import java.util.Optional;

/**
 * Regla 2: si existe una visita abierta ("Dentro"), NO se bloquea el paso.
 * Se regulariza: la visita anterior se cierra como "Cerrada por Sistema (Salida Olvidada)"
 * y se abre una nueva. Así lo exige el flujo 4 del requerimiento.
 */
public class SalidaOlvidadaStrategy implements IngresoStrategy {

    @Override
    public ResultadoIngreso evaluar(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta) {
        return visitaAbierta
                .map(anterior -> ResultadoIngreso.regularizar(anterior,
                        "Salida olvidada detectada (visita #" + anterior.getId()
                        + "). Se cierra automáticamente y se registra el nuevo ingreso."))
                .orElse(null);
    }
}
