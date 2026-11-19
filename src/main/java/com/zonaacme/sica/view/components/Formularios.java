package com.zonaacme.sica.view.components;

import com.zonaacme.sica.model.entity.Empresa;
import com.zonaacme.sica.model.entity.Incidente;
import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Usuario;
import com.zonaacme.sica.model.enums.EstadoAcceso;
import com.zonaacme.sica.model.enums.TipoPersona;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;
import java.net.URLConnection;
import java.awt.image.BufferedImage;
import java.util.List;

/** Diálogos de formulario. La validación de negocio la hacen los servicios. */
public final class Formularios {

    private Formularios() {
    }

    private static JComponent campo(JLabel etiqueta, JComponent componente) {
        JPanel celda = new JPanel(new java.awt.BorderLayout(4, 2));
        celda.setOpaque(false);
        celda.add(etiqueta, java.awt.BorderLayout.NORTH);
        celda.add(componente, java.awt.BorderLayout.CENTER);
        return celda;
    }

    public static Persona pedirPersona(Component padre, Persona inicial, List<Empresa> empresas) {
        JTextField nombre = new JTextField(inicial == null ? "" : inicial.getNombre(), 20);
        JTextField documento = new JTextField(inicial == null ? "" : inicial.getDocumentoIdentidad(), 20);
        JTextField foto = new JTextField(inicial == null ? "" : inicial.getUrlFoto(), 20);

        JComboBox<Empresa> comboEmpresas = new JComboBox<>();
        comboEmpresas.addItem(null);
        empresas.forEach(comboEmpresas::addItem);
        if (inicial != null && inicial.getEmpresaId() != null) {
            empresas.stream().filter(e -> e.getId().equals(inicial.getEmpresaId()))
                    .findFirst().ifPresent(comboEmpresas::setSelectedItem);
        }
        comboEmpresas.setRenderer((list, value, index, selected, focus) ->
                new JLabel(value == null ? "— Sin empresa —" : value.getNombre()));

        JComboBox<TipoPersona> comboTipo = new JComboBox<>(TipoPersona.values());
        if (inicial != null && inicial.getTipoPersona() != null) {
            comboTipo.setSelectedItem(inicial.getTipoPersona());
        }

        JComboBox<EstadoAcceso> comboEstado = new JComboBox<>(EstadoAcceso.values());
        if (inicial != null && inicial.getEstadoAccesoId() != null) {
            EstadoAccesoHelper.seleccionar(comboEstado, inicial.getEstadoAccesoId());
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(campo(UiTheme.etiqueta("Nombre completo"), nombre));
        form.add(campo(UiTheme.etiqueta("Documento de identidad"), documento));
        form.add(campo(UiTheme.etiqueta("Empresa"), comboEmpresas));
        form.add(campo(UiTheme.etiqueta("Tipo de persona"), comboTipo));
        form.add(campo(UiTheme.etiqueta("Estado de acceso"), comboEstado));
        form.add(campo(UiTheme.etiqueta("URL de foto (opcional)"), foto));

        int respuesta = JOptionPane.showConfirmDialog(padre, form,
                inicial == null ? "Nueva persona" : "Editar persona",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return null;
        }

        Persona persona = inicial == null ? new Persona() : inicial;
        persona.setNombre(nombre.getText().trim());
        persona.setDocumentoIdentidad(documento.getText().trim());
        Empresa empresa = (Empresa) comboEmpresas.getSelectedItem();
        persona.setEmpresaId(empresa == null ? null : empresa.getId());
        persona.setTipoPersona((TipoPersona) comboTipo.getSelectedItem());
        persona.setEstadoAccesoId(((EstadoAcceso) comboEstado.getSelectedItem()).getId());
        persona.setUrlFoto(foto.getText().trim());
        return persona;
    }

    public static Empresa pedirEmpresa(Component padre, Empresa inicial) {
        JTextField nombre = new JTextField(inicial == null ? "" : inicial.getNombre(), 22);
        JTextField contacto = new JTextField(inicial == null ? "" : inicial.getContactoPrincipal(), 22);
        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(campo(UiTheme.etiqueta("Nombre de la empresa"), nombre));
        form.add(campo(UiTheme.etiqueta("Contacto principal"), contacto));

        int respuesta = JOptionPane.showConfirmDialog(padre, form,
                inicial == null ? "Nueva empresa" : "Editar empresa",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return null;
        }
        Empresa empresa = inicial == null ? new Empresa() : inicial;
        empresa.setNombre(nombre.getText().trim());
        empresa.setContactoPrincipal(contacto.getText().trim());
        return empresa;
    }

    public static Usuario pedirUsuario(Component padre, Usuario inicial, List<Rol> roles) {
        JTextField nombre = new JTextField(inicial == null ? "" : inicial.getNombre(), 20);
        JTextField email = new JTextField(inicial == null ? "" : inicial.getEmail(), 20);
        JPasswordField password = new JPasswordField(20);
        password.putClientProperty("JTextField.placeholderText",
                inicial == null ? "Contraseña" : "Dejar vacío para mantener");

        JComboBox<Rol> comboRoles = new JComboBox<>(roles.toArray(new Rol[0]));
        if (inicial != null && inicial.getRolId() != null) {
            roles.stream().filter(r -> r.getId().equals(inicial.getRolId()))
                    .findFirst().ifPresent(comboRoles::setSelectedItem);
        }
        JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Activo", "Inactivo"});
        if (inicial != null && !inicial.isActivo()) {
            comboEstado.setSelectedIndex(1);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(campo(UiTheme.etiqueta("Nombre"), nombre));
        form.add(campo(UiTheme.etiqueta("Email"), email));
        form.add(campo(UiTheme.etiqueta("Contraseña"), password));
        form.add(campo(UiTheme.etiqueta("Rol"), comboRoles));
        form.add(campo(UiTheme.etiqueta("Estado"), comboEstado));

        int respuesta = JOptionPane.showConfirmDialog(padre, form,
                inicial == null ? "Nuevo usuario" : "Editar usuario",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return null;
        }

        Usuario usuario = inicial == null ? new Usuario() : inicial;
        usuario.setNombre(nombre.getText().trim());
        usuario.setEmail(email.getText().trim());
        Rol rol = (Rol) comboRoles.getSelectedItem();
        usuario.setRolId(rol == null ? null : rol.getId());
        usuario.setActivo(comboEstado.getSelectedIndex() == 0);
        return usuario;
    }

    public static String passwordNuevo(Component padre, Usuario usuario) {
        JPasswordField pass = new JPasswordField(20);
        int respuesta = JOptionPane.showConfirmDialog(padre, pass,
                "Nueva contraseña para " + usuario.getEmail(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return respuesta == JOptionPane.OK_OPTION ? new String(pass.getPassword()) : null;
    }

    public static Incidente pedirIncidente(Component padre, int visitaId) {
        JTextField descripcion = new JTextField(34);
        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(UiTheme.etiqueta("Visita #" + visitaId));
        form.add(descripcion);
        int respuesta = JOptionPane.showConfirmDialog(padre, form, "Registrar incidente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return null;
        }
        Incidente incidente = new Incidente();
        incidente.setVisitaId(visitaId);
        incidente.setDescripcion(descripcion.getText().trim());
        return incidente;
    }

    /** Carga la foto desde URL en segundo plano; si falla, deja las iniciales. */
    public static void cargarFoto(JLabel destino, String url, String iniciales) {
        destino.setIcon(null);
        destino.setText(iniciales);
        destino.setHorizontalAlignment(JLabel.CENTER);
        destino.setFont(new Font("Segoe UI", Font.BOLD, 26));
        destino.setForeground(UiTheme.PRIMARIO);
        if (url == null || url.isBlank()) {
            return;
        }
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    URLConnection conexion = new URL(url).openConnection();
                    conexion.setConnectTimeout(1500);
                    conexion.setReadTimeout(1500);
                    BufferedImage imagen = ImageIO.read(conexion.getInputStream());
                    if (imagen == null) {
                        return null;
                    }
                    Image escalada = imagen.getScaledInstance(92, 92, Image.SCALE_SMOOTH);
                    return new ImageIcon(escalada);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icono = get();
                    if (icono != null) {
                        destino.setIcon(icono);
                        destino.setText("");
                    }
                } catch (Exception ignorado) {
                    // Se conservan las iniciales como fallback.
                }
            }
        }.execute();
    }

    /** Pequeño helper para evitar acoplar el formulario a los ids del lookup. */
    private static final class EstadoAccesoHelper {
        private static void seleccionar(JComboBox<EstadoAcceso> combo, int id) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).getId() == id) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
    }
}
