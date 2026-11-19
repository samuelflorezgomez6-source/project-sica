package com.zonaacme.sica.model.entity;

import com.zonaacme.sica.exception.AccesoDenegadoException;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sesión de una ventana de Swing. NO es Singleton: cada ventana tiene la suya,
 * lo que permite abrir Guarda y Funcionario simultáneamente con roles distintos.
 * <p>
 * Los permisos se obtienen de la BD al iniciar sesión y se validan en memoria
 * (Streams) tanto en la UI como en la capa de servicio.
 * </p>
 */
public class Sesion {

    private final Usuario usuario;
    private final Set<String> permisos;
    private final LocalDateTime inicio;

    public Sesion(Usuario usuario, Set<String> permisos) {
        this.usuario = usuario;
        this.permisos = new LinkedHashSet<>(permisos);
        this.inicio = LocalDateTime.now();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Set<String> getPermisos() {
        return permisos;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public boolean tienePermiso(String nombrePermiso) {
        return permisos.contains(nombrePermiso);
    }

    /** Validación en la capa de servicio: lanza un error claro si no hay permiso. */
    public void requerir(String nombrePermiso) {
        if (!tienePermiso(nombrePermiso)) {
            throw new AccesoDenegadoException(nombrePermiso);
        }
    }

    /** Filtrado funcional de permisos por prefijo (útil para agrupar módulos). */
    public Set<String> permisosQueEmpiezanCon(String prefijo) {
        return permisos.stream()
                .filter(p -> p.startsWith(prefijo))
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toString() {
        return usuario.getNombre() + " [" + usuario.getNombreRol() + "]";
    }
}
