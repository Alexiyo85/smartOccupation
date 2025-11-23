package smartoccupationTest.servicios;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PagoServiceTest {

    @Mock private PagoDAO pagoDAO;
    @Mock private AlquilerDAO alquilerDAO;
    @Mock private EstadoCobroDAO estadoCobroDAO;

    @InjectMocks private PagoService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarPago_ok() {
        Pago p = new Pago();
        p.setNumero_expediente(10);

        Alquiler a = new Alquiler();
        a.setNumero_expediente(10);
        a.setPrecio_total_estimado(BigDecimal.valueOf(1000));

        EstadoCobro pagado = new EstadoCobro(2, "pagado");

        when(alquilerDAO.obtenerPorId(10)).thenReturn(a);
        when(pagoDAO.insertar(p)).thenReturn(true);
        when(pagoDAO.obtenerTotalPagadoPorAlquiler(10)).thenReturn(BigDecimal.valueOf(1000));
        when(estadoCobroDAO.obtenerPorNombre("pagado")).thenReturn(pagado);

        boolean ok = service.registrarPago(p);

        assertThat(ok).isTrue();
        verify(alquilerDAO).actualizar(a);
    }

    @Test
    void eliminarPago_ok() {
        when(pagoDAO.eliminar(5)).thenReturn(true);

        boolean ok = service.eliminarPago(5);

        assertThat(ok).isTrue();
    }

    @Test
    void buscarPagosPorFecha_ok() {
        Pago p1 = new Pago();
        Pago p2 = new Pago();

        when(pagoDAO.obtenerPorRangoFechas(any(), any()))
                .thenReturn(Arrays.asList(p1, p2));

        List<Pago> lista = service.buscarPagosPorFecha(LocalDate.now().minusDays(3), LocalDate.now());

        assertThat(lista).hasSize(2);
        verify(pagoDAO). obtenerPorRangoFechas(any(), any());
    }
}
