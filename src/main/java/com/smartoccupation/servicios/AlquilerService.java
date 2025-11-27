package com.smartoccupation.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.ClienteDAO; 
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente; 
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Vivienda;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors; 

/**
 * Servicio para gestionar la lógica de negocio (Business Logic) de los alquileres.
 * <p>
 * Se encarga de coordinar las operaciones de los DAOs, aplicar reglas de negocio
 * (como verificar la disponibilidad de la vivienda) y realizar la "hidratación"
 * de objetos relacionados (Cliente y Vivienda).
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class AlquilerService {

    private final AlquilerDAO alquilerDAO;
    private final ViviendaDAO viviendaDAO;
    private final EstadoCobroDAO estadoDAO;
    private final ClienteDAO clienteDAO; 

    /**
     * Constructor para inyección de dependencias. Todos los DAOs deben ser proporcionados.
     *
     * @param alquilerDAO El DAO para la entidad Alquiler.
     * @param viviendaDAO El DAO para la entidad Vivienda.
     * @param estadoDAO El DAO para la entidad EstadoCobro.
     * @param clienteDAO El DAO para la entidad Cliente, usado para la hidratación.
     * @throws IllegalArgumentException Si alguno de los DAOs proporcionados es nulo.
     */
    public AlquilerService(AlquilerDAO alquilerDAO, ViviendaDAO viviendaDAO, EstadoCobroDAO estadoDAO, ClienteDAO clienteDAO) {
        if (alquilerDAO == null || viviendaDAO == null || estadoDAO == null || clienteDAO == null) {
            throw new IllegalArgumentException("Los DAOs no pueden ser nulos");
        }
        this.alquilerDAO = alquilerDAO;
        this.viviendaDAO = viviendaDAO;
        this.estadoDAO = estadoDAO;
        this.clienteDAO = clienteDAO; 
    }

    /**
     * Carga los objetos Cliente y Vivienda asociados a un Alquiler (Hydration).
     * <p>
     * Este proceso toma un objeto Alquiler que solo tiene IDs (claves foráneas)
     * y consulta la base de datos usando los respectivos DAOs para poblar
     * las propiedades de objetos completos (lazy loading simulado).
     * </p>
     *
     * @param alquiler El objeto Alquiler con solo los IDs cargados.
     * @return El objeto Alquiler con las propiedades Cliente y Vivienda
     * cargadas. Devuelve {@code null} si el alquiler de entrada es {@code null}.
     */
    private Alquiler cargarObjetosRelacionados(Alquiler alquiler) {
        if (alquiler == null) {
            return null;
        }

        // Cargar Cliente
        Cliente cliente = clienteDAO.obtenerPorId(alquiler.getIdCliente());
        if (cliente != null) {
            alquiler.setCliente(cliente);
        } else {
            System.err.println("Advertencia: Cliente con ID " + alquiler.getIdCliente() + " no encontrado.");
        }

        // Cargar Vivienda
        Vivienda vivienda = viviendaDAO.obtenerPorId(alquiler.getIdVivienda());
        if (vivienda != null) {
            alquiler.setVivienda(vivienda);

            // Calcula el precio si es necesario y si la Vivienda ya fue cargada.
            if (alquiler.getPrecioTotalEstimado() == null && vivienda.getPrecio_mensual() != null) {
                alquiler.calcularPrecioTotal(vivienda.getPrecio_mensual());
            }
        } else {
            System.err.println("Advertencia: Vivienda con ID " + alquiler.getIdVivienda() + " no encontrada.");
        }

        // Aquí también se debería cargar EstadoCobro, si fuera necesario para la UI
        // En este ejemplo, se omite el EstadoCobro por simplicidad, aunque sería el mismo patrón.
        return alquiler;
    }

    /**
     * Crea un nuevo registro de alquiler, aplicando la regla de negocio de
     * que la vivienda debe estar en estado **"disponible"** antes de alquilarse.
     * Si la creación es exitosa, actualiza el estado de la vivienda a **"ocupado"**.
     *
     * @param alquiler El objeto {@code Alquiler} a persistir.
     * @return {@code true} si la creación y la actualización de la vivienda fueron exitosas, {@code false} en caso contrario.
     * @throws IllegalArgumentException Si el ID de la vivienda no existe.
     * @throws IllegalStateException Si la vivienda existe, pero no está disponible, o si no existe el estado inicial necesario.
     */
    public boolean crearAlquiler(Alquiler alquiler) {
        Vivienda vivienda = viviendaDAO.obtenerPorId(alquiler.getIdVivienda());
        if (vivienda == null) {
            throw new IllegalArgumentException("La vivienda no existe");
        }
        if (!"disponible".equalsIgnoreCase(vivienda.getEstado())) {
            throw new IllegalStateException("La vivienda no está disponible para alquilar. Estado actual: " + vivienda.getEstado());
        }

        // Prepara los datos del alquiler antes de persistir
        alquiler.calcularFechaFin();
        BigDecimal precioMensual = vivienda.getPrecio_mensual();
        alquiler.calcularPrecioTotal(precioMensual);
        
        // Asignar el estado inicial (ej. "pendiente")
        EstadoCobro estadoPendiente = estadoDAO.obtenerPorNombre("pendiente");
        if (estadoPendiente == null) {
            throw new IllegalStateException("No existe el estado 'pendiente' en la base de datos");
        }
        alquiler.setIdEstadoCobro(estadoPendiente.getIdEstado());

        boolean exito = alquilerDAO.insertar(alquiler);
        if (exito) {
            // Regla de negocio: Cambiar estado de la vivienda a ocupado
            vivienda.setEstado("ocupado");
            viviendaDAO.actualizar(vivienda);
        }
        return exito;
    }

    /**
     * Devuelve la lista de alquileres cuyo estado de cobro coincida con el nombre proporcionado.
     * Si {@code nombreEstado} es nulo, vacío o **"Todos"**, devuelve todos los alquileres.
     * Se aplica hidratación a los objetos devueltos.
     *
     * @param nombreEstado El nombre del estado de cobro por el que filtrar.
     * @return Una lista de objetos {@code Alquiler} con sus relaciones cargadas.
     */
    public List<Alquiler> obtenerPorNombreEstado(String nombreEstado) {
        if (nombreEstado == null || nombreEstado.trim().isEmpty() || "Todos".equalsIgnoreCase(nombreEstado)) {
            return obtenerTodos();
        }
        EstadoCobro estado = estadoDAO.obtenerPorNombre(nombreEstado);
        if (estado == null) {
            return List.of(); // Devuelve lista vacía si el estado no existe
        }
        
        // 1. Obtiene la lista base por ID de estado
        // 2. Mapea cada elemento aplicando la hidratación
        return alquilerDAO.obtenerPorEstado(estado.getIdEstado())
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un registro de alquiler existente.
     * Se recalcula la fecha de fin antes de la actualización.
     *
     * @param alquiler El objeto {@code Alquiler} con los datos a actualizar.
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizarAlquiler(Alquiler alquiler) {
        alquiler.calcularFechaFin();
        return alquilerDAO.actualizar(alquiler);
    }

    /**
     * Elimina un alquiler por su número de expediente (PK) y, si el alquiler existía,
     * actualiza el estado de la vivienda asociada a **"disponible"**.
     *
     * @param numeroExpediente El ID del alquiler a eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} si el alquiler no existía o si la eliminación falló.
     */
    public boolean eliminarAlquiler(int numeroExpediente) {
        Alquiler a = alquilerDAO.obtenerPorId(numeroExpediente);
        if (a == null) {
            return false;
        }

        // Regla de negocio: Liberar la vivienda
        Vivienda vivienda = viviendaDAO.obtenerPorId(a.getIdVivienda());
        if (vivienda != null) {
            vivienda.setEstado("disponible");
            viviendaDAO.actualizar(vivienda);
        }

        return alquilerDAO.eliminar(numeroExpediente);
    }

    /**
     * Obtiene un alquiler por número de expediente (PK).
     * Se aplica la **hidratación** de objetos relacionados (Cliente y Vivienda).
     *
     * @param numeroExpediente El ID del alquiler a buscar.
     * @return El objeto {@code Alquiler} con sus relaciones cargadas, o {@code null} si no se encuentra.
     */
    public Alquiler obtenerAlquiler(int numeroExpediente) {
        Alquiler alquiler = alquilerDAO.obtenerPorId(numeroExpediente);
        return cargarObjetosRelacionados(alquiler); // Aplicar Hydration
    }

    /**
     * Obtiene todos los alquileres.
     * Se aplica la **hidratación** de objetos relacionados (Cliente y Vivienda) a toda la lista.
     *
     * @return Una lista de todos los objetos {@code Alquiler} con sus relaciones cargadas.
     */
    public List<Alquiler> obtenerTodos() {
        List<Alquiler> lista = alquilerDAO.obtenerTodos();
        // Aplica "Hydration" a toda la lista usando streams
        return lista.stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de alquileres asociados a un cliente específico.
     * Se aplica la **hidratación** de objetos relacionados.
     *
     * @param idCliente El ID del cliente.
     * @return Una lista de objetos {@code Alquiler} con sus relaciones cargadas.
     */
    public List<Alquiler> obtenerPorCliente(int idCliente) {
        return alquilerDAO.obtenerPorCliente(idCliente)
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la lista de alquileres asociados a una vivienda específica.
     * Se aplica la **hidratación** de objetos relacionados.
     *
     * @param idVivienda El ID de la vivienda.
     * @return Una lista de objetos {@code Alquiler} con sus relaciones cargadas.
     */
    public List<Alquiler> obtenerPorVivienda(int idVivienda) {
        return alquilerDAO.obtenerPorVivienda(idVivienda)
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los alquileres cuyo estado de cobro es **"pendiente"**.
     * Se aplica la **hidratación** de objetos relacionados.
     *
     * @return Una lista de objetos {@code Alquiler} con sus relaciones cargadas.
     */
    public List<Alquiler> obtenerAlquileresPendientes() {
        return alquilerDAO.obtenerPendientesPago()
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los alquileres cuyo estado de cobro es **"pagado"**.
     * Se aplica la **hidratación** de objetos relacionados.
     *
     * @return Una lista de objetos {@code Alquiler} con sus relaciones cargadas.
     */
    public List<Alquiler> obtenerAlquileresPagados() {
        return alquilerDAO.obtenerPagados()
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }
}