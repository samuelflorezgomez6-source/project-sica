package com.zonaacme.sica.exception;

/** Excepción base del dominio SICA. Mensajes pensados para mostrar al usuario. */
public class SicaException extends RuntimeException {

    public SicaException(String mensaje) {
        super(mensaje);
    }

    public SicaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
