package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.gui.dialog.ViviendaDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // 👈 NUEVO: Importación para el filtro
import javax.swing.RowFilter; // 👈 NUEVO: Importación para el filtro
import java.awt.*;
import java.util.List;

public class ViviendaPanel extends javax.swing.JPanel {

    private final ViviendaService viviendaService;
    private DefaultTableModel modeloTabla;
    // 1. 👈 NUEVO: Campo para gestionar el filtro/ordenación de la tabla
    private TableRowSorter<DefaultTableModel> sorter;

    public ViviendaPanel(ViviendaService viviendaService) {
        this.viviendaService = viviendaService;
        initComponents();
        inicializarTabla();
        inicializarEventos();
        cargarViviendas();
    }

    private void inicializarTabla() {
        // Columnas de la tabla
        String[] columnas = {
            "ID", "Código Referencia", "Dirección", "Provincia", "CP",
            "Metros²", "Habitaciones", "Baños", "Precio (€)", "Estado"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable directamente
            }
        };
        tablaViviendas.setModel(modeloTabla);
        tablaViviendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 2. 👈 NUEVO: Crear y asignar el sorter al modelo de la tabla
        sorter = new TableRowSorter<>(modeloTabla);
        tablaViviendas.setRowSorter(sorter);
    }

    private void inicializarEventos() {
        btnNuevaVivienda.addActionListener(e -> abrirDialogo(null));
        btnEditar.addActionListener(e -> {
            Vivienda seleccionada = obtenerViviendaSeleccionada();
            if (seleccionada != null) {
                abrirDialogo(seleccionada);
            }
        });
        btnEliminar.addActionListener(e -> eliminarVivienda());
        btnActualizarLista.addActionListener(e -> cargarViviendas());

        // 3. 👈 NUEVO: BÚSQUEDA EN TIEMPO REAL
        txtBuscarVivienda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

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

    private void cargarViviendas() {
        List<Vivienda> lista = viviendaService.obtenerTodas();
        modeloTabla.setRowCount(0); // Limpiar tabla
        for (Vivienda v : lista) {
            modeloTabla.addRow(new Object[]{
                v.getId_vivienda(),
                v.getCodigo_referencia(),
                v.getDireccion(),
                v.getProvincia(),
                v.getCodigo_postal(),
                v.getMetros_cuadrados(),
                v.getNumero_habitaciones(),
                v.getNumero_banios(),
                v.getPrecio_mensual(),
                v.getEstado()
            });
        }
        // Asegurar que el filtro se mantenga si la tabla se recarga
        filtrarTabla();
    }

    // 4. 👈 NUEVO: Implementación del filtro
    private void filtrarTabla() {
        String busqueda = txtBuscarVivienda.getText().trim();

        if (sorter == null) {
            return;
        }

        if (busqueda.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                // Filtro Case-Insensitive en todas las columnas
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + busqueda));
            } catch (java.util.regex.PatternSyntaxException e) {
                sorter.setRowFilter(null);
            }
        }
    }

    private Vivienda obtenerViviendaSeleccionada() {
        int vistaFila = tablaViviendas.getSelectedRow();
        if (vistaFila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una vivienda.", "Atención", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // 5. 👈 CORRECCIÓN: Usar convertRowIndexToModel para obtener el ID real
        int modeloFila = tablaViviendas.convertRowIndexToModel(vistaFila);
        int id = (int) modeloTabla.getValueAt(modeloFila, 0);
        return viviendaService.obtenerVivienda(id);
    }

    private void abrirDialogo(Vivienda vivienda) {
        // Obtenemos la ventana padre correcta, puede ser JFrame o JDialog
        Window parent = SwingUtilities.getWindowAncestor(this);
        ViviendaDialog dialog;

        if (vivienda == null) {
            // Crear nueva vivienda
            dialog = new ViviendaDialog(parent, true, viviendaService);
        } else {
            // Editar vivienda existente
            dialog = new ViviendaDialog(parent, true, viviendaService, vivienda);
        }

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);

        // Actualizar la tabla después de cerrar el diálogo
        cargarViviendas();
        // El método cargarViviendas ahora llama a filtrarTabla, manteniendo el estado de búsqueda
    }

    private void eliminarVivienda() {
        Vivienda seleccionada = obtenerViviendaSeleccionada();
        if (seleccionada == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar esta vivienda?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean exito = viviendaService.eliminarVivienda(seleccionada.getId_vivienda());
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Vivienda eliminada correctamente.");
                    cargarViviendas();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar la vivienda.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrollTabla = new javax.swing.JScrollPane();
        tablaViviendas = new javax.swing.JTable();
        panelTitulo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarVivienda = new javax.swing.JTextField();
        panelBotones = new javax.swing.JPanel();
        btnNuevaVivienda = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnActualizarLista = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        tablaViviendas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Código Referencia", "Dirección", "Provincia", "CP", "Metros²", "Habitaciones", "Baños", "Precio (€)", "Estado"
            }
        ));
        ScrollTabla.setViewportView(tablaViviendas);

        add(ScrollTabla, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Buscar:");
        panelTitulo.add(jLabel1);

        txtBuscarVivienda.setColumns(15);
        panelTitulo.add(txtBuscarVivienda);

        add(panelTitulo, java.awt.BorderLayout.PAGE_START);

        btnNuevaVivienda.setText("Nueva Vivienda");
        panelBotones.add(btnNuevaVivienda);

        btnEditar.setText("Editar");
        panelBotones.add(btnEditar);

        btnEliminar.setText("Eliminar");
        panelBotones.add(btnEliminar);

        btnActualizarLista.setText("Actualizar Lista");
        panelBotones.add(btnActualizarLista);

        add(panelBotones, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane ScrollTabla;
    private javax.swing.JButton btnActualizarLista;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnNuevaVivienda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JTable tablaViviendas;
    private javax.swing.JTextField txtBuscarVivienda;
    // End of variables declaration//GEN-END:variables
}
