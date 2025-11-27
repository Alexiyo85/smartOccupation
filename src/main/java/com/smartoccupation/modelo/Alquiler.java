package com.smartoccupation.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase de modelo que representa un contrato de Alquiler de vivienda.
 * <p>
 * Contiene todos los atributos que mapean a la tabla {@code alquileres} de la
 * base de datos, incluyendo referencias a los objetos {@code Cliente},
 * {@code Vivienda} y {@code EstadoCobro}.
 * <p>
 * **Nota:** Incluye validaciones internas en los métodos set para asegurar la
 * integridad de los datos.
 *
 * @author Tu Nombre (o equipo)
 * @version 1.1
 * @since 2023-11-27
 * @see Cliente
 * @see Vivienda
 * @see EstadoCobro
 */
public class Alquiler {

    // -------------------------------
    // Atributos privados
    // -------------------------------
    /**
     * Identificador único del expediente de alquiler. Autogenerado en la BBDD.
     */
    private int numeroExpediente;

    /**
     * Fecha de inicio efectiva del contrato de alquiler.
     */
    private LocalDate fechaInicio;

    /**
     * Duración estimada del alquiler en meses. Valor debe ser >= 0.
     */
    private int tiempoMeses;

    /**
     * Duración estimada del alquiler en días adicionales (complemento a los
     * meses). Valor debe ser >= 0.
     */
    private int tiempoDias;

    /**
     * Fecha de finalización estimada del alquiler, calculada a partir de
     * {@code fechaInicio}, {@code tiempoMeses} y {@code tiempoDias}.
     */
    private LocalDate fechaFinEstimado;

    /**
     * Precio total estimado del alquiler. Se usa {@code BigDecimal} para
     * manejar cálculos monetarios con precisión. Valor debe ser >= 0.
     */
    private BigDecimal precioTotalEstimado;

    /**
     * Clave foránea al objeto Cliente. Identificador único del cliente asociado
     * al contrato.
     */
    private int idCliente;

    /**
     * Clave foránea al objeto Vivienda. Identificador único de la vivienda
     * alquilada.
     */
    private int idVivienda;

    /**
     * Clave foránea al objeto EstadoCobro. Identificador del estado de
     * pago/cobro del alquiler.
     */
    private int idEstadoCobro;

    /**
     * Estado actual del contrato de alquiler ("Activo", "Finalizado", etc.).
     * <p>
     * **Mejora:** Se recomienda usar un tipo {@code enum} (ej.
     * {@code EstadoAlquiler}) en lugar de {@code String} para limitar los
     * valores posibles y mejorar la seguridad del código.
     */
    private String estado;

    /**
     * Objeto Cliente asociado al contrato (para uso en la aplicación, no
     * persistido).
     */
    private Cliente cliente;

    /**
     * Objeto Vivienda asociada al contrato (para uso en la aplicación, no
     * persistido).
     */
    private Vivienda vivienda;

    /**
     * Objeto EstadoCobro asociado al contrato (para uso en la aplicación, no
     * persistido).
     */
    private EstadoCobro estadoCobro;

    /**
     * Constructor por defecto de la clase Alquiler.
     */
    public Alquiler() {
    }

    /**
     * Constructor completo para inicializar un objeto Alquiler.
     * <p>
     * Se utilizan los métodos set para asegurar que se aplican las validaciones
     * al momento de la instanciación.
     *
     * @param numeroExpediente Identificador único del expediente.
     * @param fechaInicio Fecha de inicio del alquiler.
     * @param tiempoMeses Duración estimada en meses.
     * @param tiempoDias Duración estimada en días.
     * @param fechaFinEstimada Fecha de fin estimada.
     * @param precioTotalEstimado Precio total estimado.
     * @param idCliente ID del cliente asociado.
     * @param idVivienda ID de la vivienda asociada.
     * @param idEstadoCobro ID del estado de cobro.
     */
    public Alquiler(int numeroExpediente, LocalDate fechaInicio, int tiempoMeses, int tiempoDias,
            LocalDate fechaFinEstimada, BigDecimal precioTotalEstimado,
            int idCliente, int idVivienda, int idEstadoCobro) {

        setNumeroExpediente(numeroExpediente);
        setFechaInicio(fechaInicio);
        setTiempoMeses(tiempoMeses);
        setTiempoDias(tiempoDias);
        setFechaFinEstimada(fechaFinEstimada);
        setPrecioTotalEstimado(precioTotalEstimado);
        setIdCliente(idCliente);
        setIdVivienda(idVivienda);
        setIdEstadoCobro(idEstadoCobro);
    }

