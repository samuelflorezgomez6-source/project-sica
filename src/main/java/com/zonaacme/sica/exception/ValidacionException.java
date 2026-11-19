package com.zonaacme.sica.exception;

/** Datos de entrada inválidos o regla de negocio incumplida. */
public class ValidacionException extends SicaException {

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
