package com.zonaacme.sica.pattern.observer;

/** PATRÓN 4 - OBSERVER: contrato del publicador de eventos de visitas. */
public interface VisitaSubject {

    void suscribir(VisitaObserver observador);

    void desuscribir(VisitaObserver observador);

    void notificar(TipoEventoVisita tipo, String mensaje);
}
