package com.smartoccupation;

import com.smartoccupation.dao.*;
import com.smartoccupation.servicios.*;
import com.smartoccupation.gui.MainFrame;
import com.smartoccupation.utilidades.ConexionBBDD;

import javax.swing.*;
import java.sql.Connection;
import java.util.logging.Logger;

public class SmartOccupation {

    private static final Logger logger = Logger.getLogger(SmartOccupation.class.getName());

    public static void main(String[] args) {

        // 1️⃣ Conexión inicial + creación de BBDD y tablas
        Connection conn = ConexionBBDD.conectar();

        if (conn == null) {
            JOptionPane.showMessageDialog(null,
                    "No es posible iniciar la aplicación sin conexión a la base de datos.",
                    "Error crítico",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 2️⃣ Crear DAOs
        ClienteDAO clienteDAO = new ClienteDAO();
        AlquilerDAO alquilerDAO = new AlquilerDAO();
        ViviendaDAO viviendaDAO = new ViviendaDAO();
        PagoDAO pagoDAO = new PagoDAO();
        EstadoCobroDAO estadoCobroDAO = new EstadoCobroDAO();

        // 3️⃣ Crear servicios
        ClienteService clienteService = new ClienteService(clienteDAO, alquilerDAO);
        ViviendaService viviendaService = new ViviendaService(viviendaDAO);
        AlquilerService alquilerService = new AlquilerService(alquilerDAO, viviendaDAO, estadoCobroDAO, clienteDAO);
        PagoService pagoService = new PagoService(pagoDAO);
        EstadoCobroService estadoCobroService = new EstadoCobroService(estadoCobroDAO);

        // 4️⃣ Configurar Look & Feel
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

        // 5️⃣ Lanzar la GUI
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                    clienteService,
                    viviendaService,
                    alquilerService,
                    pagoService,
                    estadoCobroService
            );
            frame.setVisible(true);
        });
    }
}
