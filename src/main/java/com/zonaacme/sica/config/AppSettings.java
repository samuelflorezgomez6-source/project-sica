package com.zonaacme.sica.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración centralizada de la aplicación.
 * <p>
 * Lee {@code application.properties} del classpath y expone valores tipados.
 * Si el archivo no existe (por ejemplo al ejecutar un jar sin recursos),
 * se usan los mismos valores por defecto que el docker-compose.yml.
 * </p>
 * <p>
 * Principio aplicado: <b>SRP</b>. Esta clase solo sabe dónde están los ajustes;
 * no abre conexiones ni conoce la lógica de negocio.
 * </p>
 */
public final class AppSettings {

    private static final Properties PROPERTIES = cargar();

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/sica_db?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "sica_user";
    private static final String DEFAULT_PASSWORD = "sica_pass";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    private AppSettings() {
        // Utilidad estática: no se instancia.
    }

    private static Properties cargar() {
        Properties props = new Properties();
        try (InputStream in = AppSettings.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            // No es fatal: se continúa con los valores por defecto.
            System.err.println("[SICA] No se pudo leer application.properties: " + e.getMessage());
        }
        return props;
    }

    public static String get(String clave, String defecto) {
        return PROPERTIES.getProperty(clave, defecto);
    }

    public static String dbUrl() {
        return get("db.url", DEFAULT_URL);
    }

    public static String dbUser() {
        return get("db.user", DEFAULT_USER);
    }

    public static String dbPassword() {
        return get("db.password", DEFAULT_PASSWORD);
    }

    public static String dbDriver() {
        return get("db.driver", DEFAULT_DRIVER);
    }

    public static int refreshSeconds() {
        try {
            return Integer.parseInt(get("ui.refresh.seconds", "2"));
        } catch (NumberFormatException e) {
            return 2;
        }
    }

    public static boolean isDarkTheme() {
        return "dark".equalsIgnoreCase(get("ui.theme", "light"));
    }
}
