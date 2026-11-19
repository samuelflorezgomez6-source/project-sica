package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.enums.AccionAuditoria;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de la bitácora inmutable.
 * La UI solo consulta ({@link #findAll}); la escritura la hacen los servicios.
 */
public interface BitacoraDao {

    int insertar(AccionAuditoria accion, Integer usuarioId, String tablaAfectada,
                 Integer registroId, String detalles);

    int insertarEn(Connection c, AccionAuditoria accion, Integer usuarioId, String tablaAfectada,
                   Integer registroId, String detalles) throws SQLException;

    List<BitacoraAuditoria> findAll(String accion, Integer usuarioId,
                                    LocalDateTime desde, LocalDateTime hasta);
}
