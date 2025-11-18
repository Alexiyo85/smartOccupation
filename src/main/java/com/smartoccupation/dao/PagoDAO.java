package com.smartoccupation.dao;

import com.smartoccupation.modelo.Pago;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla "pagos".
 * Contiene métodos CRUD y consultas específicas para pagos asociados a alquileres.
 */
public class PagoDAO {

    // -------------------------------
    // Insertar un pago
    // -------------------------------
    public boolean insertar(Pago pago) {
        String sql = "INSERT INTO pagos (numero_expediente, fecha_pago, cantidad) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pago.getNumero_expediente());
            ps.setDate(2, Date.valueOf(pago.getFecha_pago()));
            ps.setBigDecimal(3, pago.getCantidad());

            int filas = ps.executeUpdate();

            // Obtener id generado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pago.setId_pago(rs.getInt(1));
                }
            }

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar pago: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Obtener pagos por número de expediente
    // -------------------------------
    public List<Pago> obtenerPorAlquiler(int numeroExpediente) {
        String sql = "SELECT * FROM pagos WHERE numero_expediente = ?";
        List<Pago> lista = new ArrayList<>();

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroExpediente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPago(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener pagos por alquiler: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------
    // Obtener pagos en un rango de fechas
    // -------------------------------
    public List<Pago> obtenerPorRangoFechas(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT * FROM pagos WHERE fecha_pago BETWEEN ? AND ?";
        List<Pago> lista = new ArrayList<>();

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPago(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener pagos por rango de fechas: " + e.getMessage());
        }

        return lista;
    }

    // -------------------------------
    // Obtener total pagado por alquiler
    // -------------------------------
    public BigDecimal obtenerTotalPagadoPorAlquiler(int numeroExpediente) {
        String sql = "SELECT SUM(cantidad) AS total FROM pagos WHERE numero_expediente = ?";
        BigDecimal total = BigDecimal.ZERO;

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroExpediente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getBigDecimal("total");
                    if (total == null) total = BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al calcular total pagado por alquiler: " + e.getMessage());
        }

        return total;
    }

    // -------------------------------
    // Eliminar pago por id
    // -------------------------------
    public boolean eliminar(int idPago) {
        String sql = "DELETE FROM pagos WHERE id_pago = ?";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar pago: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Mapear ResultSet a objeto Pago
    // -------------------------------
    private Pago mapearPago(ResultSet rs) throws SQLException {
        Pago pago = new Pago();
        pago.setId_pago(rs.getInt("id_pago"));
        pago.setNumero_expediente(rs.getInt("numero_expediente"));
        pago.setFecha_pago(rs.getDate("fecha_pago").toLocalDate());
        pago.setCantidad(rs.getBigDecimal("cantidad"));
        return pago;
    }
}