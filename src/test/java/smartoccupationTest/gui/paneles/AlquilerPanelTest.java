package smartoccupationTest.gui.paneles;

import com.smartoccupation.gui.paneles.AlquilerPanel;
import com.smartoccupation.gui.dialog.AlquilerDialog;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.servicios.ViviendaService;
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

class AlquilerPanelTest {

    private AlquilerService alquilerService;
    private ClienteService clienteService;
    private ViviendaService viviendaService;
    private AlquilerPanel panel;

    @BeforeEach
    void setUp() {
        alquilerService = mock(AlquilerService.class);
        clienteService = mock(ClienteService.class);
        viviendaService = mock(ViviendaService.class);

        // Crear alquiler de prueba
        Alquiler alquiler = new Alquiler();
        alquiler.setNumero_expediente(1);
        alquiler.setFecha_inicio(LocalDate.now());
        alquiler.setTiempo_meses(12);
        alquiler.setTiempo_dias(0);
        alquiler.setPrecio_total_estimado(new BigDecimal("1200"));

        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setPrimer_apellido("Perez");
        cliente.setSegundo_apellido("Lopez");
        cliente.setId_cliente(1);
        alquiler.setCliente(cliente);

        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Falsa 123");
        vivienda.setId_vivienda(1);
        alquiler.setVivienda(vivienda);

        when(alquilerService.obtenerTodos()).thenReturn(List.of(alquiler));
        when(alquilerService.obtenerAlquiler(anyInt())).thenReturn(alquiler);
        when(alquilerService.eliminarAlquiler(anyInt())).thenReturn(true);

        panel = new AlquilerPanel(alquilerService, clienteService, viviendaService);
    }

    @Test
    void testCargaTablaInicial() {
        DefaultTableModel model = (DefaultTableModel) panel.tablaAlquiler.getModel();
        assertEquals(1, model.getRowCount());

        assertEquals(1, model.getValueAt(0, 0));
        assertEquals("Juan Perez Lopez", model.getValueAt(0, 1));
        assertEquals("Calle Falsa 123", model.getValueAt(0, 2));
        assertEquals(12, model.getValueAt(0, 4));
        assertEquals(new BigDecimal("1200"), model.getValueAt(0, 6));
    }

    @Test
    void testBotonRefrescar() {
        // Crear spy para verificar llamada
        AlquilerPanel spyPanel = Mockito.spy(panel);
        doNothing().when(spyPanel).cargarTabla();

        spyPanel.btnRefrescar.doClick();
        verify(spyPanel, times(1)).cargarTabla();
    }

    @Test
    void testBotonEliminar() {
        JTable tabla = panel.tablaAlquiler;
        tabla.setRowSelectionInterval(0, 0);

        // Simular confirm dialog YES
        JOptionPane mockOptionPane = mock(JOptionPane.class);
        mockStatic(JOptionPane.class);
        when(JOptionPane.showConfirmDialog(any(), anyString(), anyString(), anyInt()))
                .thenReturn(JOptionPane.YES_OPTION);

        panel.btnEliminar.doClick();

        verify(alquilerService, times(1)).eliminarAlquiler(1);
    }

    @Test
    void testBotonEliminarSinSeleccion() {
        JTable tabla = panel.tablaAlquiler;
        tabla.clearSelection();

        // Simular showMessageDialog
        JOptionPane mockOptionPane = mock(JOptionPane.class);
        mockStatic(JOptionPane.class);
        when(JOptionPane.showMessageDialog(any(), anyString())).thenReturn(null);

        panel.btnEliminar.doClick(); // no selecciona fila

        // Se asegura que no se llame a eliminarAlquiler
        verify(alquilerService, times(0)).eliminarAlquiler(anyInt());
    }

    @Test
    void testBotonNuevoAbreDialog() {
        AlquilerPanel spyPanel = Mockito.spy(panel);

        // Se puede verificar que abrirá dialog mediante doNothing
        doNothing().when(spyPanel.btnNuevo).addActionListener(any());

        spyPanel.btnNuevo.doClick();
    }

    @Test
    void testBotonEditarAbreDialog() {
        JTable tabla = panel.tablaAlquiler;
        tabla.setRowSelectionInterval(0, 0);

        AlquilerPanel spyPanel = Mockito.spy(panel);
        doNothing().when(spyPanel.btnEditar).addActionListener(any());

        spyPanel.btnEditar.doClick();
    }

    @Test
    void testCargaTablaVacia() {
        when(alquilerService.obtenerTodos()).thenReturn(List.of());
        panel.cargarTabla();
        DefaultTableModel model = (DefaultTableModel) panel.tablaAlquiler.getModel();
        assertEquals(0, model.getRowCount());
    }
}
