package com.zonaacme.sica.view.panels;

import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Incidente;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.util.DateUtil;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.GenericTableModel;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/** Consulta y registro de incidentes de seguridad asociados a una visita. */
public class IncidentesPanel extends BasePanel {

    private static final String[] COLUMNAS = {
            "ID", "Fecha", "Visita", "Reportado por", "Descripción"
    };

    private final GenericTableModel<Incidente> modelo = new GenericTableModel<>(COLUMNAS,
            i -> "#" + i.getId(),
            i -> DateUtil.formatear(i.getFecha()),
            i -> i.getResumenVisita() == null ? "#" + i.getVisitaId() : i.getResumenVisita(),
            i -> i.getNombreReportador() == null ? "-" : i.getNombreReportador(),
            Incidente::getDescripcion);
    private final JTable tabla = UiTheme.tabla();

    public IncidentesPanel(Sesion sesion) {
        super(sesion);
        add(superior(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(barraEstado(), BorderLayout.SOUTH);
        tabla.setModel(modelo);
        recargar();
    }

    private JPanel superior() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setOpaque(false);

        JPanel titulo = new JPanel(new BorderLayout(10, 0));
        titulo.setOpaque(false);
        titulo.add(UiTheme.titulo("Gestión de Incidentes"), BorderLayout.WEST);
        if (puede("registrar_incidente")) {
            JButton nuevo = UiTheme.boton("+ Registrar incidente", UiTheme.Variedad.PELIGRO);
            nuevo.addActionListener(e -> registrar());
            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            acciones.setOpaque(false);
            acciones.add(nuevo);
            titulo.add(acciones, BorderLayout.EAST);
        }
        superior.add(titulo);
        return superior;
    }

    private void registrar() {
        List<Visita> visitas = controller.reportes().historialFiltrado(sesion, "");
        if (visitas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay visitas registradas a las que asociar un incidente.",
                    "SICA", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JComboBox<Visita> combo = new JComboBox<>(visitas.toArray(new Visita[0]));
        combo.setRenderer((list, value, index, selected, focus) -> new JLabel(
                value == null ? "-" : "Visita #" + value.getId() + " - " + value.getNombrePersona()));
        JTextField descripcion = new JTextField(32);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(UiTheme.etiqueta("Visita asociada"));
        form.add(combo);
        form.add(UiTheme.etiqueta("Descripción del incidente"));
        form.add(descripcion);

        int respuesta = JOptionPane.showConfirmDialog(this, form, "Registrar incidente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        Visita visita = (Visita) combo.getSelectedItem();
        Incidente incidente = new Incidente();
        incidente.setVisitaId(visita.getId());
        incidente.setDescripcion(descripcion.getText().trim());

        final int[] id = {0};
        SicaController.Resultado r = controller.ejecutar(() ->
                id[0] = controller.incidentes().registrar(sesion, incidente));
        avisar(r, this);
        recargar();
    }

    @Override
    protected void recargar() {
        modelo.setDatos(controller.incidentes().listar(sesion));
    }
}
