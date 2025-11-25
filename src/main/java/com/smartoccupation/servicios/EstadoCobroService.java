package com.smartoccupation.servicios;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;

import java.util.List;

public class EstadoCobroService {

    private final EstadoCobroDAO dao;

    public EstadoCobroService(EstadoCobroDAO dao) {
        this.dao = dao;
    }

    public List<EstadoCobro> obtenerTodos() {
        return dao.obtenerTodos();
    }

    public EstadoCobro obtenerPorId(int id) {
        return dao.obtenerPorId(id);
    }
    
    /**
     * Obtiene un EstadoCobro por su nombre.
     * Este método llama al DAO para usar la funcionalidad ya implementada.
     * @param nombre El nombre del estado (e.g., "pagado").
     * @return El objeto EstadoCobro, o null si no se encuentra.
     */
    public EstadoCobro obtenerPorNombre(String nombre) { // 👈 MÉTODO AÑADIDO
        return dao.obtenerPorNombre(nombre);
    }
}