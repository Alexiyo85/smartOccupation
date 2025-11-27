package com.smartoccupation.gui.paneles;

import com.smartoccupation.gui.dialog.ClienteDialog;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.modelo.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // 👈 NUEVO: Importación necesaria para ordenar y filtrar
import java.awt.*;
import java.util.List;

/**
 * Panel de la Interfaz Gráfica (GUI) para la gestión y visualización de
 * Clientes. 🧑‍🤝‍🧑 Permite crear, editar, eliminar y aplicar filtros a la
 * lista de clientes.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ClientePanel extends javax.swing.JPanel {

    // Referencia al servicio de lógica de negocio para Clientes, inyectada.
    private final ClienteService clienteService;
    // 1. 👈 NUEVO: Campo para gestionar el filtro/ordenación de la tabla.
    private TableRowSorter<DefaultTableModel> sorter;

// ============================
//       CONSTRUCTOR
// ============================
    /**
     * Constructor del panel, inyectando la dependencia del servicio de
     * clientes.
     *
     * @param clienteService Servicio para la gestión de la lógica de negocio de
     * clientes.
     */
    public ClientePanel(ClienteService clienteService) {
        this.clienteService = clienteService;
        initComponents(); // Inicializa los componentes visuales autogenerados.
        cargarTablaClientes(); // Rellena la tabla con los datos de los clientes.
        configurarEventos(); // Configura los listeners para los botones y el campo de búsqueda.
    }

// ===========================================
//  CARGA LA TABLA CON LOS CLIENTES EXISTENTES (MODIFICADO)
// ===========================================
    /**
     * Obtiene todos los clientes del servicio y rellena la {@code JTable} con
     * sus datos. También configura el {@code TableRowSorter}.
     */
    private void cargarTablaClientes() {

        List<Cliente> lista = clienteService.obtenerTodos(); // Obtiene la lista de clientes.

        // Definición de las columnas de la tabla.
        String[] columnas = {
            "ID", "Nombre", "Primer Apellido", "Segundo Apellido",
            "DNI", "Teléfono", "Email", "Dirección", "Ciudad", "Provincia",
            "Código Postal"
        };

        // Inicializa la matriz de datos con el tamaño de la lista de clientes.
        Object[][] datos = new Object[lista.size()][columnas.length];

        // Rellena la matriz de datos con los atributos de cada objeto Cliente.
        for (int i = 0; i < lista.size(); i++) {
            Cliente c = lista.get(i);
            datos[i][0] = c.getIdCliente();
            datos[i][1] = c.getNombre();
            datos[i][2] = c.getPrimerApellido();
            datos[i][3] = c.getSegundoApellido();
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
            // Sobrescribe para hacer que todas las celdas de la tabla sean no editables.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes.setModel(modelo); // Asigna el modelo a la JTable.

        // 3. 👈 NUEVO: Creamos y asignamos el sorter al modelo de la tabla.
        // Esto permite la ordenación al hacer clic en las cabeceras y la funcionalidad de filtrado.
        sorter = new TableRowSorter<>(modelo);
        tablaClientes.setRowSorter(sorter);
    }

// ============================================
//       CONFIGURACIÓN DE LOS BOTONES
// ============================================
    /**
     * Configura los {@code ActionListener} para los botones y el
     * {@code DocumentListener} para el campo de búsqueda.
     */
    private void configurarEventos() {

        // BOTÓN AÑADIR: Abre el diálogo en modo creación (cliente nulo).
        btnAñadirCliente.addActionListener(e -> abrirDialogoCliente(null));

        // BOTÓN EDITAR: Abre el diálogo en modo edición.
        btnEditarCliente.addActionListener(e -> {
            int fila = tablaClientes.getSelectedRow(); // Obtiene el índice de la fila seleccionada en la VISTA.
            if (fila == -1) {
                // Muestra advertencia si no hay selección.
                JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Importante: Si se usa sorter, el ID debe obtenerse del modelo subyacente.
            // convertRowIndexToModel convierte el índice de la VISTA al índice del MODELO.
            int modeloFila = tablaClientes.convertRowIndexToModel(fila);

            // ID está en la columna 0 del modelo subyacente
            int id = (int) tablaClientes.getModel().getValueAt(modeloFila, 0);
            // Obtiene el objeto Cliente completo usando el ID.
            Cliente cliente = clienteService.obtenerCliente(id);
            // Abre el diálogo en modo edición.
            abrirDialogoCliente(cliente);
        });

        // BOTÓN ELIMINAR
        btnEliminarCliente.addActionListener(e -> eliminarCliente());

        // BÚSQUEDA EN TIEMPO REAL
        // Añade un DocumentListener para reaccionar a los cambios de texto en el campo de búsqueda.
        txtBuscarCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla(); // Se llama al método de filtro al insertar texto.
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla(); // Se llama al método de filtro al borrar texto.
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Este método es para atributos de texto, no cambios de contenido (raramente usado en JTextField).
                filtrarTabla();
            }
        });
    }

