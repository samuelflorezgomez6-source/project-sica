package com.zonaacme.sica.view.panels;

import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Empresa;
import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;
import com.zonaacme.sica.pattern.strategy.SolicitudIngreso;
import com.zonaacme.sica.util.DateUtil;
import com.zonaacme.sica.view.components.BasePanel;
import com.zonaacme.sica.view.components.EstadoRenderer;
import com.zonaacme.sica.view.components.Formularios;
import com.zonaacme.sica.view.components.GenericTableModel;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de control de acceso: búsqueda de personas, registro de los tres
 * flujos de entrada, bandeja de aprobaciones y check-in/check-out.
 * Se actualiza solo mediante Observer + polling (heredados de BasePanel).
 */
public class ControlAccesoPanel extends BasePanel {

    private static final String[] COLUMNAS = {
            "ID", "Persona", "Documento", "Empresa", "Estado", "Entrada", "Salida", "Placa"
    };

    private final JTextField busqueda = UiTheme.campo("Número de documento, nombre o empresa...");
    private final JLabel foto = new JLabel();
    private final JLabel datosPersona = new JLabel("Busque una persona para ver sus datos");
    private Persona personaSeleccionada;

    private final GenericTableModel<Visita> modeloPendientes = modeloVisitas();
    private final GenericTableModel<Visita> modeloCheckIn = modeloVisitas();
    private final GenericTableModel<Visita> modeloDentro = modeloVisitas();
    private final GenericTableModel<Visita> modeloHistorial = modeloVisitas();

    private final JTable tablaPendientes = tabla(modeloPendientes);
    private final JTable tablaCheckIn = tabla(modeloCheckIn);
    private final JTable tablaDentro = tabla(modeloDentro);
    private final JTable tablaHistorial = tabla(modeloHistorial);

    public ControlAccesoPanel(Sesion sesion) {
        super(sesion);
        add(superior(), BorderLayout.NORTH);
        add(pestanas(), BorderLayout.CENTER);
        add(barraEstado(), BorderLayout.SOUTH);
        recargar();
    }

    private JPanel superior() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setOpaque(false);

        JPanel titulo = new JPanel(new BorderLayout(10, 0));
        titulo.setOpaque(false);
        JLabel et = UiTheme.titulo("Control de Acceso");
        et.setText("Control de Acceso  -  Zona Acme");
        titulo.add(et, BorderLayout.WEST);

        JPanel flujos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        flujos.setOpaque(false);
        if (puede("crear_persona")) {
            JButton pre = UiTheme.boton("Pre-registrar invitado", UiTheme.Variedad.PRIMARIO);
            pre.addActionListener(e -> preRegistrar());
            flujos.add(pre);
        }
        if (puede("registrar_visita")) {
            JButton noAnunciado = UiTheme.boton("Registrar no anunciado", UiTheme.Variedad.ADVERTENCIA);
            noAnunciado.addActionListener(e -> registrar(TipoFlujoVisita.NO_ANUNCIADA));
            flujos.add(noAnunciado);

            JButton olvido = UiTheme.boton("Pase por carnet olvidado", UiTheme.Variedad.SECONDARY);
            olvido.addActionListener(e -> registrar(TipoFlujoVisita.CARNET_OLVIDADO));
            flujos.add(olvido);
        }
        titulo.add(flujos, BorderLayout.EAST);
        superior.add(titulo);

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        buscador.setOpaque(false);
        busqueda.setPreferredSize(new Dimension(380, 36));
        JButton buscar = UiTheme.boton("Buscar", UiTheme.Variedad.PRIMARIO);
        buscar.addActionListener(e -> buscarPersona());
        buscador.add(busqueda);
        buscador.add(buscar);
        if (puede("crear_persona")) {
            JButton nueva = UiTheme.boton("Nueva persona", UiTheme.Variedad.SECONDARY);
            nueva.addActionListener(e -> nuevaPersona());
            buscador.add(nueva);
        }
        superior.add(buscador);

