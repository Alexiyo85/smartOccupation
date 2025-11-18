package com.smartoccupation.dao;

import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla "clientes".
 * Proporciona operaciones CRUD y consulta de todos los clientes.
 */
public class ClienteDAO {

    private ResultSet rs;

    // -------------------------------
    // Insertar cliente
    // -------------------------------
    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO clientes " +
                "(nombre, primer_apellido, segundo_apellido, dni_nif, telefono, email, direccion, ciudad, provincia, codigo_postal) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getPrimer_apellido());
            ps.setString(3, cliente.getSegundo_apellido());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad());
            ps.setString(9, cliente.getProvincia());
            ps.setString(10, cliente.getCodigo_postal());

            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) cliente.setId_cliente(rs.getInt(1));
            }

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar cliente: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Actualizar cliente
    // -------------------------------
    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre=?, primer_apellido=?, segundo_apellido=?, dni_nif=?, " +
                "telefono=?, email=?, direccion=?, ciudad=?, provincia=?, codigo_postal=? WHERE id_cliente=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getPrimer_apellido());
            ps.setString(3, cliente.getSegundo_apellido());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setString(7, cliente.getDireccion());
            ps.setString(8, cliente.getCiudad());
            ps.setString(9, cliente.getProvincia());
            ps.setString(10, cliente.getCodigo_postal());
            ps.setInt(11, cliente.getId_cliente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Eliminar cliente por ID
    // -------------------------------
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

    // -------------------------------
    // Obtener cliente por ID
    // -------------------------------
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

    // -------------------------------
    // Obtener todos los clientes
    // -------------------------------
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

    // -------------------------------
    // Mapear ResultSet → Cliente
    // -------------------------------
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId_cliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setPrimer_apellido(rs.getString("primer_apellido"));
        c.setSegundo_apellido(rs.getString("segundo_apellido"));
        c.setDni(rs.getString("dni_nif"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setCiudad(rs.getString("ciudad"));
        c.setProvincia(rs.getString("provincia"));
        c.setCodigo_postal(rs.getString("codigo_postal"));
        return c;
    }

    // -------------------------------
    // Obtener cliente por DNI
    // -------------------------------
    public Cliente obtenerPorDni(String dni) {
        String sql = "SELECT * FROM clientes WHERE dni_nif=?";
        Cliente cliente = null;
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni.toUpperCase()); // Normalizamos a mayúsculas
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) cliente = mapearCliente(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cliente por DNI: " + e.getMessage());
        }
        return cliente;
    }

}
