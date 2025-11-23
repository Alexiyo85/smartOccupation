package com.smartoccupation.servicios;

import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Alquiler;

import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO;
    private final AlquilerDAO alquilerDAO;

    public ClienteService(ClienteDAO clienteDAO, AlquilerDAO alquilerDAO) {
        this.clienteDAO = clienteDAO;
        this.alquilerDAO = alquilerDAO;
    }

    public boolean crearCliente(Cliente cliente) {
        Cliente existente = clienteDAO.obtenerPorDni(cliente.getDni());
        if (existente != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI");
        }
        return clienteDAO.insertar(cliente);
    }

    public boolean actualizarCliente(Cliente cliente) {
        return clienteDAO.actualizar(cliente);
    }

    public boolean eliminarCliente(int idCliente) {
        List<Alquiler> alquileres = alquilerDAO.obtenerPorCliente(idCliente);
        if (!alquileres.isEmpty()) {
            throw new IllegalStateException("No se puede eliminar cliente con alquileres activos");
        }
        return clienteDAO.eliminar(idCliente);
    }

    public Cliente obtenerCliente(int idCliente) {
        return clienteDAO.obtenerPorId(idCliente);
    }

    public List<Cliente> obtenerTodos() {
        return clienteDAO.obtenerTodos();
    }
}
