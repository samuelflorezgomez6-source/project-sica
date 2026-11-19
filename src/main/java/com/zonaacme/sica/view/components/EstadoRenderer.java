package com.zonaacme.sica.view.components;

import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Font;

/** Renderer de celdas de estado: indicador visual con color corporativo. */
public class EstadoRenderer extends DefaultTableCellRenderer {

    public EstadoRenderer() {
        setHorizontalAlignment(CENTER);
        setOpaque(true);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
        setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
    }

    @Override
    protected void setValue(Object value) {
        String texto = value == null ? "—" : value.toString();
        setText(texto);
        Color color = colorPara(texto);
        setForeground(color);
        setBackground(new Color(
                Math.min(255, color.getRed() + 200),
                Math.min(255, color.getGreen() + 200),
                Math.min(255, color.getBlue() + 200)));
    }

    private Color colorPara(String estado) {
        if (estado.contains("Dentro")) return UiTheme.EXITO;
        if (estado.contains("Pendiente")) return UiTheme.ADVERTENCIA;
        if (estado.contains("Aprobado")) return UiTheme.INFORMACION;
        if (estado.contains("Rechazado") || estado.contains("Cerrada") || estado.contains("Expirado")) {
            return UiTheme.PELIGRO;
        }
        return UiTheme.TEXTO_SUAVE;
    }
}
