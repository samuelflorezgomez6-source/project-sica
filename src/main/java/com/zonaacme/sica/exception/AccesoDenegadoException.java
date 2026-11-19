package com.zonaacme.sica.exception;

/** Se lanza cuando la sesión actual no posee el permiso requerido (RBAC). */
public class AccesoDenegadoException extends SicaException {

    public AccesoDenegadoException(String permiso) {
        super("Acceso denegado: su rol no tiene el permiso '" + permiso
                + "'. Solicite autorización al administrador.");
    }
}
