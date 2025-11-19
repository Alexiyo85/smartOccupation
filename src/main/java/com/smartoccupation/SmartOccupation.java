package com.smartoccupation;

import com.smartoccupation.servicios.*;
import com.smartoccupation.gui.MainFrame;

import javax.swing.SwingUtilities;

public class SmartOccupation {

    public static void main(String[] args) {
        // Ejecutar la GUI en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Crear servicios
            ClienteService clienteService = new ClienteService();
            ViviendaService viviendaService = new ViviendaService();
            AlquilerService alquilerService = new AlquilerService();
            PagoService pagoService = new PagoService();

            // Crear la GUI principal y pasar los servicios
            MainFrame mainFrame = new MainFrame(clienteService, viviendaService, alquilerService, pagoService);
            mainFrame.setVisible(true);
        });
    }
}
