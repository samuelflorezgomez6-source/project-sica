package com.zonaacme.sica;

import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;
import com.zonaacme.sica.pattern.factory.VisitaFactory;
import com.zonaacme.sica.pattern.strategy.SolicitudIngreso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica que la Factory asigna el estado inicial correcto según el flujo. */
class VisitaFactoryTest {

    private final VisitaFactory factory = new VisitaFactory();

    private static SolicitudIngreso solicitud(TipoFlujoVisita flujo) {
        Persona persona = new Persona();
        persona.setId(2);
        persona.setDocumentoIdentidad("20000002");
        return new SolicitudIngreso(persona, flujo, "XYZ-987", 4, null);
    }

    @Test
    void preRegistradaNaceAprobadaYAprobadorQuedaRegistrado() {
        Visita visita = factory.crear(solicitud(TipoFlujoVisita.PRE_REGISTRADA));

        assertEquals(EstadoVisita.APROBADO.getId(), visita.getEstadoVisitaId());
        assertEquals(4, visita.getVisitaAprobadaPor());
        assertNull(visita.getFechaEntrada());
        assertEquals(TipoFlujoVisita.PRE_REGISTRADA, visita.getTipoFlujo());
    }

    @Test
    void noAnunciadaNacePendienteSinAprobador() {
        Visita visita = factory.crear(solicitud(TipoFlujoVisita.NO_ANUNCIADA));

        assertEquals(EstadoVisita.PENDIENTE.getId(), visita.getEstadoVisitaId());
        assertNull(visita.getVisitaAprobadaPor());
    }

    @Test
    void salidaOlvidadaNaceDentroConFechaDeEntrada() {
        Visita visita = factory.crear(
                solicitud(TipoFlujoVisita.SALIDA_OLVIDADA), EstadoVisita.DENTRO);

        assertEquals(EstadoVisita.DENTRO.getId(), visita.getEstadoVisitaId());
        assertNotNull(visita.getFechaEntrada());
    }
}
