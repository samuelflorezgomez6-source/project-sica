package com.zonaacme.sica.config;

import com.zonaacme.sica.dao.BitacoraDao;
import com.zonaacme.sica.dao.EmpresaDao;
import com.zonaacme.sica.dao.IncidenteDao;
import com.zonaacme.sica.dao.PermisoDao;
import com.zonaacme.sica.dao.PersonaDao;
import com.zonaacme.sica.dao.RolDao;
import com.zonaacme.sica.dao.TransactionRunner;
import com.zonaacme.sica.dao.UsuarioDao;
import com.zonaacme.sica.dao.VisitaDao;
import com.zonaacme.sica.dao.impl.BitacoraDaoJdbc;
import com.zonaacme.sica.dao.impl.EmpresaDaoJdbc;
import com.zonaacme.sica.dao.impl.IncidenteDaoJdbc;
import com.zonaacme.sica.dao.impl.PermisoDaoJdbc;
import com.zonaacme.sica.dao.impl.PersonaDaoJdbc;
import com.zonaacme.sica.dao.impl.RolDaoJdbc;
import com.zonaacme.sica.dao.impl.UsuarioDaoJdbc;
import com.zonaacme.sica.dao.impl.VisitaDaoJdbc;
import com.zonaacme.sica.pattern.observer.VisitaEventBus;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.AuthService;
import com.zonaacme.sica.service.EmpresaService;
import com.zonaacme.sica.service.IncidenteService;
import com.zonaacme.sica.service.PermisoService;
import com.zonaacme.sica.service.PersonaService;
import com.zonaacme.sica.service.ReporteService;
import com.zonaacme.sica.service.UsuarioService;
import com.zonaacme.sica.service.VisitaService;
import com.zonaacme.sica.service.impl.AuditoriaServiceImpl;
import com.zonaacme.sica.service.impl.AuthServiceImpl;
import com.zonaacme.sica.service.impl.EmpresaServiceImpl;
import com.zonaacme.sica.service.impl.IncidenteServiceImpl;
import com.zonaacme.sica.service.impl.PermisoServiceImpl;
import com.zonaacme.sica.service.impl.PersonaServiceImpl;
import com.zonaacme.sica.service.impl.ReporteServiceImpl;
import com.zonaacme.sica.service.impl.UsuarioServiceImpl;
import com.zonaacme.sica.service.impl.VisitaServiceImpl;

/**
 * Composition root de SICA (inyección manual de dependencias, sin framework).
 * <p>
 * Es un Singleton del mismo patrón #1: garantiza que todas las ventanas
 * compartan las MISMAS instancias de servicio y, sobre todo, el mismo
 * {@link VisitaEventBus}. Por eso una aprobación hecha en la ventana del
 * Funcionario llega a la ventana del Guarda sin reiniciar nada.
 * </p>
 */
public final class ServiceLocator {

    private static final ServiceLocator INSTANCE = new ServiceLocator();

    // DAOs
    private final BitacoraDao bitacoraDao = new BitacoraDaoJdbc();
    private final UsuarioDao usuarioDao = new UsuarioDaoJdbc();
    private final RolDao rolDao = new RolDaoJdbc();
    private final PermisoDao permisoDao = new PermisoDaoJdbc();
    private final EmpresaDao empresaDao = new EmpresaDaoJdbc();
    private final PersonaDao personaDao = new PersonaDaoJdbc();
    private final VisitaDao visitaDao = new VisitaDaoJdbc();
    private final IncidenteDao incidenteDao = new IncidenteDaoJdbc();
    private final TransactionRunner transactionRunner = new TransactionRunner();

    // Observer compartido por todas las ventanas
    private final VisitaEventBus eventBus = new VisitaEventBus();

    // Servicios
    private final AuditoriaService auditoriaService = new AuditoriaServiceImpl(bitacoraDao);
    private final AuthService authService = new AuthServiceImpl(usuarioDao, permisoDao, auditoriaService);
    private final PermisoService permisoService = new PermisoServiceImpl(permisoDao, rolDao, auditoriaService);
    private final UsuarioService usuarioService = new UsuarioServiceImpl(usuarioDao, rolDao, auditoriaService);
    private final PersonaService personaService =
            new PersonaServiceImpl(personaDao, empresaDao, auditoriaService, eventBus);
    private final EmpresaService empresaService = new EmpresaServiceImpl(empresaDao, auditoriaService);
    private final VisitaService visitaService = new VisitaServiceImpl(
            visitaDao, personaDao, transactionRunner, bitacoraDao, eventBus, auditoriaService);
    private final IncidenteService incidenteService =
            new IncidenteServiceImpl(incidenteDao, visitaDao, auditoriaService);
    private final ReporteService reporteService = new ReporteServiceImpl(visitaDao, auditoriaService);

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        return INSTANCE;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public PermisoService getPermisoService() {
        return permisoService;
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public PersonaService getPersonaService() {
        return personaService;
    }

    public EmpresaService getEmpresaService() {
        return empresaService;
    }

    public VisitaService getVisitaService() {
        return visitaService;
    }

    public IncidenteService getIncidenteService() {
        return incidenteService;
    }

    public ReporteService getReporteService() {
        return reporteService;
    }

    public AuditoriaService getAuditoriaService() {
        return auditoriaService;
    }

    public VisitaEventBus getEventBus() {
        return eventBus;
    }
}
