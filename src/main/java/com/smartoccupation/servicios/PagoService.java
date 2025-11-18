package com.smartoccupation.servicios;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PagoService {

    private final PagoDAO pagoDAO = new PagoDAO();
    private final AlquilerDAO alquilerDAO = new AlquilerDAO();
    private final EstadoCobroDAO estadoDAO = new EstadoCobroDAO();

    public boolean registrarPago(Pago pago) {
        Alquiler alquiler = alquilerDAO.obtenerPorId(pago.getNumero_expediente());
        if (alquiler == null) {
            throw new IllegalArgumentException("Alquiler no encontrado");
        }

        boolean exito = pagoDAO.insertar(pago);

        // Actualizar estado de cobro
        BigDecimal totalPagado = pagoDAO.obtenerTotalPagadoPorAlquiler(alquiler.getNumero_expediente());
        if (totalPagado.compareTo(alquiler.getPrecio_total_estimado()) >= 0) {
            EstadoCobro estadoPagado = estadoDAO.obtenerPorNombre("pagado");
            alquiler.setId_estado_cobro(estadoPagado.getId_estado());
            alquilerDAO.actualizar(alquiler);
        }

        return exito;
    }

    public List<Pago> obtenerPagosPorAlquiler(int numeroExpediente) {
        return pagoDAO.obtenerPorAlquiler(numeroExpediente);
    }

    public List<Pago> obtenerPagosPorRango(LocalDate desde, LocalDate hasta) {
        return pagoDAO.obtenerPorRangoFechas(desde, hasta);
    }

    public boolean eliminarPago(int idPago) {
        return pagoDAO.eliminar(idPago);
    }
}