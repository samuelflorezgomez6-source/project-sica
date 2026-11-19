package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.dao.VisitaDao;
import com.zonaacme.sica.model.entity.Visita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link VisitaDao}. */
public class VisitaDaoJdbc extends BaseDao implements VisitaDao {

    private static final String SELECT_BASE =
            "SELECT v.id, v.persona_id, v.fecha_entrada, v.fecha_salida, v.estado_visita_id, "
            + "v.vehiculo_placa, v.visita_aprobada_por, p.nombre AS nombre_persona, "
            + "p.documento_identidad, p.url_foto, e.nombre AS nombre_empresa, "
            + "ev.nombre_estado, u.nombre AS nombre_aprobador "
            + "FROM visitas v "
            + "JOIN personas p ON p.id = v.persona_id "
            + "LEFT JOIN empresas e ON e.id = p.empresa_id "
            + "LEFT JOIN visita_estados ev ON ev.id = v.estado_visita_id "
            + "LEFT JOIN usuarios u ON u.id = v.visita_aprobada_por ";

    private static final String INSERT =
            "INSERT INTO visitas (persona_id, fecha_entrada, fecha_salida, estado_visita_id, "
            + "vehiculo_placa, visita_aprobada_por) VALUES (?, ?, ?, ?, ?, ?)";

    private static final RowMapper<Visita> MAPEADOR = rs -> {
        Visita v = new Visita();
        v.setId(rs.getInt("id"));
        v.setPersonaId(rs.getInt("persona_id"));
        v.setFechaEntrada(leerFecha(rs, "fecha_entrada"));
        v.setFechaSalida(leerFecha(rs, "fecha_salida"));
        v.setEstadoVisitaId(rs.getInt("estado_visita_id"));
        v.setVehiculoPlaca(rs.getString("vehiculo_placa"));
        v.setVisitaAprobadaPor(rs.getInt("visita_aprobada_por"));
        v.setNombrePersona(rs.getString("nombre_persona"));
        v.setDocumentoPersona(rs.getString("documento_identidad"));
        v.setUrlFoto(rs.getString("url_foto"));
        v.setNombreEmpresa(rs.getString("nombre_empresa"));
        v.setNombreEstado(rs.getString("nombre_estado"));
        v.setNombreAprobador(rs.getString("nombre_aprobador"));
        return v;
    };

    @Override
    public List<Visita> findAll() {
        return consultar(SELECT_BASE + "ORDER BY v.id DESC", MAPEADOR);
    }

    @Override
    public List<Visita> findPendientes() {
        return consultar(SELECT_BASE + "WHERE v.estado_visita_id IN (3, 7) ORDER BY v.id DESC", MAPEADOR);
    }

    @Override
    public List<Visita> findDentro() {
        return consultar(SELECT_BASE + "WHERE v.estado_visita_id = 1 "
                + "ORDER BY v.fecha_entrada DESC", MAPEADOR);
    }

    @Override
    public List<Visita> findByPersona(int personaId) {
        return consultar(SELECT_BASE + "WHERE v.persona_id = ? ORDER BY v.id DESC", MAPEADOR, personaId);
    }

    @Override
    public Optional<Visita> findAbiertaByPersona(int personaId) {
        return consultar(SELECT_BASE + "WHERE v.persona_id = ? AND v.estado_visita_id = 1 "
                + "ORDER BY v.id DESC LIMIT 1", MAPEADOR, personaId).stream().findFirst();
    }

    @Override
    public Optional<Visita> findById(int id) {
        return consultar(SELECT_BASE + "WHERE v.id = ?", MAPEADOR, id).stream().findFirst();
    }

    @Override
    public int insertar(Visita visita) {
        return insertar(INSERT, parametrosInsertar(visita));
    }

    @Override
    public int insertarEn(Connection c, Visita visita) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(INSERT)) {
            Object[] params = parametrosInsertar(visita);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    @Override
    public int actualizarEstado(int visitaId, int estadoId, Integer aprobadaPor) {
        return ejecutar(
                "UPDATE visitas SET estado_visita_id = ?, "
                + "visita_aprobada_por = COALESCE(?, visita_aprobada_por) WHERE id = ?",
                estadoId, aprobadaPor, visitaId);
    }

    @Override
    public int registrarCheckIn(int visitaId, LocalDateTime entrada, int estadoId) {
        return ejecutar("UPDATE visitas SET fecha_entrada = ?, estado_visita_id = ? WHERE id = ?",
                Timestamp.valueOf(entrada), estadoId, visitaId);
    }

    @Override
    public int registrarCheckOut(int visitaId, LocalDateTime salida, int estadoId) {
        return ejecutar("UPDATE visitas SET fecha_salida = ?, estado_visita_id = ? WHERE id = ?",
                Timestamp.valueOf(salida), estadoId, visitaId);
    }

    @Override
    public int cerrarEn(Connection c, int visitaId, LocalDateTime salida, int estadoId)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE visitas SET fecha_salida = ?, estado_visita_id = ? WHERE id = ?")) {
            ps.setTimestamp(1, Timestamp.valueOf(salida));
            ps.setInt(2, estadoId);
            ps.setInt(3, visitaId);
            return ps.executeUpdate();
        }
    }

    private static Object[] parametrosInsertar(Visita v) {
        return new Object[]{
                v.getPersonaId(),
                v.getFechaEntrada() == null ? null : Timestamp.valueOf(v.getFechaEntrada()),
                v.getFechaSalida() == null ? null : Timestamp.valueOf(v.getFechaSalida()),
                v.getEstadoVisitaId(),
                v.getVehiculoPlaca(),
                v.getVisitaAprobadaPor()
        };
    }
}
