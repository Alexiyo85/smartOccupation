package com.smartoccupation.dao;

import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla "estados_cobro".
 * Permite gestionar los estados de cobro de los alquileres.
 */
public class EstadoCobroDAO {

    // -------------------------------
    // Insertar un nuevo estado
    // -------------------------------
    public boolean insertar(EstadoCobro estado) {
        String sql = "INSERT INTO estados_cobro (nombre_estado) VALUES (?)";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, estado.getNombre_estado());

            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    estado.setId_estado(rs.getInt(1));
                }
            }

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar estado de cobro: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Obtener estado por id
    // -------------------------------
    public EstadoCobro obtenerPorId(int idEstado) {
        String sql = "SELECT * FROM estados_cobro WHERE id_estado = ?";
        EstadoCobro estado = null;

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estado = mapearEstado(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener estado por id: " + e.getMessage());
        }

        return estado;
    }

    // -------------------------------
    // Obtener estado por nombre
    // -------------------------------
    public EstadoCobro obtenerPorNombre(String nombre) {
        String sql = "SELECT * FROM estados_cobro WHERE nombre_estado = ?";
        EstadoCobro estado = null;

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estado = mapearEstado(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener estado por nombre: " + e.getMessage());
        }

        return estado;
    }

    // -------------------------------
    // Obtener todos los estados
    // -------------------------------
    public List<EstadoCobro> obtenerTodos() {
        String sql = "SELECT * FROM estados_cobro";
        List<EstadoCobro> lista = new ArrayList<>();

        try (Connection conn = ConexionBBDD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearEstado(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener todos los estados: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------
    // Mapear ResultSet a objeto EstadoCobro
    // -------------------------------
    private EstadoCobro mapearEstado(ResultSet rs) throws SQLException {
        EstadoCobro estado = new EstadoCobro();
        estado.setId_estado(rs.getInt("id_estado"));
        estado.setNombre_estado(rs.getString("nombre_estado"));
        return estado;
    }
}