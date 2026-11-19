package com.zonaacme.sica.model.enums;

/** Los cuatro flujos de acceso documentados en el requerimiento. */
public enum TipoFlujoVisita {
    PRE_REGISTRADA("Invitado pre-registrado"),
    NO_ANUNCIADA("Invitado no anunciado"),
    CARNET_OLVIDADO("Carnet olvidado (pase temporal)"),
    SALIDA_OLVIDADA("Salida olvidada (regularización)");

    private final String etiqueta;

    TipoFlujoVisita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
