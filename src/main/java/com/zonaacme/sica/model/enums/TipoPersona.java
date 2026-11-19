package com.zonaacme.sica.model.enums;

/** Coincide exactamente con el ENUM de la columna {@code personas.tipo_persona}. */
public enum TipoPersona {
    TRABAJADOR("Trabajador"),
    INVITADO("Invitado");

    private final String etiqueta;

    TipoPersona(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static TipoPersona from(String texto) {
        for (TipoPersona tipo : values()) {
            if (tipo.etiqueta.equalsIgnoreCase(texto)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de persona desconocido: " + texto);
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
