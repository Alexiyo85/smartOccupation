package com.smartoccupation.servicios;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;

import java.util.List;

public class ViviendaService {

    private final ViviendaDAO viviendaDAO = new ViviendaDAO();

    public boolean crearVivienda(Vivienda vivienda) {
        return viviendaDAO.insertar(vivienda);
    }

    public boolean actualizarVivienda(Vivienda vivienda) {
        return viviendaDAO.actualizar(vivienda);
    }

    public boolean eliminarVivienda(int idVivienda) {
        Vivienda v = viviendaDAO.obtenerPorId(idVivienda);
        if (v == null) return false;
        if (!v.getEstado().equals("disponible")) {
            throw new IllegalStateException("No se puede eliminar una vivienda que no esté disponible");
        }
        return viviendaDAO.eliminar(idVivienda);
    }

    public Vivienda obtenerVivienda(int idVivienda) {
        return viviendaDAO.obtenerPorId(idVivienda);
    }

    public List<Vivienda> obtenerTodas() {
        return viviendaDAO.obtenerTodos();
    }

    public List<Vivienda> obtenerPorEstado(String estado) {
        return viviendaDAO.obtenerPorEstado(estado);
    }
}
