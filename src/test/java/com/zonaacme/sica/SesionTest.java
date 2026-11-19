package com.zonaacme.sica;

import com.zonaacme.sica.exception.AccesoDenegadoException;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Usuario;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica la validación RBAC en memoria (Streams + Lambdas). */
class SesionTest {

    private static Sesion sesionConPermisos(String... permisos) {
        Usuario usuario = new Usuario();
        usuario.setId(3);
        usuario.setNombre("Guarda");
        usuario.setRol(new Rol(3, "Guarda de Seguridad"));
        return new Sesion(usuario, Set.of(permisos));
    }

    @Test
    void tienePermisoDevuelveVerdaderoSoloParaLosCargados() {
        Sesion sesion = sesionConPermisos("ver_control_acceso", "check_in");

        assertTrue(sesion.tienePermiso("check_in"));
        assertFalse(sesion.tienePermiso("eliminar_usuario"));
    }

    @Test
    void requerirLanzaMensajeClaroSiFaltaPermiso() {
        Sesion sesion = sesionConPermisos("ver_control_acceso");

        AccesoDenegadoException ex = assertThrows(AccesoDenegadoException.class,
                () -> sesion.requerir("eliminar_usuario"));

        assertTrue(ex.getMessage().contains("eliminar_usuario"));
        assertTrue(ex.getMessage().contains("Acceso denegado"));
    }

    @Test
    void permisosQueEmpiezanConFiltraConStream() {
        Sesion sesion = sesionConPermisos("ver_personas", "crear_persona", "check_in");

        Set<String> dePersonas = sesion.permisosQueEmpiezanCon("ver_");

        assertEquals(Set.of("ver_personas"), dePersonas);
    }
}
