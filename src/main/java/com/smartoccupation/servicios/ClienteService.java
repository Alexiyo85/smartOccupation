package com.smartoccupation.servicios;

import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Alquiler;

import java.util.List;

/**
 * Clase de Servicio (Business Logic Layer) para la entidad Cliente.
 * <p>
 * Gestiona las operaciones de negocio relacionadas con los clientes,
 * aplicando validaciones y reglas como verificar la duplicidad de DNI
 * o evitar la eliminación de clientes con alquileres activos.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ClienteService {

    private final ClienteDAO clienteDAO;
    private final AlquilerDAO alquilerDAO;

    /**
     * Constructor para inyección de dependencias.
     *
     * @param clienteDAO El DAO para la entidad Cliente.
     * @param alquilerDAO El DAO para la entidad Alquiler, usado para verificar relaciones.
     * @throws IllegalArgumentException Si alguno de los DAOs proporcionados es nulo (se recomienda implementar esta validación).
     */
    public ClienteService(ClienteDAO clienteDAO, AlquilerDAO alquilerDAO) {
        // En un entorno de producción, aquí se añadiría:
        // if (clienteDAO == null || alquilerDAO == null) throw new IllegalArgumentException("Los DAOs no pueden ser nulos");
        this.clienteDAO = clienteDAO;
        this.alquilerDAO = alquilerDAO;
    }

    /**
     * Crea un nuevo cliente en la base de datos.
     * Aplica la regla de negocio: el DNI debe ser único.
     *
     * @param cliente El objeto {@code Cliente} a crear.
     * @return {@code true} si la inserción fue exitosa.
     * @throws IllegalArgumentException Si ya existe un cliente con el mismo DNI.
     */
    public boolean crearCliente(Cliente cliente) {
        // Regla de negocio: Verificar duplicidad de DNI antes de insertar
        Cliente existente = clienteDAO.obtenerPorDni(cliente.getDni());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un cliente con el DNI/NIF: " + cliente.getDni());
        }
        return clienteDAO.insertar(cliente);
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente El objeto {@code Cliente} con los datos actualizados, incluyendo el ID (PK).
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario.
     */
    public boolean actualizarCliente(Cliente cliente) {
        return clienteDAO.actualizar(cliente);
    }

    /**
     * Elimina un cliente por su ID solo si no tiene alquileres activos registrados.
     * <p>
     * Aplica la regla de negocio para mantener la integridad referencial.
     * </p>
     *
     * @param idCliente El ID del cliente a eliminar.
     * @return {@code true} si la eliminación fue exitosa.
     * @throws IllegalStateException Si el cliente tiene alquileres asociados, impidiendo su eliminación.
     */
    public boolean eliminarCliente(int idCliente) {
        // Lógica de negocio: Verificar si existen relaciones (alquileres)
        List<Alquiler> alquileres = alquilerDAO.obtenerPorCliente(idCliente);
        if (alquileres != null && !alquileres.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el cliente con ID " + idCliente + " porque tiene " + alquileres.size() + " alquileres activos.");
        }
        return clienteDAO.eliminar(idCliente);
    }

    /**
     * Obtiene un cliente por su identificador único.
     *
     * @param idCliente El ID (PK) del cliente a buscar.
     * @return El objeto {@code Cliente} encontrado o {@code null} si no existe.
     */
    public Cliente obtenerCliente(int idCliente) {
        return clienteDAO.obtenerPorId(idCliente);
    }

    /**
     * Obtiene una lista con todos los clientes registrados.
     *
     * @return Una lista de objetos {@code Cliente}.
     */
    public List<Cliente> obtenerTodos() {
        return clienteDAO.obtenerTodos();
    }
}