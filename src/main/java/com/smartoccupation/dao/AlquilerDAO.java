package com.smartoccupation.dao;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para gestionar las operaciones CRUD y consultas
 * relacionadas con la entidad {@link com.smartoccupation.modelo.Alquiler} en la
 * base de datos.
 * <p>
 * Se utiliza {@link com.smartoccupation.utilidades.ConexionBBDD} para obtener
 * la conexión y se manejan las excepciones SQL internamente.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class AlquilerDAO {

    /**
     * Constante para el estado de cobro "pendiente".
     */
    private static final String ESTADO_PENDIENTE = "pendiente";
    /**
     * Constante para el estado de cobro "pagado".
     */
    private static final String ESTADO_PAGADO = "pagado";
    // Nota: La constante RETRASADO está definida pero no se usa en este DAO.
    // private static final String ESTADO_RETRASADO = "retrasado";

    /**
     * Inserta un nuevo registro de alquiler en la base de datos.
     * El número de expediente (PK) se genera automáticamente y se asigna
     * al objeto {@code Alquiler} de entrada.
     *
     * @param alquiler El objeto {@code Alquiler} a insertar, sin el número de expediente.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     */
    public boolean insertar(Alquiler alquiler) {
        String sql = "INSERT INTO alquileres (fecha_inicio, tiempo_meses, tiempo_dias, fecha_fin_estimada, precio_total_estimado, id_cliente, id_vivienda, id_estado_cobro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(alquiler.getFechaInicio()));
            ps.setInt(2, alquiler.getTiempoMeses());
            ps.setInt(3, alquiler.getTiempoDias());
            if (alquiler.getFechaFinEstimada() != null) {
                ps.setDate(4, Date.valueOf(alquiler.getFechaFinEstimada()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBigDecimal(5, alquiler.getPrecioTotalEstimado());
            ps.setInt(6, alquiler.getIdCliente());
            ps.setInt(7, alquiler.getIdVivienda());
            ps.setInt(8, alquiler.getIdEstadoCobro());

            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    alquiler.setNumeroExpediente(rs.getInt(1));
                }
            }

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("ERROR al insertar alquiler: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un registro de alquiler existente en la base de datos.
     *
     * @param alquiler El objeto {@code Alquiler} con los datos actualizados, incluyendo el número de expediente.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizar(Alquiler alquiler) {
        String sql = "UPDATE alquileres SET fecha_inicio=?, tiempo_meses=?, tiempo_dias=?, fecha_fin_estimada=?, precio_total_estimado=?, id_cliente=?, id_vivienda=?, id_estado_cobro=? WHERE numero_expediente=?";
        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(alquiler.getFechaInicio()));
            ps.setInt(2, alquiler.getTiempoMeses());
            ps.setInt(3, alquiler.getTiempoDias());
            if (alquiler.getFechaFinEstimada() != null) {
                ps.setDate(4, Date.valueOf(alquiler.getFechaFinEstimada()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBigDecimal(5, alquiler.getPrecioTotalEstimado());
            ps.setInt(6, alquiler.getIdCliente());
            ps.setInt(7, alquiler.getIdVivienda());
            ps.setInt(8, alquiler.getIdEstadoCobro());
            ps.setInt(9, alquiler.getNumeroExpediente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("ERROR al actualizar alquiler: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un registro de alquiler de la base de datos utilizando su número de expediente.
     *
     * @param numeroExpediente El ID (PK) del alquiler a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario.
     */
    public boolean eliminar(int numeroExpediente) {
        String sql = "DELETE FROM alquileres WHERE numero_expediente=?";
        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroExpediente);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("ERROR al eliminar alquiler: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene un registro de alquiler por su número de expediente.
     *
     * @param numeroExpediente El ID (PK) del alquiler a buscar.
     * @return El objeto {@code Alquiler} encontrado o {@code null} si no existe.
     */
    public Alquiler obtenerPorId(int numeroExpediente) {
        String sql = "SELECT * FROM alquileres WHERE numero_expediente=?";
        Alquiler alquiler = null;

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroExpediente);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    alquiler = mapearAlquiler(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR al obtener alquiler por ID: " + e.getMessage());
        }
        return alquiler;
    }

    /**
     * Obtiene una lista con todos los registros de alquileres ordenados por número de expediente.
     *
     * @return Una lista de objetos {@code Alquiler}.
     */
    public List<Alquiler> obtenerTodos() {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres ORDER BY numero_expediente";

        try (Connection conn = ConexionBBDD.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapearAlquiler(rs));
            }

        } catch (SQLException e) {
            System.out.println("ERROR al obtener lista de alquileres: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene una lista de alquileres asociados a un cliente específico.
     *
     * @param idCliente El ID del cliente.
     * @return Una lista de objetos {@code Alquiler} de ese cliente.
     */
    public List<Alquiler> obtenerPorCliente(int idCliente) {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres WHERE id_cliente=?";

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlquiler(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR obteniendo alquileres por cliente: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene una lista de alquileres asociados a una vivienda específica.
     *
     * @param idVivienda El ID de la vivienda.
     * @return Una lista de objetos {@code Alquiler} para esa vivienda.
     */
    public List<Alquiler> obtenerPorVivienda(int idVivienda) {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres WHERE id_vivienda=?";

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVivienda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlquiler(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR obteniendo alquileres por vivienda: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene una lista de alquileres cuya fecha de inicio se encuentra dentro de un rango especificado.
     *
     * @param desde La fecha de inicio del rango (inclusiva).
     * @param hasta La fecha de fin del rango (inclusiva).
     * @return Una lista de objetos {@code Alquiler} dentro del rango de fechas.
     */
    public List<Alquiler> obtenerPorRangoFechas(LocalDate desde, LocalDate hasta) {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres WHERE fecha_inicio BETWEEN ? AND ?";

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlquiler(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR obteniendo alquileres por rango de fechas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene una lista de alquileres filtrados por el ID de su estado de cobro.
     *
     * @param idEstado El ID del estado de cobro (ej: 1 para Pendiente, 2 para Pagado).
     * @return Una lista de objetos {@code Alquiler} con el estado de cobro especificado.
     */
    public List<Alquiler> obtenerPorEstado(int idEstado) {
        List<Alquiler> lista = new ArrayList<>();
        String sql = "SELECT * FROM alquileres WHERE id_estado_cobro=?";

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlquiler(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR obteniendo alquileres por estado: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene todos los alquileres cuyo estado de cobro es "pendiente".
     *
     * @return Una lista de objetos {@code Alquiler} pendientes de pago.
     */
    public List<Alquiler> obtenerPendientesPago() {
        return obtenerPorEstadoNombre(ESTADO_PENDIENTE);
    }

    /**
     * Obtiene todos los alquileres cuyo estado de cobro es "pagado".
     *
     * @return Una lista de objetos {@code Alquiler} ya pagados.
     */
    public List<Alquiler> obtenerPagados() {
        return obtenerPorEstadoNombre(ESTADO_PAGADO);
    }

    /**
     * Método auxiliar privado para obtener alquileres filtrando por el nombre del estado de cobro.
     * Realiza un JOIN con la tabla de estados de cobro.
     *
     * @param nombreEstado El nombre del estado de cobro (ej: "pendiente", "pagado").
     * @return Una lista de objetos {@code Alquiler} con el estado de cobro especificado.
     */
    private List<Alquiler> obtenerPorEstadoNombre(String nombreEstado) {
        List<Alquiler> lista = new ArrayList<>();
        String sql = """
                     SELECT a.* FROM alquileres a
                     JOIN estados_cobro e ON a.id_estado_cobro = e.id_estado
                     WHERE e.nombre_estado = ?
                     """;

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreEstado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAlquiler(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR obteniendo alquileres por estado nombre (" + nombreEstado + "): " + e.getMessage());
        }

        return lista;
    }

    /**
     * Obtiene el alquiler activo (estado 'pendiente' o 'retrasado') para una vivienda específica.
     * Asume que solo puede haber un alquiler activo por vivienda.
     *
     * @param idVivienda El ID de la vivienda.
     * @return El objeto {@code Alquiler} activo encontrado o {@code null}.
     */
    public Alquiler obtenerAlquilerActivoPorVivienda(int idVivienda) {
        String sql = """
                     SELECT * FROM alquileres
                     WHERE id_vivienda=?
                     AND id_estado_cobro IN (
                         SELECT id_estado FROM estados_cobro
                         WHERE nombre_estado IN ('pendiente','retrasado')
                     )
                     LIMIT 1
                     """;

        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVivienda);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAlquiler(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("ERROR obteniendo alquiler activo por vivienda: " + e.getMessage());
        }

        return null;
    }

    /**
     * Método auxiliar para mapear una fila del {@code ResultSet} a un objeto {@code Alquiler}.
     *
     * @param rs El {@code ResultSet} apuntando a la fila actual.
     * @return Un objeto {@code Alquiler} poblado con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a los datos de la base de datos.
     */
    private Alquiler mapearAlquiler(ResultSet rs) throws SQLException {
        Alquiler a = new Alquiler();
        a.setNumeroExpediente(rs.getInt("numero_expediente"));
        a.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        a.setTiempoMeses(rs.getInt("tiempo_meses"));
        a.setTiempoDias(rs.getInt("tiempo_dias"));

        Date fechaFin = rs.getDate("fecha_fin_estimada");
        // Convertir java.sql.Date a java.time.LocalDate, manejando valores NULL
        a.setFechaFinEstimada(fechaFin != null ? fechaFin.toLocalDate() : null);

        a.setPrecioTotalEstimado(rs.getBigDecimal("precio_total_estimado"));
        a.setIdCliente(rs.getInt("id_cliente"));
        a.setIdVivienda(rs.getInt("id_vivienda"));
        a.setIdEstadoCobro(rs.getInt("id_estado_cobro"));

        return a;
    }
}