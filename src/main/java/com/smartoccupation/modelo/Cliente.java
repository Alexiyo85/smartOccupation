package com.smartoccupation.modelo;

/**
 * Clase que representa un cliente de SmartOccupation.
 * Contiene todos los datos personales y de contacto.
 * Incluye validaciones internas en setters para asegurar consistencia.
 */
public class Cliente {

    // -------------------------------
    // Atributos privados
    // -------------------------------
    private int id_cliente;           // Identificador único del cliente (PK)
    private String nombre;            // Nombre del cliente
    private String primer_apellido;   // Primer apellido
    private String segundo_apellido;  // Segundo apellido
    private String dni;               // DNI o NIF (exactamente 9 caracteres)
    private String email;             // Email del cliente (debe contener @ y .)
    private String telefono;          // Teléfono (exactamente 9 dígitos)
    private String direccion;         // Dirección completa
    private String ciudad;            // Ciudad
    private String provincia;         // Provincia
    private String codigo_postal;     // Código postal (exactamente 5 dígitos)

    // -------------------------------
    // Constructor vacío
    // -------------------------------
    public Cliente() {
        // Permite crear el objeto vacío y setear atributos después
    }

    // -------------------------------
    // Constructor completo
    // -------------------------------
    public Cliente(int id_cliente, String codigo_postal, String provincia,
                   String ciudad, String direccion, String telefono,
                   String email, String dni, String segundo_apellido,
                   String primer_apellido, String nombre) {

        this.id_cliente = id_cliente;

        // Se utilizan los setters para aplicar validaciones internas
        setNombre(nombre);
        setPrimer_apellido(primer_apellido);
        setSegundo_apellido(segundo_apellido);
        setDni(dni);
        setEmail(email);
        setTelefono(telefono);
        setDireccion(direccion);
        setCiudad(ciudad);
        setProvincia(provincia);
        setCodigo_postal(codigo_postal);
    }

    // -------------------------------
    // Getters y Setters con validaciones
    // -------------------------------

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente; // PK autogenerado, normalmente no necesita validación
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        this.nombre = nombre.trim();
    }

    public String getPrimer_apellido() {
        return primer_apellido;
    }

    public void setPrimer_apellido(String primer_apellido) {
        if (primer_apellido == null || primer_apellido.isBlank())
            throw new IllegalArgumentException("El primer apellido no puede estar vacío");
        this.primer_apellido = primer_apellido.trim();
    }

    public String getSegundo_apellido() {
        return segundo_apellido;
    }

    public void setSegundo_apellido(String segundo_apellido) {
        if (segundo_apellido == null || segundo_apellido.isBlank())
            throw new IllegalArgumentException("El segundo apellido no puede estar vacío");
        this.segundo_apellido = segundo_apellido.trim();
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || dni.length() != 9)
            throw new IllegalArgumentException("El DNI debe tener 9 caracteres");
        this.dni = dni.toUpperCase(); // Convertimos a mayúsculas
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@") || !email.contains("."))
            throw new IllegalArgumentException("El email no es válido");
        this.email = email.toLowerCase(); // Convertimos a minúsculas
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.matches("\\d{9}"))
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 números");
        this.telefono = telefono;
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
        if (codigo_postal == null || !codigo_postal.matches("\\d{5}"))
            throw new IllegalArgumentException("El código postal debe tener exactamente 5 números");
        this.codigo_postal = codigo_postal;
    }

    // -------------------------------
    // toString para depuración
    // -------------------------------
    @Override
    public String toString() {
        return "Cliente{" +
                "id_cliente=" + id_cliente +
                ", nombre='" + nombre + '\'' +
                ", primer_apellido='" + primer_apellido + '\'' +
                ", segundo_apellido='" + segundo_apellido + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", provincia='" + provincia + '\'' +
                ", codigo_postal='" + codigo_postal + '\'' +
                '}';
    }
}