        JPanel tarjeta = UiTheme.panelBlanco(new BorderLayout(16, 8));
        foto.setPreferredSize(new Dimension(96, 96));
        foto.setBorder(BorderFactory.createLineBorder(new Color(224, 231, 240), 1, true));
        tarjeta.add(foto, BorderLayout.WEST);
        datosPersona.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tarjeta.add(datosPersona, BorderLayout.CENTER);
        Formularios.cargarFoto(foto, null, "?");
        superior.add(tarjeta);
        return superior;
    }

    private JTabbedPane pestanas() {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(UiTheme.FUENTE_BASE);

        JPanel pendientes = new JPanel(new BorderLayout());
        pendientes.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);
        pendientes.add(botonesAprobacion(), BorderLayout.SOUTH);
        pestanas.addTab("Solicitudes pendientes", pendientes);

        JPanel listas = new JPanel(new BorderLayout());
        listas.add(new JScrollPane(tablaCheckIn), BorderLayout.CENTER);
        JPanel surCheck = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        surCheck.setOpaque(false);
        JButton checkIn = UiTheme.boton("Registrar check-in", UiTheme.Variedad.EXITO);
        checkIn.setEnabled(puede("check_in"));
        checkIn.addActionListener(e -> hacerCheckIn());
        surCheck.add(checkIn);
        listas.add(surCheck, BorderLayout.SOUTH);
        pestanas.addTab("Aprobadas - pendientes de ingreso", listas);

        JPanel dentro = new JPanel(new BorderLayout());
        dentro.add(new JScrollPane(tablaDentro), BorderLayout.CENTER);
        JPanel surDentro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        surDentro.setOpaque(false);
        JButton checkOut = UiTheme.boton("Registrar check-out", UiTheme.Variedad.PRIMARIO);
        checkOut.setEnabled(puede("check_out"));
        checkOut.addActionListener(e -> hacerCheckOut());
        surDentro.add(checkOut);
        if (puede("registrar_incidente")) {
            JButton incidente = UiTheme.boton("Registrar incidente", UiTheme.Variedad.PELIGRO);
            incidente.addActionListener(e -> registrarIncidente());
            surDentro.add(incidente);
        }
        dentro.add(surDentro, BorderLayout.SOUTH);
        pestanas.addTab("Dentro ahora", dentro);

        JPanel historial = new JPanel(new BorderLayout());
        historial.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
        pestanas.addTab("Historial reciente", historial);
        return pestanas;
    }

    private JPanel botonesAprobacion() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        JButton aprobar = UiTheme.boton("Aprobar", UiTheme.Variedad.EXITO);
        aprobar.setEnabled(puede("aprobar_visita"));
        aprobar.addActionListener(e -> cambiarEstado(false));
        JButton rechazar = UiTheme.boton("Rechazar", UiTheme.Variedad.PELIGRO);
        rechazar.setEnabled(puede("rechazar_visita"));
        rechazar.addActionListener(e -> cambiarEstado(true));
        botones.add(aprobar);
        botones.add(rechazar);
        return botones;
    }

    private static GenericTableModel<Visita> modeloVisitas() {
        return new GenericTableModel<>(COLUMNAS,
                v -> "#" + v.getId(),
                Visita::getNombrePersona,
                Visita::getDocumentoPersona,
                Visita::getNombreEmpresa,
                Visita::getNombreEstado,
                v -> DateUtil.formatear(v.getFechaEntrada()),
                v -> DateUtil.formatear(v.getFechaSalida()),
                v -> v.getVehiculoPlaca() == null ? "-" : v.getVehiculoPlaca());
    }

    private static JTable tabla(GenericTableModel<Visita> modelo) {
        JTable tabla = UiTheme.tabla();
        tabla.setModel(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(4).setCellRenderer(new EstadoRenderer());
        return tabla;
    }

    // ---------- Acciones de la vista (delegan siempre al controlador) ----------

    private void buscarPersona() {
        final List<Persona>[] resultado = new List[]{new ArrayList<>()};
        SicaController.Resultado r = controller.ejecutar(() ->
                resultado[0] = controller.personas().buscar(sesion, busqueda.getText()));
        if (!avisar(r, this)) {
            return;
        }
        if (resultado[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna persona con ese criterio.",
                    "SICA", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        personaSeleccionada = resultado[0].size() == 1
                ? resultado[0].get(0)
                : (Persona) JOptionPane.showInputDialog(this, "Seleccione una persona:",
                        "SICA", JOptionPane.QUESTION_MESSAGE, null,
                        resultado[0].toArray(), resultado[0].get(0));
        if (personaSeleccionada != null) {
            mostrarPersona();
        }
    }

    private void mostrarPersona() {
        String html = "<html><b>" + personaSeleccionada.getNombre() + "</b><br>"
                + "Documento: " + personaSeleccionada.getDocumentoIdentidad()
                + " &nbsp;-&nbsp; Tipo: " + personaSeleccionada.getTipoPersona().getEtiqueta()
                + "<br>Empresa: " + (personaSeleccionada.getNombreEmpresa() == null
                    ? "-" : personaSeleccionada.getNombreEmpresa())
                + " &nbsp;-&nbsp; Acceso: " + (personaSeleccionada.getNombreEstado() == null
                    ? "-" : personaSeleccionada.getNombreEstado()) + "</html>";
        datosPersona.setText(html);
        String iniciales = personaSeleccionada.getNombre().substring(0, 1).toUpperCase();
        Formularios.cargarFoto(foto, personaSeleccionada.getUrlFoto(), iniciales);
    }

    private void nuevaPersona() {
        List<Empresa> empresas = controller.empresas().listar(sesion);
        Persona persona = Formularios.pedirPersona(this, null, empresas);
        if (persona == null) {
            return;
        }
        final int[] id = {0};
        SicaController.Resultado r = controller.ejecutar(() -> id[0] = controller.personas().crear(sesion, persona));
        if (avisar(r, this)) {
            persona.setId(id[0]);
            personaSeleccionada = persona;
            mostrarPersona();
        }
    }

    private void preRegistrar() {
        if (personaSeleccionada == null) {
            Persona persona = Formularios.pedirPersona(this, null, controller.empresas().listar(sesion));
            if (persona == null) {
                return;
            }
            final int[] id = {0};
            SicaController.Resultado creado = controller.ejecutar(() ->
                    id[0] = controller.personas().crear(sesion, persona));
            if (!avisar(creado, this)) {
                return;
            }
            persona.setId(id[0]);
            personaSeleccionada = persona;
            mostrarPersona();
        }
        registrar(TipoFlujoVisita.PRE_REGISTRADA);
    }

    private void registrar(TipoFlujoVisita flujo) {
        if (personaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Primero busque o registre una persona.",
                    "SICA", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String placa = JOptionPane.showInputDialog(this, "Placa del vehículo (opcional):",
                "SICA", JOptionPane.QUESTION_MESSAGE);
        Integer aprobador = flujo == TipoFlujoVisita.PRE_REGISTRADA
                ? sesion.getUsuario().getId() : null;
        SolicitudIngreso solicitud = new SolicitudIngreso(
                personaSeleccionada, flujo, placa, aprobador, null);

        SicaController.Resultado r = controller.ejecutar(() ->
                controller.visitas().registrarIngreso(sesion, solicitud));
        avisar(r, this);
        recargar();
    }

    private void cambiarEstado(boolean rechazar) {
        Visita visita = seleccion(tablaPendientes, modeloPendientes);
        if (visita == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() -> {
            if (rechazar) {
                controller.visitas().rechazar(sesion, visita.getId());
            } else {
                controller.visitas().aprobar(sesion, visita.getId());
            }
        });
        avisar(r, this);
        recargar();
    }

    private void hacerCheckIn() {
        Visita visita = seleccion(tablaCheckIn, modeloCheckIn);
        if (visita == null) {
            return;
        }
        String placa = JOptionPane.showInputDialog(this, "Placa del vehículo (opcional):",
                "SICA", JOptionPane.QUESTION_MESSAGE);
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.visitas().checkIn(sesion, visita.getId(), placa));
        avisar(r, this);
        recargar();
    }

    private void hacerCheckOut() {
        Visita visita = seleccion(tablaDentro, modeloDentro);
        if (visita == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.visitas().checkOut(sesion, visita.getId()));
        avisar(r, this);
        recargar();
    }

    private void registrarIncidente() {
        Visita visita = seleccion(tablaDentro, modeloDentro);
        if (visita == null) {
            return;
        }
        var incidente = Formularios.pedirIncidente(this, visita.getId());
        if (incidente == null) {
            return;
        }
        SicaController.Resultado r = controller.ejecutar(() ->
                controller.incidentes().registrar(sesion, incidente));
        avisar(r, this);
    }

    private Visita seleccion(JTable tabla, GenericTableModel<Visita> modelo) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila de la tabla.",
                    "SICA", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return modelo.getFila(tabla.convertRowIndexToModel(fila));
    }

    @Override
    protected void recargar() {
        modeloPendientes.setDatos(controller.visitas().pendientes(sesion));
        modeloCheckIn.setDatos(controller.visitas().aprobadasParaCheckIn(sesion));
        modeloDentro.setDatos(controller.visitas().dentro(sesion));
        if (puede("ver_reportes")) {
            modeloHistorial.setDatos(controller.visitas().historial(sesion));
        }
    }
}
