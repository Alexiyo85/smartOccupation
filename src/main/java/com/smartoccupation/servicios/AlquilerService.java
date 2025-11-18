package com.smartoccupation.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Vivienda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AlquilerService {

    private final AlquilerDAO alquilerDAO = new AlquilerDAO();
    private final ViviendaDAO viviendaDAO = new ViviendaDAO();
    private final EstadoCobroDAO estadoDAO = new EstadoCobroDAO();

    // Crear un alquiler solo si la vivienda está disponible
    public boolean crearAlquiler(Alquiler alquiler) {
        Vivienda vivienda = viviendaDAO.obtenerPorId(alquiler.getId_vivienda());
        if (vivienda == null) {
            throw new IllegalArgumentException("La vivienda no existe");
        }
        if (!vivienda.getEstado().equals("disponible")) {
            throw new IllegalStateException("La vivienda no está disponible");
        }

        // Calcular fecha fin y precio total si no están definidos
        alquiler.calcularFechaFin();
        if (alquiler.getPrecio_total_estimado() == null) {
            alquiler.calcularPrecioTotal(vivienda.getPrecio_mensual().divide(BigDecimal.valueOf(30)));
        }

        // Estado inicial pendiente
        EstadoCobro estadoPendiente = estadoDAO.obtenerPorNombre("pendiente");
        alquiler.setId_estado_cobro(estadoPendiente.getId_estado());

        boolean exito = alquilerDAO.insertar(alquiler);
        if (exito) {
            vivienda.setEstado("ocupado");
            viviendaDAO.actualizar(vivienda);
        }
        return exito;
    }

    public boolean actualizarAlquiler(Alquiler alquiler) {
        alquiler.calcularFechaFin();
        return alquilerDAO.actualizar(alquiler);
    }

    public boolean eliminarAlquiler(int numeroExpediente) {
        Alquiler a = alquilerDAO.obtenerPorId(numeroExpediente);
        if (a == null) return false;

        Vivienda vivienda = viviendaDAO.obtenerPorId(a.getId_vivienda());
        if (vivienda != null) {
            vivienda.setEstado("disponible");
            viviendaDAO.actualizar(vivienda);
        }
        return alquilerDAO.eliminar(numeroExpediente);
    }

    public Alquiler obtenerAlquiler(int numeroExpediente) {
        return alquilerDAO.obtenerPorId(numeroExpediente);
    }

    public List<Alquiler> obtenerTodos() {
        return alquilerDAO.obtenerTodos();
    }

    public List<Alquiler> obtenerPorCliente(int idCliente) {
        return alquilerDAO.obtenerPorCliente(idCliente);
    }

    public List<Alquiler> obtenerPorVivienda(int idVivienda) {
        return alquilerDAO.obtenerPorVivienda(idVivienda);
    }

    public List<Alquiler> obtenerAlquileresPendientes() {
        return alquilerDAO.obtenerPendientesPago();
    }

    public List<Alquiler> obtenerAlquileresPagados() {
        return alquilerDAO.obtenerPagados();
    }
}