package com.zonaacme.sica;

import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoAcceso;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;
import com.zonaacme.sica.pattern.strategy.IngresoStrategyResolver;
import com.zonaacme.sica.pattern.strategy.ResultadoIngreso;
import com.zonaacme.sica.pattern.strategy.SolicitudIngreso;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica el orden de prioridad de las reglas Strategy de ingreso. */
class IngresoStrategyResolverTest {

    private final IngresoStrategyResolver resolver = new IngresoStrategyResolver();

    private static Persona persona(int estadoAccesoId) {
        Persona persona = new Persona();
        persona.setId(7);
        persona.setNombre("Persona de prueba");
        persona.setDocumentoIdentidad("10000007");
        persona.setEstadoAccesoId(estadoAccesoId);
        return persona;
    }

    private static SolicitudIngreso solicitud(Persona persona, TipoFlujoVisita flujo) {
        return new SolicitudIngreso(persona, flujo, "ABC-123", 4, null);
    }

    @Test
    void personaBloqueadaSeRechazaAntesQueCualquierFlujo() {
        ResultadoIngreso resultado = resolver.resolver(
                solicitud(persona(EstadoAcceso.PROHIBIDO.getId()), TipoFlujoVisita.PRE_REGISTRADA),
                Optional.empty());

        assertFalse(resultado.isPermitido());
        assertTrue(resultado.getMensaje().contains("prohibición"));
    }

    @Test
    void visitaAbiertaSeRegularizaComoSalidaOlvidada() {
        Visita anterior = new Visita();
        anterior.setId(42);
        anterior.setEstadoVisitaId(EstadoVisita.DENTRO.getId());

        ResultadoIngreso resultado = resolver.resolver(
                solicitud(persona(EstadoAcceso.ACTIVO.getId()), TipoFlujoVisita.CARNET_OLVIDADO),
                Optional.of(anterior));

        assertTrue(resultado.isRegularizarSalida());
        assertEquals(EstadoVisita.DENTRO, resultado.getEstadoDestino());
        assertEquals(anterior, resultado.getVisitaAnterior());
    }

    @Test
    void preRegistradoQuedaAprobadoParaCheckIn() {
        ResultadoIngreso resultado = resolver.resolver(
                solicitud(persona(EstadoAcceso.ACTIVO.getId()), TipoFlujoVisita.PRE_REGISTRADA),
                Optional.empty());

        assertTrue(resultado.isPermitido());
        assertFalse(resultado.isRequiereAprobacion());
        assertEquals(EstadoVisita.APROBADO, resultado.getEstadoDestino());
    }

    @Test
    void noAnunciadoRequiereAprobacionEnTiempoReal() {
        ResultadoIngreso resultado = resolver.resolver(
                solicitud(persona(EstadoAcceso.ACTIVO.getId()), TipoFlujoVisita.NO_ANUNCIADA),
                Optional.empty());

        assertTrue(resultado.isRequiereAprobacion());
        assertEquals(EstadoVisita.PENDIENTE, resultado.getEstadoDestino());
    }

    @Test
    void carnetOlvidadoRequiereAprobacionPuntual() {
        ResultadoIngreso resultado = resolver.resolver(
                solicitud(persona(EstadoAcceso.ACTIVO.getId()), TipoFlujoVisita.CARNET_OLVIDADO),
                Optional.empty());

        assertTrue(resultado.isRequiereAprobacion());
        assertEquals(EstadoVisita.PENDIENTE_OLVIDO, resultado.getEstadoDestino());
    }
}
