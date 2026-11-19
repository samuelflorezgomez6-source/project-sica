package com.zonaacme.sica.view.panels;

import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Usuario;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.util.DateUtil;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.EstadoRenderer;
import com.zonaacme.sica.view.components.GenericTableModel;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Reportes: quién está dentro, historial de visitas y consulta de bitácora con filtros. */
public class ReportesPanel extends BasePanel {

    private static final DateTimeFormatter FORMATO_FILTRO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final String[] COLUMNAS_VISITA = {
            "ID", "Persona", "Documento", "Empresa", "Estado", "Entrada", "Salida"
    };

    private final GenericTableModel<Visita> modeloDentro = modeloVisitas();
    private final GenericTableModel<Visita> modeloHistorial = modeloVisitas();
    private final JTextField filtroHistorial = UiTheme.campo("Filtrar por persona, documento, empresa o estado…");

    private final GenericTableModel<BitacoraAuditoria> modeloBitacora = new GenericTableModel<>(
            new String[]{"ID", "Fecha", "Usuario", "Acción", "Tabla", "Registro", "Detalles"},
            b -> "#" + b.getId(),
            b -> DateUtil.formatear(b.getFechaHora()),
            b -> b.getNombreUsuario() == null ? "Sistema / invitado" : b.getNombreUsuario(),
            BitacoraAuditoria::getAccionRealizada,
            b -> b.getTablaAfectada() == null ? "—" : b.getTablaAfectada(),
            b -> b.getRegistroIdAfectado() == null ? "—" : "#" + b.getRegistroIdAfectado(),
            BitacoraAuditoria::getDetalles);

    private final JComboBox<String> comboAccion = new JComboBox<>();
    private final JComboBox<Usuario> comboUsuario = new JComboBox<>();
    private final JTextField desde = UiTheme.campo("yyyy-MM-dd HH:mm");
    private final JTextField hasta = UiTheme.campo("yyyy-MM-dd HH:mm");

    public ReportesPanel(Sesion sesion) {
        super(sesion);
        add(pestanas(), BorderLayout.CENTER);
        add(barraEstado(), BorderLayout.SOUTH);
        recargar();
    }

    private JTabbedPane pestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(UiTheme.FUENTE_BASE);

