package com.smartoccupation.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago {

    private Integer id_pago;
    private int numero_expediente;
    private LocalDate fecha_pago;
    private BigDecimal cantidad;

    public Pago() {}

    public Pago(Integer id_pago, int numero_expediente, LocalDate fecha_pago, BigDecimal cantidad) {
        this.id_pago = id_pago;
        this.numero_expediente = numero_expediente;
        this.fecha_pago = fecha_pago;
        this.cantidad = cantidad;
    }

    public Integer getId_pago() {
        return id_pago;
    }

    public void setId_pago(Integer id_pago) {
        this.id_pago = id_pago;
    }

    public int getNumero_expediente() {
        return numero_expediente;
    }

    public void setNumero_expediente(int numero_expediente) {
        this.numero_expediente = numero_expediente;
    }

    public LocalDate getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(LocalDate fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}
