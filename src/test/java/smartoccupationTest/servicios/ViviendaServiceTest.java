package smartoccupationTest.servicios;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ViviendaServiceTest {

    private ViviendaDAO viviendaDAO;
    private ViviendaService service;

    @BeforeEach
    void setUp() {
        viviendaDAO = mock(ViviendaDAO.class);
        service = new ViviendaService(viviendaDAO);
    }

    @Test
    void testCrearVivienda() {
        Vivienda v = new Vivienda();
        when(viviendaDAO.insertar(v)).thenReturn(true);

        boolean exito = service.crearVivienda(v);
        assertThat(exito).isTrue();
        verify(viviendaDAO).insertar(v);
    }

    @Test
    void testActualizarVivienda() {
        Vivienda v = new Vivienda();
        v.setIdVivienda(1);
        when(viviendaDAO.actualizar(v)).thenReturn(true);

        boolean exito = service.actualizarVivienda(v);
        assertThat(exito).isTrue();
        verify(viviendaDAO).actualizar(v);
    }

    @Test
    void testEliminarViviendaDisponible() {
        Vivienda v = new Vivienda();
        v.setIdVivienda(1);
        v.setEstado("disponible");

        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);
        when(viviendaDAO.eliminar(1)).thenReturn(true);

        boolean exito = service.eliminarVivienda(1);
        assertThat(exito).isTrue();
        verify(viviendaDAO).eliminar(1);
    }

    @Test
    void testEliminarViviendaNoDisponible() {
        Vivienda v = new Vivienda();
        v.setIdVivienda(1);
        v.setEstado("ocupado");

        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);

        assertThrows(IllegalStateException.class, () -> service.eliminarVivienda(1));
        verify(viviendaDAO, never()).eliminar(anyInt());
    }

    @Test
    void testEliminarViviendaNoExiste() {
        when(viviendaDAO.obtenerPorId(99)).thenReturn(null);
        boolean exito = service.eliminarVivienda(99);
        assertThat(exito).isFalse();
        verify(viviendaDAO, never()).eliminar(anyInt());
    }

    @Test
    void testObtenerVivienda() {
        Vivienda v = new Vivienda();
        v.setIdVivienda(1);

        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);
        Vivienda result = service.obtenerVivienda(1);

        assertThat(result).isNotNull();
        assertThat(result.getIdVivienda()).isEqualTo(1);
    }

    @Test
    void testObtenerTodas() {
        Vivienda v1 = new Vivienda();
        Vivienda v2 = new Vivienda();

        when(viviendaDAO.obtenerTodos()).thenReturn(List.of(v1, v2));
        List<Vivienda> lista = service.obtenerTodas();

        assertThat(lista).hasSize(2);
    }

    @Test
    void testObtenerPorEstado() {
        Vivienda v1 = new Vivienda();
        Vivienda v2 = new Vivienda();

        when(viviendaDAO.obtenerPorEstado("disponible")).thenReturn(List.of(v1, v2));
        List<Vivienda> lista = service.obtenerPorEstado("disponible");

        assertThat(lista).hasSize(2);
    }
}

