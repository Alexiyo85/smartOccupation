package com.smartoccupation.gui.paneles;

import com.smartoccupation.gui.dialog.AlquilerDialog;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.ClienteService; // IMPORTAR
import com.smartoccupation.servicios.ViviendaService; // IMPORTAR

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AlquilerPanel extends javax.swing.JPanel {

    private final AlquilerService alquilerService;
    // Agregamos estos servicios para pasárselos al diálogo
    private final ClienteService clienteService = new ClienteService();
    private final ViviendaService viviendaService = new ViviendaService();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AlquilerPanel(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
        initComponents();
        configurarTabla();
        cargarTabla();
        configurarEventos();
    }

    // ------------------------------------------------------------
    // CONFIGURACIÓN TABLA
    // ------------------------------------------------------------
    private void configurarTabla() {
        String[] columnas = {
            "Expediente", "Cliente", "Vivienda", "Fecha Inicio",
            "Fecha Fin Est.", "Precio Total", "Estado"
        };

        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAlquiler.setModel(model);
        tablaAlquiler.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // ------------------------------------------------------------
    // CARGAR DATOS EN TABLA
    // ------------------------------------------------------------
    private void cargarTabla() {
        try {
            List<Alquiler> alquileres = alquilerService.obtenerTodos();
            DefaultTableModel model = (DefaultTableModel) tablaAlquiler.getModel();
            model.setRowCount(0);

            for (Alquiler a : alquileres) {
                model.addRow(new Object[]{
                    a.getNumero_expediente(),
                    a.getCliente() != null ? a.getCliente().getNombre() : "—",
                    a.getVivienda() != null ? a.getVivienda().getDireccion() : "—",
                    a.getFecha_inicio() != null ? a.getFecha_inicio().format(formatter) : "",
                    a.getFecha_fin_estimada() != null ? a.getFecha_fin_estimada().format(formatter) : "",
                    a.getPrecio_total_estimado(),
                    a.getEstado()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error cargando alquileres: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------
    // EVENTOS DE BOTONES
    // ------------------------------------------------------------
    private void configurarEventos() {

        // ==================================================
        //  BOTÓN NUEVO (CORREGIDO)
        // ==================================================
        btnNuevo.addActionListener(e -> {
            // 1. Obtenemos la ventana padre como Window (sin casting a Frame)
            Window parent = SwingUtilities.getWindowAncestor(this);

            // 2. Pasamos los 3 servicios requeridos por el nuevo constructor
            AlquilerDialog dialog = new AlquilerDialog(parent, true, alquilerService, clienteService, viviendaService);

            dialog.setVisible(true);

            if (dialog.isGuardado()) {
                cargarTabla();
            }
        });

        // BOTÓN EDITAR
        btnEditar.addActionListener(e -> editarSeleccionado());

        // BOTÓN ELIMINAR
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        // BOTÓN REFRESCAR
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

    private void editarSeleccionado() {
        int fila = tablaAlquiler.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un alquiler para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expediente = (int) tablaAlquiler.getValueAt(fila, 0);
        Alquiler alquiler = alquilerService.obtenerAlquiler(expediente);

        if (alquiler == null) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar el alquiler.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ==================================================
        //  EDICIÓN (CORREGIDO)
        // ==================================================
        Window parent = SwingUtilities.getWindowAncestor(this);

        // Usamos el constructor de edición que acepta los servicios y la entidad
        AlquilerDialog dialog = new AlquilerDialog(parent, true, alquilerService, clienteService, viviendaService, alquiler);

        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarTabla();
        }
    }

    private void eliminarSeleccionado() {
        int fila = tablaAlquiler.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un alquiler para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int expediente = (int) tablaAlquiler.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea eliminar este alquiler?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = alquilerService.eliminarAlquiler(expediente);
            if (eliminado) {
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el alquiler.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error eliminando alquiler: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
