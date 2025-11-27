package com.smartoccupation.modelo;

/**
 * Representa un cliente con sus datos personales y de contacto.
 * <p>
 * Esta clase incluye validaciones básicas en los métodos 'setter' para asegurar
 * que los campos obligatorios se establezcan correctamente (ej: nombre, DNI, código postal).
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class Cliente {

    /**
     * Identificador único del cliente.
     */
    private int idCliente;
    /**
     * Nombre del cliente. Obligatorio.
     */
    private String nombre;
    /**
     * Primer apellido del cliente. Obligatorio.
     */
    private String primerApellido;
    /**
     * Segundo apellido del cliente. Opcional.
     */
    private String segundoApellido; // Opcional
    /**
     * Documento Nacional de Identidad (DNI) del cliente. Obligatorio (9 caracteres).
     */
    private String dni;             // Obligatorio 9 caracteres
    /**
     * Correo electrónico del cliente. Opcional.
     */
    private String email;           // Opcional
    /**
     * Número de teléfono del cliente. Opcional (9 dígitos).
     */
    private String telefono;        // Opcional
    /**
     * Dirección de residencia completa (calle, número, etc.).
     */
    private String direccion;
    /**
     * Ciudad de residencia.
     */
    private String ciudad;
    /**
     * Provincia de residencia.
     */
    private String provincia;
    /**
     * Código Postal. Obligatorio (5 dígitos).
     */
    private String codigoPostal;    // Obligatorio 5 dígitos

    /**
     * Constructor vacío por defecto.
     */
    public Cliente() { }

    /**
     * Constructor completo para inicializar todos los atributos del cliente.
     *
     * @param idCliente Identificador único del cliente.
     * @param codigoPostal Código postal (5 dígitos obligatorios).
     * @param provincia Provincia de residencia (obligatoria).
     * @param ciudad Ciudad de residencia (obligatoria).
     * @param direccion Dirección completa (obligatoria).
     * @param telefono Número de teléfono (opcional, 9 dígitos).
     * @param email Correo electrónico (opcional, debe ser válido).
     * @param dni DNI del cliente (9 caracteres obligatorios).
     * @param segundoApellido Segundo apellido (opcional).
     * @param primerApellido Primer apellido (obligatorio).
     * @param nombre Nombre del cliente (obligatorio).
     * @throws IllegalArgumentException si algún campo obligatorio no cumple con las validaciones (ej. nombre, DNI, CP).
     */
    public Cliente(int idCliente, String codigoPostal, String provincia,
                   String ciudad, String direccion, String telefono,
                   String email, String dni, String segundoApellido,
                   String primerApellido, String nombre) {
        this.idCliente = idCliente;
        // Se utilizan los setters para aplicar las validaciones
        setNombre(nombre);
        setPrimerApellido(primerApellido);
        setSegundoApellido(segundoApellido);
        setDni(dni);
        setEmail(email);
        setTelefono(telefono);
        setDireccion(direccion);
        setCiudad(ciudad);
        setProvincia(provincia);
        setCodigoPostal(codigoPostal);
    }

    // Getters y Setters

    /**
     * Obtiene el identificador único del cliente.
     *
     * @return El ID del cliente.
     */
    public int getIdCliente() { return idCliente; }

    /**
     * Establece el identificador único del cliente.
     *
     * @param id_cliente El nuevo ID del cliente.
     */
    public void setIdCliente(int id_cliente) { this.idCliente = id_cliente; }

    /**
     * Obtiene el nombre del cliente.
     *
     * @return El nombre del cliente.
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del cliente.
     * El nombre no puede ser nulo ni estar vacío.
     *
     * @param nombre El nuevo nombre del cliente.
     * @throws IllegalArgumentException si el nombre es nulo o está en blanco.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Obtiene el primer apellido del cliente.
     *
     * @return El primer apellido del cliente.
     */
    public String getPrimerApellido() { return primerApellido; }

    /**
     * Establece el primer apellido del cliente.
     * El primer apellido no puede ser nulo ni estar vacío.
     *
     * @param primer_apellido El nuevo primer apellido del cliente.
     * @throws IllegalArgumentException si el primer apellido es nulo o está en blanco.
     */
    public void setPrimerApellido(String primer_apellido) {
        if (primer_apellido == null || primer_apellido.isBlank()) {
            throw new IllegalArgumentException("El primer apellido no puede estar vacío");
        }
        this.primerApellido = primer_apellido.trim();
    }

    /**
     * Obtiene el segundo apellido del cliente. Puede ser nulo.
     *
     * @return El segundo apellido del cliente o {@code null} si no tiene.
     */
    public String getSegundoApellido() { return segundoApellido; }

    /**
     * Establece el segundo apellido del cliente.
     * Si el valor es nulo o está en blanco, se establece como {@code null}.
     *
     * @param segundo_apellido El nuevo segundo apellido del cliente.
     */
    public void setSegundoApellido(String segundo_apellido) {
        if (segundo_apellido == null || segundo_apellido.isBlank()) {
            this.segundoApellido = null; // Permitir vacío
        } else {
            this.segundoApellido = segundo_apellido.trim();
        }
    }

    /**
     * Obtiene el DNI del cliente.
     *
     * @return El DNI del cliente en mayúsculas.
     */
    public String getDni() { return dni; }

    /**
     * Establece el DNI del cliente.
     * El DNI debe tener exactamente 9 caracteres. Se almacena en mayúsculas.
     *
     * @param dni El nuevo DNI del cliente.
     * @throws IllegalArgumentException si el DNI es nulo o no tiene 9 caracteres.
     */
    public void setDni(String dni) {
        if (dni == null || dni.length() != 9) {
            throw new IllegalArgumentException("El DNI debe tener 9 caracteres");
        }
        this.dni = dni.toUpperCase();
    }

    /**
     * Obtiene el correo electrónico del cliente. Puede ser nulo.
     *
     * @return El email del cliente en minúsculas, o {@code null} si no tiene.
     */
    public String getEmail() { return email; }

    /**
     * Establece el correo electrónico del cliente.
     * Si el valor es nulo o está en blanco, se establece como {@code null}.
     *
     * @param email El nuevo correo electrónico del cliente.
     * @throws IllegalArgumentException si el email no es válido (no contiene '@' ni '.').
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            this.email = null; // Permitir vacío
        } else if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("El email no es válido");
        } else {
            this.email = email.toLowerCase();
        }
    }

    /**
     * Obtiene el número de teléfono del cliente. Puede ser nulo.
     *
     * @return El número de teléfono, o {@code null} si no tiene.
     */
    public String getTelefono() { return telefono; }

    /**
     * Establece el número de teléfono del cliente.
     * Si no es nulo ni vacío, debe tener exactamente 9 dígitos.
     *
     * @param telefono El nuevo número de teléfono.
     * @throws IllegalArgumentException si el teléfono no tiene exactamente 9 dígitos.
     */
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            this.telefono = null; // Permitir vacío
        } else if (!telefono.matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 dígitos");
        } else {
            this.telefono = telefono;
        }
    }

    /**
     * Obtiene la dirección de residencia del cliente.
     *
     * @return La dirección del cliente.
     */
    public String getDireccion() { return direccion; }

    /**
     * Establece la dirección de residencia del cliente.
     * La dirección no puede ser nula ni estar vacía.
     *
     * @param direccion La nueva dirección del cliente.
     * @throws IllegalArgumentException si la dirección es nula o está en blanco.
     */
    public void setDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía");
        }
        this.direccion = direccion.trim();
    }

    /**
     * Obtiene la ciudad de residencia del cliente.
     *
     * @return La ciudad del cliente.
     */
    public String getCiudad() { return ciudad; }

    /**
     * Establece la ciudad de residencia del cliente.
     * La ciudad no puede ser nula ni estar vacía.
     *
     * @param ciudad La nueva ciudad del cliente.
     * @throws IllegalArgumentException si la ciudad es nula o está en blanco.
     */
    public void setCiudad(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        }
        this.ciudad = ciudad.trim();
    }

    /**
     * Obtiene la provincia de residencia del cliente.
     *
     * @return La provincia del cliente.
     */
    public String getProvincia() { return provincia; }

    /**
     * Establece la provincia de residencia del cliente.
     * La provincia no puede ser nula ni estar vacía.
     *
     * @param provincia La nueva provincia del cliente.
     * @throws IllegalArgumentException si la provincia es nula o está en blanco.
     */
    public void setProvincia(String provincia) {
        if (provincia == null || provincia.isBlank()) {
            throw new IllegalArgumentException("La provincia no puede estar vacía");
        }
        this.provincia = provincia.trim();
    }

    /**
     * Obtiene el código postal del cliente.
     *
     * @return El código postal del cliente.
     */
    public String getCodigo_postal() { return codigoPostal; }

    /**
     * Establece el código postal del cliente.
     * Debe tener exactamente 5 dígitos.
     *
     * @param codigo_postal El nuevo código postal.
     * @throws IllegalArgumentException si el código postal no tiene 5 dígitos.
     */
    public void setCodigoPostal(String codigo_postal) {
        if (codigo_postal == null || !codigo_postal.matches("\\d{5}")) {
            throw new IllegalArgumentException("El código postal debe tener exactamente 5 dígitos");
        }
        this.codigoPostal = codigo_postal;
    }

    /**
     * Devuelve una representación en cadena del objeto {@code Cliente}.
     * Si el nombre y primer apellido están disponibles, devuelve: "Nombre Apellido1 (DNI)".
     * Si no, devuelve una representación genérica.
     *
     * @return Una cadena que representa al cliente.
     */
    @Override
    public String toString() {
        if (nombre != null && primerApellido != null) {
            return nombre + " " + primerApellido + " (" + dni + ")";
        }
        return "Cliente{id_cliente=" + idCliente + ", nombre='" + nombre + "'}";
    }
}