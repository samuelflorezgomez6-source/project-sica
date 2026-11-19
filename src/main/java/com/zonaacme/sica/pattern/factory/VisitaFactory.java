package com.zonaacme.sica.pattern.factory;

import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;
import com.zonaacme.sica.pattern.strategy.SolicitudIngreso;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * PATRÓN 3 - FACTORY METHOD.
 * <p>
 * Centraliza la construcción de {@link Visita} según el flujo de acceso.
 * Si mañana aparece un quinto flujo, se-agrega una entrada al mapa y una
 * estrategia, sin tocar el servicio (OCP). El estado inicial y la fecha de
 * entrada dependen del tipo de visita creada.
 * </p>
 */
public class VisitaFactory {

    private static final Map<TipoFlujoVisita, EstadoVisita> ESTADOS_INICIALES = Map.of(
            TipoFlujoVisita.PRE_REGISTRADA, EstadoVisita.APROBADO,
            TipoFlujoVisita.NO_ANUNCIADA, EstadoVisita.PENDIENTE,
            TipoFlujoVisita.CARNET_OLVIDADO, EstadoVisita.PENDIENTE_OLVIDO,
            TipoFlujoVisita.SALIDA_OLVIDADA, EstadoVisita.DENTRO);

    /** Crea la visita con el estado que corresponde a su flujo. */
    public Visita crear(SolicitudIngreso solicitud) {
        return crear(solicitud, ESTADOS_INICIALES.get(solicitud.getFlujo()));
    }

    /**
     * Crea la visita con un estado explícito. Se usa cuando la estrategia de
     * regularización (salida olvidada) fuerza el estado DENTRO.
     */
    public Visita crear(SolicitudIngreso solicitud, EstadoVisita estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Flujo de visita no soportado: " + solicitud.getFlujo());
        }
        Visita visita = new Visita();
        visita.setPersonaId(solicitud.getPersona().getId());
        visita.setEstadoVisitaId(estado.getId());
        visita.setVehiculoPlaca(solicitud.getVehiculoPlaca());
        visita.setVisitaAprobadaPor(
                estado == EstadoVisita.APROBADO ? solicitud.getFuncionarioId() : null);
        visita.setFechaEntrada(estado == EstadoVisita.DENTRO ? LocalDateTime.now() : null);
        visita.setTipoFlujo(solicitud.getFlujo());
        return visita;
    }
}
