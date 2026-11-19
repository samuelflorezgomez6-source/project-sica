package com.zonaacme.sica.view.panels;

import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Permiso;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Usuario;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.Formularios;
import com.zonaacme.sica.view.components.GenericTableModel;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Gestión de usuarios y asignación de permisos a roles (RBAC dinámico). */
public class UsuariosPanel extends BasePanel {

    private static final String[] COLUMNAS = {
            "ID", "Nombre", "Email", "Rol", "Estado"
    };

    private final GenericTableModel<Usuario> modelo = new GenericTableModel<>(COLUMNAS,
            u -> "#" + u.getId(),
            Usuario::getNombre,
            Usuario::getEmail,
            Usuario::getNombreRol,
            u -> u.isActivo() ? "Activo" : "Inactivo");
    private final JTable tabla = tabla();

    private final JComboBox<Rol> comboRoles = new JComboBox<>();
    private final DefaultTableModel modeloPermisos = new DefaultTableModel(
            new String[]{"Asignado", "Permiso", "Descripción"}, 0) {
        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == 0 ? Boolean.class : String.class;
        }
    };
    private final JTable tablaPermisos = new JTable(modeloPermisos);
    private List<Permiso> catalogoPermisos = new ArrayList<>();

    public UsuariosPanel(Sesion sesion) {
        super(sesion);
        add(pestanas(), BorderLayout.CENTER);
        add(barraEstado(), BorderLayout.SOUTH);
        recargar();
    }

    private JTabbedPane pestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(UiTheme.FUENTE_BASE);

        JPanel usuarios = new JPanel(new BorderLayout());
        usuarios.add(superiorUsuarios(), BorderLayout.NORTH);
        usuarios.add(new JScrollPane(tabla), BorderLayout.CENTER);
        pestanas.addTab("Usuarios", usuarios);

        JPanel roles = new JPanel(new BorderLayout(10, 10));
        roles.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 12, 12));
        roles.add(superiorRoles(), BorderLayout.NORTH);
        UiTheme.tabla().setRowHeight(28);
        tablaPermisos.setRowHeight(28);
        tablaPermisos.setFont(UiTheme.FUENTE_BASE);
        tablaPermisos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tablaPermisos.getColumnModel().getColumn(0).setCellRenderer(centro);
        tablaPermisos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaPermisos.getColumnModel().getColumn(1).setPreferredWidth(220);
        roles.add(new JScrollPane(tablaPermisos), BorderLayout.CENTER);
        pestanas.addTab("Roles y Permisos", roles);
        return pestanas;
    }

    private JPanel superiorUsuarios() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setOpaque(false);
        superior.setBorder(javax.swing.BorderFactory.createEmptyBorder(18, 18, 10, 18));

        JPanel titulo = new JPanel(new BorderLayout(10, 0));
        titulo.setOpaque(false);
        titulo.add(UiTheme.titulo("Usuarios y Permisos"), BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.setOpaque(false);
        if (puede("crear_usuario")) {
            JButton nuevo = UiTheme.boton("+ Nuevo usuario", UiTheme.Variedad.PRIMARIO);
            nuevo.addActionListener(e -> nuevo());
            acciones.add(nuevo);
        }
        if (puede("editar_usuario")) {
            JButton editar = UiTheme.boton("Editar", UiTheme.Variedad.SECONDARY);
            editar.addActionListener(e -> editar());
            JButton clave = UiTheme.boton("Cambiar contraseña", UiTheme.Variedad.SECONDARY);
            clave.addActionListener(e -> cambiarClave());
            acciones.add(editar);
            acciones.add(clave);
        }
        if (puede("eliminar_usuario")) {
            JButton eliminar = UiTheme.boton("Eliminar", UiTheme.Variedad.PELIGRO);
            eliminar.addActionListener(e -> eliminar());
            acciones.add(eliminar);
        }
        titulo.add(acciones, BorderLayout.EAST);
        superior.add(titulo);
        return superior;
    }

    private JPanel superiorRoles() {
        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        superior.setOpaque(false);
        superior.add(UiTheme.seccion("Rol:"));
        comboRoles.setPreferredSize(new Dimension(240, 32));
        comboRoles.addActionListener(e -> cargarPermisosDelRol());
        superior.add(comboRoles);

        if (puede("gestionar_roles_permisos")) {
            JButton guardar = UiTheme.boton("Guardar permisos", UiTheme.Variedad.PRIMARIO);
            guardar.addActionListener(e -> guardarPermisos());
            superior.add(guardar);
        } else {
            superior.add(UiTheme.etiqueta("(solo lectura)"));
        }
        return superior;
    }

    private JTable tabla() {
        JTable t = UiTheme.tabla();
        t.setModel(modelo);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }

    private Usuario seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.",
                    "SICA", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.getFila(tabla.convertRowIndexToModel(fila));
    }

    private List<Rol> roles() {
        return controller.permisos().listarRoles();
    }

    private void nuevo() {
        Usuario usuario = Formularios.pedirUsuario(this, null, roles());
        if (usuario == null) {
            return;
        }
        String clave = JOptionPane.showInputDialog(this, "Contraseña inicial:",
                "Nuevo usuario", JOptionPane.QUESTION_MESSAGE);
        if (clave == null) {
            return;
        }
        final int[] id = {0};
        SicaController.Resultado r = controller.ejecutar(() ->
                id[0] = controller.usuarios().crear(sesion, usuario, clave));
        if (avisar(r, this)) {
            usuario.setId(id[0]);
            recargar();
        }
    }

    private void editar() {
        Usuario seleccion = seleccionado();
        if (seleccion == null) {
            return;
        }
        Usuario usuario = Formularios.pedirUsuario(this, seleccion, roles());
        if (usuario == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.usuarios().actualizar(sesion, usuario, null));
        avisar(r, this);
        recargar();
    }

    private void cambiarClave() {
        Usuario usuario = seleccionado();
        if (usuario == null) {
            return;
        }
        String clave = Formularios.passwordNuevo(this, usuario);
        if (clave == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.usuarios().actualizar(sesion, usuario, clave));
        avisar(r, this);
    }

    private void eliminar() {
        Usuario usuario = seleccionado();
        if (usuario == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al usuario " + usuario.getEmail() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.usuarios().eliminar(sesion, usuario.getId()));
        avisar(r, this);
        recargar();
    }

    private void cargarPermisosDelRol() {
        Rol rol = (Rol) comboRoles.getSelectedItem();
        if (rol == null) {
            return;
        }
        Set<String> asignados = controller.permisos().permisosDelRol(rol.getId()).stream()
                .map(Permiso::getNombrePermiso)
                .collect(Collectors.toSet());
        modeloPermisos.setRowCount(0);
        catalogoPermisos.forEach(p -> modeloPermisos.addRow(new Object[]{
                asignados.contains(p.getNombrePermiso()),
                p.getNombrePermiso(),
                p.getDescripcion() == null ? "" : p.getDescripcion()
        }));
    }

    private void guardarPermisos() {
        Rol rol = (Rol) comboRoles.getSelectedItem();
        if (rol == null) {
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < modeloPermisos.getRowCount(); i++) {
            if (Boolean.TRUE.equals(modeloPermisos.getValueAt(i, 0))) {
                ids.add(catalogoPermisos.get(i).getId());
            }
        }
        int rolId = rol.getId();
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.permisos().reemplazarPermisos(sesion, rolId, ids));
        avisar(r, this);
    }

    @Override
    protected void recargar() {
        modelo.setDatos(controller.usuarios().listar(sesion));

        Rol seleccionActual = (Rol) comboRoles.getSelectedItem();
        Integer idActual = seleccionActual == null ? null : seleccionActual.getId();
        comboRoles.removeAllItems();
        roles().forEach(comboRoles::addItem);
        if (idActual != null) {
            for (int i = 0; i < comboRoles.getItemCount(); i++) {
                Rol rol = comboRoles.getItemAt(i);
                if (rol != null && rol.getId().equals(idActual)) {
                    comboRoles.setSelectedIndex(i);
                    break;
                }
            }
        }
        catalogoPermisos = controller.permisos().listarPermisos();
        cargarPermisosDelRol();
    }
}
