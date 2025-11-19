package com.smartoccupation.gui;

import com.smartoccupation.gui.paneles.ClientePanel;
import com.smartoccupation.gui.paneles.ViviendaPanel;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.gui.paneles.AlquilerPanel;
import com.smartoccupation.gui.paneles.PagoPanel;
import com.smartoccupation.gui.paneles.EstadoCobroPanel;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public class MainFrame extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(MainFrame.class.getName());

    // Servicios
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;
    private final AlquilerService alquilerService;
    private final PagoService pagoService;

    // Componentes principales
    private JTabbedPane tabbedPane;
    private ClientePanel panelClientes;
    private ViviendaPanel panelViviendas;
    private AlquilerPanel panelAlquileres;
    private PagoPanel panelPagos;
    private EstadoCobroPanel panelEstadosCobro;

    /**
     * Constructor con servicios inyectados.
     */
    public MainFrame(ClienteService clienteService,
                     ViviendaService viviendaService,
                     AlquilerService alquilerService,
                     PagoService pagoService) {
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;
        this.alquilerService = alquilerService;
        this.pagoService = pagoService;

        initComponents();
    }

    /**
     * Inicializa la interfaz principal.
     */
    private void initComponents() {
        setTitle("SmartOccupation");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Crear el JTabbedPane
        tabbedPane = new JTabbedPane();

        // Inicializar paneles con servicios
        panelClientes = new ClientePanel(clienteService);
        panelViviendas = new ViviendaPanel(viviendaService);
        panelAlquileres = new AlquileresPanel(alquilerService, viviendaService, clienteService);
        panelPagos = new PagoPanel(pagoService, alquilerService);
        panelEstadosCobro = new EstadoCobroPanel(); // Puedes añadir EstadoCobroService si lo creas

        // Añadir pestañas al JTabbedPane
        tabbedPane.addTab("Clientes", panelClientes);
        tabbedPane.addTab("Viviendas", panelViviendas);
        tabbedPane.addTab("Alquileres", panelAlquileres);
        tabbedPane.addTab("Pagos", panelPagos);
        tabbedPane.addTab("Estados de Cobro", panelEstadosCobro);

        // Añadir el JTabbedPane al frame principal
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Método main para lanzar la aplicación
     */
    public static void main(String[] args) {
        // Crear servicios
        ClienteService clienteService = new ClienteService();
        ViviendaService viviendaService = new ViviendaService();
        AlquilerService alquilerService = new AlquilerService();
        PagoService pagoService = new PagoService();

        // Configurar look & feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.severe("Error al establecer Look & Feel: " + ex.getMessage());
        }

        // Mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(clienteService, viviendaService, alquilerService, pagoService);
            frame.setVisible(true);
        });
    }
}
