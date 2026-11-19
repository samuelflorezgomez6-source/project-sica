package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.Sesion;

/** Autenticación con BCrypt y carga dinámica de permisos desde la BD. */
public interface AuthService {

    Sesion login(String email, String password);
}
