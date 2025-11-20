package com.smartoccupation.modelo;

// ... imports (si los hay)
/**
 * Representa un estado de cobro para un alquiler. Corresponde a la tabla
 * "estados_cobro".
 */
public class EstadoCobro {

    private Integer id_estado; // Usar Integer para permitir NULL en objetos nuevos
    private String nombre_estado; // "pagado", "pendiente", "retrasado", etc.

    public EstadoCobro() {
    }

    public EstadoCobro(Integer id_estado, String nombre_estado) {
        this.id_estado = id_estado;
        setNombre_estado(nombre_estado);
    }

    // Getters y Setters
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
        // Validación corregida: Solo verificamos que no sea nulo/vacío y limpiamos espacios.
        if (nombre_estado == null || nombre_estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado no puede ser nulo o vacío.");
        }
        // Eliminamos la restricción a solo "pagado", "pendiente", "retrasado".
        this.nombre_estado = nombre_estado.trim();
    }

    // Método para ser usado por el JComboBox y el Servicio (más limpio)
    public String getNombre() {
        return nombre_estado;
    }

    // Sobrescribir equals y hashCode es CRUCIAL si esta clase se usa en JComboBox
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        // Comparamos por clase y si el ID es igual (es la PK)
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EstadoCobro that = (EstadoCobro) o;
        // Comparación segura para IDs autoincrementales
        return id_estado != null && id_estado.equals(that.id_estado);
    }

    @Override
    public int hashCode() {
        return id_estado != null ? id_estado.hashCode() : 0;
    }

    @Override
    public String toString() {
        return nombre_estado; // Usamos solo el nombre para que se vea bien en la UI (JComboBox)
    }
}
