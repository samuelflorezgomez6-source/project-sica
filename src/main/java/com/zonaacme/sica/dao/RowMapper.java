package com.zonaacme.sica.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PATRÓN DAO - Mapeador funcional de filas.
 * Permite escribir los mapeos como lambdas y reutilizar la lógica JDBC en BaseDao.
 */
@FunctionalInterface
public interface RowMapper<T> {
    T map(ResultSet rs) throws SQLException;
}
