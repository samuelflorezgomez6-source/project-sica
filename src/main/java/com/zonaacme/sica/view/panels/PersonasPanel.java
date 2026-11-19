package com.zonaacme.sica.view.panels;

import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Empresa;
import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.EstadoAcceso;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.EstadoRenderer;
import com.zonaacme.sica.view.components.Formularios;
import com.zonaacme.sica.view.components.GenericTableModel;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Gestión de personas: alta, edición, baja y activación/bloqueo de acceso.
 * El bloqueo se refleja de inmediato en las reglas Strategy del control de acceso.
 */
public class PersonasPanel extends BasePanel {

    private static final String[] COLUMNAS = {
            "ID", "Nombre", "Documento", "Empresa", "Tipo", "Estado de acceso"
    };

    private final JTextField filtro = UiTheme.campo("Filtrar por nombre, documento o empresa…");
    private final GenericTableModel<Persona> modelo = new GenericTableModel<>(COLUMNAS,
            p -> "#" + p.getId(),
            Persona::getNombre,
            Persona::getDocumentoIdentidad,
            p -> p.getNombreEmpresa() == null ? "—" : p.getNombreEmpresa(),
            p -> p.getTipoPersona() == null ? "—" : p.getTipoPersona().getEtiqueta(),
            p -> p.getNombreEstado() == null ? "—" : p.getNombreEstado());
    private final JTable tabla = tabla();

    public PersonasPanel(Sesion sesion) {
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
        titulo.add(UiTheme.titulo("Gestión de Personas"), BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.setOpaque(false);
        if (puede("crear_persona")) {
            JButton nuevo = UiTheme.boton("＋ Nueva persona", UiTheme.Variedad.PRIMARIO);
            nuevo.addActionListener(e -> nuevo());
            acciones.add(nuevo);
        }
        if (puede("editar_persona")) {
            JButton editar = UiTheme.boton("Editar", UiTheme.Variedad.SECONDARY);
            editar.addActionListener(e -> editar());
            acciones.add(editar);
        }
        if (puede("eliminar_persona")) {
            JButton eliminar = UiTheme.boton("Eliminar", UiTheme.Variedad.PELIGRO);
            eliminar.addActionListener(e -> eliminar());
            acciones.add(eliminar);
        }
        if (puede("bloquear_persona")) {
            JButton bloquear = UiTheme.boton("Bloquear acceso", UiTheme.Variedad.ADVERTENCIA);
            bloquear.addActionListener(e -> cambiarAcceso(EstadoAcceso.PROHIBIDO));
            JButton activar = UiTheme.boton("Activar acceso", UiTheme.Variedad.EXITO);
            activar.addActionListener(e -> cambiarAcceso(EstadoAcceso.ACTIVO));
            acciones.add(bloquear);
            acciones.add(activar);
        }
        titulo.add(acciones, BorderLayout.EAST);
        superior.add(titulo);

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buscador.setOpaque(false);
        filtro.setPreferredSize(new Dimension(360, 36));
        JButton buscar = UiTheme.boton("Buscar", UiTheme.Variedad.PRIMARIO);
        buscar.addActionListener(e -> recargar());
        buscador.add(filtro);
        buscador.add(buscar);
        superior.add(buscador);
        return superior;
    }

    private JTable tabla() {
        JTable t = UiTheme.tabla();
        t.setModel(modelo);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getColumnModel().getColumn(5).setCellRenderer(new EstadoRenderer());
        return t;
    }

    private Persona seleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una persona de la tabla.",
                    "SICA", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.getFila(tabla.convertRowIndexToModel(fila));
    }

    private List<Empresa> empresas() {
        return controller.empresas().listar(sesion);
    }

    private void nuevo() {
        Persona persona = Formularios.pedirPersona(this, null, empresas());
        if (persona == null) {
            return;
        }
        final int[] id = {0};
        SicaController.Resultado r = controller.ejecutar(() ->
                id[0] = controller.personas().crear(sesion, persona));
        if (avisar(r, this)) {
            persona.setId(id[0]);
            recargar();
        }
    }

    private void editar() {
        Persona seleccion = seleccionada();
        if (seleccion == null) {
            return;
        }
        Persona persona = Formularios.pedirPersona(this, seleccion, empresas());
        if (persona == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.personas().actualizar(sesion, persona));
        avisar(r, this);
        recargar();
    }

    private void eliminar() {
        Persona persona = seleccionada();
        if (persona == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + persona.getNombre() + "? Esta acción quedará registrada en la bitácora.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.personas().eliminar(sesion, persona.getId()));
        avisar(r, this);
        recargar();
    }

    private void cambiarAcceso(EstadoAcceso estado) {
        Persona persona = seleccionada();
        if (persona == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.personas().cambiarEstadoAcceso(sesion, persona.getId(), estado));
        avisar(r, this);
        recargar();
    }

    @Override
    protected void recargar() {
        modelo.setDatos(controller.personas().buscar(sesion, filtro.getText()));
    }
}
