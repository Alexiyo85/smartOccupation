package com.smartoccupation.dao;

import com.smartoccupation.modelo.Pago;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public PagoDAO() {}

    private Pago map(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("id_pago"),
                rs.getInt("numero_expediente"),
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getBigDecimal("cantidad")
        );
    }

    public boolean insertar(Pago p) {
        String sql = "INSERT INTO pagos (numero_expediente, fecha_pago, cantidad) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getNumero_expediente());
            ps.setDate(2, Date.valueOf(p.getFecha_pago()));
            ps.setBigDecimal(3, p.getCantidad());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        p.setId_pago(rs.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new RuntimeException("Error insertando pago", e);
        }
    }

    public List<Pago> obtenerTodos() {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos ORDER BY fecha_pago DESC";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(map(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando pagos", e);
        }

        return lista;
    }

    public List<Pago> buscarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE fecha_pago BETWEEN ? AND ? ORDER BY fecha_pago DESC";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error consultando pagos por rango", e);
        }

        return lista;
    }

    public boolean eliminar(int idPago) {
        String sql = "DELETE FROM pagos WHERE id_pago=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando pago", e);
        }
    }
    
    public List<Pago> obtenerPorExpediente(int numeroExpediente) {
    List<Pago> lista = new ArrayList<>();
    String sql = "SELECT * FROM pagos WHERE numero_expediente = ? ORDER BY fecha_pago ASC";

    try (Connection conn = ConexionBBDD.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, numeroExpediente);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error consultando pagos por expediente", e);
    }

    return lista;
}

}
