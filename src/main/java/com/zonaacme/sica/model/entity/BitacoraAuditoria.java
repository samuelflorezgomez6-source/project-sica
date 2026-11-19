package com.zonaacme.sica.model.entity;

import java.time.LocalDateTime;

/** Entidad de la tabla {@code bitacora_auditoria}. Solo lectura desde la UI. */
public class BitacoraAuditoria {

    private long id;
    private Integer usuarioId;
    private LocalDateTime fechaHora;
    private String accionRealizada;
    private String tablaAfectada;
    private Integer registroIdAfectado;
    private String detalles;

    // Dato derivado (JOIN), no columna de la tabla.
    private String nombreUsuario;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getAccionRealizada() {
        return accionRealizada;
    }

    public void setAccionRealizada(String accionRealizada) {
        this.accionRealizada = accionRealizada;
    }

    public String getTablaAfectada() {
        return tablaAfectada;
    }

    public void setTablaAfectada(String tablaAfectada) {
        this.tablaAfectada = tablaAfectada;
    }

    public Integer getRegistroIdAfectado() {
        return registroIdAfectado;
    }

    public void setRegistroIdAfectado(Integer registroIdAfectado) {
        this.registroIdAfectado = registroIdAfectado;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public String toString() {
        return accionRealizada + " - " + fechaHora;
    }
}
