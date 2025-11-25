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

public class AlquilerPanel extends JPanel {

    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;

    // Dentro de AlquilerPanel.java
    public AlquilerPanel(AlquilerService alquilerService,
            ClienteService clienteService,
            ViviendaService viviendaService) {
        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;

        initComponents();
        cargarTabla();

        // 🟢 Corrección para habilitar la tabla si initComponents la deshabilitó
        tablaAlquiler.setEnabled(true);

        iniciarEventos();
    }

    private void iniciarEventos() {
        btnRefrescar.addActionListener(e -> cargarTabla());

        // Dentro de iniciarEventos() al abrir diálogo:
        btnNuevo.addActionListener(e -> {
            AlquilerDialog dialog = new AlquilerDialog(
                    SwingUtilities.getWindowAncestor(this), true,
                    alquilerService, clienteService, viviendaService
            );
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                cargarTabla();
            }
        });

        btnEditar.addActionListener(e -> {
            int fila = tablaAlquiler.getSelectedRow();
            if (fila == -1) {
                FormUtils.mostrarAdvertencia(this, "Seleccione un alquiler.");
                return;
            }

            int id = Integer.parseInt(tablaAlquiler.getValueAt(fila, 0).toString());
            Alquiler a = alquilerService.obtenerAlquiler(id);

            AlquilerDialog dialog = new AlquilerDialog(
                    SwingUtilities.getWindowAncestor(this), true,
                    alquilerService, clienteService, viviendaService
            );
            dialog.cargarAlquiler(a);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                cargarTabla();
            }
        });
        btnEliminar.addActionListener(e -> eliminar());
    }

    private void eliminar() {
        int fila = tablaAlquiler.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un alquiler.");
            return;
        }

        int id = Integer.parseInt(tablaAlquiler.getValueAt(fila, 0).toString());

        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar el alquiler?", "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            if (alquilerService.eliminarAlquiler(id)) {
                JOptionPane.showMessageDialog(this, "Alquiler eliminado.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar.");
            }
        }
    }

    private void cargarTabla() {
        List<Alquiler> lista = alquilerService.obtenerTodos();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Vivienda", "Inicio", "Meses", "Días", "Total"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (Alquiler a : lista) {
            model.addRow(new Object[]{
                a.getNumero_expediente(),
                a.getCliente() != null
                ? a.getCliente().getNombre() + " " + a.getCliente().getPrimer_apellido() + " " + a.getCliente().getSegundo_apellido()
                : a.getId_cliente(),
                a.getVivienda() != null ? a.getVivienda().getDireccion() : a.getId_vivienda(),
                a.getFecha_inicio(),
                a.getTiempo_meses(),
                a.getTiempo_dias(),
                a.getPrecio_total_estimado()
            });
        }

        tablaAlquiler.setModel(model);
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
