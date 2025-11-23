package com.smartoccupation;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.gui.MainFrame;
import com.smartoccupation.servicios.*;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Logger;

public class SmartOccupation {

    private static final Logger logger = Logger.getLogger(SmartOccupation.class.getName());

    public static void main(String[] args) {

        // Crear DAOs
        ClienteDAO clienteDAO = new ClienteDAO();
        AlquilerDAO alquilerDAO = new AlquilerDAO();
        ViviendaDAO viviendaDAO = new ViviendaDAO();
        PagoDAO pagoDAO = new PagoDAO();
        EstadoCobroDAO estadoCobroDAO = new EstadoCobroDAO();

        // Crear servicios con inyección de dependencias
        ClienteService clienteService = new ClienteService(clienteDAO, alquilerDAO);
        ViviendaService viviendaService = new ViviendaService(viviendaDAO);
        AlquilerService alquilerService = new AlquilerService(alquilerDAO, viviendaDAO, estadoCobroDAO);

        // 🔥 CORREGIDO: ahora se envían los 3 DAOs necesarios
        PagoService pagoService = new PagoService(pagoDAO, alquilerDAO, estadoCobroDAO);

        EstadoCobroService estadoCobroService = new EstadoCobroService(estadoCobroDAO);

        // Configurar Look & Feel
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

        // Ejecutar GUI en el EDT
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
