package com.smartoccupation.dao;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para gestionar las operaciones CRUD y consultas
 * relacionadas con la entidad {@link com.smartoccupation.modelo.Vivienda} en la
 * base de datos.
 * <p>
 * Permite manejar la persistencia de las propiedades de alquiler.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ViviendaDAO {

    /**
     * Inserta un nuevo registro de vivienda en la base de datos.
     * El ID de la vivienda (PK) se genera automáticamente y se asigna
     * al objeto {@code Vivienda} de entrada.
     *
     * @param vivienda El objeto {@code Vivienda} a insertar.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     */
    public boolean insertar(Vivienda vivienda) {
        String sql = "INSERT INTO viviendas " +
                "(codigo_referencia, direccion, ciudad, provincia, codigo_postal, metros_cuadrados, " +
                "numero_habitaciones, numero_banios, precio_mensual, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, vivienda.getCodigoReferencia());
            ps.setString(2, vivienda.getDireccion());
            ps.setString(3, vivienda.getCiudad());
            ps.setString(4, vivienda.getProvincia());
            ps.setString(5, vivienda.getCodigoPostal());
            ps.setInt(6, vivienda.getMetrosCuadrados());
            ps.setInt(7, vivienda.getNumeroHabitaciones());
            ps.setInt(8, vivienda.getNumeroBanios());
            ps.setBigDecimal(9, vivienda.getPrecio_mensual());
            ps.setString(10, vivienda.getEstado());

            int filas = ps.executeUpdate();
            // Recupera la clave generada
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) vivienda.setIdVivienda(rs.getInt(1));
            }
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar vivienda: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un registro de vivienda existente en la base de datos.
     *
     * @param vivienda El objeto {@code Vivienda} con los datos actualizados, incluyendo el ID (PK).
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizar(Vivienda vivienda) {
        String sql = "UPDATE viviendas SET codigo_referencia=?, direccion=?, ciudad=?, provincia=?, codigo_postal=?, " +
                "metros_cuadrados=?, numero_habitaciones=?, numero_banios=?, precio_mensual=?, estado=? WHERE id_vivienda=?";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vivienda.getCodigoReferencia());
            ps.setString(2, vivienda.getDireccion());
            ps.setString(3, vivienda.getCiudad());
            ps.setString(4, vivienda.getProvincia());
            ps.setString(5, vivienda.getCodigoPostal());
            ps.setInt(6, vivienda.getMetrosCuadrados());
            ps.setInt(7, vivienda.getNumeroHabitaciones());
            ps.setInt(8, vivienda.getNumeroBanios());
            ps.setBigDecimal(9, vivienda.getPrecio_mensual());
            ps.setString(10, vivienda.getEstado());
            ps.setInt(11, vivienda.getIdVivienda());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar vivienda: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de vivienda de la base de datos utilizando su ID.
     *
     * @param idVivienda El ID (PK) de la vivienda a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario.
     */
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

    /**
     * Obtiene un registro de vivienda por su identificador único.
     *
     * @param idVivienda El ID (PK) de la vivienda a buscar.
     * @return El objeto {@code Vivienda} encontrado o {@code null} si no existe.
     */
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

    /**
     * Obtiene una lista con todos los registros de viviendas, ordenados por ID.
     *
     * @return Una lista de objetos {@code Vivienda}.
     */
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
    
    /**
     * Obtiene una lista de viviendas que se encuentran en un estado específico (ej: "disponible", "alquilada").
     *
     * @param estado El estado de la vivienda por el que filtrar.
     * @return Una lista de objetos {@code Vivienda} que coinciden con el estado.
     */
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

    /**
     * Obtiene una lista de viviendas cuyo precio mensual de alquiler se encuentra entre los valores mínimo y máximo especificados.
     *
     * @param min El precio mensual mínimo (inclusivo).
     * @param max El precio mensual máximo (inclusivo).
     * @return Una lista de objetos {@code Vivienda} dentro del rango de precios.
     */
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

    /**
     * Obtiene un registro de vivienda utilizando su código de referencia único.
     *
     * @param codigoReferencia El código de referencia de la vivienda a buscar.
     * @return El objeto {@code Vivienda} encontrado o {@code null} si no existe.
     */
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

    /**
     * Método auxiliar para mapear una fila del {@code ResultSet} a un objeto {@code Vivienda}.
     *
     * @param rs El {@code ResultSet} apuntando a la fila actual.
     * @return Un objeto {@code Vivienda} poblado con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a los datos de la base de datos.
     */
    private Vivienda mapearVivienda(ResultSet rs) throws SQLException {
        Vivienda v = new Vivienda();
        v.setIdVivienda(rs.getInt("id_vivienda"));
        v.setCodigoReferencia(rs.getString("codigo_referencia"));
        v.setDireccion(rs.getString("direccion"));
        v.setCiudad(rs.getString("ciudad"));
        v.setProvincia(rs.getString("provincia"));
        v.setCodigoPostal(rs.getString("codigo_postal"));
        v.setMetrosCuadrados(rs.getInt("metros_cuadrados"));
        v.setNumeroHabitaciones(rs.getInt("numero_habitaciones"));
        v.setNumeroBanios(rs.getInt("numero_banios"));
        v.setPrecioMensual(rs.getBigDecimal("precio_mensual"));
        v.setEstado(rs.getString("estado"));
        return v;
    }
}