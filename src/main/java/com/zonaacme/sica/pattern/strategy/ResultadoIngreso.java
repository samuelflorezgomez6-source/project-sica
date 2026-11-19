package com.zonaacme.sica.pattern.strategy;

import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.EstadoVisita;

/** Resultado de aplicar una regla de ingreso (PATRÓN STRATEGY). */
public final class ResultadoIngreso {

    private final boolean permitido;
    private final boolean requiereAprobacion;
    private final boolean regularizarSalida;
    private final EstadoVisita estadoDestino;
    private final String mensaje;
    private final Visita visitaAnterior;

    private ResultadoIngreso(boolean permitido, boolean requiereAprobacion, boolean regularizarSalida,
                             EstadoVisita estadoDestino, String mensaje, Visita visitaAnterior) {
        this.permitido = permitido;
        this.requiereAprobacion = requiereAprobacion;
        this.regularizarSalida = regularizarSalida;
        this.estadoDestino = estadoDestino;
        this.mensaje = mensaje;
        this.visitaAnterior = visitaAnterior;
    }

    public static ResultadoIngreso permitido(EstadoVisita estado, String mensaje) {
        return new ResultadoIngreso(true, false, false, estado, mensaje, null);
    }

    public static ResultadoIngreso pendiente(EstadoVisita estado, String mensaje) {
        return new ResultadoIngreso(true, true, false, estado, mensaje, null);
    }

    public static ResultadoIngreso regularizar(Visita anterior, String mensaje) {
        return new ResultadoIngreso(true, false, true, EstadoVisita.DENTRO, mensaje, anterior);
    }

    public static ResultadoIngreso denegado(String mensaje) {
        return new ResultadoIngreso(false, false, false, null, mensaje, null);
    }

    public boolean isPermitido() {
        return permitido;
    }

    public boolean isRequiereAprobacion() {
        return requiereAprobacion;
    }

    public boolean isRegularizarSalida() {
        return regularizarSalida;
    }

    public EstadoVisita getEstadoDestino() {
        return estadoDestino;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Visita getVisitaAnterior() {
        return visitaAnterior;
    }
}