    // -------------------------------
    // Getters y Setters
    // -------------------------------
    /**
     * Obtiene el número de expediente del alquiler.
     *
     * @return El número de expediente.
     */
    public int getNumeroExpediente() {
        return numeroExpediente;
    }


    /**
     * Establece el número de expediente.
     *
     * @param numeroExpediente El número de expediente, debe ser no negativo.
     * @throws IllegalArgumentException Si el número es negativo.
     */
    public void setNumeroExpediente(int numeroExpediente) {
        if (numeroExpediente < 0) {
            throw new IllegalArgumentException("El número de expediente no puede ser negativo");
        }
        this.numeroExpediente = numeroExpediente;
    }

    /**
     * Obtiene la fecha de inicio del alquiler.
     *
     * @return La fecha de inicio.
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    
    /**
     * Establece la fecha de inicio del alquiler.
     *
     * @param fechaInicio La fecha de inicio.
     * @throws IllegalArgumentException Si la fecha es nula.
     * <p>
     * **Nota de BP:** Se ha eliminado la llamada interna a {@code calcularFechaFin()} para mejorar
     * el Principio de Responsabilidad Única (SRP) y evitar cálculos repetidos.
     */
    public void setFechaInicio(LocalDate fecha_inicio) {
        if (fecha_inicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        this.fechaInicio = fecha_inicio;
        calcularFechaFin();
    }
    
    /**
     * Obtiene el tiempo estimado en meses.
     *
     * @return El tiempo en meses.
     */
    public int getTiempoMeses() {
        return tiempoMeses;
    }
    
    /**
     * Establece el tiempo estimado en meses.
     *
     * @param tiempoMeses El tiempo en meses, debe ser no negativo.
     * @throws IllegalArgumentException Si el valor es negativo.
     * <p>
     * **Nota de BP:** Se ha eliminado la llamada interna a {@code calcularFechaFin()}.
     */
    public void setTiempoMeses(int tiempo_meses) {
        if (tiempo_meses < 0) {
            throw new IllegalArgumentException("El tiempo en meses no puede ser negativo");
        }
        this.tiempoMeses = tiempo_meses;
        calcularFechaFin();
    }
    
    /**
     * Obtiene el tiempo estimado en días.
     *
     * @return El tiempo en días.
     */
    public int getTiempoDias() {
        return tiempoDias;
    }
    
    /**
     * Establece el tiempo estimado en días.
     *
     * @param tiempoDias El tiempo en días, debe ser no negativo.
     * @throws IllegalArgumentException Si el valor es negativo.
     * <p>
     */
    public void setTiempoDias(int tiempo_dias) {
        if (tiempo_dias < 0) {
            throw new IllegalArgumentException("El tiempo en días no puede ser negativo");
        }
        this.tiempoDias = tiempo_dias;
        calcularFechaFin();
    }
    
    /**
     * Obtiene la fecha de finalización estimada del alquiler.
     *
     * @return La fecha de finalización estimada.
     */
    public LocalDate getFechaFinEstimada() {
        return fechaFinEstimado;
    }
    
    /**
     * Establece la fecha de finalización estimada.
     *
     * @param fechaFinEstimada La fecha de fin estimada.
     * @throws IllegalArgumentException Si la fecha de fin es anterior a la fecha de inicio.
     */
    public void setFechaFinEstimada(LocalDate fecha_fin_estimada) {
        if (fecha_fin_estimada != null && fechaInicio != null
                && fecha_fin_estimada.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha fin estimada no puede ser anterior a la fecha de inicio");
        }
        this.fechaFinEstimado = fecha_fin_estimada;
    }
    
    /**
     * Obtiene el precio total estimado del alquiler.
     *
     * @return El precio total estimado ({@code BigDecimal}).
     */
    public BigDecimal getPrecioTotalEstimado() {
        return precioTotalEstimado;
    }
    
    /**
     * Establece el precio total estimado del alquiler.
     *
     * @param precioTotalEstimado El precio total estimado, debe ser no negativo.
     * @throws IllegalArgumentException Si el precio es negativo.
     */
    public void setPrecioTotalEstimado(BigDecimal precio_total_estimado) {
        if (precio_total_estimado != null && precio_total_estimado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio total estimado no puede ser negativo");
        }
        this.precioTotalEstimado = precio_total_estimado;
    }

    /**
     * Obtiene el ID del cliente asociado.
     *
     * @return El ID del cliente (clave foránea).
     */
    public int getIdCliente() {
        return idCliente;
    }
    
    /**
     * Establece el ID del cliente.
     *
     * @param idCliente El ID del cliente, debe ser mayor que 0.
     * @throws IllegalArgumentException Si el ID es menor o igual a 0.
     */
    public void setIdCliente(int id_cliente) {
        if (id_cliente <= 0) {
            throw new IllegalArgumentException("El id del cliente debe ser mayor que 0");
        }
        this.idCliente = id_cliente;
    }

    /**
     * Obtiene el ID de la vivienda asociada.
     *
     * @return El ID de la vivienda (clave foránea).
     */
    public int getIdVivienda() {
        return idVivienda;
    }

    /**
     * Establece el ID de la vivienda.
     *
     * @param idVivienda El ID de la vivienda, debe ser mayor que 0.
     * @throws IllegalArgumentException Si el ID es menor o igual a 0.
     */
    public void setIdVivienda(int id_vivienda) {
        if (id_vivienda <= 0) {
            throw new IllegalArgumentException("El id de la vivienda debe ser mayor que 0");
        }
        this.idVivienda = id_vivienda;
    }

    /**
     * Obtiene el ID del estado de cobro.
     *
     * @return El ID del estado de cobro (clave foránea).
     */
    public int getIdEstadoCobro() {
        return idEstadoCobro;
    }

    /**
     * Establece el ID del estado de cobro.
     *
     * @param idEstadoCobro El ID del estado de cobro, debe ser mayor que 0.
     * @throws IllegalArgumentException Si el ID es menor o igual a 0.
     */
    public void setIdEstadoCobro(int id_estado_cobro) {
        if (id_estado_cobro <= 0) {
            throw new IllegalArgumentException("El id del estado de cobro debe ser mayor que 0");
        }
        this.idEstadoCobro = id_estado_cobro;
    }

    /**
     * Obtiene el estado actual del contrato (ej. "Activo").
     *
     * @return El estado del contrato como String.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual del contrato.
     *
     * @param estado El nuevo estado del contrato.
     * <p>
     * **Mejora de BP:** Este setter debería aceptar un {@code EstadoAlquiler} (enum) en lugar de un {@code String}.
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el objeto Cliente asociado.
     *
     * @return El objeto Cliente.
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Establece el objeto Cliente y actualiza el campo {@code idCliente}.
     *
     * @param cliente El objeto Cliente a asociar.
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            this.idCliente = cliente.getIdCliente();
        }
    }

    /**
     * Obtiene el objeto Vivienda asociado.
     *
     * @return El objeto Vivienda.
     */
    public Vivienda getVivienda() {
        return vivienda;
    }

    /**
     * Establece el objeto Vivienda y actualiza el campo {@code idVivienda}.
     *
     * @param vivienda El objeto Vivienda a asociar.
     */
    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
        if (vivienda != null) {
            this.idVivienda = vivienda.getIdVivienda();
        }
    }

