package com.zonaacme.sica.model.enums;

import java.util.Arrays;

/** Lookup {@code persona_estados_acceso}. El id es la llave primaria en la BD. */
public enum EstadoAcceso {
    ACTIVO(1, "Activo"),
    PROHIBIDO(2, "Con Prohibicion de Ingreso");

    private final int id;
    private final String nombre;

    EstadoAcceso(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    /** Búsqueda en memoria con Stream + Lambda. */
    public static EstadoAcceso fromNombre(String nombre) {
        return Arrays.stream(values())
                .filter(e -> e.nombre.equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de acceso desconocido: " + nombre));
    }

    @Override
    public String toString() {
        return nombre;
    }
}
