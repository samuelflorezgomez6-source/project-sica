package com.zonaacme.sica.pattern.observer;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PATRÓN 4 - OBSERVER (publicador).
 * <p>
 * Mantiene la lista de ventanas suscritas. Cuando el servicio de visitas
 * aprueba, rechaza o registra un ingreso, llama a {@link #notificar(...)}.
 * Cada callback se envía al <b>Event Dispatch Thread</b> de Swing con
 * {@code SwingUtilities.invokeLater}, de modo que los paneles puedan actualizar
 * sus tablas sin violar las reglas de concurrencia de Swing.
 * </p>
 * <p>
 * {@code CopyOnWriteArrayList} permite que hilos de distintas ventanas se
 * suscriban o reciban eventos sin {@code ConcurrentModificationException}.
 * </p>
 */
public class VisitaEventBus implements VisitaSubject {

    private final List<VisitaObserver> observadores = new CopyOnWriteArrayList<>();

    @Override
    public void suscribir(VisitaObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void desuscribir(VisitaObserver observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificar(TipoEventoVisita tipo, String mensaje) {
        observadores.forEach(observador ->
                SwingUtilities.invokeLater(() -> observador.onVisitasActualizadas(tipo, mensaje)));
    }

    public int cantidadObservadores() {
        return observadores.size();
    }
}
