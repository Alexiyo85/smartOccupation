package com.smartoccupation.gui;

import com.smartoccupation.gui.paneles.*;
import com.smartoccupation.servicios.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final ClienteService clienteService;
    private final ViviendaService viviendaService;
    private final AlquilerService alquilerService;
    private final PagoService pagoService;
    private final EstadoCobroService estadoCobroService;

    private JTabbedPane tabbedPane;
    private ClientePanel panelClientes;
    private ViviendaPanel panelViviendas;
    private AlquilerPanel panelAlquileres;
    private PagoPanel panelPagos;
    private EstadoCobroPanel panelEstadosCobro;

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

    private void initComponents() {
        setTitle("SmartOccupation - Gestión de Alquileres");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Paneles con sus servicios
        panelClientes = new ClientePanel(clienteService);
        panelViviendas = new ViviendaPanel(viviendaService);
        panelAlquileres = new AlquilerPanel(alquilerService, clienteService, viviendaService);
        panelPagos = new PagoPanel(pagoService, alquilerService, estadoCobroService);
        panelEstadosCobro = new EstadoCobroPanel( alquilerService, pagoService);

        // Añadir paneles a las pestañas
        tabbedPane.addTab("Clientes", panelClientes);
        tabbedPane.addTab("Viviendas", panelViviendas);
        tabbedPane.addTab("Alquileres", panelAlquileres);
        tabbedPane.addTab("Pagos", panelPagos);
        tabbedPane.addTab("Estados de Cobro", panelEstadosCobro);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