// ===========================
//       FILTRO DE BÚSQUEDA (MODIFICADO Y CORREGIDO)
// ===========================
    /**
     * Aplica un filtro de expresión regular a la tabla basado en el texto del
     * campo de búsqueda.
     */
    private void filtrarTabla() {
        String busqueda = txtBuscarCliente.getText().trim(); // Obtiene y limpia el texto de búsqueda.

        if (sorter == null) {
            // Evitar fallo si se llama antes de cargar la tabla y asignar el sorter.
            return;
        }

        if (busqueda.isEmpty()) {
            // Si el campo está vacío, quita cualquier filtro activo.
            sorter.setRowFilter(null);
        } else {
            try {
                // 4. 👈 NUEVO: Aplicar el filtro usando una expresión regular.
                // RowFilter.regexFilter() filtra las filas cuya información coincida con el patrón.
                // "(?i)" ignora mayúsculas/minúsculas.
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + busqueda));
            } catch (java.util.regex.PatternSyntaxException e) {
                // Si la expresión es inválida (ej: un solo paréntesis), simplemente no aplicamos filtro.
                sorter.setRowFilter(null);
            }
        }
    }

// ===========================
//     ABRIR DIÁLOGO CLIENTE
// ===========================
    /**
     * Abre el diálogo de creación o edición de clientes.
     *
     * @param cliente El objeto Cliente a editar (o null para crear uno nuevo).
     */
    private void abrirDialogoCliente(Cliente cliente) {
        // Obtenemos la ventana padre (sea JFrame o JDialog) para que el diálogo sea modal relativo a ella.
        Window parent = SwingUtilities.getWindowAncestor(this);

        ClienteDialog dialog;

        // El constructor de ClienteDialog ahora acepta Window, así que esto es seguro
        if (cliente == null) {
            // Modo Creación
            dialog = new ClienteDialog(parent, true, clienteService);
        } else {
            // Modo Edición
            dialog = new ClienteDialog(parent, true, clienteService, cliente);
        }

        dialog.setLocationRelativeTo(parent); // Centra el diálogo respecto a la ventana padre.
        dialog.setVisible(true); // Muestra el diálogo y espera a que se cierre.

        // Recargar la tabla al cerrar el diálogo
        cargarTablaClientes();
        filtrarTabla(); // Volver a aplicar el filtro si había alguno para mantener el contexto.
    }

// ===========================
//       ELIMINAR CLIENTE
// ===========================
    /**
     * Maneja el proceso de eliminación de un cliente seleccionado.
     */
    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow(); // Fila seleccionada en la VISTA.

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Importante: Debemos obtener el ID de la fila en el MODELO.
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
                // Llama al servicio para eliminar el cliente.
                clienteService.eliminarCliente(id);
                cargarTablaClientes(); // Recarga la tabla tras el éxito.
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
            } catch (Exception e) {
                // Muestra un mensaje de error si la eliminación falla (ej: por FK constraint).
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Método autogenerado por el diseñador de GUI para inicializar los
     * componentes.
     */
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
