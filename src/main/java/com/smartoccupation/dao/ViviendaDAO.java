package com.smartoccupation.dao;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la tabla "viviendas".
 * CRUD y consultas por estado, rango de precio o código de referencia.
 */
public class ViviendaDAO {

    // -------------------------------
    // Insertar vivienda
    // -------------------------------
    public boolean insertar(Vivienda vivienda) {
        String sql = "INSERT INTO viviendas " +
                "(codigo_referencia, direccion, ciudad, provincia, codigo_postal, metros_cuadrados, " +
                "numero_habitaciones, numero_banios, precio_mensual, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vivienda.getCodigo_referencia());
            ps.setString(2, vivienda.getDireccion());
            ps.setString(3, vivienda.getCiudad());
            ps.setString(4, vivienda.getProvincia());
            ps.setString(5, vivienda.getCodigo_postal());
            ps.setInt(6, vivienda.getMetros_cuadrados());
            ps.setInt(7, vivienda.getNumero_habitaciones());
            ps.setInt(8, vivienda.getNumero_banios());
            ps.setBigDecimal(9, vivienda.getPrecio_mensual());
            ps.setString(10, vivienda.getEstado());

            int filas = ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) vivienda.setId_vivienda(rs.getInt(1));
            }
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar vivienda: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Actualizar vivienda
    // -------------------------------
    public boolean actualizar(Vivienda vivienda) {
        String sql = "UPDATE viviendas SET codigo_referencia=?, direccion=?, ciudad=?, provincia=?, codigo_postal=?, " +
                "metros_cuadrados=?, numero_habitaciones=?, numero_banios=?, precio_mensual=?, estado=? WHERE id_vivienda=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vivienda.getCodigo_referencia());
            ps.setString(2, vivienda.getDireccion());
            ps.setString(3, vivienda.getCiudad());
            ps.setString(4, vivienda.getProvincia());
            ps.setString(5, vivienda.getCodigo_postal());
            ps.setInt(6, vivienda.getMetros_cuadrados());
            ps.setInt(7, vivienda.getNumero_habitaciones());
            ps.setInt(8, vivienda.getNumero_banios());
            ps.setBigDecimal(9, vivienda.getPrecio_mensual());
            ps.setString(10, vivienda.getEstado());
            ps.setInt(11, vivienda.getId_vivienda());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar vivienda: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Eliminar vivienda
    // -------------------------------
    public boolean eliminar(int idVivienda) {
        String sql = "DELETE FROM viviendas WHERE id_vivienda=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVivienda);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar vivienda: " + e.getMessage());
            return false;
        }
    }

    // -------------------------------
    // Obtener vivienda por ID
    // -------------------------------
    public Vivienda obtenerPorId(int idVivienda) {
        String sql = "SELECT * FROM viviendas WHERE id_vivienda=?";
        Vivienda v = null;
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVivienda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) v = mapearVivienda(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener vivienda: " + e.getMessage());
        }
        return v;
    }

    // -------------------------------
    // Obtener todas las viviendas
    // -------------------------------
    public List<Vivienda> obtenerTodos() {
        List<Vivienda> lista = new ArrayList<>();
        String sql = "SELECT * FROM viviendas ORDER BY id_vivienda";
        try (Connection conn = ConexionBBDD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapearVivienda(rs));
        } catch (SQLException e) {
            System.out.println("Error al obtener todas las viviendas: " + e.getMessage());
        }
        return lista;
    }

    // -------------------------------
    // Consultas adicionales
    // -------------------------------
    public List<Vivienda> obtenerPorEstado(String estado) {
        List<Vivienda> lista = new ArrayList<>();
        String sql = "SELECT * FROM viviendas WHERE estado=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearVivienda(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener viviendas por estado: " + e.getMessage());
        }
        return lista;
    }

    public List<Vivienda> obtenerPorRangoPrecio(BigDecimal min, BigDecimal max) {
        List<Vivienda> lista = new ArrayList<>();
        String sql = "SELECT * FROM viviendas WHERE precio_mensual BETWEEN ? AND ?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, min);
            ps.setBigDecimal(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapearVivienda(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener viviendas por rango de precio: " + e.getMessage());
        }
        return lista;
    }

    public Vivienda obtenerPorCodigoReferencia(String codigoReferencia) {
        Vivienda v = null;
        String sql = "SELECT * FROM viviendas WHERE codigo_referencia=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) v = mapearVivienda(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener vivienda por código: " + e.getMessage());
        }
        return v;
    }

    // -------------------------------
    // Mapear ResultSet → Vivienda
    // -------------------------------
    private Vivienda mapearVivienda(ResultSet rs) throws SQLException {
        Vivienda v = new Vivienda();
        v.setId_vivienda(rs.getInt("id_vivienda"));
        v.setCodigo_referencia(rs.getString("codigo_referencia"));
        v.setDireccion(rs.getString("direccion"));
        v.setCiudad(rs.getString("ciudad"));
        v.setProvincia(rs.getString("provincia"));
        v.setCodigo_postal(rs.getString("codigo_postal"));
        v.setMetros_cuadrados(rs.getInt("metros_cuadrados"));
        v.setNumero_habitaciones(rs.getInt("numero_habitaciones"));
        v.setNumero_banios(rs.getInt("numero_banios"));
        v.setPrecio_mensual(rs.getBigDecimal("precio_mensual"));
        v.setEstado(rs.getString("estado"));
        return v;
    }
}