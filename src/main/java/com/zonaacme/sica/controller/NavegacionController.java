package com.zonaacme.sica.controller;

import com.zonaacme.sica.view.LoginFrame;

import javax.swing.SwingUtilities;

/** Controlador de navegación entre ventanas (multi-sesión concurrent). */
public final class NavegacionController {

    /** Abre una ventana de login independiente con su propia Sesion. */
    public void abrirNuevaVentanaSesion() {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
