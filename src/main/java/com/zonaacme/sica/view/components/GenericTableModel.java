package com.zonaacme.sica.view.components;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Modelo de tabla reutilizable: recibe las columnas y una lambda por columna.
 * Evita repetir cinco clases de modelos y hace evidente el uso de Streams/Lambdas.
 */
public class GenericTableModel<T> extends AbstractTableModel {

    private final String[] columnas;
    private final List<Function<T, Object>> extractores;
    private List<T> datos = new ArrayList<>();

    @SafeVarargs
    public GenericTableModel(String[] columnas, Function<T, Object>... extractores) {
        this.columnas = columnas;
        this.extractores = List.of(extractores);
    }

    public void setDatos(List<T> datos) {
        this.datos = datos == null ? new ArrayList<>() : new ArrayList<>(datos);
        fireTableDataChanged();
    }

    public T getFila(int fila) {
        return datos.get(fila);
    }

    public List<T> getDatos() {
        return datos;
    }

    @Override
    public int getRowCount() {
        return datos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        return extractores.get(columna).apply(datos.get(fila));
    }
}
