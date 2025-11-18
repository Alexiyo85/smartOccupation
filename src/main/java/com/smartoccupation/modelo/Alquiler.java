package com.smartoccupation.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase que representa un alquiler de vivienda.
 * Contiene todos los atributos de la tabla alquileres de la BBDD.
 * Incluye validaciones internas para garantizar consistencia.
 */
public class Alquiler {

    // -------------------------------
    // Atributos privados
    // -------------------------------
    private int numero_expediente;             // Autogenerado en BBDD
    private LocalDate fecha_inicio;            // Fecha de inicio del alquiler
    private int tiempo_meses;                  // Tiempo estimado en meses
    private int tiempo_dias;                   // Tiempo estimado en días
    private LocalDate fecha_fin_estimada;      // Calculada automáticamente
    private BigDecimal precio_total_estimado;  // Debe ser >= 0

    private int id_cliente;                    // FK a Cliente
    private int id_vivienda;                   // FK a Vivienda
    private int id_estado_cobro;               // FK a EstadoCobro
    private String estado;                     // Estado "Activo", "Finalizado", etc.

    private Cliente cliente;                   // Objeto cliente asociado
    private Vivienda vivienda;                 // Objeto vivienda asociado

    // -------------------------------
    // Constructores
    // -------------------------------
    public Alquiler() { }

    public Alquiler(int numero_expediente, LocalDate fecha_inicio, int tiempo_meses, int tiempo_dias,
                    LocalDate fecha_fin_estimada, BigDecimal precio_total_estimado,
                    int id_cliente, int id_vivienda, int id_estado_cobro) {

        setNumero_expediente(numero_expediente);
        setFecha_inicio(fecha_inicio);
        setTiempo_meses(tiempo_meses);
        setTiempo_dias(tiempo_dias);
        setFecha_fin_estimada(fecha_fin_estimada);
        setPrecio_total_estimado(precio_total_estimado);
        setId_cliente(id_cliente);
        setId_vivienda(id_vivienda);
        setId_estado_cobro(id_estado_cobro);
    }

    // -------------------------------
    // Getters y Setters
    // -------------------------------
    public int getNumero_expediente() { return numero_expediente; }
    public void setNumero_expediente(int numero_expediente) {
        if (numero_expediente < 0)
            throw new IllegalArgumentException("El número de expediente no puede ser negativo");
        this.numero_expediente = numero_expediente;
    }

    public LocalDate getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(LocalDate fecha_inicio) {
        if (fecha_inicio == null)
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        this.fecha_inicio = fecha_inicio;
        calcularFechaFin();
    }

    public int getTiempo_meses() { return tiempo_meses; }
    public void setTiempo_meses(int tiempo_meses) {
        if (tiempo_meses < 0)
            throw new IllegalArgumentException("El tiempo en meses no puede ser negativo");
        this.tiempo_meses = tiempo_meses;
        calcularFechaFin();
    }

    public int getTiempo_dias() { return tiempo_dias; }
    public void setTiempo_dias(int tiempo_dias) {
        if (tiempo_dias < 0)
            throw new IllegalArgumentException("El tiempo en días no puede ser negativo");
        this.tiempo_dias = tiempo_dias;
        calcularFechaFin();
    }

    public LocalDate getFecha_fin_estimada() { return fecha_fin_estimada; }
    public void setFecha_fin_estimada(LocalDate fecha_fin_estimada) {
        if (fecha_fin_estimada != null && fecha_inicio != null &&
                fecha_fin_estimada.isBefore(fecha_inicio)) {
            throw new IllegalArgumentException("La fecha fin estimada no puede ser anterior a la fecha de inicio");
        }
        this.fecha_fin_estimada = fecha_fin_estimada;
    }

    public BigDecimal getPrecio_total_estimado() { return precio_total_estimado; }
    public void setPrecio_total_estimado(BigDecimal precio_total_estimado) {
        if (precio_total_estimado != null && precio_total_estimado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio total estimado no puede ser negativo");
        }
        this.precio_total_estimado = precio_total_estimado;
    }

    public int getId_cliente() { return id_cliente; }
    public void setId_cliente(int id_cliente) {
        if (id_cliente <= 0)
            throw new IllegalArgumentException("El id del cliente debe ser mayor que 0");
        this.id_cliente = id_cliente;
    }

    public int getId_vivienda() { return id_vivienda; }
    public void setId_vivienda(int id_vivienda) {
        if (id_vivienda <= 0)
            throw new IllegalArgumentException("El id de la vivienda debe ser mayor que 0");
        this.id_vivienda = id_vivienda;
    }

    public int getId_estado_cobro() { return id_estado_cobro; }
    public void setId_estado_cobro(int id_estado_cobro) {
        if (id_estado_cobro <= 0)
            throw new IllegalArgumentException("El id del estado de cobro debe ser mayor que 0");
        this.id_estado_cobro = id_estado_cobro;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) this.id_cliente = cliente.getId_cliente();
    }

    public Vivienda getVivienda() { return vivienda; }
    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
        if (vivienda != null) this.id_vivienda = vivienda.getId_vivienda();
    }

    // Método auxiliar para mostrar nombre completo del cliente
    public String getNombreCliente() {
        if (cliente != null) {
            return cliente.getNombre() + " " + cliente.getPrimer_apellido() + " " + cliente.getSegundo_apellido();
        }
        return "";
    }

    // -------------------------------
    // Métodos auxiliares
    // -------------------------------
    public void calcularFechaFin() {
        if (fecha_inicio != null) {
            this.fecha_fin_estimada = fecha_inicio.plusMonths(tiempo_meses).plusDays(tiempo_dias);
        }
    }

    public void calcularPrecioTotal(BigDecimal precioDiario) {
        if (precioDiario == null || precioDiario.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El precio diario debe ser mayor o igual a cero");
        int totalDias = tiempo_dias + tiempo_meses * 30; // Aproximación: mes = 30 días
        this.precio_total_estimado = precioDiario.multiply(BigDecimal.valueOf(totalDias));
    }

    // -------------------------------
    // toString
    // -------------------------------
    @Override
    public String toString() {
        return "Alquiler{" +
                "numero_expediente=" + numero_expediente +
                ", fecha_inicio=" + fecha_inicio +
                ", tiempo_meses=" + tiempo_meses +
                ", tiempo_dias=" + tiempo_dias +
                ", fecha_fin_estimada=" + fecha_fin_estimada +
                ", precio_total_estimado=" + precio_total_estimado +
                ", id_cliente=" + id_cliente +
                ", id_vivienda=" + id_vivienda +
                ", id_estado_cobro=" + id_estado_cobro +
                ", estado='" + estado + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNombre() + " " + cliente.getPrimer_apellido() + " " + cliente.getSegundo_apellido() : "null") +
                ", vivienda=" + (vivienda != null ? vivienda.getCodigo_referencia() : "null") +
                '}';
    }
}
