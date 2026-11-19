package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.dao.UsuarioDao;
import com.zonaacme.sica.model.entity.Rol;
import com.zonaacme.sica.model.entity.Usuario;

import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link UsuarioDao}. */
public class UsuarioDaoJdbc extends BaseDao implements UsuarioDao {

    private static final String SELECT_BASE =
            "SELECT u.id, u.nombre, u.email, u.password, u.rol_id, u.esta_activo, r.nombre_rol "
            + "FROM usuarios u LEFT JOIN roles r ON r.id = u.rol_id ";

    private static final RowMapper<Usuario> MAPEADOR = rs -> {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRolId(rs.getInt("rol_id"));
        u.setActivo(rs.getBoolean("esta_activo"));
        u.setRol(new Rol(rs.getInt("rol_id"), rs.getString("nombre_rol")));
        return u;
    };

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return consultar(SELECT_BASE + "WHERE u.email = ?", MAPEADOR, email)
                .stream().findFirst();
    }

    @Override
    public Optional<Usuario> findById(int id) {
        return consultar(SELECT_BASE + "WHERE u.id = ?", MAPEADOR, id)
                .stream().findFirst();
    }

    @Override
    public List<Usuario> findAll() {
        return consultar(SELECT_BASE + "ORDER BY u.id", MAPEADOR);
    }

    @Override
    public int insertar(Usuario u, String hashPassword) {
        return insertar(
                "INSERT INTO usuarios (nombre, email, password, rol_id, esta_activo) VALUES (?, ?, ?, ?, ?)",
                u.getNombre(), u.getEmail(), hashPassword, u.getRolId(), u.isActivo());
    }

    @Override
    public int actualizar(Usuario u, String hashPassword) {
        if (hashPassword == null || hashPassword.isBlank()) {
            return ejecutar(
                    "UPDATE usuarios SET nombre = ?, email = ?, rol_id = ?, esta_activo = ? WHERE id = ?",
                    u.getNombre(), u.getEmail(), u.getRolId(), u.isActivo(), u.getId());
        }
        return ejecutar(
                "UPDATE usuarios SET nombre = ?, email = ?, rol_id = ?, esta_activo = ?, password = ? WHERE id = ?",
                u.getNombre(), u.getEmail(), u.getRolId(), u.isActivo(), hashPassword, u.getId());
    }

    @Override
    public int eliminar(int id) {
        return ejecutar("DELETE FROM usuarios WHERE id = ?", id);
    }

    @Override
    public int cambiarEstado(int id, boolean activo) {
        return ejecutar("UPDATE usuarios SET esta_activo = ? WHERE id = ?", activo, id);
    }
}
