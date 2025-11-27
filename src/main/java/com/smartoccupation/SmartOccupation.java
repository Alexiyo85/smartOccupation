package com.smartoccupation;

import com.smartoccupation.dao.*;
import com.smartoccupation.servicios.*;
import com.smartoccupation.gui.MainFrame;
import com.smartoccupation.utilidades.ConexionBBDD;

import javax.swing.*;
import java.sql.Connection;
import java.util.logging.Logger;

/**
 * Clase principal que inicializa y lanza la aplicación SmartOccupation. 🏡
 * <p>
 * Se encarga de establecer la conexión inicial con la base de datos,
 * instanciar la capa de Acceso a Datos (DAOs), la capa de Lógica de Negocio
 * (Servicios) y, finalmente, construir e iniciar la Interfaz Gráfica de Usuario (GUI),
 * inyectando las dependencias necesarias.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class SmartOccupation {

    private static final Logger logger = Logger.getLogger(SmartOccupation.class.getName());

    /**
     * Punto de entrada principal para la aplicación.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {

        // 1️⃣ Conexión inicial + creación de BBDD y tablas
        // Se llama al método estático que verifica la existencia y crea la estructura si es necesario.
        Connection conn = ConexionBBDD.conectar();

        if (conn == null) {
            JOptionPane.showMessageDialog(null,
                    "No es posible iniciar la aplicación sin conexión a la base de datos.",
                    "Error crítico",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 2️⃣ Crear DAOs (Capa de Acceso a Datos)
        // Se instancian los objetos responsables de interactuar directamente con la BBDD.
        ClienteDAO clienteDAO = new ClienteDAO();
        AlquilerDAO alquilerDAO = new AlquilerDAO();
        ViviendaDAO viviendaDAO = new ViviendaDAO();
        PagoDAO pagoDAO = new PagoDAO();
        EstadoCobroDAO estadoCobroDAO = new EstadoCobroDAO();

        // 3️⃣ Crear Servicios (Capa de Lógica de Negocio)
        // Se inyectan los DAOs necesarios en cada servicio (Inyección de Dependencias).
        ClienteService clienteService = new ClienteService(clienteDAO, alquilerDAO);
        ViviendaService viviendaService = new ViviendaService(viviendaDAO);
        // El servicio de alquiler necesita múltiples DAOs para la hidratación y reglas de negocio.
        AlquilerService alquilerService = new AlquilerService(alquilerDAO, viviendaDAO, estadoCobroDAO, clienteDAO);
        PagoService pagoService = new PagoService(pagoDAO);
        EstadoCobroService estadoCobroService = new EstadoCobroService(estadoCobroDAO);

        // 4️⃣ Configurar Look & Feel (Apariencia de la GUI)
        try {
            // Intenta establecer el Look & Feel "Nimbus" para una apariencia moderna.
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.severe("Error al establecer Look & Feel: " + ex.getMessage());
            // Continúa con el Look & Feel por defecto si falla.
        }

        // 5️⃣ Lanzar la GUI
        // Se usa SwingUtilities.invokeLater para asegurar que la interfaz se crea en el Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(
                    clienteService,
                    viviendaService,
                    alquilerService,
                    pagoService,
                    estadoCobroService
            );
            // Se inyectan todos los servicios en el MainFrame
            frame.setVisible(true);
        });
    }
}