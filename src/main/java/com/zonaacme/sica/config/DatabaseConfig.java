package com.zonaacme.sica.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * PATRÓN 1 - SINGLETON (conexión a base de datos).
 * <p>
 * Expone un único punto de acceso al {@link Connection} factory de la aplicación.
 * Se usa el "initialization-on-demand holder", que es thread-safe sin need de
 * {@code synchronized}: la JVM crea la instancia una sola vez, la primera vez
 * que se llama a {@link #getInstance()}.
 * </p>
 * <p>
 * Importante: DAO abre y cierra una conexión nuevas por operación
 * (try-with-resources). El Singleton no mantiene una conexión abierta, evita
 * fugas y funciona bien con las múltiples ventanas concurrentes.
 * </p>
 */
public final class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConfig() {
        this.url = AppSettings.dbUrl();
        this.user = AppSettings.dbUser();
        this.password = AppSettings.dbPassword();
        try {
            Class.forName(AppSettings.dbDriver());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se encontró el driver JDBC de MySQL", e);
        }
    }

    /** Holder thread-safe del Singleton. */
    private static final class Holder {
        private static final DatabaseConfig INSTANCE = new DatabaseConfig();
    }

    public static DatabaseConfig getInstance() {
        return Holder.INSTANCE;
    }

    /** Devuelve una conexión nueva; el llamado debe cerrarla con try-with-resources. */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Comprueba conectividad; se usa al arrancar la aplicación. */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection.isValid(3);
        } catch (SQLException e) {
            System.err.println("[SICA] MySQL no disponible: " + e.getMessage());
            return false;
        }
    }
}
