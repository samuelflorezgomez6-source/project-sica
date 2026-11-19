package com.zonaacme.sica.dao;

/**
 * Expone la plantilla transaccional de {@link BaseDao} a la capa de servicio.
 * Permite que una regularización (cerrar visita + abrir visita + auditar)
 * se ejecute como una única unidad atómica.
 */
public class TransactionRunner extends BaseDao {

    public void ejecutar(SqlAction accion) {
        enTransaccion(accion);
    }
}
