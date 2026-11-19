package com.zonaacme.sica.view.components;

import com.zonaacme.sica.config.AppSettings;
import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.pattern.observer.TipoEventoVisita;
import com.zonaacme.sica.pattern.observer.VisitaObserver;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Base de los paneles: se suscribe al Observer, refresca la vista en el EDT
 * y ejecuta un polling nativo con ScheduledExecutorService.
 * <p>
 * El polling complementa al Observer: si una ventana se abre después de un
 * cambio, o se pierde un evento, en menos de 2 segundos vuelve a sincronizar.
 * </p>
 */
public abstract class BasePanel extends JPanel implements VisitaObserver {

    protected final SicaController controller = new SicaController();
    protected final Sesion sesion;
    private final ScheduledExecutorService temporizador;
    private JLabel etiquetaEstado;
    private volatile boolean activo = true;

    protected BasePanel(Sesion sesion) {
        this.sesion = sesion;
        setLayout(new BorderLayout());
        setBackground(UiTheme.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        this.temporizador = Executors.newSingleThreadScheduledExecutor(tarea -> {
            Thread hilo = new Thread(tarea, "sica-refresh-" + sesion.getUsuario().getId());
            hilo.setDaemon(true);
            return hilo;
        });
        this.temporizador.scheduleWithFixedDelay(() -> {
            if (activo) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        recargar();
                    } catch (RuntimeException ignorado) {
                        // Un refresco fallido no debe matar el hilo del temporizador.
                    }
                });
            }
        }, 1, Math.max(1, AppSettings.refreshSeconds()), TimeUnit.SECONDS);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        controller.visitas().suscribir(this);
    }

    @Override
    public void removeNotify() {
        controller.visitas().desuscribir(this);
        super.removeNotify();
    }

    protected boolean puede(String permiso) {
        return sesion.tienePermiso(permiso);
    }

    /** Barra inferior con el último evento recibido en tiempo real. */
    protected JPanel barraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setOpaque(false);
        etiquetaEstado = new JLabel("> En vivo: esperando eventos...");
        etiquetaEstado.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        etiquetaEstado.setForeground(UiTheme.TEXTO_SUAVE);
        barra.add(etiquetaEstado);
        return barra;
    }

    /** Muestra el resultado de una acción del controlador. */
    protected boolean avisar(SicaController.Resultado resultado, java.awt.Component padre) {
        JOptionPane.showMessageDialog(padre, resultado.mensaje(),
                resultado.ok() ? "SICA" : "No se pudo completar",
                resultado.ok() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        return resultado.ok();
    }

    protected abstract void recargar();

    @Override
    public void onVisitasActualizadas(TipoEventoVisita tipo, String mensaje) {
        if (!activo) {
            return;
        }
        try {
            recargar();
            if (etiquetaEstado != null) {
                etiquetaEstado.setText("> En vivo [" + tipo + "]: " + mensaje);
                etiquetaEstado.setForeground(UiTheme.colorEstadoPorEvento(tipo));
            }
        } catch (RuntimeException ignorado) {
            // El polling periódico reintentará la carga.
        }
    }

    public void dispose() {
        activo = false;
        controller.visitas().desuscribir(this);
        temporizador.shutdownNow();
    }
}
