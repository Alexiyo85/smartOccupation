package com.smartoccupation.modelo;

import java.math.BigDecimal;

/**
 * Clase que representa una vivienda en SmartOccupation.
 * Corresponde a la tabla "viviendas" en la base de datos.
 * Incluye validaciones internas para asegurar consistencia de los datos.
 */
public class Vivienda {

    // -------------------------------
    // Atributos privados
    // -------------------------------
    private int id_vivienda;               // PK, autogenerado
    private String codigo_referencia;      // Código único de la vivienda
    private String direccion;              // Dirección completa
    private String ciudad;                 // Ciudad
    private String provincia;              // Provincia
    private String codigo_postal;          // Solo números
    private int metros_cuadrados;          // ≥ 0
    private int numero_habitaciones;       // ≥ 0
    private int numero_banios;             // ≥ 0
    private BigDecimal precio_mensual;     // ≥ 0
    private String estado;                 // "disponible", "reservado" o "ocupado"

    // -------------------------------
    // Constructor vacío
    // -------------------------------
    public Vivienda() {
        // Permite crear un objeto vacío y setear atributos después
    }

    // -------------------------------
    // Constructor completo
    // -------------------------------
    public Vivienda(int id_vivienda, String codigo_referencia, String direccion,
                    String ciudad, String provincia, String codigo_postal,
                    int metros_cuadrados, int numero_habitaciones, int numero_banios,
                    BigDecimal precio_mensual, String estado) {

        this.id_vivienda = id_vivienda;

        // Usamos setters para aplicar validaciones internas
        setCodigo_referencia(codigo_referencia);
        setDireccion(direccion);
        setCiudad(ciudad);
        setProvincia(provincia);
        setCodigo_postal(codigo_postal);
        setMetros_cuadrados(metros_cuadrados);
        setNumero_habitaciones(numero_habitaciones);
        setNumero_banios(numero_banios);
        setPrecio_mensual(precio_mensual);
        setEstado(estado);
    }

    // -------------------------------
    // Getters y Setters con validaciones
    // -------------------------------

    public int getId_vivienda() {
        return id_vivienda;
    }

    public void setId_vivienda(int id_vivienda) {
        if (id_vivienda < 0)
            throw new IllegalArgumentException("El id de la vivienda no puede ser negativo");
        this.id_vivienda = id_vivienda;
    }

    public String getCodigo_referencia() {
        return codigo_referencia;
    }

    public void setCodigo_referencia(String codigo_referencia) {
        if (codigo_referencia == null || codigo_referencia.isBlank())
            throw new IllegalArgumentException("El código de referencia no puede estar vacío");
        this.codigo_referencia = codigo_referencia.trim();
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank())
            throw new IllegalArgumentException("La dirección no puede estar vacía");
        this.direccion = direccion.trim();
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        if (ciudad == null || ciudad.isBlank())
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        this.ciudad = ciudad.trim();
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        if (provincia == null || provincia.isBlank())
            throw new IllegalArgumentException("La provincia no puede estar vacía");
        this.provincia = provincia.trim();
    }

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public void setCodigo_postal(String codigo_postal) {
        if (codigo_postal == null || !codigo_postal.matches("\\d+"))
            throw new IllegalArgumentException("El código postal solo puede contener números");
        this.codigo_postal = codigo_postal;
    }

    public int getMetros_cuadrados() {
        return metros_cuadrados;
    }

    public void setMetros_cuadrados(int metros_cuadrados) {
        if (metros_cuadrados < 0)
            throw new IllegalArgumentException("Los metros cuadrados no pueden ser negativos");
        this.metros_cuadrados = metros_cuadrados;
    }

    public int getNumero_habitaciones() {
        return numero_habitaciones;
    }

    public void setNumero_habitaciones(int numero_habitaciones) {
        if (numero_habitaciones < 0)
            throw new IllegalArgumentException("El número de habitaciones no puede ser negativo");
        this.numero_habitaciones = numero_habitaciones;
    }

    public int getNumero_banios() {
        return numero_banios;
    }

    public void setNumero_banios(int numero_banios) {
        if (numero_banios < 0)
            throw new IllegalArgumentException("El número de baños no puede ser negativo");
        this.numero_banios = numero_banios;
    }

    public BigDecimal getPrecio_mensual() {
        return precio_mensual;
    }

    public void setPrecio_mensual(BigDecimal precio_mensual) {
        if (precio_mensual == null || precio_mensual.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("El precio mensual no puede ser negativo");
        this.precio_mensual = precio_mensual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null ||
                (!estado.equalsIgnoreCase("disponible") &&
                        !estado.equalsIgnoreCase("reservado") &&
                        !estado.equalsIgnoreCase("ocupado")))
            throw new IllegalArgumentException("El estado debe ser 'disponible', 'reservado' o 'ocupado'");
        this.estado = estado.toLowerCase();
    }

    // -------------------------------
    // toString para depuración
    // -------------------------------
    @Override
    public String toString() {
        return "Vivienda{" +
                "id_vivienda=" + id_vivienda +
                ", codigo_referencia='" + codigo_referencia + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", codigo_postal='" + codigo_postal + '\'' +
                ", metros_cuadrados=" + metros_cuadrados +
                ", numero_habitaciones=" + numero_habitaciones +
                ", numero_banios=" + numero_banios +
                ", precio_mensual=" + precio_mensual +
                ", estado='" + estado + '\'' +
                '}';
    }
}
