package com.zonaacme.sica.view.panels;

import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Empresa;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.Formularios;
import com.zonaacme.sica.view.components.GenericTableModel;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Gestión de empresas del complejo Zona Acme. */
public class EmpresasPanel extends BasePanel {

    private static final String[] COLUMNAS = {"ID", "Empresa", "Contacto principal"};

    private final GenericTableModel<Empresa> modelo = new GenericTableModel<>(COLUMNAS,
            e -> "#" + e.getId(),
            Empresa::getNombre,
            e -> e.getContactoPrincipal() == null ? "—" : e.getContactoPrincipal());
    private final JTable tabla = tabla();

    public EmpresasPanel(Sesion sesion) {
        super(sesion);
        add(superior(), BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(barraEstado(), BorderLayout.SOUTH);
        recargar();
    }

    private JPanel superior() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setOpaque(false);

        JPanel titulo = new JPanel(new BorderLayout(10, 0));
        titulo.setOpaque(false);
        titulo.add(UiTheme.titulo("Gestión de Empresas"), BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.setOpaque(false);
        if (puede("crear_empresa")) {
            JButton nuevo = UiTheme.boton("＋ Nueva empresa", UiTheme.Variedad.PRIMARIO);
            nuevo.addActionListener(e -> nuevo());
            acciones.add(nuevo);
        }
        if (puede("editar_empresa")) {
            JButton editar = UiTheme.boton("Editar", UiTheme.Variedad.SECONDARY);
            editar.addActionListener(e -> editar());
            acciones.add(editar);
        }
        if (puede("eliminar_empresa")) {
            JButton eliminar = UiTheme.boton("Eliminar", UiTheme.Variedad.PELIGRO);
            eliminar.addActionListener(e -> eliminar());
            acciones.add(eliminar);
        }
        titulo.add(acciones, BorderLayout.EAST);
        superior.add(titulo);
        return superior;
    }

    private JTable tabla() {
        JTable t = UiTheme.tabla();
        t.setModel(modelo);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    private Empresa seleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una empresa de la tabla.",
                    "SICA", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.getFila(tabla.convertRowIndexToModel(fila));
    }

    private void nuevo() {
        Empresa empresa = Formularios.pedirEmpresa(this, null);
        if (empresa == null) {
            return;
        }
        final int[] id = {0};
        SicaController.Resultado r = controller.ejecutar(() ->
                id[0] = controller.empresas().crear(sesion, empresa));
        if (avisar(r, this)) {
            empresa.setId(id[0]);
            recargar();
        }
    }

    private void editar() {
        Empresa seleccion = seleccionada();
        if (seleccion == null) {
            return;
        }
        Empresa empresa = Formularios.pedirEmpresa(this, seleccion);
        if (empresa == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.empresas().actualizar(sesion, empresa));
        avisar(r, this);
        recargar();
    }

    private void eliminar() {
        Empresa empresa = seleccionada();
        if (empresa == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la empresa " + empresa.getNombre() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.empresas().eliminar(sesion, empresa.getId()));
        avisar(r, this);
        recargar();
    }

    @Override
    protected void recargar() {
        modelo.setDatos(controller.empresas().listar(sesion));
    }
}
