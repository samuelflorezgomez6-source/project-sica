package com.zonaacme.sica.dao.impl;

import com.zonaacme.sica.dao.BaseDao;
import com.zonaacme.sica.dao.PersonaDao;
import com.zonaacme.sica.dao.RowMapper;
import com.zonaacme.sica.model.entity.Persona;
import com.zonaacme.sica.model.enums.TipoPersona;

import java.util.List;
import java.util.Optional;

/** Implementación JDBC de {@link PersonaDao}. */
public class PersonaDaoJdbc extends BaseDao implements PersonaDao {

    private static final String SELECT_BASE =
            "SELECT p.id, p.nombre, p.documento_identidad, p.empresa_id, p.tipo_persona, "
            + "p.estado_acceso_id, p.url_foto, e.nombre AS nombre_empresa, ea.nombre_estado "
            + "FROM personas p "
            + "LEFT JOIN empresas e ON e.id = p.empresa_id "
            + "LEFT JOIN persona_estados_acceso ea ON ea.id = p.estado_acceso_id ";

    private static final RowMapper<Persona> MAPEADOR = rs -> {
        Persona p = new Persona();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setDocumentoIdentidad(rs.getString("documento_identidad"));
        p.setEmpresaId(rs.getInt("empresa_id"));
        p.setTipoPersona(TipoPersona.from(rs.getString("tipo_persona")));
        p.setEstadoAccesoId(rs.getInt("estado_acceso_id"));
        p.setUrlFoto(rs.getString("url_foto"));
        p.setNombreEmpresa(rs.getString("nombre_empresa"));
        p.setNombreEstado(rs.getString("nombre_estado"));
        return p;
    };

    @Override
    public List<Persona> findAll() {
        return consultar(SELECT_BASE + "ORDER BY p.id", MAPEADOR);
    }

    @Override
    public Optional<Persona> findById(int id) {
        return consultar(SELECT_BASE + "WHERE p.id = ?", MAPEADOR, id).stream().findFirst();
    }

    @Override
    public Optional<Persona> findByDocumento(String documento) {
        return consultar(SELECT_BASE + "WHERE p.documento_identidad = ?", MAPEADOR, documento)
                .stream().findFirst();
    }

    @Override
    public List<Persona> findByEmpresa(int empresaId) {
        return consultar(SELECT_BASE + "WHERE p.empresa_id = ? ORDER BY p.nombre", MAPEADOR, empresaId);
    }

    @Override
    public int insertar(Persona p) {
        return insertar(
                "INSERT INTO personas (nombre, documento_identidad, empresa_id, tipo_persona, "
                + "estado_acceso_id, url_foto) VALUES (?, ?, ?, ?, ?, ?)",
                p.getNombre(), p.getDocumentoIdentidad(), p.getEmpresaId(),
                p.getTipoPersona().getEtiqueta(), p.getEstadoAccesoId(), p.getUrlFoto());
    }

    @Override
    public int actualizar(Persona p) {
        return ejecutar(
                "UPDATE personas SET nombre = ?, documento_identidad = ?, empresa_id = ?, "
                + "tipo_persona = ?, estado_acceso_id = ?, url_foto = ? WHERE id = ?",
                p.getNombre(), p.getDocumentoIdentidad(), p.getEmpresaId(),
                p.getTipoPersona().getEtiqueta(), p.getEstadoAccesoId(), p.getUrlFoto(), p.getId());
    }

    @Override
    public int eliminar(int id) {
        return ejecutar("DELETE FROM personas WHERE id = ?", id);
    }

    @Override
    public int cambiarEstadoAcceso(int personaId, int estadoId) {
        return ejecutar("UPDATE personas SET estado_acceso_id = ? WHERE id = ?", estadoId, personaId);
    }
}
