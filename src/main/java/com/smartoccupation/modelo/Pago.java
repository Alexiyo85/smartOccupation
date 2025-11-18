package com.smartoccupation.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un pago de un alquiler.
 * Corresponde a la tabla "pagos".
 */
public class Pago {

    private int id_pago;                // PK autogenerado
    private int numero_expediente;      // FK a Alquiler
    private LocalDate fecha_pago;       // Fecha del pago
    private BigDecimal cantidad;        // Monto pagado

    public Pago() { }

    public Pago(int id_pago, int numero_expediente, LocalDate fecha_pago, BigDecimal cantidad) {
        this.id_pago = id_pago;
        this.numero_expediente = numero_expediente;
        this.fecha_pago = fecha_pago;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public int getId_pago() { return id_pago; }
    public void setId_pago(int id_pago) { this.id_pago = id_pago; }

    public int getNumero_expediente() { return numero_expediente; }
    public void setNumero_expediente(int numero_expediente) { this.numero_expediente = numero_expediente; }

    public LocalDate getFecha_pago() { return fecha_pago; }
    public void setFecha_pago(LocalDate fecha_pago) { this.fecha_pago = fecha_pago; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id_pago=" + id_pago +
                ", numero_expediente=" + numero_expediente +
                ", fecha_pago=" + fecha_pago +
                ", cantidad=" + cantidad +
                '}';
    }
}

