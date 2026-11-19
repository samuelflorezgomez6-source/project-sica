package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Visita;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolvedor de estrategias: aplica las reglas en orden de prioridad
 * y devuelve el primer resultado aplicable (API Stream + lambda).
 */
public class IngresoStrategyResolver {

    private final List<IngresoStrategy> estrategias = List.of(
            new PersonaBloqueadaStrategy(),
            new SalidaOlvidadaStrategy(),
            new PreRegistradoStrategy(),
            new NoAnunciadoStrategy(),
            new CarnetOlvidadoStrategy());

    public ResultadoIngreso resolver(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta) {
        return estrategias.stream()
                .map(estrategia -> estrategia.evaluar(solicitud, visitaAbierta))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ValidacionException("No se pudo determinar la regla de ingreso."));
    }
}
