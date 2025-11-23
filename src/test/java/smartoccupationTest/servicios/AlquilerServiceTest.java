package smartoccupationTest.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.AlquilerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AlquilerServiceTest {

    @Mock private AlquilerDAO alquilerDAO;
    @Mock private ViviendaDAO viviendaDAO;
    @Mock private EstadoCobroDAO estadoDAO;

    @InjectMocks private AlquilerService alquilerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearAlquiler_viviendaDisponible() {
        Vivienda v = new Vivienda();
        v.setId_vivienda(1);
        v.setEstado("disponible");
        v.setPrecio_mensual(BigDecimal.valueOf(900));

        Alquiler a = new Alquiler();
        a.setId_vivienda(1);

        EstadoCobro ec = new EstadoCobro();
        ec.setId_estado(1);

        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);
        when(estadoDAO.obtenerPorNombre("pendiente")).thenReturn(ec);
        when(alquilerDAO.insertar(a)).thenReturn(true);

        boolean ok = alquilerService.crearAlquiler(a);

        assertThat(ok).isTrue();
        assertThat(v.getEstado()).isEqualTo("ocupado");
        verify(viviendaDAO).actualizar(v);
    }

    @Test
    void crearAlquiler_viviendaOcupada() {
        Vivienda v = new Vivienda();
        v.setEstado("ocupado");
        when(viviendaDAO.obtenerPorId(1)).thenReturn(v);

        Alquiler a = new Alquiler();
        a.setId_vivienda(1);

        assertThrows(IllegalStateException.class, () -> alquilerService.crearAlquiler(a));
    }

    @Test
    void eliminarAlquiler_devuelveViviendaDisponible() {
        Alquiler alq = new Alquiler();
        alq.setNumero_expediente(10);
        alq.setId_vivienda(5);

        Vivienda v = new Vivienda();
        v.setEstado("ocupado");
        v.setId_vivienda(5);

        when(alquilerDAO.obtenerPorId(10)).thenReturn(alq);
        when(viviendaDAO.obtenerPorId(5)).thenReturn(v);
        when(alquilerDAO.eliminar(10)).thenReturn(true);

        boolean ok = alquilerService.eliminarAlquiler(10);

        assertThat(ok).isTrue();
        assertThat(v.getEstado()).isEqualTo("disponible");
        verify(viviendaDAO).actualizar(v);
    }
}
