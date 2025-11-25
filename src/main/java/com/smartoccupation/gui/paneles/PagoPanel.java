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

public class PagoPanel extends javax.swing.JPanel {

    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private final EstadoCobroService estadoCobroService;

    private final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PagoPanel(PagoService pagoService, AlquilerService alquilerService, EstadoCobroService estadoCobroService) {
        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        this.estadoCobroService = estadoCobroService;

        initComponents();

        // Inserto el combo de estado en el panel superior (jPanel1) sin tocar el bloque autogenerado
        setupEstadoFilter();

        configurarEventos();
        cargarPagos();
    }

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

    private void configurarEventos() {
        btnNuevoPago.addActionListener(e -> abrirDialogNuevoPago());
        btnRefrescar.addActionListener(e -> cargarPagos());
        btnEliminarPago.addActionListener(e -> eliminarPago());
        btnBuscarFechas.addActionListener(e -> buscarPorFechas());
        cbEstadoFilter.addActionListener(e -> aplicarFiltros()); // al cambiar estado, aplicar
        // doble click para editar (opcional)
        tablaPagos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarPagoSeleccionado();
                }
            }
        });
    }

    private void cargarPagos() {
        try {
            List<Pago> lista = pagoService.listarTodosLosPagos();
            actualizarTablaConFiltro(lista);
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error cargando pagos: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        // Aplica filtros de fecha y estado y recarga la tabla
        LocalDate desde = dcDesde.getDate() != null
                ? dcDesde.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        LocalDate hasta = dcHasta.getDate() != null
                ? dcHasta.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        try {
            List<Pago> lista;
            if (desde != null || hasta != null) {
                // Si al menos uno de los dos está, usamos búsqueda por rango (PagoDAO acepta ambos)
                if (desde == null) desde = LocalDate.of(1900,1,1);
                if (hasta == null) hasta = LocalDate.of(3000,1,1);
                lista = pagoService.buscarPagosPorFecha(desde, hasta);
            } else {
                lista = pagoService.listarTodosLosPagos();
            }
            actualizarTablaConFiltro(lista);
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error aplicando filtros: " + e.getMessage());
        }
    }

    private void buscarPorFechas() {
        aplicarFiltros();
    }

    private void actualizarTablaConFiltro(List<Pago> pagos) {
        // Si hay filtro de estado distinto de "Todos", aplicarlo:
        String estadoSeleccionado = ((String) cbEstadoFilter.getSelectedItem());
        boolean filtrar = estadoSeleccionado != null && !estadoSeleccionado.equalsIgnoreCase("Todos");

        DefaultTableModel modelo = (DefaultTableModel) tablaPagos.getModel();
        modelo.setRowCount(0);

        // Para evitar muchas llamadas repetidas a DB, cacheamos alquileres y estados
        Map<Integer, Alquiler> alquilerCache = new HashMap<>();
        Map<Integer, String> estadoNombreCache = new HashMap<>();

        for (Pago p : pagos) {
            Alquiler alq = alquilerCache.computeIfAbsent(p.getNumero_expediente(),
                    k -> alquilerService.obtenerAlquiler(k));

            String estadoNombre = "";
            if (alq != null) {
                Integer idEstado = alq.getId_estado_cobro();
                if (idEstado != null) {
                    // cached lookup of nombre de estado
                    estadoNombre = estadoNombreCache.computeIfAbsent(idEstado, id -> {
                        EstadoCobro ec = estadoCobroService.obtenerPorId(id);
                        return ec != null ? ec.getNombre_estado() : "";
                    });
                }
            }

            // Si filtro activo, comprobar coincidencia
            if (filtrar) {
                if (estadoNombre == null || !estadoNombre.equalsIgnoreCase(estadoSeleccionado)) {
                    continue; // no coincide -> saltar fila
                }
            }

            String fechaStr = p.getFecha_pago() != null ? p.getFecha_pago().format(FECHA_FORMATO) : "";
            Double cantDouble = p.getCantidad() != null ? p.getCantidad().doubleValue() : null;
            Object alquilerLabel = (alq != null) ? ("#" + alq.getNumero_expediente()) : p.getNumero_expediente();

            modelo.addRow(new Object[]{
                p.getId_pago(),
                alquilerLabel,
                fechaStr,
                cantDouble,
                estadoNombre // 👈 Columna de Estado añadida
            });
        }
    }

    private void abrirDialogNuevoPago() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        // 🚨 CAMBIO AQUÍ: Se pasa el estadoCobroService al constructor de PagoDialog
        PagoDialog dialog = new PagoDialog(parent, true, pagoService, alquilerService, estadoCobroService); 
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarPagos();
        }
    }

    private void eliminarPago() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            FormUtils.mostrarAdvertencia(this, "Debe seleccionar un pago para eliminar.");
            return;
        }

        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el pago seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            pagoService.eliminarPago(idPago);
            cargarPagos();
        } catch (Exception e) {
            FormUtils.mostrarError(this, "Error eliminando pago: " + e.getMessage());
        }
    }

    private void editarPagoSeleccionado() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) return;
        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        // No tienes dialogo de edición de pago separado; si quieres crear uno reutilizamos PagoDialog
        // pero PagoDialog actualmente solo crea pagos. Implementar edición si lo deseas.
        FormUtils.mostrarInfo(this, "Doble click detectado sobre pago ID " + idPago + ". (Edición no implementada)");
    }
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
