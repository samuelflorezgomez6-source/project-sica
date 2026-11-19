package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.IncidenteDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.model.entity.Incidente;

import java.util.List;

/** Implementación JDBC de {@link IncidenteDao}. */
public class IncidenteDaoJdbc extends BaseDao implements IncidenteDao {

    private static final String SELECT_BASE =
            "SELECT i.id, i.visita_id, i.reportado_por_id, i.fecha, i.descripcion, "
            + "u.nombre AS nombre_reportador, "
            + "CONCAT(COALESCE(p.nombre, 'Desconocido'), ' (Visita #', i.visita_id, ')') AS resumen_visita "
            + "FROM incidentes i "
            + "JOIN usuarios u ON u.id = i.reportado_por_id "
            + "LEFT JOIN visitas v ON v.id = i.visita_id "
            + "LEFT JOIN personas p ON p.id = v.persona_id ";

    private static final RowMapper<Incidente> MAPEADOR = rs -> {
        Incidente i = new Incidente();
        i.setId(rs.getInt("id"));
        i.setVisitaId(rs.getInt("visita_id"));
        i.setReportadoPorId(rs.getInt("reportado_por_id"));
        i.setFecha(leerFecha(rs, "fecha"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setNombreReportador(rs.getString("nombre_reportador"));
        i.setResumenVisita(rs.getString("resumen_visita"));
        return i;
    };

    @Override
    public List<Incidente> findAll() {
        return consultar(SELECT_BASE + "ORDER BY i.fecha DESC, i.id DESC", MAPEADOR);
    }

    @Override
    public List<Incidente> findByVisita(int visitaId) {
        return consultar(SELECT_BASE + "WHERE i.visita_id = ? ORDER BY i.fecha DESC", MAPEADOR, visitaId);
    }

    @Override
    public int insertar(Incidente incidente) {
        return insertar(
                "INSERT INTO incidentes (visita_id, reportado_por_id, fecha, descripcion) VALUES (?, ?, ?, ?)",
                incidente.getVisitaId(), incidente.getReportadoPorId(),
                java.sql.Timestamp.valueOf(incidente.getFecha()), incidente.getDescripcion());
    }
}
