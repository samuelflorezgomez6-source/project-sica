package com.zonaacme.sica.view;

import com.zonaacme.sica.controller.NavegacionController;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.UiTheme;
import com.zonaacme.sica.view.panels.ControlAccesoPanel;
import com.zonaacme.sica.view.panels.EmpresasPanel;
import com.zonaacme.sica.view.panels.IncidentesPanel;
import com.zonaacme.sica.view.panels.PersonasPanel;
import com.zonaacme.sica.view.panels.ReportesPanel;
import com.zonaacme.sica.view.panels.UsuariosPanel;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.function.Supplier;

/**
 * Ventana principal. El menú se construye SOLO con los módulos que el rol
 * tiene permitidos según los permisos consultados desde la BD (RBAC dinámico).
 */
public class MainFrame extends JFrame {

    private final Sesion sesion;
    private final NavegacionController navegacion = new NavegacionController();
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel areaTrabajo = new JPanel(tarjetas);
    private JPanel menu;
    private JButton botonActivo;

    public MainFrame(Sesion sesion) {
        super("SICA - Zona Acme");
        this.sesion = sesion;
        setSize(1320, 780);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UiTheme.FONDO);

        this.menu = menuLateral();
        add(cabecera(), BorderLayout.NORTH);
        add(menu, BorderLayout.WEST);
        add(areaTrabajo, BorderLayout.CENTER);
        construirModulos();

        if (areaTrabajo.getComponentCount() > 0) {
            tarjetas.first(areaTrabajo);
        }
    }

    private JPanel cabecera() {
        JPanel cabecera = new JPanel(new BorderLayout(16, 8));
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 231, 240)),
                BorderFactory.createEmptyBorder(14, 22, 14, 22)));

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setOpaque(false);
        JLabel titulo = new JLabel("SICA  -  Panel de Control");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(UiTheme.TEXTO);
        JLabel sub = new JLabel("Zona Acme - Sistema Integrado de Control de Acceso");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(UiTheme.TEXTO_SUAVE);
        izquierda.add(titulo, BorderLayout.NORTH);
        izquierda.add(sub, BorderLayout.SOUTH);

        JPanel derecha = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        JLabel usuario = new JLabel("<html><b>" + sesion.getUsuario().getNombre()
                + "</b><br><font color='#64748B' size='10'>"
                + sesion.getUsuario().getNombreRol() + "</font></html>");
        usuario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        JButton nuevaVentana = UiTheme.boton("+ Nueva ventana", UiTheme.Variedad.SECONDARY);
        nuevaVentana.addActionListener(e -> navegacion.abrirNuevaVentanaSesion());

        JButton salir = UiTheme.boton("Cerrar sesión", UiTheme.Variedad.SECONDARY);
        salir.addActionListener(e -> cerrarSesion());

        derecha.add(usuario);
        derecha.add(nuevaVentana);
        derecha.add(salir);

        cabecera.add(izquierda, BorderLayout.WEST);
        cabecera.add(derecha, BorderLayout.EAST);
        return cabecera;
    }

    private JPanel menuLateral() {
        JPanel menu = new JPanel();
        menu.setBackground(UiTheme.PRIMARIO_OSCURO);
        menu.setPreferredSize(new Dimension(228, 700));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(24, 12, 16, 12));

        JLabel titulo = new JLabel("  MÓDULOS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titulo.setForeground(new Color(160, 190, 230));
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        menu.add(titulo);
        menu.add(javax.swing.Box.createVerticalStrut(14));
        return menu;
    }

    private void construirModulos() {
        // Se pasa un Supplier: el panel solo se construye si el rol tiene el permiso.
        // Así ningún panel ejecuta consultas para las que su usuario no está autorizado.
        anadirModulo(menu, "Control de Acceso", "control", "ver_control_acceso",
                () -> new ControlAccesoPanel(sesion));
        anadirModulo(menu, "Gestión de Personas", "personas", "ver_personas",
                () -> new PersonasPanel(sesion));
        anadirModulo(menu, "Gestión de Empresas", "empresas", "ver_empresas",
                () -> new EmpresasPanel(sesion));
        anadirModulo(menu, "Usuarios y Permisos", "usuarios", "ver_usuarios",
                () -> new UsuariosPanel(sesion));
        anadirModulo(menu, "Incidentes", "incidentes", "ver_incidentes",
                () -> new IncidentesPanel(sesion));
        anadirModulo(menu, "Reportes y Auditoría", "reportes", "ver_reportes",
                () -> new ReportesPanel(sesion));

        if (areaTrabajo.getComponentCount() == 0) {
            JPanel vacio = new JPanel();
            vacio.setBackground(UiTheme.FONDO);
            vacio.add(UiTheme.titulo("Su rol no tiene módulos asignados."));
            areaTrabajo.add(vacio, "vacio");
        }
    }

    private void anadirModulo(JPanel menu, String texto, String clave,
                              String permiso, Supplier<BasePanel> fabrica) {
        if (!sesion.tienePermiso(permiso)) {
            return;
        }
        BasePanel panel = fabrica.get();
        JButton boton = new JButton(texto);
        boton.setFocusable(false);
        boton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(30, 78, 145));
        boton.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 10));
        boton.setAlignmentX(LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(210, 46));
        boton.addActionListener(e -> {
            resaltar(boton);
            tarjetas.show(areaTrabajo, clave);
        });
        menu.add(boton);
        menu.add(javax.swing.Box.createVerticalStrut(6));
        areaTrabajo.add(panel, clave);

        if (botonActivo == null) {
            resaltar(boton);
        }
    }

    private void resaltar(JButton boton) {
        if (botonActivo != null) {
            botonActivo.setBackground(new Color(30, 78, 145));
        }
        botonActivo = boton;
        boton.setBackground(UiTheme.PRIMARIO);
    }

    private void cerrarSesion() {
        for (java.awt.Component componente : areaTrabajo.getComponents()) {
            if (componente instanceof BasePanel panel) {
                panel.dispose();
            }
        }
        dispose();
        if (Frame.getWindows().length == 0) {
            new LoginFrame().setVisible(true);
        }
    }
}
