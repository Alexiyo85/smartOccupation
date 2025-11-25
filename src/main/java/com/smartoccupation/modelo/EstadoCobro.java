package com.smartoccupation.modelo;

public class EstadoCobro {

    private Integer id_estado;
    private String nombre_estado; // pagado | pendiente | retrasado

    public EstadoCobro() {}

    public EstadoCobro(Integer id_estado, String nombre_estado) {
        this.id_estado = id_estado;
        this.nombre_estado = nombre_estado;
    }

    public Integer getId_estado() {
        return id_estado;
    }

    public void setId_estado(Integer id_estado) {
        this.id_estado = id_estado;
    }

    public String getNombre_estado() {
        return nombre_estado;
    }

    public void setNombre_estado(String nombre_estado) {
        this.nombre_estado = nombre_estado;
    }

    @Override
    public String toString() {
        return nombre_estado;
    }
}
