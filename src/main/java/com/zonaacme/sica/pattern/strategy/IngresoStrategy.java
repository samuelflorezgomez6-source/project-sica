package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;

import java.util.Optional;

/**
 * PATRÓN 3 (segundo uso) - STRATEGY: una regla de validación de ingreso.
 * <p>
 * Convención: devuelve {@code null} cuando la regla no aplica al caso;
 * el resolvedor toma la primera regla aplicable en orden de prioridad.
 * </p>
 */
public interface IngresoStrategy {

    ResultadoIngreso evaluar(SolicitudIngreso solicitud, Optional<Visita> visitaAbierta);
}
