package com.smartoccupation.gui;

import com.smartoccupation.gui.paneles.ClientePanel;
import com.smartoccupation.gui.paneles.ViviendaPanel;
import com.smartoccupation.gui.paneles.AlquilerPanel;
import com.smartoccupation.gui.paneles.PagoPanel;
import com.smartoccupation.gui.paneles.EstadoCobroPanel;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.servicios.EstadoCobroService; // Importación necesaria

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Ventana principal de la aplicación. Utiliza un JTabbedPane para contener los
 * diferentes paneles de gestión. Implementa Inyección de Dependencias (DI) para
 * los servicios.
 */
public class MainFrame extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(MainFrame.class.getName());

    // ===============================================================
    // 1. DECLARACIÓN DE SERVICIOS (DEPENDENCIAS)
    // ===============================================================
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;
    private final AlquilerService alquilerService;
    private final PagoService pagoService;
    private final EstadoCobroService estadoCobroService; // Declaración del servicio de EstadoCobro

    // ===============================================================
    // 2. DECLARACIÓN DE COMPONENTES UI (Paneles)
    // ===============================================================
    private JTabbedPane tabbedPane;
    private ClientePanel panelClientes;
    private ViviendaPanel panelViviendas;
    private AlquilerPanel panelAlquileres;
    private PagoPanel panelPagos;
    private EstadoCobroPanel panelEstadosCobro;

    /**
     * Constructor principal. Recibe todos los servicios necesarios. Esto
     * asegura que la lógica de negocio esté desacoplada de la UI.
     */
    public MainFrame(ClienteService clienteService,
            ViviendaService viviendaService,
            AlquilerService alquilerService,
            PagoService pagoService,
            EstadoCobroService estadoCobroService) { // Recibimos el servicio de EstadoCobro

        // Asignación de servicios (Inyección de Dependencias)
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;
        this.alquilerService = alquilerService;
        this.pagoService = pagoService;
        this.estadoCobroService = estadoCobroService; // Inicialización

        initComponents();
    }

    /**
     * Inicializa los componentes de la interfaz principal (JFrame y
     * JTabbedPane).
     */
    private void initComponents() {
        setTitle("SmartOccupation - Gestión de Alquileres");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null); // Centrar la ventana
        setLayout(new BorderLayout());

        // Crear el contenedor de pestañas
        tabbedPane = new JTabbedPane();

        // Inicializar paneles pasando sus respectivas dependencias
        panelClientes = new ClientePanel(clienteService);
        panelViviendas = new ViviendaPanel(viviendaService);
        panelAlquileres = new AlquilerPanel(alquilerService);
        panelPagos = new PagoPanel(pagoService, alquilerService);

        // Se inicializa el panel de catálogo con su servicio correspondiente
        panelEstadosCobro = new EstadoCobroPanel(estadoCobroService);

        // Añadir pestañas al JTabbedPane
        tabbedPane.addTab("Clientes", panelClientes);
        tabbedPane.addTab("Viviendas", panelViviendas);
        tabbedPane.addTab("Alquileres", panelAlquileres);
        tabbedPane.addTab("Pagos", panelPagos);
        tabbedPane.addTab("Estados de Cobro", panelEstadosCobro); // Corrección: Faltaba el punto y coma

        // Añadir el JTabbedPane al centro del frame principal
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Método main: Punto de entrada de la aplicación. Se encarga de crear e
     * inicializar todos los servicios (la capa de negocio) y de lanzar la
     * interfaz en el hilo de eventos de Swing (EDT).
     */
    public static void main(String[] args) {
        // 1. Crear e inicializar todos los servicios (capa de negocio/DAO)
        ClienteService clienteService = new ClienteService();
        ViviendaService viviendaService = new ViviendaService();
        AlquilerService alquilerService = new AlquilerService();
        PagoService pagoService = new PagoService();
        EstadoCobroService estadoCobroService = new EstadoCobroService(); // Inicialización del servicio

        // 2. Configurar el Look & Feel de la interfaz (ejemplo: Nimbus)
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

        // 3. Lanzar la interfaz en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Instanciar el MainFrame inyectando todos los servicios
            MainFrame frame = new MainFrame(clienteService,
                    viviendaService,
                    alquilerService,
                    pagoService,
                    estadoCobroService); // Pasar el nuevo servicio
            frame.setVisible(true);
        });
    }
}
