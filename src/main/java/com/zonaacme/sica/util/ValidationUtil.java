package com.zonaacme.sica.util;

import com.zonaacme.sica.exception.ValidacionException;

import java.util.regex.Pattern;

/** Validaciones reutilizables de entrada. Fallan con un mensaje claro para el usuario. */
public final class ValidationUtil {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern DOCUMENTO = Pattern.compile("^[A-Za-z0-9\\-]{4,20}$");

    private ValidationUtil() {
    }

    public static String requerir(String valor, String nombreCampo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("El campo '" + nombreCampo + "' es obligatorio.");
        }
        return valor.trim();
    }

    public static void validarEmail(String email) {
        requerir(email, "email");
        if (!EMAIL.matcher(email.trim()).matches()) {
            throw new ValidacionException("El email no tiene un formato válido.");
        }
    }

    public static void validarDocumento(String documento) {
        requerir(documento, "documento de identidad");
        if (!DOCUMENTO.matcher(documento.trim()).matches()) {
            throw new ValidacionException("El documento debe tener entre 4 y 20 caracteres alfanuméricos.");
        }
    }

    public static Integer requerirId(Integer id, String nombreCampo) {
        if (id == null || id <= 0) {
            throw new ValidacionException("Debe seleccionar un " + nombreCampo + " válido.");
        }
        return id;
    }
}
