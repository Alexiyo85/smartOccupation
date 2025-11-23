package smartoccupationTest.servicios;

import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.servicios.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteDAO clienteDAO;

    @Mock
    private AlquilerDAO alquilerDAO;

    @InjectMocks
    private ClienteService clienteService; // Mockito inyectará los mocks

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------
    // Método auxiliar: crear un cliente completo y válido
    // -------------------------------
    private Cliente crearClienteValido() {
        Cliente c = new Cliente();
        c.setNombre("Juan");
        c.setPrimer_apellido("Pérez");
        c.setSegundo_apellido("Gómez");
        c.setDni("12345678A");
        c.setTelefono("600123456");
        c.setEmail("juan.perez@example.com");
        c.setDireccion("Calle Falsa 123");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigo_postal("28080");
        return c;
    }

    // -------------------------------
    // Test: crear cliente exitosamente
    // -------------------------------
    @Test
    void crearCliente_exitoso() {
        Cliente c = crearClienteValido();

        // Simulamos que no existe cliente con ese DNI
        when(clienteDAO.obtenerPorDni(c.getDni())).thenReturn(null);
        when(clienteDAO.insertar(c)).thenReturn(true);

        boolean resultado = clienteService.crearCliente(c);

        assertThat(resultado).isTrue();
        verify(clienteDAO).insertar(c);
    }

    // -------------------------------
    // Test: crear cliente con DNI duplicado lanza excepción
    // -------------------------------
    @Test
    void crearCliente_dniDuplicado_lanzaExcepcion() {
        Cliente c = crearClienteValido();

        // Simulamos que ya existe un cliente con ese DNI
        when(clienteDAO.obtenerPorDni(c.getDni())).thenReturn(new Cliente());

        assertThrows(IllegalArgumentException.class, () -> clienteService.crearCliente(c));
        verify(clienteDAO, never()).insertar(any());
    }

    // -------------------------------
    // Test: eliminar cliente sin alquileres exitosamente
    // -------------------------------
    @Test
    void eliminarCliente_sinAlquileres_exitoso() {
        int idCliente = 1;

        // Simulamos que el cliente no tiene alquileres
        when(alquilerDAO.obtenerPorCliente(idCliente)).thenReturn(Collections.emptyList());
        when(clienteDAO.eliminar(idCliente)).thenReturn(true);

        boolean resultado = clienteService.eliminarCliente(idCliente);

        assertThat(resultado).isTrue();
        verify(clienteDAO).eliminar(idCliente);
    }

    // -------------------------------
    // Test: eliminar cliente con alquileres lanza excepción
    // -------------------------------
    @Test
    void eliminarCliente_conAlquileres_lanzaExcepcion() {
        int idCliente = 1;

        // Simulamos que el cliente tiene al menos un alquiler
        when(alquilerDAO.obtenerPorCliente(idCliente)).thenReturn(Collections.singletonList(new Alquiler()));

        assertThrows(IllegalStateException.class, () -> clienteService.eliminarCliente(idCliente));
        verify(clienteDAO, never()).eliminar(anyInt());
    }
}
