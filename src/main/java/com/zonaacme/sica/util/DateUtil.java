package com.zonaacme.sica.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/** Formateo consistente de fechas en tablas, reportes y bitácora. */
public final class DateUtil {

    public static final DateTimeFormatter FORMATO_TABLA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private DateUtil() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now();
    }

    public static Timestamp aTimestamp(LocalDateTime fecha) {
        return fecha == null ? null : Timestamp.valueOf(fecha);
    }

    public static String formatear(LocalDateTime fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO_TABLA);
    }

    public static String formatear(Timestamp fecha) {
        if (fecha == null) {
            return "-";
        }
        LocalDateTime local = LocalDateTime.ofInstant(fecha.toInstant(), ZoneId.systemDefault());
        return local.format(FORMATO_TABLA);
    }
}
