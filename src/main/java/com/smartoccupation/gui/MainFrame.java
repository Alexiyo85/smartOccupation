package com.smartoccupation.gui;

import com.smartoccupation.gui.paneles.*;
import com.smartoccupation.servicios.*;

import javax.swing.*;
import java.awt.*;

/**
 * La ventana principal de la aplicación SmartOccupation. 
 * <p>
 * Esta clase extiende {@link JFrame} y actúa como el contenedor principal de la Interfaz Gráfica de Usuario (GUI).
 * Es responsable de:
 * <ul>
 * <li>Recibir y almacenar todas las instancias de la capa de Servicios (Business Logic Layer) mediante inyección de dependencias.</li>
 * <li>Inicializar el {@link JTabbedPane} que organiza las distintas funcionalidades de la aplicación (Clientes, Viviendas, Alquileres, Pagos, Estados de Cobro).</li>
 * <li>Crear los paneles específicos de la GUI, inyectándoles los servicios que necesitan.</li>
 * </ul>
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class MainFrame extends JFrame {

    // ----------------------------------------------------
    // Dependencias de Servicios (Capa de Lógica de Negocio)
    // ----------------------------------------------------
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;
    private final AlquilerService alquilerService;
    private final PagoService pagoService;
    private final EstadoCobroService estadoCobroService;

    // ----------------------------------------------------
    // Componentes de la GUI
    // ----------------------------------------------------
    private JTabbedPane tabbedPane;
    private ClientePanel panelClientes;
    private ViviendaPanel panelViviendas;
    private AlquilerPanel panelAlquileres;
    private PagoPanel panelPagos;
    private EstadoCobroPanel panelEstadosCobro;

    /**
     * Constructor principal del MainFrame.
     * <p>
     * Recibe todas las dependencias de la capa de servicios para inyectarlas
     * en los paneles específicos de la GUI.
     * </p>
     *
     * @param clienteService El servicio para la gestión de clientes.
     * @param viviendaService El servicio para la gestión de viviendas.
     * @param alquilerService El servicio para la gestión de alquileres.
     * @param pagoService El servicio para la gestión de pagos.
     * @param estadoCobroService El servicio para la consulta de estados de cobro.
     */
    public MainFrame(ClienteService clienteService,
                     ViviendaService viviendaService,
                     AlquilerService alquilerService,
                     PagoService pagoService,
                     EstadoCobroService estadoCobroService) {

        this.clienteService = clienteService;
        this.viviendaService = viviendaService;
        this.alquilerService = alquilerService;
        this.pagoService = pagoService;
        this.estadoCobroService = estadoCobroService;

        initComponents();
    }

    /**
     * Inicializa y configura todos los componentes de la interfaz de usuario.
     */
    private void initComponents() {
        setTitle("SmartOccupation - Gestión de Alquileres");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 700);
        // Centra la ventana en la pantalla
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // ----------------------------------------------------
        // Creación de Paneles e Inyección de Dependencias
        // ----------------------------------------------------

        // ClientePanel solo necesita ClienteService
        panelClientes = new ClientePanel(clienteService);
        
        // ViviendaPanel solo necesita ViviendaService
        panelViviendas = new ViviendaPanel(viviendaService);
        
        // AlquilerPanel necesita servicios para Clientes, Viviendas y Alquileres.
        panelAlquileres = new AlquilerPanel(alquilerService, clienteService, viviendaService);
        
        // PagoPanel necesita PagoService y referencias a AlquilerService y EstadoCobroService para operaciones relacionadas.
        panelPagos = new PagoPanel(pagoService, alquilerService, estadoCobroService);
        
        // EstadoCobroPanel necesita AlquilerService y PagoService para operaciones de reporte/vista.
        panelEstadosCobro = new EstadoCobroPanel( alquilerService, pagoService);

        // ----------------------------------------------------
        // Añadir paneles a las pestañas del JTabbedPane
        // ----------------------------------------------------
        tabbedPane.addTab("Clientes", panelClientes);
        tabbedPane.addTab("Viviendas", panelViviendas);
        tabbedPane.addTab("Alquileres", panelAlquileres);
        tabbedPane.addTab("Pagos", panelPagos);
        tabbedPane.addTab("Estados de Cobro", panelEstadosCobro);

        add(tabbedPane, BorderLayout.CENTER);
    }
}