    /**
     * Obtiene el objeto EstadoCobro asociado.
     *
     * @return El objeto EstadoCobro.
     */
    public EstadoCobro getEstadoCobro() {
        return estadoCobro;
    }

    /**
     * Establece el objeto EstadoCobro.
     *
     * @param estadoCobro El objeto EstadoCobro a asociar.
     */
    public void setEstadoCobro(EstadoCobro estadoCobro) {
        this.estadoCobro = estadoCobro;
    }

    
    /**
     * Método auxiliar para generar el nombre completo del cliente.
     *
     * @return Una cadena que contiene el nombre completo del cliente (Nombre Apellido1 Apellido2) o una cadena vacía si el objeto Cliente es nulo.
     */
    public String getNombreCliente() {
        if (cliente != null) {
            return cliente.getNombre() + " " + cliente.getPrimerApellido() + " " + cliente.getSegundoApellido();
        }
        return "";
    }

    // -------------------------------
    // Métodos auxiliares
    // -------------------------------
    
    /**
     * Calcula y establece la fecha de finalización estimada del alquiler
     * sumando {@code tiempoMeses} y {@code tiempoDias} a la {@code fechaInicio}.
     * <p>
     * Se debe llamar a este método después de que {@code fechaInicio}, {@code tiempoMeses} y
     * {@code tiempoDias} hayan sido establecidos.
     */
    public void calcularFechaFin() {
        if (fechaInicio != null) {
            this.fechaFinEstimado = fechaInicio.plusMonths(tiempoMeses).plusDays(tiempoDias);
        }
    }

    
    /**
     * Calcula el precio total estimado del alquiler basándose en el precio mensual
     * y la duración total (meses completos + días extra).
     * <p>
     * Utiliza {@code BigDecimal} para realizar cálculos con alta precisión y
     * redondea el resultado final a dos decimales (formato moneda).
     *
     * @param precioMensual El precio mensual base de la vivienda. Debe ser no negativo.
     * @throws IllegalArgumentException si {@code precioMensual} es nulo o negativo.
     * @see #setPrecioTotalEstimado(BigDecimal)
     */
    public void calcularPrecioTotal(BigDecimal precioMensual) {
        if (precioMensual == null || precioMensual.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio mensual debe ser mayor o igual a cero");
        }

        // 1. Calcular la parte del precio por meses completos
        BigDecimal precioPorMeses = precioMensual.multiply(BigDecimal.valueOf(tiempoMeses));

        // 2. Calcular el precio diario (con alta precisión)
        // Usamos una escala de 8 para el cálculo del precio diario para minimizar errores intermedios
        BigDecimal precioDiario = precioMensual.divide(
                BigDecimal.valueOf(30),
                8, // Alta precisión para la división
                BigDecimal.ROUND_HALF_UP
        );

        // 3. Calcular la parte del precio por días extra
        BigDecimal precioPorDias = precioDiario.multiply(BigDecimal.valueOf(tiempoDias));

        // 4. Sumar ambos componentes
        BigDecimal totalSinRedondear = precioPorMeses.add(precioPorDias);

        // 5. Redondear el resultado final a 2 decimales (moneda)
        this.precioTotalEstimado = totalSinRedondear.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // -------------------------------
    // toString
    // -------------------------------
    /**
     * Genera una representación corta del objeto, ideal para mostrar en componentes UI
     * como un JComboBox.
     *
     * @return Cadena formateada como: "Expediente [NumeroExpediente] - [Nombre Cliente]".
     */
    public String toComboString() {
        return "Expediente " + numeroExpediente + " - "
                + (cliente != null ? cliente.getNombre() : "Cliente desconocido");
    }

    /**
     * Proporciona una representación de cadena detallada del objeto Alquiler,
     * útil para depuración y logs.
     *
     * @return Cadena formateada como: "[Exp. [Numero]] [Nombre Completo Cliente] - [Dirección Vivienda]".
     */
    @Override
    public String toString() {
        // 1. Construir clienteNombre con los tres campos
        String clienteNombre;
        if (cliente != null) {
            // Mejorada: Usar String.format para una construcción más limpia
            clienteNombre = String.format("%s %s %s",
                    cliente.getNombre(),
                    cliente.getPrimerApellido(),
                    cliente.getSegundoApellido());
        } else {
            clienteNombre = "ID Cliente: " + idCliente;
        }

        // 2. Construir viviendaDireccion
        String viviendaDireccion = (vivienda != null)
                ? vivienda.getDireccion()
                : "ID Vivienda: " + idVivienda;

        // 3. Devolver la cadena final combinada
        return String.format("[Exp. %d] %s - %s",
                numeroExpediente,
                clienteNombre,
                viviendaDireccion);
    }

}
