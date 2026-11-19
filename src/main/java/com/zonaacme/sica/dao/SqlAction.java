package com.zonaacme.sica.dao;

import java.sql.Connection;
import java.sql.SQLException;

/** Acción SQL que se ejecuta dentro de una transacción (BaseDao.enTransaccion). */
@FunctionalInterface
public interface SqlAction {
    void ejecutar(Connection connection) throws SQLException;
}
