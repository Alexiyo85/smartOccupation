package com.smartoccupation.modelo;

/**
 * Representa un estado de cobro para un alquiler.
 * Corresponde a la tabla "estados_cobro".
 */
public class EstadoCobro {

    private int id_estado;             // PK autogenerado
    private String nombre_estado;      // "pagado", "pendiente", "retrasado"

    public EstadoCobro() { }

    public EstadoCobro(int id_estado, String nombre_estado) {
        this.id_estado = id_estado;
        setNombre_estado(nombre_estado);
    }

    // Getters y Setters
    public int getId_estado() { return id_estado; }
    public void setId_estado(int id_estado) { this.id_estado = id_estado; }

    public String getNombre_estado() { return nombre_estado; }
    public void setNombre_estado(String nombre_estado) {
        if (nombre_estado == null ||
                (!nombre_estado.equalsIgnoreCase("pagado") &&
                        !nombre_estado.equalsIgnoreCase("pendiente") &&
                        !nombre_estado.equalsIgnoreCase("retrasado")))
            throw new IllegalArgumentException("Estado inválido");
        this.nombre_estado = nombre_estado.toLowerCase();
    }

    @Override
    public String toString() {
        return "EstadoCobro{" +
                "id_estado=" + id_estado +
                ", nombre_estado='" + nombre_estado + '\'' +
                '}';
    }
}
