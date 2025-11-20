package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.servicios.EstadoCobroService;
import com.smartoccupation.gui.dialog.EstadoCobroDialog; // Necesario para Nuevo/Editar
import com.smartoccupation.gui.util.FormUtils;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * Panel de gestión para la entidad EstadoCobro (Catálogo).
 */
public class EstadoCobroPanel extends javax.swing.JPanel {

    private final EstadoCobroService estadoCobroService;

    // ===============================================================
    // CONSTRUCTOR
    // ===============================================================
    public EstadoCobroPanel(EstadoCobroService estadoCobroService) {
        this.estadoCobroService = estadoCobroService;
        initComponents();
        configurarTabla();
        configurarEventos();
        cargarTabla(); // Cargar datos al iniciar
    }

    // ===============================================================
    // LÓGICA DE TABLA
    // ===============================================================
    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos la tabla no editable
            }
        };

        // Definición de las columnas de la tabla de catálogo
        String[] columnas = {"ID", "Nombre / Descripción"};
        modelo.setColumnIdentifiers(columnas);
        tablaEstadoCobro.setModel(modelo);

        // Ocultar la columna ID, pero mantenerla en el modelo
        FormUtils.ocultarColumna(tablaEstadoCobro, 0);
    }

    public void cargarTabla() {
        try {
            List<EstadoCobro> estados = estadoCobroService.obtenerTodos();
            DefaultTableModel modelo = (DefaultTableModel) tablaEstadoCobro.getModel();
            modelo.setRowCount(0); // Limpiar filas existentes

            for (EstadoCobro estado : estados) {
                // Fila: ID, Nombre
                modelo.addRow(new Object[]{
                    estado.getId_estado(),
                    estado.getNombre_estado()
                });
            }
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error al cargar los estados de cobro: " + e.getMessage());
        }
    }

    private EstadoCobro obtenerSeleccionado() {
        int fila = tablaEstadoCobro.getSelectedRow();
        if (fila == -1) {
            FormUtils.mostrarAdvertencia(this, "Debe seleccionar un estado de cobro de la lista.");
            return null;
        }
        try {
            // El ID está en la columna 0 (oculta)
            Integer id = (Integer) tablaEstadoCobro.getModel().getValueAt(fila, 0);
            return estadoCobroService.obtenerEstadoCobroPorId(id);
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error al obtener el estado seleccionado: " + e.getMessage());
            return null;
        }
    }

    // ===============================================================
    // LÓGICA DE EVENTOS (CRUD)
    // ===============================================================
    private void configurarEventos() {
        btnNuevo.addActionListener(e -> nuevoEstadoCobro());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnRefrescar.addActionListener(e -> cargarTabla());
    }

        private void nuevoEstadoCobro() {
        // Obtenemos el Frame principal para centrar el diálogo
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

        // Creamos el diálogo para un nuevo registro (null como EstadoCobro)
        EstadoCobroDialog dialog = new EstadoCobroDialog(parent, true, estadoCobroService, null);
        dialog.setVisible(true);

        // Si se guardó algo, recargamos la tabla
        cargarTabla();
    }

    private void editarSeleccionado() {
        EstadoCobro seleccionado = obtenerSeleccionado();
        if (seleccionado != null) {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

            // Creamos el diálogo cargando el EstadoCobro seleccionado
            EstadoCobroDialog dialog = new EstadoCobroDialog(parent, true, estadoCobroService, seleccionado);
            dialog.setVisible(true);

            // Si se actualizó algo, recargamos la tabla
            cargarTabla();
        }
    }

    private void eliminarSeleccionado() {
        EstadoCobro seleccionado = obtenerSeleccionado();
        if (seleccionado != null) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar el estado: " + seleccionado.getNombre() + "?",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                try {
                    estadoCobroService.eliminarEstadoCobro(seleccionado.getId_estado());
                    FormUtils.mostrarInfo(this, "Estado de cobro eliminado correctamente.");
                    cargarTabla(); // Recargar tras eliminar
                } catch (Exception e) {
                    // Nota: Si el estado está en uso (llave foránea), la DB tirará un error.
                    FormUtils.mostrarError(this, "Error al eliminar el estado de cobro: "
                            + "Verifique que no esté en uso por algún registro. "
                            + e.getMessage());
                }
            }
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
        tablaEstadoCobro = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        btnNuevo.setText("Nuevo");
        jPanel1.add(btnNuevo);

        btnEditar.setText("Editar");
        jPanel1.add(btnEditar);

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });
        jPanel1.add(btnEliminar);

        btnRefrescar.setText("Refrescar");
        jPanel1.add(btnRefrescar);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        tablaEstadoCobro.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablaEstadoCobro);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnEliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaEstadoCobro;
    // End of variables declaration//GEN-END:variables
}
