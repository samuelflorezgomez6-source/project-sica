package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.EstadoAcceso;

import java.util.List;
import java.util.Optional;

/** Gestión de personas y bloqueo de acceso. */
public interface PersonaService {

    List<Persona> listar(Sesion sesion);

    /** Filtrado en memoria con Streams + lambdas. */
    List<Persona> buscar(Sesion sesion, String texto);

    Optional<Persona> porDocumento(Sesion sesion, String documento);

    int crear(Sesion sesion, Persona persona);

    int actualizar(Sesion sesion, Persona persona);

    int eliminar(Sesion sesion, int personaId);

    void cambiarEstadoAcceso(Sesion sesion, int personaId, EstadoAcceso estado);
}
