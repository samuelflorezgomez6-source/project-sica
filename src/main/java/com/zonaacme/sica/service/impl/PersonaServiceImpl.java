package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.EmpresaDao;
import com.zonaacme.sica.dao.PersonaDao;
import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.model.enums.EstadoAcceso;
import com.zonaacme.sica.pattern.observer.TipoEventoVisita;
import com.zonaacme.sica.pattern.observer.VisitaSubject;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.PersonaService;
import com.zonaacme.sica.util.ValidationUtil;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Gestión de personas: incluye el bloqueo inmediato de acceso y su auditoría. */
public class PersonaServiceImpl implements PersonaService {

    private final PersonaDao personaDao;
    private final EmpresaDao empresaDao;
    private final AuditoriaService auditoriaService;
    private final VisitaSubject eventos;

    public PersonaServiceImpl(PersonaDao personaDao, EmpresaDao empresaDao,
                              AuditoriaService auditoriaService, VisitaSubject eventos) {
        this.personaDao = personaDao;
        this.empresaDao = empresaDao;
        this.auditoriaService = auditoriaService;
        this.eventos = eventos;
    }

    @Override
    public List<Persona> listar(Sesion sesion) {
        sesion.requerir("ver_personas");
        return personaDao.findAll();
    }

    @Override
    public List<Persona> buscar(Sesion sesion, String texto) {
        sesion.requerir("ver_personas");
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return personaDao.findAll().stream()
                .filter(p -> filtro.isEmpty()
                        || p.getNombre().toLowerCase(Locale.ROOT).contains(filtro)
                        || p.getDocumentoIdentidad().toLowerCase(Locale.ROOT).contains(filtro)
                        || (p.getNombreEmpresa() != null
                            && p.getNombreEmpresa().toLowerCase(Locale.ROOT).contains(filtro)))
                .toList();
    }

    @Override
    public Optional<Persona> porDocumento(Sesion sesion, String documento) {
        sesion.requerir("ver_personas");
        ValidationUtil.validarDocumento(documento);
        return personaDao.findByDocumento(documento.trim());
    }

    @Override
    public int crear(Sesion sesion, Persona persona) {
        sesion.requerir("crear_persona");
        validar(persona);
        if (personaDao.findByDocumento(persona.getDocumentoIdentidad()).isPresent()) {
            throw new ValidacionException("Ya existe una persona con ese documento.");
        }
        int id = personaDao.insertar(persona);
        auditoriaService.registrar(AccionAuditoria.CREACION_PERSONA, sesion, "personas", id,
                "Persona creada: " + persona.getNombre() + " (" + persona.getDocumentoIdentidad() + ")");
        eventos.notificar(TipoEventoVisita.ACTUALIZACION_GENERAL, "Listado de personas actualizado.");
        return id;
    }

    @Override
    public int actualizar(Sesion sesion, Persona persona) {
        sesion.requerir("editar_persona");
        ValidationUtil.requerirId(persona.getId(), "persona");
        validar(persona);
        int filas = personaDao.actualizar(persona);
        auditoriaService.registrar(AccionAuditoria.ACTUALIZACION_PERSONA, sesion, "personas",
                persona.getId(), "Persona actualizada: " + persona.getDocumentoIdentidad());
        eventos.notificar(TipoEventoVisita.ACTUALIZACION_GENERAL, "Listado de personas actualizado.");
        return filas;
    }

    @Override
    public int eliminar(Sesion sesion, int personaId) {
        sesion.requerir("eliminar_persona");
        int filas = personaDao.eliminar(personaId);
        auditoriaService.registrar(AccionAuditoria.ELIMINACION_PERSONA, sesion, "personas",
                personaId, "Persona eliminada #" + personaId);
        return filas;
    }

    @Override
    public void cambiarEstadoAcceso(Sesion sesion, int personaId, EstadoAcceso estado) {
        sesion.requerir("bloquear_persona");
        if (personaDao.findById(personaId).isEmpty()) {
            throw new ValidacionException("La persona seleccionada no existe.");
        }
        personaDao.cambiarEstadoAcceso(personaId, estado.getId());
        auditoriaService.registrar(AccionAuditoria.CAMBIO_ESTADO_ACCESO, sesion, "personas",
                personaId, "Nuevo estado de acceso: " + estado.getNombre());
        eventos.notificar(TipoEventoVisita.ACTUALIZACION_GENERAL,
                "Estado de acceso actualizado: " + estado.getNombre());
    }

    private void validar(Persona persona) {
        ValidationUtil.requerir(persona.getNombre(), "nombre");
        ValidationUtil.validarDocumento(persona.getDocumentoIdentidad());
        if (persona.getTipoPersona() == null) {
            throw new ValidacionException("Debe indicar el tipo de persona.");
        }
        if (persona.getEmpresaId() != null && empresaDao.findById(persona.getEmpresaId()).isEmpty()) {
            throw new ValidacionException("La empresa seleccionada no existe.");
        }
        if (persona.getEstadoAccesoId() == null) {
            persona.setEstadoAccesoId(EstadoAcceso.ACTIVO.getId());
        }
    }
}
