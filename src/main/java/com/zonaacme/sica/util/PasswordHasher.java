package com.zonaacme.sica.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad de seguridad: hash y verificación de contraseñas con BCrypt.
 * <p>
 * BCrypt incluye salt aleatorio automático, por lo que dos passwords iguales
 * producen hashes distintos. Nunca se almacena texto plano en la BD.
 * </p>
 */
public final class PasswordHasher {

    private static final int COSTE = 10;

    private PasswordHasher() {
    }

    public static String hash(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt(COSTE));
    }

    public static boolean verificar(String passwordPlano, String hashAlmacenado) {
        if (passwordPlano == null || hashAlmacenado == null || hashAlmacenado.isBlank()) {
            return false;
        }
        return BCrypt.checkpw(passwordPlano, hashAlmacenado);
    }
}
