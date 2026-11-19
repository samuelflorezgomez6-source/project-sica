package com.zonaacme.sica.service.impl;

import com.zonaacme.sica.dao.BitacoraDao;
import com.zonaacme.sica.dao.PersonaDao;
import com.zonaacme.sica.dao.TransactionRunner;
import com.zonaacme.sica.dao.VisitaDao;
import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.entity.Sesion;
import com.zonaacme.sica.model.entity.Visita;
import com.zonaacme.sica.model.enums.AccionAuditoria;
import com.zonaacme.sica.model.enums.EstadoVisita;
import com.zonaacme.sica.model.enums.TipoFlujoVisita;
import com.zonaacme.sica.pattern.factory.VisitaFactory;
import com.zonaacme.sica.pattern.observer.TipoEventoVisita;
import com.zonaacme.sica.pattern.observer.VisitaEventBus;
import com.zonaacme.sica.pattern.observer.VisitaObserver;
import com.zonaacme.sica.pattern.strategy.IngresoStrategyResolver;
import com.zonaacme.sica.pattern.strategy.ResultadoIngreso;
import com.zonaacme.sica.pattern.strategy.SolicitudIngreso;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.VisitaService;
import com.zonaacme.sica.util.DateUtil;
import com.zonaacme.sica.util.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Servicio central de control de acceso. Aquí convergen los 5 patrones:
 * Singleton (DAO/localizador), Factory (VisitaFactory), Strategy (resolver),
 * Observer (VisitaEventBus) y DAO (VisitaDao).
 * <p>
 * Cada método valida el permiso RBAC con {@code sesion.requerir(...)} y deja
 * constancia en la bitácora. Las operaciones de varios pasos son transaccionales.
 * </p>
 */
public class VisitaServiceImpl implements VisitaService {

    private final VisitaDao visitaDao;
    private final PersonaDao personaDao;
    private final TransactionRunner transactionRunner;
    private final BitacoraDao bitacoraDao;
    private final VisitaEventBus eventos;
    private final AuditoriaService auditoriaService;
    private final IngresoStrategyResolver resolver = new IngresoStrategyResolver();
    private final VisitaFactory factory = new VisitaFactory();

    public VisitaServiceImpl(VisitaDao visitaDao, PersonaDao personaDao,
                             TransactionRunner transactionRunner, BitacoraDao bitacoraDao,
                             VisitaEventBus eventos, AuditoriaService auditoriaService) {
        this.visitaDao = visitaDao;
        this.personaDao = personaDao;
        this.transactionRunner = transactionRunner;
        this.bitacoraDao = bitacoraDao;
        this.eventos = eventos;
        this.auditoriaService = auditoriaService;
    }

    // ---------- Observer ----------

    @Override
    public void suscribir(VisitaObserver observador) {
        eventos.suscribir(observador);
    }

    @Override
    public void desuscribir(VisitaObserver observador) {
        eventos.desuscribir(observador);
    }

    @Override
    public void notificar(TipoEventoVisita tipo, String mensaje) {
        eventos.notificar(tipo, mensaje);
    }

    // ---------- Consultas ----------

    @Override
    public List<Visita> pendientes(Sesion sesion) {
        sesion.requerir("ver_control_acceso");
        return visitaDao.findPendientes();
    }

    @Override
    public List<Visita> aprobadasParaCheckIn(Sesion sesion) {
        sesion.requerir("ver_control_acceso");
        return visitaDao.findAll().stream()
                .filter(v -> v.getEstado() == EstadoVisita.APROBADO && v.getFechaEntrada() == null)
                .toList();
    }

    @Override
    public List<Visita> dentro(Sesion sesion) {
        sesion.requerir("ver_control_acceso");
        return visitaDao.findDentro();
    }

    @Override
    public List<Visita> historial(Sesion sesion) {
        sesion.requerir("ver_reportes");
        return visitaDao.findAll().stream()
                .filter(v -> v.getEstado() != EstadoVisita.DENTRO
                             && !v.esPendiente())
                .toList();
    }

    @Override
    public List<Visita> porPersona(Sesion sesion, int personaId) {
        sesion.requerir("ver_control_acceso");
        return visitaDao.findByPersona(personaId);
    }

    // ---------- Flujos 1, 2 y 3: registro de la visita ----------

