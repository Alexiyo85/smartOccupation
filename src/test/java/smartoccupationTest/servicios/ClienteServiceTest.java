package smartoccupationTest.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.servicios.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    private ClienteDAO clienteDAO;
    private AlquilerDAO alquilerDAO;
    private ClienteService service;

    @BeforeEach
    void setUp() {
        clienteDAO = mock(ClienteDAO.class);
        alquilerDAO = mock(AlquilerDAO.class);
        service = new ClienteService(clienteDAO, alquilerDAO);
    }

    @Test
    void testCrearClienteExitoso() {
        Cliente c = new Cliente();
        c.setDni("12345678A");

        when(clienteDAO.obtenerPorDni("12345678A")).thenReturn(null);
        when(clienteDAO.insertar(c)).thenReturn(true);

        boolean exito = service.crearCliente(c);
        assertThat(exito).isTrue();

        verify(clienteDAO).insertar(c);
    }

    @Test
    void testCrearClienteDniDuplicado() {
        Cliente existente = new Cliente();
        existente.setDni("12345678A");

        Cliente nuevo = new Cliente();
        nuevo.setDni("12345678A");

        when(clienteDAO.obtenerPorDni("12345678A")).thenReturn(existente);

        assertThrows(IllegalArgumentException.class, () -> service.crearCliente(nuevo));
        verify(clienteDAO, never()).insertar(any());
    }

    @Test
    void testActualizarCliente() {
        Cliente c = new Cliente();
        c.setId_cliente(1);
        when(clienteDAO.actualizar(c)).thenReturn(true);

        boolean exito = service.actualizarCliente(c);
        assertThat(exito).isTrue();
        verify(clienteDAO).actualizar(c);
    }

    @Test
    void testEliminarClienteSinAlquileres() {
        int idCliente = 1;

        when(alquilerDAO.obtenerPorCliente(idCliente)).thenReturn(List.of());
        when(clienteDAO.eliminar(idCliente)).thenReturn(true);

        boolean exito = service.eliminarCliente(idCliente);
        assertThat(exito).isTrue();

        verify(clienteDAO).eliminar(idCliente);
    }

    @Test
    void testEliminarClienteConAlquileres() {
        int idCliente = 1;
        Alquiler a = new Alquiler();
        a.setId_cliente(idCliente);

        when(alquilerDAO.obtenerPorCliente(idCliente)).thenReturn(List.of(a));

        assertThrows(IllegalStateException.class, () -> service.eliminarCliente(idCliente));
        verify(clienteDAO, never()).eliminar(anyInt());
    }

    @Test
    void testObtenerCliente() {
        Cliente c = new Cliente();
        c.setId_cliente(1);

        when(clienteDAO.obtenerPorId(1)).thenReturn(c);
        Cliente result = service.obtenerCliente(1);

        assertThat(result).isNotNull();
        assertThat(result.getId_cliente()).isEqualTo(1);
    }

    @Test
    void testObtenerTodos() {
        Cliente c1 = new Cliente();
        Cliente c2 = new Cliente();

        when(clienteDAO.obtenerTodos()).thenReturn(List.of(c1, c2));

        List<Cliente> lista = service.obtenerTodos();
        assertThat(lista).hasSize(2);
    }
}
