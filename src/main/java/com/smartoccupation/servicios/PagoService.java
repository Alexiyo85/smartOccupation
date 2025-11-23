package com.smartoccupation.servicios;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.modelo.Alquiler;

import java.time.LocalDate;
import java.util.List;

public class PagoService {

    private final PagoDAO pagoDAO;
    private final AlquilerDAO alquilerDAO;
    private final EstadoCobroDAO estadoDAO;

    // Constructor con inyección de dependencias
    public PagoService(PagoDAO pagoDAO, AlquilerDAO alquilerDAO, EstadoCobroDAO estadoDAO) {
        if (pagoDAO == null || alquilerDAO == null || estadoDAO == null) {
            throw new IllegalArgumentException("Los DAOs no pueden ser nulos");
        }
        this.pagoDAO = pagoDAO;
        this.alquilerDAO = alquilerDAO;
        this.estadoDAO = estadoDAO;
    }

    public boolean registrarPago(Pago pago) {
        Alquiler alquiler = alquilerDAO.obtenerPorId(pago.getNumero_expediente());
        if (alquiler == null) throw new IllegalArgumentException("Alquiler no encontrado");

        boolean exito = pagoDAO.insertar(pago);

        // Actualizar estado de cobro
        var totalPagado = pagoDAO.obtenerTotalPagadoPorAlquiler(alquiler.getNumero_expediente());
        if (totalPagado.compareTo(alquiler.getPrecio_total_estimado()) >= 0) {
            var estadoPagado = estadoDAO.obtenerPorNombre("pagado");
            alquiler.setId_estado_cobro(estadoPagado.getId_estado());
            alquilerDAO.actualizar(alquiler);
        }
        return exito;
    }

    public boolean eliminarPago(int idPago) {
        return pagoDAO.eliminar(idPago);
    }

    public List<Pago> listarTodosLosPagos() {
        return pagoDAO.obtenerTodos();
    }

    public List<Pago> buscarPagosPorFecha(LocalDate desde, LocalDate hasta) {
        return pagoDAO.obtenerPorRangoFechas(desde, hasta);
    }

    public List<Pago> obtenerPagosPorAlquiler(int numeroExpediente) {
        return pagoDAO.obtenerPorAlquiler(numeroExpediente);
    }
}
