package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.VisitaDao;
import com.zonaacme.sica.model.entity.BitacoraAuditoria;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.ReporteService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/** Reportes: who-is-in, historial filtrable y consulta de bitácora. */
public class ReporteServiceImpl implements ReporteService {

    private final VisitaDao visitaDao;
    private final AuditoriaService auditoriaService;

    public ReporteServiceImpl(VisitaDao visitaDao, AuditoriaService auditoriaService) {
        this.visitaDao = visitaDao;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public List<Visita> personasDentro(Sesion sesion) {
        sesion.requerir("ver_reportes");
        return visitaDao.findDentro();
    }

    @Override
    public List<Visita> historialFiltrado(Sesion sesion, String texto) {
        sesion.requerir("ver_reportes");
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return visitaDao.findAll().stream()
                .filter(v -> filtro.isEmpty()
                        || v.getNombrePersona().toLowerCase(Locale.ROOT).contains(filtro)
                        || v.getDocumentoPersona().toLowerCase(Locale.ROOT).contains(filtro)
                        || (v.getNombreEmpresa() != null
                            && v.getNombreEmpresa().toLowerCase(Locale.ROOT).contains(filtro))
                        || (v.getNombreEstado() != null
                            && v.getNombreEstado().toLowerCase(Locale.ROOT).contains(filtro)))
                .toList();
    }

    @Override
    public List<BitacoraAuditoria> bitacoraFiltrada(Sesion sesion, String accion, Integer usuarioId,
                                                    LocalDateTime desde, LocalDateTime hasta) {
        return auditoriaService.consultar(sesion, accion, usuarioId, desde, hasta);
    }
}
