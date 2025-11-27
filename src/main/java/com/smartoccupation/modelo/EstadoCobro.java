package com.smartoccupation.modelo;

/**
 * Representa el estado actual de un cobro o factura.
 * <p>
 * Los estados típicos de cobro incluyen:
 * <ul>
 * <li>pagado</li>
 * <li>pendiente</li>
 * <li>retrasado</li>
 * </ul>
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class EstadoCobro {

    /**
     * Identificador único del estado de cobro.
     */
    private Integer idEstado;
    /**
     * Nombre descriptivo del estado de cobro (ej: pagado, pendiente, retrasado).
     */
    private String nombreEstado; // pagado | pendiente | retrasado

    /**
     * Constructor vacío por defecto.
     */
    public EstadoCobro() {}

    /**
     * Constructor para inicializar un objeto EstadoCobro con su ID y nombre.
     *
     * @param idEstado Identificador único del estado.
     * @param nombreEstado Nombre descriptivo del estado (ej: "pagado").
     */
    public EstadoCobro(Integer id_estado, String nombre_estado) {
        this.idEstado = id_estado;
        this.nombreEstado = nombre_estado;
    }

    /**
     * Obtiene el identificador del estado de cobro.
     *
     * @return El ID del estado.
     */
    public Integer getIdEstado() {
        return idEstado;
    }

    /**
     * Establece el identificador del estado de cobro.
     *
     * @param id_estado El nuevo ID del estado.
     */
    public void setIdEstado(Integer id_estado) {
        this.idEstado = id_estado;
    }

    /**
     * Obtiene el nombre descriptivo del estado de cobro.
     *
     * @return El nombre del estado (ej: "pendiente").
     */
    public String getNombreEstado() {
        return nombreEstado;
    }

    /**
     * Establece el nombre descriptivo del estado de cobro.
     *
     * @param nombre_estado El nuevo nombre del estado.
     */
    public void setNombreEstado(String nombre_estado) {
        this.nombreEstado = nombre_estado;
    }

    /**
     * Devuelve una representación en cadena del objeto {@code EstadoCobro},
     * que es simplemente el nombre del estado.
     *
     * @return El nombre del estado de cobro.
     */
    @Override
    public String toString() {
        return nombreEstado;
    }
}