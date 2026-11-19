package com.zonaacme.sica.dao;

import com.zonaacme.sica.config.DatabaseConfig;
import com.zonaacme.sica.exception.PersistenceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Plantilla JDBC compartida por todos los DAO (PATRÓN DAO).
 * <p>
 * Centraliza try-with-resources, parámetros y traducción de excepciones.
 * Cada DAO concreto solo define sus SQL y el RowMapper de su entidad.
 * El principio DIP se cumple: los servicios dependen de interfaces DAO,
 * nunca de esta implementación.
 * </p>
 */
public abstract class BaseDao {

    protected Connection abrirConexion() {
        try {
            return DatabaseConfig.getInstance().getConnection();
        } catch (SQLException e) {
            throw new PersistenceException("conectar con MySQL", e);
        }
    }

    protected <T> List<T> consultar(String sql, RowMapper<T> mapper, Object... parametros) {
        List<T> resultados = new ArrayList<>();
        try (Connection c = abrirConexion();
             PreparedStatement ps = preparar(c, sql, parametros);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultados.add(mapper.map(rs));
            }
            return resultados;
        } catch (SQLException e) {
            throw new PersistenceException("consultar datos", e);
        }
    }

    protected int ejecutar(String sql, Object... parametros) {
        try (Connection c = abrirConexion();
             PreparedStatement ps = preparar(c, sql, parametros)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceException("escribir datos", e);
        }
    }

    /** Inserta y devuelve el id autogenerado (clave primaria). */
    protected int insertar(String sql, Object... parametros) {
        try (Connection c = abrirConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            aplicarParametros(ps, parametros);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new PersistenceException("insertar datos", e);
        }
    }

    /** Ejecuta varias sentencias en una única transacción: todo o nada. */
    protected void enTransaccion(SqlAction accion) {
        try (Connection c = abrirConexion()) {
            boolean autoCommitOriginal = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                accion.ejecutar(c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(autoCommitOriginal);
            }
        } catch (SQLException e) {
            throw new PersistenceException("ejecutar transacción", e);
        }
    }

    /** Ejecuta una sentencia dentro de una conexión ya existente (transacciones). */
    protected int ejecutarEn(Connection c, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = preparar(c, sql, parametros)) {
            return ps.executeUpdate();
        }
    }

    private static PreparedStatement preparar(Connection c, String sql, Object[] parametros)
            throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        aplicarParametros(ps, parametros);
        return ps;
    }

    private static void aplicarParametros(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i];
            if (valor instanceof LocalDateTime fecha) {
                valor = Timestamp.valueOf(fecha);
            } else if (valor instanceof Enum<?> enumeracion) {
                valor = enumeracion.name();
            }
            ps.setObject(i + 1, valor);
        }
    }

    /** Convierte un TIMESTAMP de ResultSet a LocalDateTime de forma segura. */
    protected static LocalDateTime leerFecha(ResultSet rs, String columna) throws SQLException {
        Timestamp ts = rs.getTimestamp(columna);
        return ts == null ? null : ts.toLocalDateTime();
    }
}
