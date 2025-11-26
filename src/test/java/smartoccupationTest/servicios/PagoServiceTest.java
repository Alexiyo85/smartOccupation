package smartoccupationTest.servicios;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PagoServiceTest {

    private PagoDAO pagoDAO;
    private PagoService service;

    @BeforeEach
    void setUp() {
        pagoDAO = mock(PagoDAO.class);
        service = new PagoService(pagoDAO);
    }

    @Test
    void testRegistrarPago() {
        Pago pago = new Pago();
        when(pagoDAO.insertar(pago)).thenReturn(true);

        boolean exito = service.registrarPago(pago);
        assertThat(exito).isTrue();
        verify(pagoDAO).insertar(pago);
    }

    @Test
    void testListarTodosLosPagos() {
        Pago p1 = new Pago();
        Pago p2 = new Pago();
        when(pagoDAO.obtenerTodos()).thenReturn(List.of(p1, p2));

        List<Pago> lista = service.listarTodosLosPagos();
        assertThat(lista).hasSize(2);
        verify(pagoDAO).obtenerTodos();
    }

    @Test
    void testBuscarPagosPorFecha() {
        LocalDate desde = LocalDate.of(2025, 1, 1);
        LocalDate hasta = LocalDate.of(2025, 12, 31);
        Pago p1 = new Pago();
        Pago p2 = new Pago();

        when(pagoDAO.buscarPorRangoFechas(desde, hasta)).thenReturn(List.of(p1, p2));

        List<Pago> lista = service.buscarPagosPorFecha(desde, hasta);
        assertThat(lista).hasSize(2);
        verify(pagoDAO).buscarPorRangoFechas(desde, hasta);
    }

    @Test
    void testEliminarPago() {
        when(pagoDAO.eliminar(1)).thenReturn(true);

        boolean exito = service.eliminarPago(1);
        assertThat(exito).isTrue();
        verify(pagoDAO).eliminar(1);
    }

    @Test
    void testObtenerPagosPorExpediente() {
        Pago p1 = new Pago();
        Pago p2 = new Pago();
        when(pagoDAO.obtenerPorExpediente(100)).thenReturn(List.of(p1, p2));

        List<Pago> lista = service.obtenerPagosPorExpediente(100);
        assertThat(lista).hasSize(2);
        verify(pagoDAO).obtenerPorExpediente(100);
    }
}
