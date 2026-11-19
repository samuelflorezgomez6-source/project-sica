package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.BitacoraDao;
import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.service.AuditoriaService;

import java.time.LocalDateTime;
import java.util.List;

/** Implementación de la bitácora. Toda escritura se hace desde la capa de servicio. */
public class AuditoriaServiceImpl implements AuditoriaService {

    private final BitacoraDao bitacoraDao;

    public AuditoriaServiceImpl(BitacoraDao bitacoraDao) {
        this.bitacoraDao = bitacoraDao;
    }

    @Override
    public void registrar(AccionAuditoria accion, Sesion sesion, String tablaAfectada,
                          Integer registroId, String detalles) {
        bitacoraDao.insertar(accion, idDe(sesion), tablaAfectada, registroId, detalles);
    }

    @Override
    public List<BitacoraAuditoria> consultar(Sesion sesion, String accion, Integer usuarioId,
                                             LocalDateTime desde, LocalDateTime hasta) {
        sesion.requerir("consultar_bitacora");
        return bitacoraDao.findAll(accion, usuarioId, desde, hasta);
    }

    /** Un login fallido no tiene sesión: el id de usuario puede ser nulo. */
    public static Integer idDe(Sesion sesion) {
        return sesion == null || sesion.getUsuario() == null
                ? null : sesion.getUsuario().getId();
    }
}
