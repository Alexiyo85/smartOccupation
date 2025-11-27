package smartoccupationTest.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.AlquilerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlquilerServiceTest {

    private AlquilerDAO alquilerDAO;
    private ViviendaDAO viviendaDAO;
    private EstadoCobroDAO estadoDAO;
    private ClienteDAO clienteDAO;
    private AlquilerService alquilerService;

    private Vivienda viviendaDisponible;
    private EstadoCobro estadoPendiente;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        alquilerDAO = mock(AlquilerDAO.class);
        viviendaDAO = mock(ViviendaDAO.class);
        estadoDAO = mock(EstadoCobroDAO.class);
        clienteDAO = mock(ClienteDAO.class);

        alquilerService = new AlquilerService(alquilerDAO, viviendaDAO, estadoDAO, clienteDAO);

        // Datos de prueba
        viviendaDisponible = new Vivienda();
        viviendaDisponible.setIdVivienda(1);
        viviendaDisponible.setPrecioMensual(BigDecimal.valueOf(1000));
        viviendaDisponible.setEstado("disponible");

        estadoPendiente = new EstadoCobro();
        estadoPendiente.setIdEstado(1);
        estadoPendiente.setNombreEstado("pendiente");

        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNombre("Juan");
        cliente.setPrimerApellido("Pérez");
        cliente.setSegundoApellido("Gómez");
    }

    @Test
    void testCrearAlquiler_exitoso() {
        Alquiler alquiler = new Alquiler();
        alquiler.setIdVivienda(1);
        alquiler.setIdCliente(1);
        alquiler.setTiempoMeses(2);
        alquiler.setTiempoDias(15);
        alquiler.setFechaInicio(LocalDate.of(2025, 11, 1));

        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);
        when(estadoDAO.obtenerPorNombre("pendiente")).thenReturn(estadoPendiente);
        when(alquilerDAO.insertar(any(Alquiler.class))).thenReturn(true);

        boolean exito = alquilerService.crearAlquiler(alquiler);

        assertTrue(exito);
        assertEquals("ocupado", viviendaDisponible.getEstado());

        verify(viviendaDAO).actualizar(viviendaDisponible);

        // Comprobar cálculo de precio total
        assertEquals(BigDecimal.valueOf(2500.00).setScale(2), alquiler.getPrecioTotalEstimado());
        // Fecha fin estimada
        assertEquals(LocalDate.of(2026, 1, 16), alquiler.getFechaFinEstimada());
    }

    @Test
    void testCrearAlquiler_viviendaNoDisponible() {
        viviendaDisponible.setEstado("ocupado");
        Alquiler alquiler = new Alquiler();
        alquiler.setIdVivienda(1);
        alquiler.setIdCliente(1);
        alquiler.setTiempoMeses(1);
        alquiler.setTiempoDias(0);
        alquiler.setFechaInicio(LocalDate.now());

        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);

        assertThrows(IllegalStateException.class, () -> alquilerService.crearAlquiler(alquiler));
    }

    @Test
    void testObtenerAlquiler_hydration() {
        Alquiler alquiler = new Alquiler();
        alquiler.setNumeroExpediente(123);
        alquiler.setIdCliente(1);
        alquiler.setIdVivienda(1);
        alquiler.setTiempoMeses(1);
        alquiler.setTiempoDias(0);
        alquiler.setFechaInicio(LocalDate.of(2025,11,1));

        when(alquilerDAO.obtenerPorId(123)).thenReturn(alquiler);
        when(clienteDAO.obtenerPorId(1)).thenReturn(cliente);
        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);

        Alquiler result = alquilerService.obtenerAlquiler(123);

        assertNotNull(result.getCliente());
        assertNotNull(result.getVivienda());
        assertEquals("Juan Pérez Gómez", result.getNombreCliente());
    }

    @Test
    void testEliminarAlquiler() {
        Alquiler alquiler = new Alquiler();
        alquiler.setNumeroExpediente(123);
        alquiler.setIdVivienda(1);
        viviendaDisponible.setEstado("ocupado");

        when(alquilerDAO.obtenerPorId(123)).thenReturn(alquiler);
        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);
        when(alquilerDAO.eliminar(123)).thenReturn(true);

        boolean exito = alquilerService.eliminarAlquiler(123);

        assertTrue(exito);
        assertEquals("disponible", viviendaDisponible.getEstado());
        verify(viviendaDAO).actualizar(viviendaDisponible);
    }

    @Test
    void testObtenerPorNombreEstado() {
        EstadoCobro estado = new EstadoCobro();
        estado.setIdEstado(1);
        estado.setNombreEstado("pendiente");

        Alquiler a1 = new Alquiler();
        a1.setIdCliente(1);
        a1.setIdVivienda(1);
        Alquiler a2 = new Alquiler();
        a2.setIdCliente(1);
        a2.setIdVivienda(1);

        when(estadoDAO.obtenerPorNombre("pendiente")).thenReturn(estado);
        when(alquilerDAO.obtenerPorEstado(1)).thenReturn(Arrays.asList(a1, a2));
        when(clienteDAO.obtenerPorId(1)).thenReturn(cliente);
        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);

        List<Alquiler> lista = alquilerService.obtenerPorNombreEstado("pendiente");

        assertEquals(2, lista.size());
        for (Alquiler a : lista) {
            assertNotNull(a.getCliente());
            assertNotNull(a.getVivienda());
        }
    }

    @Test
    void testActualizarAlquiler() {
        Alquiler alquiler = new Alquiler();
        alquiler.setNumeroExpediente(1);
        alquiler.setTiempoMeses(2);
        alquiler.setTiempoDias(5);
        alquiler.setFechaInicio(LocalDate.of(2025,11,1));

        when(alquilerDAO.actualizar(alquiler)).thenReturn(true);

        boolean result = alquilerService.actualizarAlquiler(alquiler);

        assertTrue(result);
    }

    @Test
    void testObtenerTodos() {
        Alquiler a1 = new Alquiler();
        a1.setIdCliente(1);
        a1.setIdVivienda(1);
        Alquiler a2 = new Alquiler();
        a2.setIdCliente(1);
        a2.setIdVivienda(1);

        when(alquilerDAO.obtenerTodos()).thenReturn(Arrays.asList(a1, a2));
        when(clienteDAO.obtenerPorId(1)).thenReturn(cliente);
        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);

        List<Alquiler> lista = alquilerService.obtenerTodos();

        assertEquals(2, lista.size());
        for (Alquiler a : lista) {
            assertNotNull(a.getCliente());
            assertNotNull(a.getVivienda());
        }
    }

    @Test
    void testObtenerPorCliente() {
        Alquiler a = new Alquiler();
        a.setIdCliente(1);
        a.setIdVivienda(1);

        when(alquilerDAO.obtenerPorCliente(1)).thenReturn(List.of(a));
        when(clienteDAO.obtenerPorId(1)).thenReturn(cliente);
        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);

        List<Alquiler> lista = alquilerService.obtenerPorCliente(1);

        assertEquals(1, lista.size());
        assertEquals(cliente, lista.get(0).getCliente());
    }

    @Test
    void testObtenerPorVivienda() {
        Alquiler a = new Alquiler();
        a.setIdCliente(1);
        a.setIdVivienda(1);

        when(alquilerDAO.obtenerPorVivienda(1)).thenReturn(List.of(a));
        when(clienteDAO.obtenerPorId(1)).thenReturn(cliente);
        when(viviendaDAO.obtenerPorId(1)).thenReturn(viviendaDisponible);

        List<Alquiler> lista = alquilerService.obtenerPorVivienda(1);

        assertEquals(1, lista.size());
        assertEquals(viviendaDisponible, lista.get(0).getVivienda());
    }

}
