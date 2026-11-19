package com.zonaacme.sica.view.components;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.pattern.observer.TipoEventoVisita;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

/**
 * Tema visual centralizado: FlatLaf + estilos corporativos de Zona Acme.
 * Toda la estética se define aquí para que los paneles queden limpios y
 * la sustentación sea sencilla: "los botones se ven así porque están en UiTheme".
 */
public final class UiTheme {

    public static final Color PRIMARIO = new Color(37, 99, 178);
    public static final Color PRIMARIO_OSCURO = new Color(24, 68, 128);
    public static final Color FONDO = new Color(244, 247, 251);
    public static final Color TARJETA = Color.WHITE;
    public static final Color TEXTO = new Color(30, 41, 59);
    public static final Color TEXTO_SUAVE = new Color(100, 116, 139);
    public static final Color EXITO = new Color(22, 146, 92);
    public static final Color ADVERTENCIA = new Color(194, 130, 14);
    public static final Color PELIGRO = new Color(198, 55, 55);
    public static final Color INFORMACION = new Color(37, 99, 178);

    public static final Font FUENTE_BASE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 14);

    private UiTheme() {
    }

    public static void instalar(boolean oscuro) {
        try {
            if (oscuro) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
        } catch (Exception e) {
            System.err.println("[SICA] No se pudo instalar FlatLaf: " + e.getMessage());
        }
        UIManager.put("Button.arc", 12);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.showGrid", Boolean.FALSE);
        UIManager.put("ScrollPane.showButtons", Boolean.FALSE);
        UIManager.put("defaultFont", FUENTE_BASE);
    }

    public static JButton boton(String texto, Variedad variedad) {
        JButton b = new JButton(texto);
        b.setFocusable(false);
        b.setMargin(new Insets(8, 16, 8, 16));
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.putClientProperty("JButton.buttonType", "roundRect");
        switch (variedad) {
            case PRIMARIO -> {
                b.setBackground(PRIMARIO);
                b.setForeground(Color.WHITE);
            }
            case EXITO -> {
                b.setBackground(EXITO);
                b.setForeground(Color.WHITE);
            }
            case PELIGRO -> {
                b.setBackground(PELIGRO);
                b.setForeground(Color.WHITE);
            }
            case ADVERTENCIA -> {
                b.setBackground(ADVERTENCIA);
                b.setForeground(Color.WHITE);
            }
            case SECONDARY -> b.setBackground(new Color(232, 238, 246));
        }
        return b;
    }

    public enum Variedad { PRIMARIO, EXITO, PELIGRO, ADVERTENCIA, SECONDARY }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_TITULO);
        l.setForeground(TEXTO);
        return l;
    }

    public static JLabel seccion(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_SECCION);
        l.setForeground(PRIMARIO_OSCURO);
        return l;
    }

    public static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_BASE);
        l.setForeground(TEXTO);
        return l;
    }

    public static JTextField campo(String placeholder) {
        JTextField campo = new JTextField(18);
        campo.putClientProperty("JTextField.placeholderText", placeholder);
        campo.setFont(FUENTE_BASE);
        return campo;
    }

    public static JTable tabla() {
        JTable tabla = new JTable();
        tabla.setRowHeight(32);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(214, 228, 248));
        tabla.setSelectionForeground(TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);
        return tabla;
    }

    public static JScrollPane scroll(JComponent componente) {
        JScrollPane scroll = new JScrollPane(componente);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(TARJETA);
        return scroll;
    }

    public static JPanel tarjeta(String titulo, String valor, Color acento) {
        JPanel panel = new JPanel();
        panel.setBackground(TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 231, 240), 1, true),
                new EmptyBorder(16, 18, 16, 18)));
        panel.setLayout(new java.awt.BorderLayout(8, 6));

        JLabel nombre = new JLabel(titulo.toUpperCase());
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nombre.setForeground(TEXTO_SUAVE);
        JLabel numero = new JLabel(valor);
        numero.setFont(new Font("Segoe UI", Font.BOLD, 24));
        numero.setForeground(acento);
        panel.add(nombre, java.awt.BorderLayout.NORTH);
        panel.add(numero, java.awt.BorderLayout.CENTER);
        return panel;
    }

    public static JPanel panelBlanco(java.awt.LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(TARJETA);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 231, 240), 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return p;
    }

    public static Color colorEstadoPorEvento(TipoEventoVisita tipo) {
        return switch (tipo) {
            case NUEVA_SOLICITUD -> ADVERTENCIA;
            case APROBADA, CHECK_IN -> EXITO;
            case RECHAZADA, SALIDA_OLVIDADA -> PELIGRO;
            case CHECK_OUT -> TEXTO_SUAVE;
            default -> PRIMARIO;
        };
    }

    public static Color colorEstado(EstadoVisita estado) {
        if (estado == null) {
            return TEXTO_SUAVE;
        }
        return switch (estado) {
            case DENTRO -> EXITO;
            case PENDIENTE, PENDIENTE_OLVIDO -> ADVERTENCIA;
            case APROBADO -> INFORMACION;
            case RECHAZADO, EXPIRADO, CERRADA_SISTEMA -> PELIGRO;
            case FUERA -> TEXTO_SUAVE;
        };
    }
}
