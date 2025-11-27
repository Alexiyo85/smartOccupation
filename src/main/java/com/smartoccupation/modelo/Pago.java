package com.smartoccupation.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un registro de pago asociado a un número de expediente.
 * <p>
 * Utiliza {@link java.math.BigDecimal} para la cantidad para asegurar
 * la precisión en las operaciones monetarias y {@link java.time.LocalDate}
 * para manejar la fecha del pago.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class Pago {

    /**
     * Identificador único del pago.
     */
    private Integer idPago;
    /**
     * Número del expediente al que está asociado este pago.
     */
    private int numeroExpediente;
    /**
     * Fecha en la que se realizó el pago.
     */
    private LocalDate fechaPago;
    /**
     * Cantidad monetaria del pago. Se usa BigDecimal para precisión.
     */
    private BigDecimal cantidad;

    /**
     * Constructor vacío por defecto.
     */
    public Pago() {}

    /**
     * Constructor para inicializar todos los atributos de un pago.
     *
     * @param idPago Identificador único del pago.
     * @param numeroExpediente Número del expediente asociado al pago.
     * @param fechaPago Fecha en la que se realizó el pago.
     * @param cantidad Cantidad monetaria del pago.
     */
    public Pago(Integer idPago, int numeroExpediente, LocalDate fechaPago, BigDecimal cantidad) {
        this.idPago = idPago;
        this.numeroExpediente = numeroExpediente;
        this.fechaPago = fechaPago;
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el identificador único del pago.
     *
     * @return El ID del pago.
     */
    public Integer getIdPago() {
        return idPago;
    }

    /**
     * Establece el identificador único del pago.
     *
     * @param idPago El nuevo ID del pago.
     */
    public void setId_pago(Integer idPago) {
        this.idPago = idPago;
    }

    /**
     * Obtiene el número del expediente asociado al pago.
     *
     * @return El número de expediente.
     */
    public int getNumeroExpediente() {
        return numeroExpediente;
    }

    /**
     * Establece el número del expediente asociado al pago.
     *
     * @param numeroExpediente El nuevo número de expediente.
     */
    public void setNumeroExpediente(int numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    /**
     * Obtiene la fecha en la que se realizó el pago.
     *
     * @return La fecha del pago.
     */
    public LocalDate getFechaPago() {
        return fechaPago;
    }

    /**
     * Establece la fecha en la que se realizó el pago.
     *
     * @param fechaPago La nueva fecha del pago.
     */
    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Obtiene la cantidad monetaria del pago.
     *
     * @return La cantidad pagada en formato {@code BigDecimal}.
     */
    public BigDecimal getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad monetaria del pago.
     *
     * @param cantidad La nueva cantidad del pago.
     */
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}