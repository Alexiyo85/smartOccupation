package com.smartoccupation.dao;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlquilerDAO {

    private static final String ESTADO_PENDIENTE = "pendiente";
    private static final String ESTADO_PAGADO = "pagado";
    // Nota: La constante RETRASADO está definida pero no se usa en este DAO.
    // private static final String ESTADO_RETRASADO = "retrasado"; 

    public boolean insertar(Alquiler alquiler) {
        String sql = "INSERT INTO alquileres (fecha_inicio, tiempo_meses, tiempo_dias, fecha_fin_estimada, precio_total_estimado, id_cliente, id_vivienda, id_estado_cobro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(alquiler.getFecha_inicio()));
            ps.setInt(2, alquiler.getTiempo_meses());
            ps.setInt(3, alquiler.getTiempo_dias());
            if (alquiler.getFecha_fin_estimada() != null) {
                ps.setDate(4, Date.valueOf(alquiler.getFecha_fin_estimada()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBigDecimal(5, alquiler.getPrecio_total_estimado());
            ps.setInt(6, alquiler.getId_cliente());
            ps.setInt(7, alquiler.getId_vivienda());
            ps.setInt(8, alquiler.getId_estado_cobro());

            int filas = ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    alquiler.setNumero_expediente(rs.getInt(1));
                }
            }

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("ERROR al insertar alquiler: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Alquiler alquiler) {
        String sql = "UPDATE alquileres SET fecha_inicio=?, tiempo_meses=?, tiempo_dias=?, fecha_fin_estimada=?, precio_total_estimado=?, id_cliente=?, id_vivienda=?, id_estado_cobro=? WHERE numero_expediente=?";
        try (Connection conn = ConexionBBDD.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(alquiler.getFecha_inicio()));
            ps.setInt(2, alquiler.getTiempo_meses());
            ps.setInt(3, alquiler.getTiempo_dias());
            if (alquiler.getFecha_fin_estimada() != null) {
                ps.setDate(4, Date.valueOf(alquiler.getFecha_fin_estimada()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBigDecimal(5, alquiler.getPrecio_total_estimado());
            ps.setInt(6, alquiler.getId_cliente());
            ps.setInt(7, alquiler.getId_vivienda());
            ps.setInt(8, alquiler.getId_estado_cobro());
            ps.setInt(9, alquiler.getNumero_expediente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("ERROR al actualizar alquiler: " + e.getMessage());
            return false;
        }
    }

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

    public List<Alquiler> obtenerPendientesPago() {
        return obtenerPorEstadoNombre(ESTADO_PENDIENTE);
    }

    public List<Alquiler> obtenerPagados() {
        return obtenerPorEstadoNombre(ESTADO_PAGADO);
    }

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

    private Alquiler mapearAlquiler(ResultSet rs) throws SQLException {
        Alquiler a = new Alquiler();
        a.setNumero_expediente(rs.getInt("numero_expediente"));
        a.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
        a.setTiempo_meses(rs.getInt("tiempo_meses"));
        a.setTiempo_dias(rs.getInt("tiempo_dias"));

        Date fechaFin = rs.getDate("fecha_fin_estimada");
        a.setFecha_fin_estimada(fechaFin != null ? fechaFin.toLocalDate() : null);

        a.setPrecio_total_estimado(rs.getBigDecimal("precio_total_estimado"));
        a.setId_cliente(rs.getInt("id_cliente"));
        a.setId_vivienda(rs.getInt("id_vivienda"));
        a.setId_estado_cobro(rs.getInt("id_estado_cobro"));

        return a;
    }
}
