package com.smartoccupation;

import com.smartoccupation.gui.MainFrame;
import com.smartoccupation.servicios.*; // Asumimos que estos imports están aquí
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Logger;

public class SmartOccupation {
    
    private static final Logger logger = Logger.getLogger(SmartOccupation.class.getName());

    public static void main(String[] args) {
        
        // Crear instancias de servicios (capa de negocio)
        ClienteService clienteService = new ClienteService();
        ViviendaService viviendaService = new ViviendaService();
        AlquilerService alquilerService = new AlquilerService();
        PagoService pagoService = new PagoService();
        // AÑADIR ESTE SERVICIO:
        EstadoCobroService estadoCobroService = new EstadoCobroService(); // <--- 1. CREAR INSTANCIA

        // Configurar Look & Feel (Opcional, pero bueno tenerlo)
        try {
            // ... código de Look & Feel
        } catch (Exception ex) {
            logger.severe("Error al establecer Look & Feel: " + ex.getMessage());
        }

        // Mostrar la ventana principal en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // CORREGIR LA LLAMADA AL CONSTRUCTOR:
            MainFrame frame = new MainFrame(
                clienteService, 
                viviendaService, 
                alquilerService, 
                pagoService,
                estadoCobroService // <--- 2. PASAR EL NUEVO ARGUMENTO
            );
            frame.setVisible(true);
        });
    }
}