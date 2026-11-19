package com.zonaacme.sica.exception;

/** Fallo al acceder a MySQL. Oculta detalles técnicos al usuario final. */
public class PersistenceException extends SicaException {

    public PersistenceException(String operacion, Throwable causa) {
        super("No se pudo completar la operación '" + operacion
                + "'. Verifique que MySQL esté activo. Detalle: " + causa.getMessage(), causa);
    }
}
