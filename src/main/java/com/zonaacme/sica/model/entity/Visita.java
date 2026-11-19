package com.zonaacme.sica.model.entity;

import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;

import java.time.LocalDateTime;

/** Entidad de la tabla {@code visitas} con los datos enriquecidos para tablas y reportes. */
public class Visita {

    private Integer id;
    private Integer personaId;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private Integer estadoVisitaId;
    private String vehiculoPlaca;
    private Integer visitaAprobadaPor;

    // Datos derivados (JOIN), no columnas de la tabla.
    private String nombrePersona;
    private String documentoPersona;
    private String urlFoto;
    private String nombreEmpresa;
    private String nombreEstado;
    private String nombreAprobador;

    /** Tipo de flujo que originó el registro. No se persiste: lo usa VisitaFactory. */
    private transient TipoFlujoVisita tipoFlujo;

    public Visita() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Integer personaId) {
        this.personaId = personaId;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Integer getEstadoVisitaId() {
        return estadoVisitaId;
    }

    public void setEstadoVisitaId(Integer estadoVisitaId) {
        this.estadoVisitaId = estadoVisitaId;
    }

    public String getVehiculoPlaca() {
        return vehiculoPlaca;
    }

    public void setVehiculoPlaca(String vehiculoPlaca) {
        this.vehiculoPlaca = vehiculoPlaca;
    }

    public Integer getVisitaAprobadaPor() {
        return visitaAprobadaPor;
    }

    public void setVisitaAprobadaPor(Integer visitaAprobadaPor) {
        this.visitaAprobadaPor = visitaAprobadaPor;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public String getDocumentoPersona() {
        return documentoPersona;
    }

    public void setDocumentoPersona(String documentoPersona) {
        this.documentoPersona = documentoPersona;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getNombreAprobador() {
        return nombreAprobador;
    }

    public void setNombreAprobador(String nombreAprobador) {
        this.nombreAprobador = nombreAprobador;
    }

    public TipoFlujoVisita getTipoFlujo() {
        return tipoFlujo;
    }

    public void setTipoFlujo(TipoFlujoVisita tipoFlujo) {
        this.tipoFlujo = tipoFlujo;
    }

    public EstadoVisita getEstado() {
        return estadoVisitaId == null ? null : EstadoVisita.fromId(estadoVisitaId);
    }

    public boolean esPendiente() {
        EstadoVisita estado = getEstado();
        return estado != null && estado.esPendiente();
    }

    @Override
    public String toString() {
        return "Visita #" + id + " - " + nombrePersona + " (" + nombreEstado + ")";
    }
}
