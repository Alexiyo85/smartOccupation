package com.smartoccupation.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.ClienteDAO; // 👈 Importación necesaria
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente; // 👈 Importación necesaria
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Vivienda;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors; // 👈 Importación necesaria

/**
 * Servicio para gestionar la lógica de negocio de los alquileres.
 */
public class AlquilerService {

    private final AlquilerDAO alquilerDAO;
    private final ViviendaDAO viviendaDAO;
    private final EstadoCobroDAO estadoDAO;
    private final ClienteDAO clienteDAO; // 👈 Nueva dependencia para cargar el objeto Cliente

    public AlquilerService(AlquilerDAO alquilerDAO, ViviendaDAO viviendaDAO, EstadoCobroDAO estadoDAO, ClienteDAO clienteDAO) {
        if (alquilerDAO == null || viviendaDAO == null || estadoDAO == null || clienteDAO == null) {
            throw new IllegalArgumentException("Los DAOs no pueden ser nulos");
        }
        this.alquilerDAO = alquilerDAO;
        this.viviendaDAO = viviendaDAO;
        this.estadoDAO = estadoDAO;
        this.clienteDAO = clienteDAO; // 👈 Inicialización
    }

    /**
     * Carga los objetos Cliente y Vivienda asociados a un Alquiler (Hydration).
     *
     * @param alquiler El objeto Alquiler con solo los IDs cargados.
     * @return El objeto Alquiler con las propiedades Cliente y Vivienda
     * cargadas.
     */
    private Alquiler cargarObjetosRelacionados(Alquiler alquiler) {
        if (alquiler == null) {
            return null;
        }

        // Cargar Cliente
        // Usamos el ClienteDAO inyectado
        Cliente cliente = clienteDAO.obtenerPorId(alquiler.getId_cliente());
        if (cliente != null) {
            alquiler.setCliente(cliente);
        } else {
            System.err.println("Advertencia: Cliente con ID " + alquiler.getId_cliente() + " no encontrado.");
        }

        // Cargar Vivienda
        // Usamos el ViviendaDAO inyectado
        Vivienda vivienda = viviendaDAO.obtenerPorId(alquiler.getId_vivienda());
        if (vivienda != null) {
            alquiler.setVivienda(vivienda);

            if (alquiler.getPrecio_total_estimado() == null && vivienda.getPrecio_mensual() != null) {
                alquiler.calcularPrecioTotal(vivienda.getPrecio_mensual());
            }
        } else {
            System.err.println("Advertencia: Vivienda con ID " + alquiler.getId_vivienda() + " no encontrada.");
        }

        // Asume que EstadoCobro también tiene su propio DAO/Service si es necesario cargarlo
        return alquiler;
    }

    /**
     * Crear un nuevo alquiler solo si la vivienda está disponible.
     */
    public boolean crearAlquiler(Alquiler alquiler) {
        Vivienda vivienda = viviendaDAO.obtenerPorId(alquiler.getId_vivienda());
        if (vivienda == null) {
            throw new IllegalArgumentException("La vivienda no existe");
        }
        if (!"disponible".equalsIgnoreCase(vivienda.getEstado())) {
            throw new IllegalStateException("La vivienda no está disponible");
        }

        // Calcular fecha fin y precio total si no están definidos
        alquiler.calcularFechaFin();
        BigDecimal precioMensual = vivienda.getPrecio_mensual();
        alquiler.calcularPrecioTotal(precioMensual);
        // Estado inicial pendiente
        EstadoCobro estadoPendiente = estadoDAO.obtenerPorNombre("pendiente");
        if (estadoPendiente == null) {
            throw new IllegalStateException("No existe el estado 'pendiente' en la base de datos");
        }
        alquiler.setId_estado_cobro(estadoPendiente.getId_estado());

        boolean exito = alquilerDAO.insertar(alquiler);
        if (exito) {
            vivienda.setEstado("ocupado");
            viviendaDAO.actualizar(vivienda);
        }
        return exito;
    }

    /**
     * Devuelve la lista de alquileres cuyo estado (nombre) coincida con el
     * proporcionado. Si nombreEstado es null o "Todos" devuelve todos los
     * alquileres.
     */
    public List<Alquiler> obtenerPorNombreEstado(String nombreEstado) {
        if (nombreEstado == null || nombreEstado.trim().isEmpty() || "Todos".equalsIgnoreCase(nombreEstado)) {
            return obtenerTodos();
        }
        EstadoCobro estado = estadoDAO.obtenerPorNombre(nombreEstado);
        if (estado == null) {
            return List.of(); // vacío si el estado no existe
        }
        // Nota: El DAO devuelve Alquileres sin objetos anidados. ¡Debemos cargarlos!
        return alquilerDAO.obtenerPorEstado(estado.getId_estado())
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un alquiler existente.
     */
    public boolean actualizarAlquiler(Alquiler alquiler) {
        alquiler.calcularFechaFin();
        return alquilerDAO.actualizar(alquiler);
    }

    /**
     * Elimina un alquiler por número de expediente y libera la vivienda
     * asociada.
     */
    public boolean eliminarAlquiler(int numeroExpediente) {
        Alquiler a = alquilerDAO.obtenerPorId(numeroExpediente);
        if (a == null) {
            return false;
        }

        Vivienda vivienda = viviendaDAO.obtenerPorId(a.getId_vivienda());
        if (vivienda != null) {
            vivienda.setEstado("disponible");
            viviendaDAO.actualizar(vivienda);
        }

        return alquilerDAO.eliminar(numeroExpediente);
    }

    /**
     * Obtiene un alquiler por número de expediente. Incluye la carga de objetos
     * Cliente y Vivienda.
     */
    public Alquiler obtenerAlquiler(int numeroExpediente) {
        Alquiler alquiler = alquilerDAO.obtenerPorId(numeroExpediente);
        return cargarObjetosRelacionados(alquiler); // 👈 Aplicar Hydration
    }

    /**
     * Obtiene todos los alquileres. Incluye la carga de objetos Cliente y
     * Vivienda.
     */
    public List<Alquiler> obtenerTodos() {
        List<Alquiler> lista = alquilerDAO.obtenerTodos();
        // Aplica "Hydration" a toda la lista usando streams
        return lista.stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene alquileres de un cliente específico.
     */
    public List<Alquiler> obtenerPorCliente(int idCliente) {
        // Nota: Deberías aplicar Hydration a esta lista también si se usa en la GUI
        return alquilerDAO.obtenerPorCliente(idCliente)
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene alquileres de una vivienda específica.
     */
    public List<Alquiler> obtenerPorVivienda(int idVivienda) {
        // Nota: Deberías aplicar Hydration a esta lista también si se usa en la GUI
        return alquilerDAO.obtenerPorVivienda(idVivienda)
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene alquileres pendientes de pago.
     */
    public List<Alquiler> obtenerAlquileresPendientes() {
        // Nota: Deberías aplicar Hydration a esta lista también si se usa en la GUI
        return alquilerDAO.obtenerPendientesPago()
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene alquileres ya pagados.
     */
    public List<Alquiler> obtenerAlquileresPagados() {
        // Nota: Deberías aplicar Hydration a esta lista también si se usa en la GUI
        return alquilerDAO.obtenerPagados()
                .stream()
                .map(this::cargarObjetosRelacionados)
                .collect(Collectors.toList());
    }
}
