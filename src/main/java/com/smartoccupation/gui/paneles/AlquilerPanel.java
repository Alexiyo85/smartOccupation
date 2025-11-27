package com.smartoccupation.gui.paneles;

import com.smartoccupation.gui.dialog.AlquilerDialog;
import com.smartoccupation.gui.util.FormUtils;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.servicios.ViviendaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Panel de la Interfaz Gráfica (GUI) para la gestión y visualización de Alquileres.
 * Permite crear, editar, eliminar y ver la lista de alquileres.
 * * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class AlquilerPanel extends JPanel {

    // Referencia al servicio de lógica de negocio para Alquileres
    private final AlquilerService alquilerService;
    // Referencia al servicio de lógica de negocio para Clientes (para diálogos/combos)
    private final ClienteService clienteService;
    // Referencia al servicio de lógica de negocio para Viviendas (para diálogos/combos)
    private final ViviendaService viviendaService;

    // Dentro de AlquilerPanel.java
    /**
     * Constructor que recibe por inyección de dependencias los servicios necesarios.
     */
    public AlquilerPanel(AlquilerService alquilerService,
                         ClienteService clienteService,
                         ViviendaService viviendaService) {
        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;

        initComponents(); // Inicializa los componentes visuales generados por el diseñador
        cargarTabla(); // Carga los datos de los alquileres en la tabla al iniciar

        // 🟢 Corrección para habilitar la tabla si initComponents la deshabilitó
        // Asegura que la tabla esté interactiva después de la inicialización
        tablaAlquiler.setEnabled(true); 

        iniciarEventos(); // Configura los listeners para los botones
    }

    /**
     * Configura los ActionListeners para los botones del panel.
     */
    private void iniciarEventos() {
        // Listener para el botón Refrescar: simplemente recarga la tabla
        btnRefrescar.addActionListener(e -> cargarTabla());

        // Dentro de iniciarEventos() al abrir diálogo:
        // Listener para el botón Nuevo: abre el diálogo de creación
        btnNuevo.addActionListener(e -> {
            // Crea una instancia del diálogo de Alquiler, pasándole los servicios
            AlquilerDialog dialog = new AlquilerDialog(
                    // Obtiene la ventana principal (Frame) para hacerlo modal
                    SwingUtilities.getWindowAncestor(this), true,
                    alquilerService, clienteService, viviendaService
            );
            dialog.setVisible(true); // Muestra el diálogo
            // Si el diálogo indica que se guardó un registro, actualiza la tabla
            if (dialog.isGuardado()) {
                cargarTabla();
            }
        });

        // Listener para el botón Editar: abre el diálogo de edición
        btnEditar.addActionListener(e -> {
            int fila = tablaAlquiler.getSelectedRow(); // Obtiene la fila seleccionada
            if (fila == -1) {
                // Si no hay fila seleccionada, muestra advertencia y sale
                FormUtils.mostrarAdvertencia(this, "Seleccione un alquiler.");
                return;
            }

            // Obtiene el ID (Número de Expediente) de la primera columna (índice 0)
            int id = Integer.parseInt(tablaAlquiler.getValueAt(fila, 0).toString());
            // Obtiene el objeto Alquiler completo del servicio
            Alquiler a = alquilerService.obtenerAlquiler(id);

            // Crea el diálogo de edición
            AlquilerDialog dialog = new AlquilerDialog(
                    SwingUtilities.getWindowAncestor(this), true,
                    alquilerService, clienteService, viviendaService
            );
            dialog.cargarAlquiler(a); // Carga los datos del alquiler en el formulario del diálogo
            dialog.setVisible(true); // Muestra el diálogo
            // Si el diálogo indica que se guardó una modificación, actualiza la tabla
            if (dialog.isGuardado()) {
                cargarTabla();
            }
        });
        // Listener para el botón Eliminar
        btnEliminar.addActionListener(e -> eliminar());
    }

    /**
     * Maneja la lógica para eliminar un alquiler seleccionado.
     */
    private void eliminar() {
        int fila = tablaAlquiler.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un alquiler.");
            return;
        }

        // Obtener el ID de la fila seleccionada
        int id = Integer.parseInt(tablaAlquiler.getValueAt(fila, 0).toString());

        // Pide confirmación al usuario antes de eliminar
        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar el alquiler?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            // Llama al servicio para ejecutar la eliminación
            try {
                if (alquilerService.eliminarAlquiler(id)) {
                    JOptionPane.showMessageDialog(this, "Alquiler eliminado.");
                    cargarTabla(); // Recarga la tabla tras la eliminación exitosa
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
                }
            } catch (IllegalStateException ex) {
                 // Captura excepciones de reglas de negocio (ej: no se puede eliminar si tiene pagos)
                 JOptionPane.showMessageDialog(this, "Error de Negocio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Obtiene la lista de todos los alquileres del servicio y actualiza la tabla.
     */
    public void cargarTabla() {
        // Obtiene la lista de todos los alquileres
        List<Alquiler> lista = alquilerService.obtenerTodos();

        // Define el modelo de la tabla con las columnas deseadas
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Vivienda", "Inicio", "Meses", "Días", "Total"}, 0
        ) {
            // Sobrescribe isCellEditable para que ninguna celda pueda ser editada directamente en la tabla
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // Itera sobre la lista de alquileres para llenar el modelo
        for (Alquiler a : lista) {
            model.addRow(new Object[]{
                a.getNumeroExpediente(), // ID (PK)
                // Muestra el nombre completo del cliente si el objeto está hidratado, sino muestra el ID
                a.getCliente() != null
                ? a.getCliente().getNombre() + " " + a.getCliente().getPrimerApellido() + " " + a.getCliente().getSegundoApellido()
                : a.getIdCliente(),
                // Muestra la dirección de la vivienda si está hidratada, sino muestra el ID
                a.getVivienda() != null ? a.getVivienda().getDireccion() : a.getIdVivienda(),
                a.getFechaInicio(),
                a.getTiempoMeses(),
                a.getTiempoDias(),
                a.getPrecioTotalEstimado() // Valor BigDecimal (podría mejorarse con FormUtils.formatBigDecimal)
            });
        }

        tablaAlquiler.setModel(model); // Asigna el modelo a la tabla
    }

    /**
     * Código autogenerado para inicializar los componentes visuales.
     * Este código define la estructura del panel (botones, tabla, layout).
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaAlquiler = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        btnNuevo.setText("Nuevo Alquiler");
        jPanel1.add(btnNuevo);

        btnEditar.setText("Editar");
        jPanel1.add(btnEditar);

        btnEliminar.setText("Eliminar");
        jPanel1.add(btnEliminar);

        btnRefrescar.setText("Refrescar");
        jPanel1.add(btnRefrescar);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        tablaAlquiler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaAlquiler.setEnabled(false);
        jScrollPane1.setViewportView(tablaAlquiler);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaAlquiler;
    // End of variables declaration//GEN-END:variables
}
