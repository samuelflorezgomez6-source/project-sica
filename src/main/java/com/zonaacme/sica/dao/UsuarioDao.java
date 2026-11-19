package com.zonaacme.sica.dao;

import com.zonaacme.sica.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

/** Contrato de persistencia de usuarios (PATRÓN DAO). */
public interface UsuarioDao {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(int id);

    List<Usuario> findAll();

    int insertar(Usuario usuario, String hashPassword);

    int actualizar(Usuario usuario, String hashPassword);

    int eliminar(int id);

    int cambiarEstado(int id, boolean activo);
}
