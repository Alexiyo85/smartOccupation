package com.smartoccupation.gui.paneles;

import com.smartoccupation.gui.dialog.ClienteDialog;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // 👈 NUEVO: Importación necesaria
import java.awt.*;
import java.util.List;

public class ClientePanel extends javax.swing.JPanel {

    private final ClienteService clienteService;
    // 1. 👈 NUEVO: Campo para gestionar el filtro/ordenación de la tabla
    private TableRowSorter<DefaultTableModel> sorter;

// ============================
//         CONSTRUCTOR
// ============================
    public ClientePanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents();
        cargarTablaClientes();
        configurarEventos();
    }

// ===========================================
//  CARGA LA TABLA CON LOS CLIENTES EXISTENTES (MODIFICADO)
// ===========================================
    private void cargarTablaClientes() {

        List<Cliente> lista = clienteService.obtenerTodos();

        String[] columnas = {
            "ID", "Nombre", "Primer Apellido", "Segundo Apellido",
            "DNI", "Teléfono", "Email", "Dirección", "Ciudad", "Provincia",
            "Código Postal"
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
            datos[i][7] = c.getDireccion();
            datos[i][8] = c.getCiudad();
            datos[i][9] = c.getProvincia();
            datos[i][10] = c.getCodigo_postal();
        }

        // 2. Creamos el modelo
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes.setModel(modelo);

        // 3. 👈 NUEVO: Creamos y asignamos el sorter al modelo de la tabla
        sorter = new TableRowSorter<>(modelo);
        tablaClientes.setRowSorter(sorter);
    }

// ============================================
//       CONFIGURACIÓN DE LOS BOTONES
// ============================================
    private void configurarEventos() {

        // BOTÓN AÑADIR
        btnAñadirCliente.addActionListener(e -> abrirDialogoCliente(null));

        // BOTÓN EDITAR
        btnEditarCliente.addActionListener(e -> {
            int fila = tablaClientes.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Importante: Si se usa sorter, el ID debe obtenerse del modelo subyacente.
            // Para obtener la fila real del modelo subyacente:
            int modeloFila = tablaClientes.convertRowIndexToModel(fila);

            // ID está en la columna 0 del modelo subyacente
            int id = (int) tablaClientes.getModel().getValueAt(modeloFila, 0);
            Cliente cliente = clienteService.obtenerCliente(id);
            abrirDialogoCliente(cliente);
        });

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
//      FILTRO DE BÚSQUEDA (MODIFICADO Y CORREGIDO)
// ===========================
    private void filtrarTabla() {
        String busqueda = txtBuscarCliente.getText().trim();

        if (sorter == null) {
            // Evitar fallo si se llama antes de cargar la tabla.
            return;
        }

        if (busqueda.isEmpty()) {
            // Si el campo está vacío, no hay filtro.
            sorter.setRowFilter(null);
        } else {
            try {
                // 4. 👈 NUEVO: Aplicar el filtro usando una expresión regular.
                // "(?i)" ignora mayúsculas/minúsculas.
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + busqueda));
            } catch (java.util.regex.PatternSyntaxException e) {
                // Si la expresión es inválida (ej: un solo paréntesis), simplemente no aplicamos filtro.
                sorter.setRowFilter(null);
            }
        }
    }

// ===========================
//    ABRIR DIÁLOGO CLIENTE
// ===========================
    private void abrirDialogoCliente(Cliente cliente) {
        // Obtenemos la ventana padre (sea JFrame o JDialog)
        Window parent = SwingUtilities.getWindowAncestor(this);

        ClienteDialog dialog;

        // El constructor de ClienteDialog ahora acepta Window, así que esto es seguro
        if (cliente == null) {
            dialog = new ClienteDialog(parent, true, clienteService);
        } else {
            dialog = new ClienteDialog(parent, true, clienteService, cliente);
        }

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        // Recargar la tabla al cerrar el diálogo
        cargarTablaClientes();
        filtrarTabla(); // Aplicar el filtro si había alguno
    }

// ===========================
//     ELIMINAR CLIENTE
// ===========================
    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Importante: Debemos obtener el ID de la fila en el modelo, no en la vista,
        // ya que el sorter podría haber reordenado o filtrado la vista.
        int modeloFila = tablaClientes.convertRowIndexToModel(fila);
        int id = (int) tablaClientes.getModel().getValueAt(modeloFila, 0);

        // Confirmación un poco más detallada
        String nombre = (String) tablaClientes.getModel().getValueAt(modeloFila, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar al cliente: " + nombre + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteService.eliminarCliente(id);
                cargarTablaClientes();
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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

        txtBuscarCliente.setColumns(15);
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