        JPanel dentro = new JPanel(new BorderLayout());
        JLabel tituloDentro = UiTheme.seccion("  Personas dentro del complejo ahora mismo");
        tituloDentro.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 12, 8, 12));
        dentro.add(tituloDentro, BorderLayout.NORTH);
        dentro.add(new JScrollPane(tabla(modeloDentro)), BorderLayout.CENTER);
        pestanas.addTab("Who is in", dentro);

        JPanel historial = new JPanel(new BorderLayout());
        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filtro.setOpaque(false);
        filtro.add(filtroHistorial);
        JButton buscar = UiTheme.boton("Buscar", UiTheme.Variedad.PRIMARIO);
        buscar.addActionListener(e -> recargar());
        filtro.add(buscar);
        historial.add(filtro, BorderLayout.NORTH);
        historial.add(new JScrollPane(tabla(modeloHistorial)), BorderLayout.CENTER);
        pestanas.addTab("Historial de visitas", historial);

        // La bitácora solo se muestra a quienes tienen el permiso explícito.
        if (puede("consultar_bitacora")) {
            JPanel bitacora = new JPanel(new BorderLayout());
            bitacora.add(filtrosBitacora(), BorderLayout.NORTH);
            bitacora.add(new JScrollPane(tablaBitacora()), BorderLayout.CENTER);
            pestanas.addTab("Bitácora de auditoría", bitacora);
        }
        return pestanas;
    }

    private JPanel filtrosBitacora() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(14, 14, 8, 14));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fila.setOpaque(false);
        fila.add(UiTheme.etiqueta("Acción:"));
        comboAccion.addItem("");
        Arrays.stream(AccionAuditoria.values()).map(AccionAuditoria::getCodigo)
                .forEach(comboAccion::addItem);
        comboAccion.setPreferredSize(new Dimension(210, 30));
        fila.add(comboAccion);

        fila.add(UiTheme.etiqueta("Usuario:"));
        comboUsuario.addItem(null);
        comboUsuario.setRenderer((list, value, index, selected, focus) -> new JLabel(
                value == null ? "Todos" : value.getNombre()));
        comboUsuario.setPreferredSize(new Dimension(210, 30));
        fila.add(comboUsuario);

        JButton buscar = UiTheme.boton("Aplicar filtros", UiTheme.Variedad.PRIMARIO);
        buscar.addActionListener(e -> cargarBitacora());
        fila.add(buscar);
        panel.add(fila);

        JPanel fechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fechas.setOpaque(false);
        fechas.add(UiTheme.etiqueta("Desde:"));
        desde.setPreferredSize(new Dimension(170, 30));
        fechas.add(desde);
        fechas.add(UiTheme.etiqueta("Hasta:"));
        hasta.setPreferredSize(new Dimension(170, 30));
        fechas.add(hasta);
        fechas.add(UiTheme.etiqueta("(formato: yyyy-MM-dd HH:mm)"));
        panel.add(fechas);
        return panel;
    }

    private static GenericTableModel<Visita> modeloVisitas() {
        return new GenericTableModel<>(COLUMNAS_VISITA,
                v -> "#" + v.getId(),
                Visita::getNombrePersona,
                Visita::getDocumentoPersona,
                Visita::getNombreEmpresa,
                Visita::getNombreEstado,
                v -> DateUtil.formatear(v.getFechaEntrada()),
                v -> DateUtil.formatear(v.getFechaSalida()));
    }

    private JTable tabla(GenericTableModel<Visita> modelo) {
        JTable tabla = UiTheme.tabla();
        tabla.setModel(modelo);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer());
        return tabla;
    }

    private JTable tablaBitacora() {
        JTable tabla = UiTheme.tabla();
        tabla.setModel(modeloBitacora);
        return tabla;
    }

    private void cargarBitacora() {
        if (!puede("consultar_bitacora")) {
            return;
        }
        String accion = (String) comboAccion.getSelectedItem();
        Usuario usuario = (Usuario) comboUsuario.getSelectedItem();
        Integer usuarioId = usuario == null ? null : usuario.getId();

        LocalDateTime fechaDesde;
        LocalDateTime fechaHasta;
        try {
            fechaDesde = desde.getText().isBlank() ? null : LocalDateTime.parse(desde.getText().trim(), FORMATO_FILTRO);
            fechaHasta = hasta.getText().isBlank() ? null : LocalDateTime.parse(hasta.getText().trim(), FORMATO_FILTRO);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "El formato de fecha debe ser yyyy-MM-dd HH:mm (por ejemplo 2026-11-19 08:30).",
                    "Filtro inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final List<BitacoraAuditoria>[] resultado = new List[]{new ArrayList<>()};
        SicaController.Resultado r = controller.ejecutar(() ->
                resultado[0] = controller.reportes().bitacoraFiltrada(
                        sesion, accion, usuarioId, fechaDesde, fechaHasta));
        avisar(r, this);
        modeloBitacora.setDatos(resultado[0]);
    }

    @Override
    protected void recargar() {
        modeloDentro.setDatos(controller.reportes().personasDentro(sesion));
        modeloHistorial.setDatos(controller.reportes().historialFiltrado(sesion, filtroHistorial.getText()));

        if (puede("consultar_bitacora")) {
            Usuario seleccionActual = (Usuario) comboUsuario.getSelectedItem();
            Integer idActual = seleccionActual == null ? null : seleccionActual.getId();
            comboUsuario.removeAllItems();
            comboUsuario.addItem(null);
            controller.usuarios().listar(sesion).forEach(comboUsuario::addItem);
            if (idActual != null) {
                for (int i = 0; i < comboUsuario.getItemCount(); i++) {
                    Usuario u = comboUsuario.getItemAt(i);
                    if (u != null && u.getId().equals(idActual)) {
                        comboUsuario.setSelectedIndex(i);
                        break;
                    }
                }
            }
            cargarBitacora();
        }
    }
}
