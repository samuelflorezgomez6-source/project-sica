package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.PermisoDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.model.entity.Permiso;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Implementación JDBC de {@link PermisoDao}. */
public class PermisoDaoJdbc extends BaseDao implements PermisoDao {

    private static final RowMapper<Permiso> MAPEADOR = rs ->
            new Permiso(rs.getInt("id"), rs.getString("nombre_permiso"), rs.getString("descripcion"));

    @Override
    public List<Permiso> findAll() {
        return consultar("SELECT id, nombre_permiso, descripcion FROM permisos ORDER BY id", MAPEADOR);
    }

    @Override
    public Set<String> findNombresPorRol(int rolId) {
        List<String> nombres = consultar(
                "SELECT p.nombre_permiso FROM permisos p "
                + "JOIN rol_permisos rp ON rp.permiso_id = p.id WHERE rp.rol_id = ?",
                rs -> rs.getString("nombre_permiso"), rolId);
        return new LinkedHashSet<>(nombres);
    }
}
