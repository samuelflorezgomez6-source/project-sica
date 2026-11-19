package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Visita;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de visitas (operación central de SICA). */
public interface VisitaDao {

    List<Visita> findAll();

    List<Visita> findPendientes();

    List<Visita> findDentro();

    List<Visita> findByPersona(int personaId);

    Optional<Visita> findAbiertaByPersona(int personaId);

    Optional<Visita> findById(int id);

    int insertar(Visita visita);

    int insertarEn(Connection c, Visita visita) throws SQLException;

    int actualizarEstado(int visitaId, int estadoId, Integer aprobadaPor);

    int registrarCheckIn(int visitaId, LocalDateTime entrada, int estadoId);

    int registrarCheckOut(int visitaId, LocalDateTime salida, int estadoId);

    int cerrarEn(Connection c, int visitaId, LocalDateTime salida, int estadoId) throws SQLException;
}
