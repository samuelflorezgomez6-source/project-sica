package com.zonaacme.sica.service;

import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.AccionAuditoria;

import java.time.LocalDateTime;
import java.util.List;

/** Escritura y consulta de la bitácora inmutable. */
public interface AuditoriaService {

    /** Lo invocan los servicios después de cada operación crítica. */
    void registrar(AccionAuditoria accion, Sesion sesion, String tablaAfectada,
                   Integer registroId, String detalles);

    /** Consulta con permiso RBAC 'consultar_bitacora'. */
    List<BitacoraAuditoria> consultar(Sesion sesion, String accion, Integer usuarioId,
                                      LocalDateTime desde, LocalDateTime hasta);
}
