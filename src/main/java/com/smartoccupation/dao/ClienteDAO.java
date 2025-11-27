package com.smartoccupation.dao;

import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para gestionar las operaciones CRUD y consultas
 * relacionadas con la entidad {@link com.smartoccupation.modelo.Cliente} en la
 * base de datos.
 * <p>
 * Facilita la interacción entre la lógica de negocio y la persistencia de datos
 * para la tabla 'clientes'.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ClienteDAO {

    /**
     * Inserta un nuevo registro de cliente en la base de datos.
     * El ID del cliente (PK) se genera automáticamente y se asigna
     * al objeto {@code Cliente} de entrada.
     *
     * @param cliente El objeto {@code Cliente} a insertar.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     */
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes " +
                "(nombre, primer_apellido, segundo_apellido, dni_nif, telefono, email, direccion, ciudad, provincia, codigo_postal) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getPrimerApellido());
            // Manejo de campos opcionales (puede ser null)
            ps.setString(3, cliente.getSegundoApellido());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad());
            ps.setString(9, cliente.getProvincia());
            ps.setString(10, cliente.getCodigo_postal());

            int filas = ps.executeUpdate();

            // Recupera la clave generada
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) cliente.setIdCliente(rs.getInt(1));
            }

            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un registro de cliente existente en la base de datos.
     *
     * @param cliente El objeto {@code Cliente} con los datos actualizados, incluyendo el ID (PK).
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre=?, primer_apellido=?, segundo_apellido=?, dni_nif=?, " +
                "telefono=?, email=?, direccion=?, ciudad=?, provincia=?, codigo_postal=? WHERE id_cliente=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getPrimerApellido());
            ps.setString(3, cliente.getSegundoApellido());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad());
            ps.setString(9, cliente.getProvincia());
            ps.setString(10, cliente.getCodigo_postal());
            ps.setInt(11, cliente.getIdCliente()); // Clave de actualización

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de cliente de la base de datos utilizando su ID.
     *
     * @param idCliente El ID (PK) del cliente a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario.
     */
    public boolean eliminar(int idCliente) {
        String sql = "DELETE FROM clientes WHERE id_cliente=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un registro de cliente por su identificador único.
     *
     * @param idCliente El ID (PK) del cliente a buscar.
     * @return El objeto {@code Cliente} encontrado o {@code null} si no existe.
     */
    public Cliente obtenerPorId(int idCliente) {
        String sql = "SELECT * FROM clientes WHERE id_cliente=?";
        Cliente cliente = null;
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) cliente = mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cliente: " + e.getMessage());
        }
        return cliente;
    }

    /**
     * Obtiene una lista con todos los registros de clientes ordenados por ID.
     *
     * @return Una lista de objetos {@code Cliente}.
     */
    public List<Cliente> obtenerTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY id_cliente";
        try (Connection conn = ConexionBBDD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapearCliente(rs));
        } catch (SQLException e) {
            System.out.println("Error al obtener todos los clientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Obtiene un registro de cliente por su DNI/NIF.
     *
     * @param dni El DNI/NIF del cliente a buscar (se convierte a mayúsculas para la búsqueda).
     * @return El objeto {@code Cliente} encontrado o {@code null} si no existe.
     */
    public Cliente obtenerPorDni(String dni) {
        String sql = "SELECT * FROM clientes WHERE dni_nif=?";
        Cliente cliente = null;
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Aseguramos que la búsqueda se haga con mayúsculas, coherente con la validación del modelo
            ps.setString(1, dni.toUpperCase()); 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) cliente = mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cliente por DNI: " + e.getMessage());
        }
        return cliente;
    }

    /**
     * Método auxiliar para mapear una fila del {@code ResultSet} a un objeto {@code Cliente}.
     *
     * @param rs El {@code ResultSet} apuntando a la fila actual.
     * @return Un objeto {@code Cliente} poblado con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a los datos de la base de datos.
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        // Los setters del modelo (Cliente) ya incluyen lógica de validación/formato
        c.setNombre(rs.getString("nombre")); 
        c.setPrimerApellido(rs.getString("primer_apellido"));
        c.setSegundoApellido(rs.getString("segundo_apellido"));
        c.setDni(rs.getString("dni_nif")); // Se espera que ya esté en mayúsculas en la BD
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setCiudad(rs.getString("ciudad"));
        c.setProvincia(rs.getString("provincia"));
        c.setCodigoPostal(rs.getString("codigo_postal"));
        return c;
    }
}