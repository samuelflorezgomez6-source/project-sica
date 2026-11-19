package com.zonaacme.sica;

import com.zonaacme.sica.config.AppSettings;
import com.zonaacme.sica.config.DatabaseConfig;
import com.zonaacme.sica.view.LoginFrame;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Punto de entrada de SICA. */
public final class SicaApplication {

    private SicaApplication() {
    }

    public static void main(String[] args) {
        UiTheme.instalar(AppSettings.isDarkTheme());
        boolean conectado = DatabaseConfig.getInstance().testConnection();

        SwingUtilities.invokeLater(() -> {
            if (!conectado) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con MySQL en "
                        + AppSettings.dbUrl()
                        + "\n\nEjecute: docker compose up -d y vuelva a intentarlo.\n"
                        + "La ventana de login se abrirá de todos modos para mostrar el error.",
                        "SICA - Base de datos no disponible", JOptionPane.ERROR_MESSAGE);
            }
            new LoginFrame().setVisible(true);
        });
    }
}
