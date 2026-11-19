package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.BitacoraDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.enums.AccionAuditoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de la bitácora. Solo la capa de servicio llama a insertar. */
public class BitacoraDaoJdbc extends BaseDao implements BitacoraDao {

    private static final String SELECT_BASE =
            "SELECT b.id, b.usuario_id, b.fecha_hora, b.accion_realizada, b.tabla_afectada, "
            + "b.registro_id_afectado, b.detalles, u.nombre AS nombre_usuario "
            + "FROM bitacora_auditoria b LEFT JOIN usuarios u ON u.id = b.usuario_id ";

    private static final String INSERT =
            "INSERT INTO bitacora_auditoria "
            + "(usuario_id, accion_realizada, tabla_afectada, registro_id_afectado, detalles) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final RowMapper<BitacoraAuditoria> MAPEADOR = rs -> {
        BitacoraAuditoria b = new BitacoraAuditoria();
        b.setId(rs.getLong("id"));
        b.setUsuarioId(rs.getInt("usuario_id"));
        b.setFechaHora(leerFecha(rs, "fecha_hora"));
        b.setAccionRealizada(rs.getString("accion_realizada"));
        b.setTablaAfectada(rs.getString("tabla_afectada"));
        b.setRegistroIdAfectado(rs.getInt("registro_id_afectado"));
        b.setDetalles(rs.getString("detalles"));
        b.setNombreUsuario(rs.getString("nombre_usuario"));
        return b;
    };

    @Override
    public int insertar(AccionAuditoria accion, Integer usuarioId, String tablaAfectada,
                        Integer registroId, String detalles) {
        return insertar(INSERT, usuarioId, accion.getCodigo(), tablaAfectada, registroId, detalles);
    }

    @Override
    public int insertarEn(Connection c, AccionAuditoria accion, Integer usuarioId,
                          String tablaAfectada, Integer registroId, String detalles) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(INSERT)) {
            ps.setObject(1, usuarioId);
            ps.setString(2, accion.getCodigo());
            ps.setString(3, tablaAfectada);
            ps.setObject(4, registroId);
            ps.setString(5, detalles);
            return ps.executeUpdate();
        }
    }

    @Override
    public List<BitacoraAuditoria> findAll(String accion, Integer usuarioId,
                                           LocalDateTime desde, LocalDateTime hasta) {
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1 = 1 ");
        List<Object> parametros = new ArrayList<>();

        if (accion != null && !accion.isBlank()) {
            sql.append("AND b.accion_realizada = ? ");
            parametros.add(accion.trim());
        }
        if (usuarioId != null) {
            sql.append("AND b.usuario_id = ? ");
            parametros.add(usuarioId);
        }
        if (desde != null) {
            sql.append("AND b.fecha_hora >= ? ");
            parametros.add(Timestamp.valueOf(desde));
        }
        if (hasta != null) {
            sql.append("AND b.fecha_hora <= ? ");
            parametros.add(Timestamp.valueOf(hasta));
        }
        sql.append("ORDER BY b.fecha_hora DESC, b.id DESC LIMIT 500");

        return consultar(sql.toString(), MAPEADOR, parametros.toArray());
    }
}
