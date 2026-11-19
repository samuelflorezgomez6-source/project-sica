package com.zonaacme.sica.controller;

import com.zonaacme.sica.config.ServiceLocator;
import com.zonaacme.sica.exception.SicaException;
import com.zonaacme.sica.service.AuditoriaService;
import com.zonaacme.sica.service.AuthService;
import com.zonaacme.sica.service.EmpresaService;
import com.zonaacme.sica.service.IncidenteService;
import com.zonaacme.sica.service.PermisoService;
import com.zonaacme.sica.service.PersonaService;
import com.zonaacme.sica.service.ReporteService;
import com.zonaacme.sica.service.UsuarioService;
import com.zonaacme.sica.service.VisitaService;

/**
 * Controlador fachada (MVC): la vista SOLO llama a estos métodos.
 * Traduce excepciones de negocio a un {@link Resultado} que la vista muestra,
 * de modo que ningún panel contenga lógica de negocio.
 */
public final class SicaController {

    private final ServiceLocator servicios = ServiceLocator.getInstance();

    public AuthService auth() {
        return servicios.getAuthService();
    }

    public PermisoService permisos() {
        return servicios.getPermisoService();
    }

    public UsuarioService usuarios() {
        return servicios.getUsuarioService();
    }

    public PersonaService personas() {
        return servicios.getPersonaService();
    }

    public EmpresaService empresas() {
        return servicios.getEmpresaService();
    }

    public VisitaService visitas() {
        return servicios.getVisitaService();
    }

    public IncidenteService incidentes() {
        return servicios.getIncidenteService();
    }

    public ReporteService reportes() {
        return servicios.getReporteService();
    }

    public AuditoriaService auditoria() {
        return servicios.getAuditoriaService();
    }

    /** Ejecuta una acción de negocio y devuelve un resultado apto para la vista. */
    public Resultado ejecutar(Accion accion) {
        try {
            accion.ejecutar();
            return Resultado.ok("Operación realizada correctamente.");
        } catch (SicaException e) {
            return Resultado.error(e.getMessage());
        } catch (RuntimeException e) {
            return Resultado.error("Error inesperado: " + e.getMessage());
        }
    }

    @FunctionalInterface
    public interface Accion {
        void ejecutar();
    }

    public record Resultado(boolean ok, String mensaje) {
        public static Resultado ok(String mensaje) {
            return new Resultado(true, mensaje);
        }

        public static Resultado error(String mensaje) {
            return new Resultado(false, mensaje);
        }
    }
}
