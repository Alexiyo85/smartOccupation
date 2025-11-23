package smartoccupationTest.servicios;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.servicios.EstadoCobroService;
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

class EstadoCobroServiceTest {

    @Mock private EstadoCobroDAO estadoCobroDAO;
    @InjectMocks private EstadoCobroService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodos_retornaLista() {
        when(estadoCobroDAO.obtenerTodos())
                .thenReturn(Arrays.asList(new EstadoCobro(1, "pendiente")));

        List<EstadoCobro> lista = service.obtenerTodos();

        assertThat(lista).hasSize(1);
        verify(estadoCobroDAO).obtenerTodos();
    }

    @Test
    void obtenerEstadoCobroPorId_ok() {
        EstadoCobro ec = new EstadoCobro(1, "pendiente");
        when(estadoCobroDAO.obtenerPorId(1)).thenReturn(ec);

        EstadoCobro result = service.obtenerEstadoCobroPorId(1);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("pendiente");
    }

    @Test
    void crearEstadoCobro_noDebeExistirAntes() {
        EstadoCobro nuevo = new EstadoCobro(0, "pendiente");

        when(estadoCobroDAO.obtenerPorNombre("pendiente")).thenReturn(null);
        when(estadoCobroDAO.insertar(nuevo)).thenReturn(true);

        boolean ok = service.crearEstadoCobro(nuevo);

        assertThat(ok).isTrue();
        verify(estadoCobroDAO).insertar(nuevo);
    }

    @Test
    void crearEstadoCobro_yaExisteNombre_lanzaError() {
        EstadoCobro existente = new EstadoCobro(1, "pendiente");
        when(estadoCobroDAO.obtenerPorNombre("pendiente")).thenReturn(existente);

        EstadoCobro nuevo = new EstadoCobro(0, "pendiente");

        assertThrows(IllegalArgumentException.class, () -> service.crearEstadoCobro(nuevo));
    }
}