    @Override
    public ResultadoIngreso registrarIngreso(Sesion sesion, SolicitudIngreso solicitud) {
        sesion.requerir("registrar_visita");
        Persona persona = cargarPersona(solicitud.getPersona());
        Optional<Visita> abierta = visitaDao.findAbiertaByPersona(persona.getId());

        ResultadoIngreso resultado = resolver.resolver(solicitud, abierta);
        if (!resultado.isPermitido()) {
            throw new ValidacionException(resultado.getMensaje());
        }

        if (resultado.isRegularizarSalida()) {
            regularizarSalidaOlvidada(sesion, solicitud, abierta.orElseThrow());
            return resultado;
        }

        Visita visita = factory.crear(solicitud, resultado.getEstadoDestino());
        int id = visitaDao.insertar(visita);
        auditoriaService.registrar(AccionAuditoria.REGISTRO_VISITA, sesion, "visitas", id,
                "Flujo " + solicitud.getFlujo().getEtiqueta() + " | persona: "
                + persona.getNombre() + " | estado inicial: " + resultado.getEstadoDestino());

        TipoEventoVisita evento = resultado.isRequiereAprobacion()
                ? TipoEventoVisita.NUEVA_SOLICITUD
                : TipoEventoVisita.APROBADA;
        eventos.notificar(evento, "Visita #" + id + " de " + persona.getNombre()
                + ": " + resultado.getMensaje());
        return resultado;
    }

    // ---------- Flujo 4: salida olvidada + check-in de visitas ----------

    @Override
    public void checkIn(Sesion sesion, int visitaId, String placa) {
        sesion.requerir("check_in");
        Visita visita = visitaDao.findById(visitaId)
                .orElseThrow(() -> new ValidacionException("La visita seleccionada no existe."));
        Persona persona = cargarPersona(
                personaDao.findById(visita.getPersonaId())
                        .orElseThrow(() -> new ValidacionException("La persona de la visita no existe.")));

        Optional<Visita> abierta = visitaDao.findAbiertaByPersona(persona.getId());
        if (abierta.isPresent() && abierta.get().getId().equals(visitaId)) {
            throw new ValidacionException("Esta persona ya se encuentra 'Dentro' en la visita #" + visitaId + ".");
        }

        SolicitudIngreso solicitud = new SolicitudIngreso(
                persona, TipoFlujoVisita.PRE_REGISTRADA, placa, AuditoriaServiceImpl.idDe(sesion), visitaId);
        ResultadoIngreso resultado = resolver.resolver(solicitud, abierta);
        if (!resultado.isPermitido()) {
            throw new ValidacionException(resultado.getMensaje());
        }
        if (resultado.isRegularizarSalida()) {
            regularizarSalidaOlvidada(sesion, solicitud, abierta.orElseThrow());
            return;
        }

        if (visita.getEstado() != EstadoVisita.APROBADO) {
            throw new ValidacionException("Para registrar el ingreso, la visita debe estar 'Aprobada'. "
                    + "Estado actual: " + visita.getEstado());
        }

        visitaDao.registrarCheckIn(visitaId, DateUtil.ahora(), EstadoVisita.DENTRO.getId());
        auditoriaService.registrar(AccionAuditoria.CHECK_IN, sesion, "visitas", visitaId,
                "Check-in de " + persona.getNombre() + " (documento " + persona.getDocumentoIdentidad() + ")");
        eventos.notificar(TipoEventoVisita.CHECK_IN,
                "Check-in realizado: " + persona.getNombre());
    }

    /** Cierra la visita abierta y crea una nueva DENTRO, todo en una transacción. */
    private void regularizarSalidaOlvidada(Sesion sesion, SolicitudIngreso solicitud, Visita anterior) {
        LocalDateTime ahora = DateUtil.ahora();
        Integer usuarioId = AuditoriaServiceImpl.idDe(sesion);

        transactionRunner.ejecutar(c -> {
            visitaDao.cerrarEn(c, anterior.getId(), ahora, EstadoVisita.CERRADA_SISTEMA.getId());
            Visita nueva = factory.crear(solicitud, EstadoVisita.DENTRO);
            visitaDao.insertarEn(c, nueva);
            bitacoraDao.insertarEn(c, AccionAuditoria.CIERRE_AUTOMATICO_VISITA, usuarioId,
                    "visitas", anterior.getId(),
                    "Visita cerrada automáticamente por salida olvidada al intentar un nuevo ingreso.");
            bitacoraDao.insertarEn(c, AccionAuditoria.REGISTRO_VISITA, usuarioId,
                    "visitas", null,
                    "Nueva visita generada tras regularizar salida olvidada.");
            bitacoraDao.insertarEn(c, AccionAuditoria.CHECK_IN, usuarioId,
                    "visitas", null,
                    "Check-in automático de " + solicitud.getPersona().getNombre());
        });

        eventos.notificar(TipoEventoVisita.SALIDA_OLVIDADA,
                "Salida olvidada regularizada: visita #" + anterior.getId()
                + " cerrada y nuevo ingreso abierto correctamente.");
    }

