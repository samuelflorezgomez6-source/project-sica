package com.zonaacme.sica.pattern.observer;

/** PATRÓN 4 - OBSERVER: interfaz que implementan las ventanas/paneles interesados. */
public interface VisitaObserver {

    /**
     * Se ejecuta en el EDT de Swing (el bus se encarga de eso).
     *
     * @param tipo    qué ocurrió
     * @param mensaje resumen legible para la barra de estado o un aviso
     */
    void onVisitasActualizadas(TipoEventoVisita tipo, String mensaje);
}
