package com.zonaacme.sica.view;

import com.zonaacme.sica.controller.NavegacionController;
import com.zonaacme.sica.controller.SicaController;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.view.components.UiTheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Ventana de acceso. Cada instancia es una sesión independiente. */
public class LoginFrame extends JFrame {

    private final SicaController controller = new SicaController();
    private final NavegacionController navegacion = new NavegacionController();
    private final javax.swing.JTextField email = UiTheme.campo("usuario@zonaacme.com");
    private final JPasswordField password = new JPasswordField(18);
    private JButton botonIngresar;

    public LoginFrame() {
        super("SICA · Iniciar sesión");
        setSize(940, 540);
        setMinimumSize(new Dimension(820, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        this.botonIngresar = botonIngresar();
        add(panelMarca(), BorderLayout.WEST);
        add(panelFormulario(), BorderLayout.CENTER);
        getRootPane().setDefaultButton(botonIngresar);
    }

    private JPanel panelMarca() {
        JPanel marca = new JPanel();
        marca.setBackground(UiTheme.PRIMARIO_OSCURO);
        marca.setPreferredSize(new Dimension(370, 540));
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        marca.setBorder(BorderFactory.createEmptyBorder(52, 38, 40, 38));

        JLabel simbolo = new JLabel("◈", SwingConstants.CENTER);
        simbolo.setFont(new Font("Segoe UI", Font.PLAIN, 52));
        simbolo.setForeground(new Color(150, 200, 255));
        simbolo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nombre = new JLabel("SICA");
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 42));
        nombre.setForeground(Color.WHITE);
        nombre.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel("Sistema Integrado de Control de Acceso");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descripcion.setForeground(new Color(203, 220, 245));
        descripcion.setAlignmentX(LEFT_ALIGNMENT);

        marca.add(simbolo);
        marca.add(Box.createVerticalStrut(12));
        marca.add(nombre);
        marca.add(Box.createVerticalStrut(4));
        marca.add(descripcion);
        marca.add(Box.createVerticalStrut(36));
        marca.add(balada("✓", "Acceso auditado en cada operación"));
        marca.add(Box.createVerticalStrut(12));
        marca.add(balada("✓", "Aprobaciones en tiempo real"));
        marca.add(Box.createVerticalStrut(12));
        marca.add(balada("✓", "Zona Acme · 30+ empresas"));
        marca.add(Box.createVerticalGlue());

        JLabel pie = new JLabel("Java SE 17 · Swing · FlatLaf · MySQL 8");
        pie.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        pie.setForeground(new Color(160, 185, 220));
        pie.setAlignmentX(LEFT_ALIGNMENT);
        marca.add(pie);
        return marca;
    }

    private JLabel balada(String icono, String texto) {
        JLabel l = new JLabel(icono + "  " + texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(Color.WHITE);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JPanel panelFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 4, 8, 4);
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Bienvenido de nuevo");
        titulo.setFont(UiTheme.FUENTE_TITULO);
        titulo.setForeground(UiTheme.TEXTO);
        g.gridy = 0;
        formulario.add(titulo, g);

        JLabel subtitulo = new JLabel("Ingrese con su cuenta para continuar");
        subtitulo.setFont(UiTheme.FUENTE_BASE);
        subtitulo.setForeground(UiTheme.TEXTO_SUAVE);
        g.gridy = 1;
        formulario.add(subtitulo, g);

        g.gridy = 3;
        formulario.add(UiTheme.etiqueta("Correo electrónico"), g);
        email.setPreferredSize(new Dimension(330, 38));
        g.gridy = 4;
        formulario.add(email, g);

        g.gridy = 5;
        formulario.add(UiTheme.etiqueta("Contraseña"), g);
        password.setFont(UiTheme.FUENTE_BASE);
        password.setPreferredSize(new Dimension(330, 38));
        password.putClientProperty("JTextField.placeholderText", "••••••••");
        g.gridy = 6;
        formulario.add(password, g);

        JButton ingresar = botonIngresar;
        ingresar.setPreferredSize(new Dimension(330, 44));
        g.gridy = 8;
        g.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(ingresar, g);

        JButton nuevaVentana = UiTheme.boton("Abrir nueva ventana de sesión", UiTheme.Variedad.SECONDARY);
        nuevaVentana.setPreferredSize(new Dimension(330, 38));
        nuevaVentana.addActionListener(e -> navegacion.abrirNuevaVentanaSesion());
        g.gridy = 9;
        formulario.add(nuevaVentana, g);

        JLabel credenciales = new JLabel("<html><b>Credenciales de prueba</b> (contraseña: Acme2024!)<br>"
                + "superusuario@zonaacme.com · supervisor@zonaacme.com<br>"
                + "guarda@zonaacme.com · funcionario@globex.com</html>");
        credenciales.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        credenciales.setForeground(UiTheme.TEXTO_SUAVE);
        credenciales.setBorder(BorderFactory.createEmptyBorder(14, 8, 0, 8));
        g.gridy = 11;
        formulario.add(credenciales, g);

        return formulario;
    }

    private JButton botonIngresar() {
        JButton boton = UiTheme.boton("Ingresar al sistema", UiTheme.Variedad.PRIMARIO);
        boton.addActionListener(e -> intentarLogin());
        return boton;
    }

    private void intentarLogin() {
        final Sesion[] sesion = {null};
        SicaController.Resultado resultado = controller.ejecutar(() ->
                sesion[0] = controller.auth().login(email.getText(), new String(password.getPassword())));

        if (!resultado.ok()) {
            JOptionPane.showMessageDialog(this, resultado.mensaje(), "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new MainFrame(sesion[0]).setVisible(true);
        dispose();
    }
}
