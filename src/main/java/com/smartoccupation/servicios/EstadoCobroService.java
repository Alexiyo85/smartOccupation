package com.smartoccupation.servicios;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;

import java.util.List;

/**
 * Servicio para gestionar la lógica de negocio de los estados de cobro.
 */
public class EstadoCobroService {

    private final EstadoCobroDAO estadoCobroDAO;

    public EstadoCobroService() {
        this.estadoCobroDAO = new EstadoCobroDAO();
    }

    // Métodos CRUD completos
    public List<EstadoCobro> obtenerTodos() {
        return estadoCobroDAO.obtenerTodos();
    }

    public EstadoCobro obtenerEstadoCobroPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de estado de cobro inválido.");
        }
        return estadoCobroDAO.obtenerPorId(id);
    }

    public boolean crearEstadoCobro(EstadoCobro estado) throws IllegalArgumentException {
        if (estado == null) {
            throw new IllegalArgumentException("El estado de cobro no puede ser nulo.");
        }
        // Usamos el alias getNombre() para la lógica de negocio.
        if (estado.getNombre() == null || estado.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado es obligatorio.");
        }

        EstadoCobro existente = estadoCobroDAO.obtenerPorNombre(estado.getNombre());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un estado con el nombre: " + estado.getNombre());
        }

        return estadoCobroDAO.insertar(estado);
    }

    public boolean actualizarEstadoCobro(EstadoCobro estado) throws IllegalArgumentException {
        if (estado == null || estado.getId_estado() == null || estado.getId_estado() <= 0) {
            throw new IllegalArgumentException("El estado de cobro o su ID para actualizar no son válidos.");
        }
        if (estado.getNombre() == null || estado.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre del estado es obligatorio.");
        }

        // Verificar duplicados que no sean el mismo objeto que estamos editando
        EstadoCobro existente = estadoCobroDAO.obtenerPorNombre(estado.getNombre());
        if (existente != null && !existente.getId_estado().equals(estado.getId_estado())) {
            throw new IllegalArgumentException("Ya existe otro estado con el nombre: " + estado.getNombre());
        }

        return estadoCobroDAO.actualizar(estado);
    }

    public boolean eliminarEstadoCobro(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de estado de cobro inválido para eliminar.");
        }
        return estadoCobroDAO.eliminar(id);
    }
}
