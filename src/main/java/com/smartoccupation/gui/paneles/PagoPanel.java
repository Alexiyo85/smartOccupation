package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.EstadoCobroService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.gui.dialog.PagoDialog;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;
import java.time.LocalDate;

/**
 * Panel de la Interfaz Gráfica (GUI) para la gestión y visualización de Pagos.
 * 🧾 Permite listar pagos, aplicar filtros por fecha y estado de cobro del
 * alquiler asociado, y gestionar la creación/eliminación de pagos.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class PagoPanel extends javax.swing.JPanel {

    // Servicio para operaciones relacionadas con Pagos.
    private final PagoService pagoService;
    // Servicio para obtener información del Alquiler asociado al pago.
    private final AlquilerService alquilerService;
    // Servicio para obtener el nombre del Estado de Cobro (Pagado, Pendiente, etc.).
    private final EstadoCobroService estadoCobroService;

    // Formato de fecha para mostrar en la tabla.
    private final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor que recibe los servicios por inyección de dependencias.
     */
    public PagoPanel(PagoService pagoService, AlquilerService alquilerService, EstadoCobroService estadoCobroService) {
        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        this.estadoCobroService = estadoCobroService;

        initComponents(); // Inicializa los componentes autogenerados.

        // Inserto el combo de estado en el panel superior (jPanel1) sin tocar el bloque autogenerado
        setupEstadoFilter();

        configurarEventos(); // Configura los ActionListeners.
        cargarPagos(); // Carga inicial de la tabla.
    }

    /**
     * Configura el JComboBox de filtro de estado y lo añade al panel de
     * búsqueda.
     */
    private void setupEstadoFilter() {
        // Añadimos opciones: Todos / Pendiente / Pagado
        cbEstadoFilter.addItem("Todos");
        // Aseguramos que los nombres coincidan con los de la BBDD (si existen)
        cbEstadoFilter.addItem("pendiente");
        cbEstadoFilter.addItem("pagado");

        // Insertar al inicio del panel jPanel1 (autogenerado) -> asumimos jPanel1 es FlowLayout
        jPanel1.add(new JLabel("Estado:"));
        jPanel1.add(cbEstadoFilter);
    }

    /**
     * Configura los ActionListeners y MouseListeners del panel.
     */
    private void configurarEventos() {
        btnNuevoPago.addActionListener(e -> abrirDialogNuevoPago());
        btnRefrescar.addActionListener(e -> cargarPagos()); // Recarga la lista completa de pagos.
        btnEliminarPago.addActionListener(e -> eliminarPago());
        btnBuscarFechas.addActionListener(e -> buscarPorFechas()); // Al hacer clic en buscar, aplica filtros.
        cbEstadoFilter.addActionListener(e -> aplicarFiltros()); // al cambiar estado, aplicar filtros de nuevo.
        // doble click para editar (opcional)
        tablaPagos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarPagoSeleccionado(); // Llama a la lógica de edición al doble clic.
                }
            }
        });
    }

    /**
     * Obtiene todos los pagos de la base de datos y actualiza la tabla. Es el
     * punto de entrada para el refresco sin filtros de fecha preaplicados.
     */
    private void cargarPagos() {
        try {
            List<Pago> lista = pagoService.listarTodosLosPagos();
            actualizarTablaConFiltro(lista); // Pasa la lista de pagos para aplicar el filtro de estado.
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error cargando pagos: " + e.getMessage());
        }
    }

    /**
     * Combina la lógica de filtro por rango de fechas y filtro por estado.
     */
    private void aplicarFiltros() {
        // Obtiene la fecha "Desde" del componente gráfico y la convierte a LocalDate.
        LocalDate desde = dcDesde.getDate() != null
                ? dcDesde.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        // Obtiene la fecha "Hasta" del componente gráfico y la convierte a LocalDate.
        LocalDate hasta = dcHasta.getDate() != null
                ? dcHasta.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        try {
            List<Pago> lista;
            if (desde != null || hasta != null) {
                // Si al menos uno de los dos está, usamos búsqueda por rango.
                if (desde == null) {
                    desde = LocalDate.of(1900, 1, 1); // Valor mínimo por defecto.
                }
                if (hasta == null) {
                    hasta = LocalDate.of(3000, 1, 1); // Valor máximo por defecto.
                }
                lista = pagoService.buscarPagosPorFecha(desde, hasta); // Búsqueda específica en el DAO/Service.
            } else {
                // Si no hay filtro de fechas, obtiene todos los pagos.
                lista = pagoService.listarTodosLosPagos();
            }
            // Aplica el filtro de estado sobre la lista obtenida.
            actualizarTablaConFiltro(lista);
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error aplicando filtros: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar llamado por el botón "Buscar".
     */
    private void buscarPorFechas() {
        aplicarFiltros();
    }

    /**
     * Rellena la tabla con la lista de pagos, aplicando el filtro de estado de
     * cobro si es necesario, y maneja la obtención de datos de Alquiler y
     * EstadoCobro.
     *
     * @param pagos La lista de objetos Pago (posiblemente ya filtrada por
     * fecha).
     */
    private void actualizarTablaConFiltro(List<Pago> pagos) {
        // Determinar si se debe filtrar por estado.
        String estadoSeleccionado = ((String) cbEstadoFilter.getSelectedItem());
        boolean filtrar = estadoSeleccionado != null && !estadoSeleccionado.equalsIgnoreCase("Todos");

        DefaultTableModel modelo = (DefaultTableModel) tablaPagos.getModel();
        modelo.setRowCount(0); // Limpia la tabla.

        // Para evitar muchas llamadas repetidas a DB, cacheamos alquileres y estados
        Map<Integer, Alquiler> alquilerCache = new HashMap<>();
        Map<Integer, String> estadoNombreCache = new HashMap<>();

        for (Pago p : pagos) {
            // Obtener el alquiler asociado (usa caché si ya se ha consultado).
            Alquiler alq = alquilerCache.computeIfAbsent(p.getNumeroExpediente(),
                    k -> alquilerService.obtenerAlquiler(k));

            String estadoNombre = "";
            if (alq != null) {
                Integer idEstado = alq.getIdEstadoCobro();
                if (idEstado != null) {
                    // Obtener el nombre del estado (usa caché si ya se ha consultado).
                    estadoNombre = estadoNombreCache.computeIfAbsent(idEstado, id -> {
                        EstadoCobro ec = estadoCobroService.obtenerPorId(id);
                        return ec != null ? ec.getNombreEstado() : "";
                    });
                }
            }

            // Si filtro activo, comprobar coincidencia
            if (filtrar) {
                // Si el nombre del estado no coincide con el filtro, se salta esta fila.
                if (estadoNombre == null || !estadoNombre.equalsIgnoreCase(estadoSeleccionado)) {
                    continue; // no coincide -> saltar fila
                }
            }

            // Preparar datos para la fila.
            String fechaStr = p.getFechaPago() != null ? p.getFechaPago().format(FECHA_FORMATO) : "";
            Double cantDouble = p.getCantidad() != null ? p.getCantidad().doubleValue() : null;
            Object alquilerLabel = (alq != null) ? ("#" + alq.getNumeroExpediente()) : p.getNumeroExpediente();

            // Añadir la fila al modelo de la tabla.
            modelo.addRow(new Object[]{
                p.getIdPago(),
                alquilerLabel,
                fechaStr,
                cantDouble,
                estadoNombre // 👈 Columna de Estado añadida
            });
        }
    }

    /**
     * Abre el diálogo para crear un nuevo pago.
     */
    private void abrirDialogNuevoPago() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        // 🚨 CAMBIO AQUÍ: Se pasa el estadoCobroService al constructor de PagoDialog
        // Se le pasa PagoService, AlquilerService y EstadoCobroService para su lógica interna.
        PagoDialog dialog = new PagoDialog(parent, true, pagoService, alquilerService, estadoCobroService);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        // Si el diálogo indica que la operación fue exitosa, se recarga la tabla.
        if (dialog.isGuardado()) {
            cargarPagos();
        }
    }

    /**
     * Elimina el pago seleccionado en la tabla, previa confirmación.
     */
    private void eliminarPago() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            FormUtils.mostrarAdvertencia(this, "Debe seleccionar un pago para eliminar.");
            return;
        }

        // Obtiene el ID del pago de la primera columna.
        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el pago seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return; // Si no confirma, sale.
        }
        try {
            pagoService.eliminarPago(idPago); // Llama al servicio para eliminar.
            cargarPagos(); // Recarga la tabla.
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error eliminando pago: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de doble clic para una potencial edición (actualmente no
     * implementada).
     */
    private void editarPagoSeleccionado() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            return;
        }
        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        // No tienes dialogo de edición de pago separado; si quieres crear uno reutilizamos PagoDialog
        // pero PagoDialog actualmente solo crea pagos. Implementar edición si lo deseas.
        FormUtils.mostrarInfo(this, "Doble click detectado sobre pago ID " + idPago + ". (Edición no implementada)");
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
        dcDesde = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        dcHasta = new com.toedter.calendar.JDateChooser();
        cbEstadoFilter = new javax.swing.JComboBox<>();
        btnBuscarFechas = new javax.swing.JButton();
        panelBotones = new javax.swing.JPanel();
        btnNuevoPago = new javax.swing.JButton();
        btnEliminarPago = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaPagos = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Desde:");
        jPanel1.add(jLabel1);

        dcDesde.setDateFormatString("dd/MM/yyyy");
        jPanel1.add(dcDesde);

        jLabel2.setText("Hasta:");
        jPanel1.add(jLabel2);

        dcHasta.setDateFormatString("dd/MM/yyyy");
        jPanel1.add(dcHasta);

        jPanel1.add(cbEstadoFilter);

        btnBuscarFechas.setText("Buscar");
        jPanel1.add(btnBuscarFechas);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        btnNuevoPago.setText("Nuevo Pago");
        panelBotones.add(btnNuevoPago);

        btnEliminarPago.setText("Eliminar Pago");
        panelBotones.add(btnEliminarPago);

        btnRefrescar.setText("Refrescar");
        panelBotones.add(btnRefrescar);

        add(panelBotones, java.awt.BorderLayout.PAGE_END);

        tablaPagos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Pago", "Nº Expediente", "Fecha Pago", "Cantidad", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaPagos);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarFechas;
    private javax.swing.JButton btnEliminarPago;
    private javax.swing.JButton btnNuevoPago;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JComboBox<String> cbEstadoFilter;
    private com.toedter.calendar.JDateChooser dcDesde;
    private com.toedter.calendar.JDateChooser dcHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JTable tablaPagos;
    // End of variables declaration//GEN-END:variables
}
