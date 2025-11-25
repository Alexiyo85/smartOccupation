package com.smartoccupation.dao;

import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoCobroDAO {

    public EstadoCobroDAO() {
        // Constructor vacío
    }

    public List<EstadoCobro> obtenerTodos() {
        String sql = "SELECT id_estado, nombre_estado FROM estados_cobro ORDER BY id_estado";
        List<EstadoCobro> lista = new ArrayList<>();

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new EstadoCobro(
                        rs.getInt("id_estado"),
                        rs.getString("nombre_estado")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo estados de cobro", e);
        }

        return lista;
    }

    public EstadoCobro obtenerPorId(int id) {
        String sql = "SELECT id_estado, nombre_estado FROM estados_cobro WHERE id_estado = ?";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EstadoCobro(
                            rs.getInt("id_estado"),
                            rs.getString("nombre_estado")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando estado de cobro por id", e);
        }

        return null;
    }

    public EstadoCobro obtenerPorNombre(String nombre) {
        String sql = "SELECT id_estado, nombre_estado FROM estados_cobro WHERE nombre_estado = ?";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new EstadoCobro(
                            rs.getInt("id_estado"),
                            rs.getString("nombre_estado")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando estado de cobro por nombre", e);
        }

        return null;
    }
}
