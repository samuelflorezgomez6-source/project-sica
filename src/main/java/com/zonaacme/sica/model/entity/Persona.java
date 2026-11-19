package com.zonaacme.sica.model.entity;

import com.zonaacme.sica.model.enums.EstadoAcceso;
import com.zonaacme.sica.model.enums.TipoPersona;

/** Entidad de la tabla {@code personas} con los datos enriquecidos para la UI. */
public class Persona {

    private Integer id;
    private String nombre;
    private String documentoIdentidad;
    private Integer empresaId;
    private TipoPersona tipoPersona;
    private Integer estadoAccesoId;
    private String urlFoto;

    // Datos derivados (JOIN), no columnas de la tabla.
    private String nombreEmpresa;
    private String nombreEstado;

    public Persona() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public Integer getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Integer empresaId) {
        this.empresaId = empresaId;
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public Integer getEstadoAccesoId() {
        return estadoAccesoId;
    }

    public void setEstadoAccesoId(Integer estadoAccesoId) {
        this.estadoAccesoId = estadoAccesoId;
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

    public boolean estaBloqueada() {
        return estadoAccesoId != null && estadoAccesoId == EstadoAcceso.PROHIBIDO.getId();
    }

    @Override
    public String toString() {
        return nombre + " (" + documentoIdentidad + ")";
    }
}
