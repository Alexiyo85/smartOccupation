package com.smartoccupation.servicios;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;

import java.util.List;

public class EstadoCobroService {

    private final EstadoCobroDAO estadoCobroDAO;

    public EstadoCobroService(EstadoCobroDAO estadoCobroDAO) {
        if (estadoCobroDAO == null) throw new IllegalArgumentException("DAO no puede ser nulo");
        this.estadoCobroDAO = estadoCobroDAO;
    }

    public List<EstadoCobro> obtenerTodos() {
        return estadoCobroDAO.obtenerTodos();
    }

    public EstadoCobro obtenerEstadoCobroPorId(int id) {
        return estadoCobroDAO.obtenerPorId(id);
    }

    public boolean crearEstadoCobro(EstadoCobro estado) {
        EstadoCobro existente = estadoCobroDAO.obtenerPorNombre(estado.getNombre());
        if (existente != null) throw new IllegalArgumentException("Ya existe un estado con ese nombre");
        return estadoCobroDAO.insertar(estado);
    }

    public boolean actualizarEstadoCobro(EstadoCobro estado) {
        return estadoCobroDAO.actualizar(estado);
    }

    public boolean eliminarEstadoCobro(int id) {
        return estadoCobroDAO.eliminar(id);
    }
}
