package com.smartoccupation.servicios;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.modelo.Pago;

import java.time.LocalDate;
import java.util.List;

public class PagoService {

    private final PagoDAO dao;

    public PagoService(PagoDAO dao) {
        this.dao = dao;
    }

    public boolean registrarPago(Pago pago) {
        return dao.insertar(pago);
    }

    public List<Pago> listarTodosLosPagos() {
        return dao.obtenerTodos();
    }

    public List<Pago> buscarPagosPorFecha(LocalDate desde, LocalDate hasta) {
        return dao.buscarPorRangoFechas(desde, hasta);
    }

    public boolean eliminarPago(int idPago) {
        return dao.eliminar(idPago);
    }
    
    public List<Pago> obtenerPagosPorExpediente(int numeroExpediente) {
    return dao.obtenerPorExpediente(numeroExpediente);
}

}
