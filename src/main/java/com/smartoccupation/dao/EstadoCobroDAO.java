package com.smartoccupation.dao;

import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.utilidades.ConexionBBDD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO (Data Access Object) para la entidad {@link com.smartoccupation.modelo.EstadoCobro}.
 * <p>
 * Esta clase se encarga de las operaciones de lectura (consultas) sobre la tabla
 * 'estados_cobro', la cual contiene los diferentes estados que puede tener un
 * cobro (ej: pendiente, pagado, retrasado).
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class EstadoCobroDAO {

    /**
     * Constructor vacío por defecto.
     */
    public EstadoCobroDAO() {
        // Constructor vacío
    }

    /**
     * Obtiene una lista con todos los estados de cobro disponibles en la base de datos.
     *
     * @return Una lista de objetos {@link EstadoCobro}, ordenados por ID.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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
            // Se lanza una excepción de tiempo de ejecución para simplificar la capa de acceso a datos
            throw new RuntimeException("Error obteniendo estados de cobro", e);
        }

        return lista;
    }

    /**
     * Obtiene un estado de cobro específico por su identificador único.
     *
     * @param id El identificador (PK) del estado de cobro a buscar.
     * @return El objeto {@link EstadoCobro} encontrado o {@code null} si no existe un estado con ese ID.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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

    /**
     * Obtiene un estado de cobro específico por su nombre (ej: "pagado", "pendiente").
     *
     * @param nombre El nombre del estado de cobro a buscar.
     * @return El objeto {@link EstadoCobro} encontrado o {@code null} si no existe un estado con ese nombre.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
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
