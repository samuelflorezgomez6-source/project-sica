package com.zonaacme.sica.model.enums;

import java.util.Arrays;

/**
 * Lookup {@code visita_estados}. Se agregan dos estados a los cuatro del PDF
 * porque el propio documento indica "etc." y los flujos 3 y 4 los exigen:
 * PENDIENTE_OLVIDO y CERRADA_SISTEMA.
 */
public enum EstadoVisita {
    DENTRO(1, "Dentro"),
    FUERA(2, "Fuera"),
    PENDIENTE(3, "Pendiente de Aprobacion"),
    APROBADO(4, "Aprobado"),
    RECHAZADO(5, "Rechazado"),
    EXPIRADO(6, "Expirado"),
    PENDIENTE_OLVIDO(7, "Pendiente de Aprobacion por Olvido"),
    CERRADA_SISTEMA(8, "Cerrada por Sistema (Salida Olvidada)");

    private final int id;
    private final String nombre;

    EstadoVisita(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean esPendiente() {
        return this == PENDIENTE || this == PENDIENTE_OLVIDO;
    }

    /** Búsqueda en memoria con Stream + Lambda. */
    public static EstadoVisita fromNombre(String nombre) {
        return Arrays.stream(values())
                .filter(e -> e.nombre.equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de visita desconocido: " + nombre));
    }

    public static EstadoVisita fromId(int id) {
        return Arrays.stream(values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de visita desconocido: " + id));
    }

    @Override
    public String toString() {
        return nombre;
    }
}
