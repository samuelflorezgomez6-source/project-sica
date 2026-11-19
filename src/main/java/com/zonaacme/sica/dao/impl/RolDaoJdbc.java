package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.RolDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.model.entity.Permiso;
import com.zonaacme.sica.model.entity.Rol;

import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link RolDao}, incluida la asignación RBAC transaccional. */
public class RolDaoJdbc extends BaseDao implements RolDao {

    private static final RowMapper<Rol> MAPEADOR_ROL = rs ->
            new Rol(rs.getInt("id"), rs.getString("nombre_rol"));

    private static final RowMapper<Permiso> MAPEADOR_PERMISO = rs ->
            new Permiso(rs.getInt("id"), rs.getString("nombre_permiso"), rs.getString("descripcion"));

    @Override
    public List<Rol> findAll() {
        return consultar("SELECT id, nombre_rol FROM roles ORDER BY id", MAPEADOR_ROL);
    }

    @Override
    public Optional<Rol> findById(int id) {
        return consultar("SELECT id, nombre_rol FROM roles WHERE id = ?", MAPEADOR_ROL, id)
                .stream().findFirst();
    }

    @Override
    public List<Permiso> findPermisos(int rolId) {
        return consultar(
                "SELECT p.id, p.nombre_permiso, p.descripcion FROM permisos p "
                + "JOIN rol_permisos rp ON rp.permiso_id = p.id WHERE rp.rol_id = ? ORDER BY p.id",
                MAPEADOR_PERMISO, rolId);
    }

    @Override
    public void reemplazarPermisos(int rolId, List<Integer> permisoIds) {
        enTransaccion(c -> {
            ejecutarEn(c, "DELETE FROM rol_permisos WHERE rol_id = ?", rolId);
            for (Integer permisoId : permisoIds) {
                ejecutarEn(c, "INSERT INTO rol_permisos (rol_id, permiso_id) VALUES (?, ?)",
                        rolId, permisoId);
            }
        });
    }
}
