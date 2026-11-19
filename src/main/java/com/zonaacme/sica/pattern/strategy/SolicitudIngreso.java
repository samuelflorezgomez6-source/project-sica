package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;

/** Contexto que reciben las estrategias de validación de ingreso. */
public class SolicitudIngreso {

    private final Persona persona;
    private final TipoFlujoVisita flujo;
    private final String vehiculoPlaca;
    private final Integer funcionarioId;
    private final Integer visitaPreRegistradaId;

    public SolicitudIngreso(Persona persona, TipoFlujoVisita flujo, String vehiculoPlaca,
                            Integer funcionarioId, Integer visitaPreRegistradaId) {
        this.persona = persona;
        this.flujo = flujo;
        this.vehiculoPlaca = vehiculoPlaca;
        this.funcionarioId = funcionarioId;
        this.visitaPreRegistradaId = visitaPreRegistradaId;
    }

    public Persona getPersona() {
        return persona;
    }

    public TipoFlujoVisita getFlujo() {
        return flujo;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public Integer getFuncionarioId() {
        return funcionarioId;
    }

    public Integer getVisitaPreRegistradaId() {
        return visitaPreRegistradaId;
    }
}
