package com.zonaacme.sica.model.entity;

/** Entidad de la tabla {@code permisos}. Los permisos viven en la BD, no en el código. */
public class Permiso {

    private Integer id;
    private String nombrePermiso;
    private String descripcion;

    public Permiso() {
    }

    public Permiso(Integer id, String nombrePermiso, String descripcion) {
        this.id = id;
        this.nombrePermiso = nombrePermiso;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombrePermiso() {
        return nombrePermiso;
    }

    public void setNombrePermiso(String nombrePermiso) {
        this.nombrePermiso = nombrePermiso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombrePermiso;
    }
}
