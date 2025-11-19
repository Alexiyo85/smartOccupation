package com.smartoccupation.gui.paneles;

import com.smartoccupation.gui.dialog.ClienteDialog;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ClientePanel extends javax.swing.JPanel {

    private final ClienteService clienteService;

    // ============================
    //       CONSTRUCTOR
    // ============================
    public ClientePanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents();
        cargarTablaClientes();
        configurarEventos();
    }

    // ===========================================
    //  CARGA LA TABLA CON LOS CLIENTES EXISTENTES
    // ===========================================
    private void cargarTablaClientes() {

        List<Cliente> lista = clienteService.obtenerTodos();

        String[] columnas = {
            "ID", "Nombre", "Primer Apellido", "Segundo Apellido",
            "DNI", "Teléfono", "Email", "Ciudad", "Provincia"
        };

        Object[][] datos = new Object[lista.size()][columnas.length];

        for (int i = 0; i < lista.size(); i++) {
            Cliente c = lista.get(i);
            datos[i][0] = c.getId_cliente();
            datos[i][1] = c.getNombre();
            datos[i][2] = c.getPrimer_apellido();
            datos[i][3] = c.getSegundo_apellido();
            datos[i][4] = c.getDni();
            datos[i][5] = c.getTelefono();
            datos[i][6] = c.getEmail();
            datos[i][7] = c.getCiudad();
            datos[i][8] = c.getProvincia();
        }

        tablaClientes.setModel(new DefaultTableModel(datos, columnas));
    }

    // ============================================
    //      CONFIGURACIÓN DE LOS BOTONES
    // ============================================
    private void configurarEventos() {

        // BOTÓN AÑADIR
        btnAñadirCliente.addActionListener(e -> {
            ClienteDialog dialogo = new ClienteDialog(null, true, clienteService);
            dialogo.setVisible(true);
            cargarTablaClientes();
        });

        // BOTÓN EDITAR
        btnEditarCliente.addActionListener(e -> editarCliente());

        // BOTÓN ELIMINAR
        btnEliminarCliente.addActionListener(e -> eliminarCliente());

        // BÚSQUEDA EN TIEMPO REAL
        txtBuscarCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla();
            }
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla();
            }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla();
            }
        });
    }

    // ===========================
    //     FILTRO DE BÚSQUEDA
    // ===========================
    private void filtrarTabla() {
        String busqueda = txtBuscarCliente.getText().trim().toLowerCase();

        DefaultTableModel modelo = (DefaultTableModel) tablaClientes.getModel();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            boolean coincide = false;

            for (int j = 1; j < modelo.getColumnCount(); j++) {
                Object celda = modelo.getValueAt(i, j);
                if (celda != null && celda.toString().toLowerCase().contains(busqueda)) {
                    coincide = true;
                    break;
                }
            }

            tablaClientes.setRowHeight(i, coincide ? 20 : 0);
        }
    }

    // ===========================
    //    EDITAR CLIENTE
    // ===========================
    private void editarCliente() {

        int fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tablaClientes.getValueAt(fila, 0);

        Cliente cliente = clienteService.obtenerCliente(id);

        ClienteDialog dialogo = new ClienteDialog(null, true, clienteService, cliente);
        dialogo.setVisible(true);

        cargarTablaClientes();
    }

    // ===========================
    //    ELIMINAR CLIENTE
    // ===========================
    private void eliminarCliente() {

        int fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tablaClientes.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar el cliente seleccionado?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteService.eliminarCliente(id);
                cargarTablaClientes();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ----------------------------------------------------------
    // Código generado por NetBeans (NO MODIFICAR)
    // ---------------------------------------------------------*
     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarCliente = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnAñadirCliente = new javax.swing.JButton();
        btnEditarCliente = new javax.swing.JButton();
        btnEliminarCliente = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Buscar:");
        jPanel1.add(jLabel1);
        jPanel1.add(txtBuscarCliente);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        btnAñadirCliente.setText("Añadir");
        jPanel2.add(btnAñadirCliente);

        btnEditarCliente.setText("Editar");
        btnEditarCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarClienteActionPerformed(evt);
            }
        });
        jPanel2.add(btnEditarCliente);

        btnEliminarCliente.setText("Eliminar");
        jPanel2.add(btnEliminarCliente);

        add(jPanel2, java.awt.BorderLayout.PAGE_END);

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Primer Apellido", "Segundo Apellido", "DNI", "Teléfono", "Email", "Ciudad", "Provincia"
            }
        ));
        jScrollPane1.setViewportView(tablaClientes);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditarClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditarClienteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAñadirCliente;
    private javax.swing.JButton btnEditarCliente;
    private javax.swing.JButton btnEliminarCliente;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTextField txtBuscarCliente;
    // End of variables declaration//GEN-END:variables
}
