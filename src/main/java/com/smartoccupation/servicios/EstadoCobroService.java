package com.smartoccupation.servicios;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;

import java.util.List;

/**
 * Clase de Servicio (Business Logic Layer) para la entidad EstadoCobro.
 * <p>
 * Dado que esta entidad es una tabla de referencia (catálogo), el servicio
 * simplemente actúa como un intermediario directo para las consultas
 * del {@link EstadoCobroDAO}, sin aplicar lógica de negocio adicional (CRUD
 * no implementado ya que los estados no deberían modificarse frecuentemente).
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class EstadoCobroService {

    private final EstadoCobroDAO dao;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param dao El DAO para la entidad EstadoCobro. No debe ser nulo.
     */
    public EstadoCobroService(EstadoCobroDAO dao) {
        // En un entorno real, se podría añadir:
        // if (dao == null) throw new IllegalArgumentException("EstadoCobroDAO no puede ser nulo");
        this.dao = dao;
    }

    /**
     * Obtiene una lista con todos los estados de cobro disponibles.
     *
     * @return Una lista de objetos {@code EstadoCobro}.
     */
    public List<EstadoCobro> obtenerTodos() {
        return dao.obtenerTodos();
    }

    /**
     * Obtiene un estado de cobro por su identificador único.
     *
     * @param id El identificador (PK) del estado de cobro a buscar.
     * @return El objeto {@code EstadoCobro} encontrado o {@code null} si no existe.
     */
    public EstadoCobro obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }
    
    /**
     * Obtiene un EstadoCobro por su nombre.
     * Este método llama al DAO para usar la funcionalidad ya implementada.
     * * @param nombre El nombre del estado (e.g., "pagado", "pendiente").
     * @return El objeto {@code EstadoCobro}, o {@code null} si no se encuentra.
     */
    public EstadoCobro obtenerPorNombre(String nombre) {
        return dao.obtenerPorNombre(nombre);
    }
}