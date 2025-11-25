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
}
