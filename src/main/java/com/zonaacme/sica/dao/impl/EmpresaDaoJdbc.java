package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.EmpresaDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.model.entity.Empresa;

import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link EmpresaDao}. */
public class EmpresaDaoJdbc extends BaseDao implements EmpresaDao {

    private static final RowMapper<Empresa> MAPEADOR = rs -> new Empresa(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("contacto_principal"));

    @Override
    public List<Empresa> findAll() {
        return consultar("SELECT id, nombre, contacto_principal FROM empresas ORDER BY nombre", MAPEADOR);
    }

    @Override
    public Optional<Empresa> findById(int id) {
        return consultar("SELECT id, nombre, contacto_principal FROM empresas WHERE id = ?", MAPEADOR, id)
                .stream().findFirst();
    }

    @Override
    public int insertar(Empresa empresa) {
        return insertar("INSERT INTO empresas (nombre, contacto_principal) VALUES (?, ?)",
                empresa.getNombre(), empresa.getContactoPrincipal());
    }

    @Override
    public int actualizar(Empresa empresa) {
        return ejecutar("UPDATE empresas SET nombre = ?, contacto_principal = ? WHERE id = ?",
                empresa.getNombre(), empresa.getContactoPrincipal(), empresa.getId());
    }

    @Override
    public int eliminar(int id) {
        return ejecutar("DELETE FROM empresas WHERE id = ?", id);
    }
}
