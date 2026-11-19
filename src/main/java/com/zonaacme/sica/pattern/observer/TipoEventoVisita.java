package com.zonaacme.sica.pattern.observer;

/** Tipos de cambio que las ventanas pueden recibir en tiempo real. */
public enum TipoEventoVisita {
    NUEVA_SOLICITUD,
    APROBADA,
    RECHAZADA,
    CHECK_IN,
    CHECK_OUT,
    SALIDA_OLVIDADA,
    ACTUALIZACION_GENERAL
}
