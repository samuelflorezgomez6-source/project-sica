package com.zonaacme.sica.model.entity;


import java.util.LinkedHashSet;
import java.util.Set;

/** Entidad de la tabla {@code usuarios}. Incluye datos RBAC derivados para la sesiÃ³n. */
public class Usuario {

    private Integer id;
    private String nombre;
    private String email;
    private String password;
    private Integer rolId;
    private boolean activo;
    private Rol rol;
    private Set<String> permisos = new LinkedHashSet<>();

    public Usuario() {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Set<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<String> permisos) {
        this.permisos = permisos;
    }

    public String getNombreRol() {
        return rol == null ? "â€”" : rol.getNombreRol();
    }

    /** Etiqueta legible para el combo de "tipo de persona" en formularios. */
    public Set<String> getPermisosOrdenados() {
        return permisos.stream().sorted().collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toString() {
        return nombre + " <" + email + ">";
    }
}
