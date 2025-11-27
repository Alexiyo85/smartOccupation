package com.smartoccupation.modelo;

import java.math.BigDecimal;

/**
 * Clase que representa una vivienda en SmartOccupation. Corresponde a la tabla
 * "viviendas" en la base de datos.
 * <p>
 * Incluye validaciones internas en los métodos setter para asegurar
 * la consistencia de los datos, como valores no negativos para dimensiones
 * y precios, y un formato correcto para el estado.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class Vivienda {

    // -------------------------------
    // Atributos privados
    // -------------------------------
    /**
     * Identificador único de la vivienda. Clave primaria (PK), autogenerado en BD.
     */
    private int idVivienda;
    /**
     * Código único y obligatorio de la vivienda para referencia interna.
     */
    private String codigoReferencia;
    /**
     * Dirección completa de la vivienda (calle, número, piso).
     */
    private String direccion;
    /**
     * Ciudad donde se encuentra la vivienda.
     */
    private String ciudad;
    /**
     * Provincia donde se encuentra la vivienda.
     */
    private String provincia;
    /**
     * Código Postal. Solo debe contener números.
     */
    private String codigoPostal;
    /**
     * Metros cuadrados de la vivienda. Debe ser un valor no negativo.
     */
    private int metrosCuadrados;
    /**
     * Número de habitaciones. Debe ser un valor no negativo.
     */
    private int numeroHabitaciones;
    /**
     * Número de baños. Debe ser un valor no negativo.
     */
    private int numeroBanios;
    /**
     * Precio mensual de alquiler/renta. Se utiliza {@code BigDecimal} para precisión
     * y debe ser un valor no negativo.
     */
    private BigDecimal precioMensual;
    /**
     * Estado de ocupación de la vivienda. Valores permitidos:
     * "disponible", "reservado" o "ocupado".
     */
    private String estado; // "disponible", "reservado" o "ocupado"

    // -------------------------------
    // Constructor vacío
    // -------------------------------
    /**
     * Constructor vacío por defecto.
     * Permite crear un objeto y establecer sus atributos posteriormente
     * utilizando los métodos setter.
     */
    public Vivienda() {
        // Permite crear un objeto vacío y setear atributos después
    }

    // -------------------------------
    // Constructor completo
    // -------------------------------
    /**
     * Constructor completo para inicializar todos los atributos de la vivienda.
     *
     * @param idVivienda Identificador único de la vivienda.
     * @param codigoReferencia Código de referencia único de la vivienda.
     * @param direccion Dirección completa de la vivienda.
     * @param ciudad Ciudad de la vivienda.
     * @param provincia Provincia de la vivienda.
     * @param codigoPostal Código postal (solo números).
     * @param metrosCuadrados Superficie en metros cuadrados (≥ 0).
     * @param numeroHabitaciones Número de habitaciones (≥ 0).
     * @param numeroBanios Número de baños (≥ 0).
     * @param precioMensual Precio mensual de alquiler (≥ 0).
     * @param estado Estado de ocupación ("disponible", "reservado" o "ocupado").
     * @throws IllegalArgumentException Si algún parámetro no cumple con las validaciones internas.
     */
    public Vivienda(int idVivienda, String codigoReferencia, String direccion,
                    String ciudad, String provincia, String codigoPostal,
                    int metrosCuadrados, int numeroHabitaciones, int numeroBanios,
                    BigDecimal precioMensual, String estado) {

        this.idVivienda = idVivienda;

        // Usamos setters para aplicar validaciones internas
        setCodigoReferencia(codigoReferencia);
        setDireccion(direccion);
        setCiudad(ciudad);
        setProvincia(provincia);
        setCodigoPostal(codigoPostal);
        setMetrosCuadrados(metrosCuadrados);
        setNumeroHabitaciones(numeroHabitaciones);
        setNumeroBanios(numeroBanios);
        setPrecioMensual(precioMensual);
        setEstado(estado);
    }

    // -------------------------------
    // Getters y Setters con validaciones
    // -------------------------------
    /**
     * Obtiene el identificador único de la vivienda.
     *
     * @return El ID de la vivienda.
     */
    public int getIdVivienda() {
        return idVivienda;
    }

    /**
     * Establece el identificador único de la vivienda.
     *
     * @param idVivienda El nuevo ID de la vivienda.
     * @throws IllegalArgumentException si el ID es negativo.
     */
    public void setIdVivienda(int idVivienda) {
        if (idVivienda < 0) {
            throw new IllegalArgumentException("El id de la vivienda no puede ser negativo");
        }
        this.idVivienda = idVivienda;
    }

    /**
     * Obtiene el código de referencia único de la vivienda.
     *
     * @return El código de referencia.
     */
    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    /**
     * Establece el código de referencia de la vivienda.
     *
     * @param codigo_referencia El nuevo código de referencia.
     * @throws IllegalArgumentException si el código de referencia es nulo o está en blanco.
     */
    public void setCodigoReferencia(String codigo_referencia) {
        if (codigo_referencia == null || codigo_referencia.isBlank()) {
            throw new IllegalArgumentException("El código de referencia no puede estar vacío");
        }
        this.codigoReferencia = codigo_referencia.trim();
    }

    /**
     * Obtiene la dirección completa de la vivienda.
     *
     * @return La dirección.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección de la vivienda.
     *
     * @param direccion La nueva dirección.
     * @throws IllegalArgumentException si la dirección es nula o está en blanco.
     */
    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía");
        }
        this.direccion = direccion.trim();
    }

    /**
     * Obtiene la ciudad donde se encuentra la vivienda.
     *
     * @return La ciudad.
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad de la vivienda.
     *
     * @param ciudad La nueva ciudad.
     * @throws IllegalArgumentException si la ciudad es nula o está en blanco.
     */
    public void setCiudad(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        }
        this.ciudad = ciudad.trim();
    }

    /**
     * Obtiene la provincia donde se encuentra la vivienda.
     *
     * @return La provincia.
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Establece la provincia de la vivienda.
     *
     * @param provincia La nueva provincia.
     * @throws IllegalArgumentException si la provincia es nula o está en blanco.
     */
    public void setProvincia(String provincia) {
        if (provincia == null || provincia.isBlank()) {
            throw new IllegalArgumentException("La provincia no puede estar vacía");
        }
        this.provincia = provincia.trim();
    }

    /**
     * Obtiene el código postal de la vivienda.
     *
     * @return El código postal.
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Establece el código postal de la vivienda.
     * Debe contener únicamente dígitos.
     *
     * @param codigo_postal El nuevo código postal.
     * @throws IllegalArgumentException si el código postal no solo contiene números.
     */
    public void setCodigoPostal(String codigo_postal) {
        if (codigo_postal == null || !codigo_postal.matches("\\d+")) {
            throw new IllegalArgumentException("El código postal solo puede contener números");
        }
        this.codigoPostal = codigo_postal;
    }

    /**
     * Obtiene la superficie en metros cuadrados de la vivienda.
     *
     * @return Los metros cuadrados.
     */
    public int getMetrosCuadrados() {
        return metrosCuadrados;
    }

    /**
     * Establece la superficie en metros cuadrados de la vivienda.
     *
     * @param metros_cuadrados Los nuevos metros cuadrados.
     * @throws IllegalArgumentException si los metros cuadrados son negativos.
     */
    public void setMetrosCuadrados(int metros_cuadrados) {
        if (metros_cuadrados < 0) {
            throw new IllegalArgumentException("Los metros cuadrados no pueden ser negativos");
        }
        this.metrosCuadrados = metros_cuadrados;
    }

    /**
     * Obtiene el número de habitaciones de la vivienda.
     *
     * @return El número de habitaciones.
     */
    public int getNumeroHabitaciones() {
        return numeroHabitaciones;
    }

    /**
     * Establece el número de habitaciones de la vivienda.
     *
     * @param numero_habitaciones El nuevo número de habitaciones.
     * @throws IllegalArgumentException si el número de habitaciones es negativo.
     */
    public void setNumeroHabitaciones(int numero_habitaciones) {
        if (numero_habitaciones < 0) {
            throw new IllegalArgumentException("El número de habitaciones no puede ser negativo");
        }
        this.numeroHabitaciones = numero_habitaciones;
    }

    /**
     * Obtiene el número de baños de la vivienda.
     *
     * @return El número de baños.
     */
    public int getNumeroBanios() {
        return numeroBanios;
    }

    /**
     * Establece el número de baños de la vivienda.
     *
     * @param numero_banios El nuevo número de baños.
     * @throws IllegalArgumentException si el número de baños es negativo.
     */
    public void setNumeroBanios(int numero_banios) {
        if (numero_banios < 0) {
            throw new IllegalArgumentException("El número de baños no puede ser negativo");
        }
        this.numeroBanios = numero_banios;
    }

    /**
     * Obtiene el precio mensual de alquiler/renta.
     *
     * @return El precio mensual en formato {@code BigDecimal}.
     */
    public BigDecimal getPrecio_mensual() {
        return precioMensual;
    }

    /**
     * Establece el precio mensual de alquiler/renta.
     * No puede ser nulo ni negativo.
     *
     * @param precio_mensual El nuevo precio mensual.
     * @throws IllegalArgumentException si el precio mensual es nulo o negativo.
     */
    public void setPrecioMensual(BigDecimal precio_mensual) {
        // BigDecimal.compareTo(BigDecimal.ZERO) < 0 significa que es negativo
        if (precio_mensual == null || precio_mensual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio mensual no puede ser negativo");
        }
        this.precioMensual = precio_mensual;
    }

    /**
     * Obtiene el estado de ocupación actual de la vivienda.
     *
     * @return El estado, siempre en minúsculas ("disponible", "reservado" o "ocupado").
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado de ocupación de la vivienda.
     * Debe ser uno de los siguientes valores: "disponible", "reservado" o "ocupado" (sin importar mayúsculas/minúsculas).
     * Se almacena en minúsculas.
     *
     * @param estado El nuevo estado.
     * @throws IllegalArgumentException si el estado no es uno de los valores permitidos.
     */
    public void setEstado(String estado) {
        if (estado == null
                || (!estado.equalsIgnoreCase("disponible")
                && !estado.equalsIgnoreCase("reservado")
                && !estado.equalsIgnoreCase("ocupado"))) {
            throw new IllegalArgumentException("El estado debe ser 'disponible', 'reservado' o 'ocupado'");
        }
        this.estado = estado.toLowerCase();
    }

    // -------------------------------
    // toString para depuración
    // -------------------------------
    /**
     * Devuelve una representación básica en cadena de la vivienda,
     * útil para depuración o listados rápidos.
     *
     * @return Cadena que contiene el ID, Código de Referencia, Dirección y Estado.
     */
    @Override
    public String toString() {
        return idVivienda + ", " + codigoReferencia + ", " + direccion + ", " + estado;
    }
}