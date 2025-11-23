package smartoccupationTest.servicios;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ViviendaServiceTest {

    @Mock
    private ViviendaDAO viviendaDAO;

    @InjectMocks
    private ViviendaService viviendaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearVivienda() {
        Vivienda v = new Vivienda();

        when(viviendaDAO.insertar(v)).thenReturn(true);

        boolean ok = viviendaService.crearVivienda(v);

        assertThat(ok).isTrue();
        verify(viviendaDAO).insertar(v);
    }

    @Test
    void obtenerTodas() {
        Vivienda v1 = new Vivienda();
        Vivienda v2 = new Vivienda();

        when(viviendaDAO.obtenerTodos()).thenReturn(Arrays.asList(v1, v2));

        List<Vivienda> lista = viviendaService.obtenerTodas();

        assertThat(lista).hasSize(2);
        verify(viviendaDAO).obtenerTodos();
    }

    @Test
    void eliminarVivienda() {
        Vivienda v = new Vivienda();
        v.setId_vivienda(1);
        v.setEstado("disponible"); // ✅ ES LO QUE ESPERA EL SERVICE

        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);
        when(viviendaDAO.eliminar(1)).thenReturn(true);

        boolean ok = viviendaService.eliminarVivienda(1);

        assertThat(ok).isTrue();
        verify(viviendaDAO).eliminar(1);
    }

    @Test
    void eliminarVivienda_noDisponible_lanzaExcepcion() {
        Vivienda v = new Vivienda();
        v.setId_vivienda(1);
        v.setEstado("ocupado");

        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);

        assertThrows(IllegalStateException.class, ()
                -> viviendaService.eliminarVivienda(1)
        );
    }

}
