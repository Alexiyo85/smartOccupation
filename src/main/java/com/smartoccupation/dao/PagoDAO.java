package com.smartoccupation.dao;

import com.smartoccupation.modelo.Pago;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para la entidad {@link com.smartoccupation.modelo.Pago}.
 * <p>
 * Se encarga de la persistencia de los pagos en la tabla 'pagos' de la base de datos,
 * incluyendo la inserción, eliminación y diversas consultas (por rango de fechas y por expediente).
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class PagoDAO {

    /**
     * Constructor vacío por defecto.
     */
    public PagoDAO() {}

    /**
     * Método auxiliar para mapear una fila del {@code ResultSet} a un objeto {@code Pago}.
     *
     * @param rs El {@code ResultSet} apuntando a la fila actual.
     * @return Un objeto {@code Pago} poblado con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a los datos de la base de datos.
     */
    private Pago map(ResultSet rs) throws SQLException {
        return new Pago(
                rs.getInt("id_pago"),
                rs.getInt("numero_expediente"),
                // Conversión de java.sql.Date a java.time.LocalDate
                rs.getDate("fecha_pago").toLocalDate(),
                rs.getBigDecimal("cantidad")
        );
    }

    /**
     * Inserta un nuevo registro de pago en la base de datos.
     * El ID del pago (PK) se genera automáticamente y se asigna al objeto {@code Pago} de entrada.
     *
     * @param p El objeto {@code Pago} a insertar.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     * @throws RuntimeException Si ocurre un error de SQL durante la inserción.
     */
    public boolean insertar(Pago p) {
        String sql = "INSERT INTO pagos (numero_expediente, fecha_pago, cantidad) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getNumeroExpediente());
            ps.setDate(2, Date.valueOf(p.getFechaPago()));
            ps.setBigDecimal(3, p.getCantidad());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                // Recupera la clave generada
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

    /**
     * Obtiene una lista con todos los pagos registrados en la base de datos, ordenados por fecha de pago descendente.
     *
     * @return Una lista de objetos {@code Pago}.
     * @throws RuntimeException Si ocurre un error de SQL durante la consulta.
     */
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

    /**
     * Busca y obtiene una lista de pagos cuya fecha se encuentra dentro del rango especificado.
     *
     * @param desde La fecha de inicio del rango (inclusiva).
     * @param hasta La fecha de fin del rango (inclusiva).
     * @return Una lista de objetos {@code Pago} dentro del rango de fechas.
     * @throws RuntimeException Si ocurre un error de SQL durante la consulta.
     */
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

    /**
     * Elimina un registro de pago de la base de datos utilizando su ID.
     *
     * @param idPago El ID (PK) del pago a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario.
     * @throws RuntimeException Si ocurre un error de SQL durante la eliminación.
     */
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

    /**
     * Obtiene una lista de pagos asociados a un número de expediente de alquiler específico.
     * Los resultados están ordenados por fecha de pago ascendente.
     *
     * @param numeroExpediente El número del expediente de alquiler.
     * @return Una lista de objetos {@code Pago} para ese expediente.
     * @throws RuntimeException Si ocurre un error de SQL durante la consulta.
     */
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