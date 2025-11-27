package com.smartoccupation.servicios;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.modelo.Pago;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase de Servicio (Business Logic Layer) para la entidad Pago.
 * <p>
 * Actúa como intermediario entre la capa de presentación/control y la capa de
 * acceso a datos (DAO), proporcionando operaciones para registrar, consultar y
 * eliminar pagos.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class PagoService {

    private final PagoDAO dao;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param dao El DAO para la entidad Pago. No debe ser nulo.
     */
    public PagoService(PagoDAO dao) {
        // En un entorno de producción, se validaría si el DAO es nulo
        this.dao = dao;
    }

    /**
     * Registra un nuevo pago en el sistema.
     *
     * @param pago El objeto {@code Pago} a registrar.
     * @return {@code true} si el registro fue exitoso, {@code false} en caso contrario.
     */
    public boolean registrarPago(Pago pago) {
        // En un servicio más complejo, aquí se podría validar el objeto Pago,
        // actualizar el estado del alquiler relacionado (si fuera necesario), etc.
        return dao.insertar(pago);
    }

    /**
     * Obtiene una lista con todos los pagos registrados, ordenados por fecha de pago descendente.
     *
     * @return Una lista de objetos {@code Pago}.
     */
    public List<Pago> listarTodosLosPagos() {
        return dao.obtenerTodos();
    }

    /**
     * Busca y obtiene una lista de pagos realizados dentro de un rango de fechas específico.
     *
     * @param desde La fecha de inicio del rango (inclusiva).
     * @param hasta La fecha de fin del rango (inclusiva).
     * @return Una lista de objetos {@code Pago} dentro del rango de fechas.
     */
    public List<Pago> buscarPagosPorFecha(LocalDate desde, LocalDate hasta) {
        return dao.buscarPorRangoFechas(desde, hasta);
    }

    /**
     * Elimina un registro de pago utilizando su identificador único.
     *
     * @param idPago El ID (PK) del pago a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario.
     */
    public boolean eliminarPago(int idPago) {
        return dao.eliminar(idPago);
    }
    
    /**
     * Obtiene una lista de pagos asociados a un número de expediente de alquiler específico.
     *
     * @param numeroExpediente El número del expediente de alquiler.
     * @return Una lista de objetos {@code Pago} para ese expediente.
     */
    public List<Pago> obtenerPagosPorExpediente(int numeroExpediente) {
        return dao.obtenerPorExpediente(numeroExpediente);
    }
}