    // ---------- Aprobaciones y salida ----------

    @Override
    public void aprobar(Sesion sesion, int visitaId) {
        sesion.requerir("aprobar_visita");
        Visita visita = visitaDao.findById(visitaId)
                .orElseThrow(() -> new ValidacionException("La visita seleccionada no existe."));
        if (!visita.esPendiente()) {
            throw new ValidacionException("Solo se pueden aprobar visitas pendientes.");
        }
        visitaDao.actualizarEstado(visitaId, EstadoVisita.APROBADO.getId(),
                AuditoriaServiceImpl.idDe(sesion));
        auditoriaService.registrar(AccionAuditoria.APROBACION_VISITA, sesion, "visitas", visitaId,
                "Visita de " + visita.getNombrePersona() + " aprobada por "
                + sesion.getUsuario().getNombre());
        eventos.notificar(TipoEventoVisita.APROBADA,
                "Visita #" + visitaId + " aprobada. El guarda ya puede hacer el check-in.");
    }

    @Override
    public void rechazar(Sesion sesion, int visitaId) {
        sesion.requerir("rechazar_visita");
        Visita visita = visitaDao.findById(visitaId)
                .orElseThrow(() -> new ValidacionException("La visita seleccionada no existe."));
        if (!visita.esPendiente()) {
            throw new ValidacionException("Solo se pueden rechazar visitas pendientes.");
        }
        visitaDao.actualizarEstado(visitaId, EstadoVisita.RECHAZADO.getId(), null);
        auditoriaService.registrar(AccionAuditoria.RECHAZO_VISITA, sesion, "visitas", visitaId,
                "Visita de " + visita.getNombrePersona() + " rechazada por "
                + sesion.getUsuario().getNombre());
        eventos.notificar(TipoEventoVisita.RECHAZADA,
                "Visita #" + visitaId + " rechazada.");
    }

    @Override
    public void checkOut(Sesion sesion, int visitaId) {
        sesion.requerir("check_out");
        Visita visita = visitaDao.findById(visitaId)
                .orElseThrow(() -> new ValidacionException("La visita seleccionada no existe."));
        if (visita.getEstado() != EstadoVisita.DENTRO) {
            throw new ValidacionException("Solo se puede registrar la salida de una visita 'Dentro'.");
        }
        visitaDao.registrarCheckOut(visitaId, DateUtil.ahora(), EstadoVisita.FUERA.getId());
        auditoriaService.registrar(AccionAuditoria.CHECK_OUT, sesion, "visitas", visitaId,
                "Check-out de " + visita.getNombrePersona());
        eventos.notificar(TipoEventoVisita.CHECK_OUT,
                "Check-out registrado: " + visita.getNombrePersona());
    }

    // ---------- Apoyo ----------

    private Persona cargarPersona(Persona persona) {
        if (persona == null) {
            throw new ValidacionException("Debe seleccionar una persona.");
        }
        ValidationUtil.validarDocumento(persona.getDocumentoIdentidad());
        return persona;
    }

    /** Filtrado por texto con API Stream (se usa en la búsqueda rápida de la UI). */
    public List<Visita> filtrarPorNombre(List<Visita> visitas, String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        return visitas.stream()
                .filter(v -> filtro.isEmpty()
                        || (v.getNombrePersona() != null
                            && v.getNombrePersona().toLowerCase(Locale.ROOT).contains(filtro))
                        || (v.getDocumentoPersona() != null
                            && v.getDocumentoPersona().contains(filtro)))
                .toList();
    }
}
