package com.smartoccupation.servicios;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;
import java.util.List;

/**
 * Clase de Servicio (Business Logic Layer) para la entidad Vivienda.
 * <p>
 * Gestiona las operaciones de negocio relacionadas con las propiedades de alquiler,
 * aplicando validaciones y reglas, como la restricción de eliminar solo
 * viviendas que estén en estado "disponible".
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ViviendaService {

    private final ViviendaDAO viviendaDAO;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param viviendaDAO El DAO para la entidad Vivienda.
     * @throws IllegalArgumentException Si el DAO proporcionado es nulo.
     */
    public ViviendaService(ViviendaDAO viviendaDAO) {
        if (viviendaDAO == null) {
            throw new IllegalArgumentException("DAO no puede ser nulo");
        }
        this.viviendaDAO = viviendaDAO;
    }

    /**
     * Crea un nuevo registro de vivienda en la base de datos.
     *
     * @param vivienda El objeto {@code Vivienda} a crear.
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario.
     */
    public boolean crearVivienda(Vivienda vivienda) {
        // En este punto se podría añadir lógica de negocio, como verificar la unicidad
        // del código de referencia si no se hiciera ya en la base de datos o el DAO.
        return viviendaDAO.insertar(vivienda);
    }

    /**
     * Actualiza los datos de un registro de vivienda existente.
     *
     * @param vivienda El objeto {@code Vivienda} con los datos actualizados.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizarVivienda(Vivienda vivienda) {
        return viviendaDAO.actualizar(vivienda);
    }

    /**
     * Elimina la vivienda por su ID solo si su estado actual es **"disponible"**.
     * <p>
     * Esta es una regla de negocio para evitar eliminar propiedades que
     * se encuentren actualmente ocupadas o en otro estado que impida su borrado.
     * </p>
     *
     * @param idVivienda El ID (PK) de la vivienda a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} si la vivienda no existe.
     * @throws IllegalStateException Si la vivienda existe, pero su estado no es "disponible".
     */
    public boolean eliminarVivienda(int idVivienda) {
        Vivienda v = viviendaDAO.obtenerPorId(idVivienda);
        if (v == null) {
            return false;
        }
        
        // Lógica de negocio: bloquea la eliminación si no está "disponible"
        if (!"disponible".equalsIgnoreCase(v.getEstado())) {
            throw new IllegalStateException("No se puede eliminar una vivienda que no esté disponible. Estado actual: " + v.getEstado());
        }
        return viviendaDAO.eliminar(idVivienda);
    }

    /**
     * Obtiene una vivienda por su identificador único.
     *
     * @param idVivienda El ID (PK) de la vivienda a buscar.
     * @return El objeto {@code Vivienda} encontrado o {@code null} si no existe.
     */
    public Vivienda obtenerVivienda(int idVivienda) {
        return viviendaDAO.obtenerPorId(idVivienda);
    }

    /**
     * Obtiene una lista con todas las viviendas registradas.
     *
     * @return Una lista de objetos {@code Vivienda}.
     */
    public List<Vivienda> obtenerTodas() {
        return viviendaDAO.obtenerTodos();
    }

    /**
     * Obtiene una lista de viviendas que se encuentran en un estado específico.
     *
     * @param estado El estado de la vivienda (ej: "disponible", "ocupado").
     * @return Una lista de objetos {@code Vivienda} que coinciden con el estado.
     */
    public List<Vivienda> obtenerPorEstado(String estado) {
        return viviendaDAO.obtenerPorEstado(estado);
    }
}