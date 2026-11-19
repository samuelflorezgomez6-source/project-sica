package com.zonaacme.sica.model.entity;

/** Entidad de la tabla {@code empresas}. */
public class Empresa {

    private Integer id;
    private String nombre;
    private String contactoPrincipal;

    public Empresa() {
    }

    public Empresa(Integer id, String nombre, String contactoPrincipal) {
        this.id = id;
        this.nombre = nombre;
        this.contactoPrincipal = contactoPrincipal;
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

    public String getContactoPrincipal() {
        return contactoPrincipal;
    }

    public void setContactoPrincipal(String contactoPrincipal) {
        this.contactoPrincipal = contactoPrincipal;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
