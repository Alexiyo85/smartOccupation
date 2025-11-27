package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.gui.dialog.ViviendaDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter; // 👈 NUEVO: Importación para la ordenación y filtro
import javax.swing.RowFilter; // 👈 NUEVO: Importación para definir el filtro (regex)
import java.awt.*;
import java.util.List;

/**
 * Panel de la Interfaz Gráfica (GUI) para la gestión y visualización de
 * Viviendas. 🏘️ Permite listar, crear, editar y eliminar viviendas, además de
 * aplicar un filtro de búsqueda en tiempo real.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ViviendaPanel extends javax.swing.JPanel {

    // Referencia al servicio de lógica de negocio para Viviendas.
    private final ViviendaService viviendaService;
    // Modelo de datos de la tabla (estructura y contenido).
    private DefaultTableModel modeloTabla;
    // 1. 👈 NUEVO: Campo para gestionar el filtro/ordenación de la tabla.
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Constructor que recibe el servicio de viviendas por inyección de
     * dependencias.
     *
     * @param viviendaService Servicio para las operaciones CRUD de viviendas.
     */
    public ViviendaPanel(ViviendaService viviendaService) {
        this.viviendaService = viviendaService;
        initComponents(); // Inicializa los componentes visuales autogenerados.
        inicializarTabla(); // Configura el modelo y el sorter de la JTable.
        inicializarEventos(); // Configura los listeners de botones y búsqueda.
        cargarViviendas(); // Realiza la carga inicial de datos.
    }

    /**
     * Define la estructura de la tabla y configura el {@code TableRowSorter}.
     */
    private void inicializarTabla() {
        // Columnas de la tabla
        String[] columnas = {
            "ID", "Código Referencia", "Dirección", "Provincia", "CP",
            "Metros²", "Habitaciones", "Baños", "Precio (€)", "Estado"
        };
        // Define el modelo y lo hace no editable.
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable directamente
            }
        };
        tablaViviendas.setModel(modeloTabla);
        // Permite seleccionar solo una fila a la vez.
        tablaViviendas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 2. 👈 NUEVO: Crear y asignar el sorter al modelo de la tabla.
        // Esto activa la ordenación por columna al hacer clic en la cabecera.
        sorter = new TableRowSorter<>(modeloTabla);
        tablaViviendas.setRowSorter(sorter);
    }

    /**
     * Configura los ActionListeners para los botones y el DocumentListener para
     * la búsqueda.
     */
    private void inicializarEventos() {
        // Botón Nuevo: Abre el diálogo en modo creación (vivienda nula).
        btnNuevaVivienda.addActionListener(e -> abrirDialogo(null));

        // Botón Editar: Abre el diálogo en modo edición si hay una fila seleccionada.
        btnEditar.addActionListener(e -> {
            Vivienda seleccionada = obtenerViviendaSeleccionada();
            if (seleccionada != null) {
                abrirDialogo(seleccionada);
            }
        });

        // Botón Eliminar: Llama al método de eliminación.
        btnEliminar.addActionListener(e -> eliminarVivienda());

        // Botón Actualizar: Recarga los datos de la base de datos.
        btnActualizarLista.addActionListener(e -> cargarViviendas());

        // 3. 👈 NUEVO: BÚSQUEDA EN TIEMPO REAL
        // Añade un listener al documento de texto para reaccionar a cada pulsación.
        txtBuscarVivienda.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla(); // Se ejecuta cuando se inserta texto.
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla(); // Se ejecuta cuando se elimina texto.
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrarTabla(); // Rara vez usado para JTextField, pero se mantiene por completitud.
            }
        });
    }

    /**
     * Obtiene todas las viviendas del servicio y las carga en el modelo de la
     * tabla.
     */
    private void cargarViviendas() {
        List<Vivienda> lista = viviendaService.obtenerTodas();
        modeloTabla.setRowCount(0); // Limpiar tabla antes de añadir nuevos datos.

        // Itera sobre la lista y añade cada vivienda como una nueva fila.
        for (Vivienda v : lista) {
            modeloTabla.addRow(new Object[]{
                v.getIdVivienda(),
                v.getCodigoReferencia(),
                v.getDireccion(),
                v.getProvincia(),
                v.getCodigoPostal(),
                v.getMetrosCuadrados(),
                v.getNumeroHabitaciones(),
                v.getNumeroBanios(),
                v.getPrecio_mensual(),
                v.getEstado()
            });
        }
        // Asegurar que el filtro se mantenga si la tabla se recarga.
        filtrarTabla();
    }

    // 4. 👈 NUEVO: Implementación del filtro
    /**
     * Aplica el filtro de búsqueda a la tabla usando el texto introducido.
     */
    private void filtrarTabla() {
        String busqueda = txtBuscarVivienda.getText().trim();

        if (sorter == null) {
            // Protección si se llama antes de inicializar el sorter.
            return;
        }

        if (busqueda.isEmpty()) {
            // Si el campo está vacío, desactiva el filtro.
            sorter.setRowFilter(null);
        } else {
            try {
                // Filtro Case-Insensitive en todas las columnas
                // RowFilter.regexFilter crea un filtro usando una expresión regular.
                // "(?i)" es el flag para ignorar mayúsculas/minúsculas.
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + busqueda));
            } catch (java.util.regex.PatternSyntaxException e) {
                // Si el texto de búsqueda es una expresión regular inválida, no aplica filtro.
                sorter.setRowFilter(null);
            }
        }
    }

    /**
     * Obtiene la vivienda seleccionada en la vista, manejando la conversión de
     * fila si la tabla está ordenada o filtrada.
     *
     * @return El objeto Vivienda seleccionado o null si no hay selección o
     * error.
     */
    private Vivienda obtenerViviendaSeleccionada() {
        int vistaFila = tablaViviendas.getSelectedRow();
        if (vistaFila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una vivienda.", "Atención", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // 5. 👈 CORRECCIÓN: Usar convertRowIndexToModel para obtener el ID real.
        // Esto es crucial cuando se usa TableRowSorter.
        int modeloFila = tablaViviendas.convertRowIndexToModel(vistaFila);
        // El ID está en la columna 0 del modelo subyacente.
        int id = (int) modeloTabla.getValueAt(modeloFila, 0);
        return viviendaService.obtenerVivienda(id);
    }

    /**
     * Abre el diálogo de creación o edición de viviendas.
     *
     * @param vivienda La vivienda a editar (o null para crear).
     */
    private void abrirDialogo(Vivienda vivienda) {
        // Obtenemos la ventana padre correcta (JFrame o JDialog) para centrar el diálogo.
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

        // Actualizar la tabla después de cerrar el diálogo.
        cargarViviendas();
        // El método cargarViviendas ahora llama a filtrarTabla, manteniendo el estado de búsqueda
    }

    /**
     * Elimina la vivienda seleccionada tras solicitar confirmación.
     */
    private void eliminarVivienda() {
        Vivienda seleccionada = obtenerViviendaSeleccionada();
        if (seleccionada == null) {
            return;
        }

        // Solicitar confirmación.
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar esta vivienda?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Intenta eliminar y maneja el resultado del servicio.
                boolean exito = viviendaService.eliminarVivienda(seleccionada.getIdVivienda());
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Vivienda eliminada correctamente.");
                    cargarViviendas(); // Recarga la tabla.
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar la vivienda.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalStateException ex) {
                // Captura excepciones específicas (ej: si la vivienda tiene alquileres asociados).
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
