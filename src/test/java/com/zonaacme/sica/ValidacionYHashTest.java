package com.zonaacme.sica;

import com.zonaacme.sica.exception.ValidacionException;
import com.zonaacme.sica.util.PasswordHasher;
import com.zonaacme.sica.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Validaciones de entrada y hashing BCrypt (sin base de datos). */
class ValidacionYHashTest {

    @Test
    void requerirRechazaCamposVacios() {
        assertThrows(ValidacionException.class, () -> ValidationUtil.requerir("  ", "nombre"));
    }

    @Test
    void validaFormatoDeEmailYDocumento() {
        assertDoesNotThrow(() -> ValidationUtil.validarEmail("guarda@zonaacme.com"));
        assertThrows(ValidacionException.class, () -> ValidationUtil.validarEmail("correo-invalido"));
        assertDoesNotThrow(() -> ValidationUtil.validarDocumento("10000003"));
        assertThrows(ValidacionException.class, () -> ValidationUtil.validarDocumento("abc!"));
    }

    @Test
    void requerirIdRechazaNulosYCeros() {
        assertThrows(ValidacionException.class, () -> ValidationUtil.requerirId(null, "visita"));
        assertThrows(ValidacionException.class, () -> ValidationUtil.requerirId(0, "visita"));
        assertEquals(5, ValidationUtil.requerirId(5, "visita"));
    }

    @Test
    void bcryptGeneraHashDistintoYPuedeVerificarlo() {
        String hash = PasswordHasher.hash("Acme2024!");

        assertNotEquals("Acme2024!", hash);
        assertTrue(PasswordHasher.verificar("Acme2024!", hash));
        assertFalse(PasswordHasher.verificar("ContraseñaIncorrecta", hash));
    }
}
