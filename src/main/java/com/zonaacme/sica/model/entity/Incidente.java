package com.zonaacme.sica.model.entity;

import java.time.LocalDateTime;

/** Entidad de la tabla {@code incidentes}. */
public class Incidente {

    private Integer id;
    private Integer visitaId;
    private Integer reportadoPorId;
    private LocalDateTime fecha;
    private String descripcion;

    // Datos derivados (JOIN), no columnas de la tabla.
    private String nombreReportador;
    private String resumenVisita;

    public Incidente() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVisitaId() {
        return visitaId;
    }

    public void setVisitaId(Integer visitaId) {
        this.visitaId = visitaId;
    }

    public Integer getReportadoPorId() {
        return reportadoPorId;
    }

    public void setReportadoPorId(Integer reportadoPorId) {
        this.reportadoPorId = reportadoPorId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreReportador() {
        return nombreReportador;
    }

    public void setNombreReportador(String nombreReportador) {
        this.nombreReportador = nombreReportador;
    }

    public String getResumenVisita() {
        return resumenVisita;
    }

    public void setResumenVisita(String resumenVisita) {
        this.resumenVisita = resumenVisita;
    }

    @Override
    public String toString() {
        return "Incidente #" + id + " - " + descripcion;
    }
}
