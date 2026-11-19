package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.IncidenteDao;
import com.zonaacme.sica.dao.VisitaDao;
import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Incidente;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.IncidenteService;
import com.zonaacme.sica.util.DateUtil;
import com.zonaacme.sica.util.ValidationUtil;

import java.util.List;

/** Incidentes asociados a una visita concreta. */
public class IncidenteServiceImpl implements IncidenteService {

    private final IncidenteDao incidenteDao;
    private final VisitaDao visitaDao;
    private final AuditoriaService auditoriaService;

    public IncidenteServiceImpl(IncidenteDao incidenteDao, VisitaDao visitaDao,
                                AuditoriaService auditoriaService) {
        this.incidenteDao = incidenteDao;
        this.visitaDao = visitaDao;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public List<Incidente> listar(Sesion sesion) {
        sesion.requerir("ver_incidentes");
        return incidenteDao.findAll();
    }

    @Override
    public int registrar(Sesion sesion, Incidente incidente) {
        sesion.requerir("registrar_incidente");
        ValidationUtil.requerirId(incidente.getVisitaId(), "visita");
        ValidationUtil.requerir(incidente.getDescripcion(), "descripción");
        if (visitaDao.findById(incidente.getVisitaId()).isEmpty()) {
            throw new ValidacionException("La visita indicada no existe.");
        }
        incidente.setReportadoPorId(AuditoriaServiceImpl.idDe(sesion));
        incidente.setFecha(DateUtil.ahora());

        int id = incidenteDao.insertar(incidente);
        auditoriaService.registrar(AccionAuditoria.REGISTRO_INCIDENTE, sesion, "incidentes", id,
                "Incidente en visita #" + incidente.getVisitaId() + ": "
                + incidente.getDescripcion());
        return id;
    }
}
