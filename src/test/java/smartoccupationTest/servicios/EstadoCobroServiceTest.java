package smartoccupationTest.servicios;

import com.smartoccupation.gui.paneles.EstadoCobroPanel;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EstadoCobroPanelTest {

    private AlquilerService alquilerService;
    private PagoService pagoService;
    private EstadoCobroPanel panel;

    @BeforeEach
    void setUp() {
        alquilerService = mock(AlquilerService.class);
        pagoService = mock(PagoService.class);

        // Crear alquiler simulado
        Alquiler alquiler = new Alquiler();
        alquiler.setNumeroExpediente(101);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNombre("Juan");
        cliente.setPrimerApellido("Perez");
        cliente.setSegundoApellido("Lopez");
        alquiler.setCliente(cliente);

        Vivienda vivienda = new Vivienda();
        vivienda.setIdVivienda(1);
        vivienda.setDireccion("Calle Falsa 123");
        alquiler.setVivienda(vivienda);

        alquiler.setPrecioTotalEstimado(new BigDecimal("1000"));

        when(alquilerService.obtenerTodos()).thenReturn(List.of(alquiler));

        // Crear pagos simulados
        Pago pago1 = new Pago();
        pago1.setId_pago(1);
        pago1.setNumeroExpediente(101);
        pago1.setCantidad(new BigDecimal("300"));
        pago1.setFechaPago(LocalDate.now());

        Pago pago2 = new Pago();
        pago2.setId_pago(2);
        pago2.setNumeroExpediente(101);
        pago2.setCantidad(new BigDecimal("700"));
        pago2.setFechaPago(LocalDate.now());

        when(pagoService.obtenerPagosPorExpediente(101)).thenReturn(List.of(pago1, pago2));

        panel = new EstadoCobroPanel(alquilerService, pagoService);
    }

    @Test
    void testCargaInicialTabla() {
        DefaultTableModel model = (DefaultTableModel) panel.getTablaCobros().getModel();
        assertEquals(1, model.getRowCount());
        assertEquals(101, model.getValueAt(0, 0));
        assertEquals("Juan Perez Lopez", model.getValueAt(0, 1));
        assertEquals("Calle Falsa 123", model.getValueAt(0, 2));
        assertEquals("1000", model.getValueAt(0, 3));
        assertEquals("1000", model.getValueAt(0, 4)); // totalPagado
        assertEquals("0", model.getValueAt(0, 5));    // pendiente
        assertEquals("Pagado", model.getValueAt(0, 6));
    }

    @Test
    void testFiltroComboBox() {
        panel.getCbEstado().setSelectedItem("Pagado");
        panel.refrescarTabla();

        DefaultTableModel model = (DefaultTableModel) panel.getTablaCobros().getModel();
        assertEquals(1, model.getRowCount());

        panel.getCbEstado().setSelectedItem("Pendiente");
        panel.refrescarTabla();

        model = (DefaultTableModel) panel.getTablaCobros().getModel();
        assertEquals(0, model.getRowCount());
    }

    @Test
    void testBotonRefrescarLlamaCarga() {
        EstadoCobroPanel spyPanel = Mockito.spy(panel);

        // Remover listeners previos para que el spy reciba la llamada
        for (var al : spyPanel.getBtnRefrescar().getActionListeners()) {
            spyPanel.getBtnRefrescar().removeActionListener(al);
        }
        spyPanel.getBtnRefrescar().addActionListener(e -> spyPanel.refrescarTabla());

        doNothing().when(spyPanel).refrescarTabla();

        // Simular click en botón
        spyPanel.getBtnRefrescar().doClick();

        verify(spyPanel, times(1)).refrescarTabla();
    }

    @Test
    void testManejoPagoNull() {
        Pago pagoNull = new Pago();
        pagoNull.setId_pago(3);
        pagoNull.setNumeroExpediente(101);
        pagoNull.setCantidad(null);
        pagoNull.setFechaPago(LocalDate.now());

        when(pagoService.obtenerPagosPorExpediente(101)).thenReturn(List.of(pagoNull));

        panel.refrescarTabla();

        DefaultTableModel model = (DefaultTableModel) panel.getTablaCobros().getModel();
        assertEquals("Pendiente", model.getValueAt(0, 6));
        assertEquals("0", model.getValueAt(0, 4)); // totalPagado tratado como 0
        assertEquals("1000", model.getValueAt(0, 5)); // pendiente
    }

    @Test
    void testConstructorNullService() {
        assertThrows(IllegalArgumentException.class, () -> new EstadoCobroPanel(null, pagoService));
        assertThrows(IllegalArgumentException.class, () -> new EstadoCobroPanel(alquilerService, null));
    }
}
