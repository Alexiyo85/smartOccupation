package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.gui.dialog.PagoDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PagoPanel extends JPanel {

    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private static final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PagoPanel(PagoService pagoService, AlquilerService alquilerService) {
        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        initComponents();
        configurarEventos();
        cargarPagos();
    }

    // ============================================================
    // CONFIGURACIÓN DE EVENTOS
    // ============================================================
    private void configurarEventos() {
        btnNuevoPago.addActionListener(e -> abrirDialogNuevoPago());
        btnRefrescar.addActionListener(e -> cargarPagos());
        btnEliminarPago.addActionListener(e -> eliminarPago());
        btnBuscarFechas.addActionListener(e -> buscarPorFechas());
    }

    // ============================================================
    // CARGAR DATOS EN TABLA
    // ============================================================
    private void cargarPagos() {
        try {
            List<Pago> lista = pagoService.listarTodosLosPagos();
            actualizarTabla(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando pagos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTabla(List<Pago> pagos) {
        DefaultTableModel modelo = (DefaultTableModel) tablaPagos.getModel();
        modelo.setRowCount(0);

        for (Pago p : pagos) {
            String fechaStr = p.getFecha_pago() != null ? p.getFecha_pago().format(FECHA_FORMATO) : "";
            Double cantDouble = p.getCantidad() != null ? p.getCantidad().doubleValue() : null;

            modelo.addRow(new Object[]{
                p.getId_pago(),
                p.getNumero_expediente(),
                fechaStr,
                cantDouble
            });
        }
    }

    // ============================================================
    // NUEVO PAGO
    // ============================================================
    private void abrirDialogNuevoPago() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        PagoDialog dialog = new PagoDialog(parent, true, pagoService, alquilerService);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.isGuardado()) {
            cargarPagos();
        }
    }

    // ============================================================
    // ELIMINAR PAGO
    // ============================================================
    private void eliminarPago() {
        int fila = tablaPagos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un pago para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idPago = (int) tablaPagos.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el pago seleccionado?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            pagoService.eliminarPago(idPago);
            cargarPagos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error eliminando pago: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // BÚSQUEDA POR FECHAS
    // ============================================================
    private void buscarPorFechas() {
        LocalDate desde = dcDesde.getDate() != null
                ? dcDesde.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;
        LocalDate hasta = dcHasta.getDate() != null
                ? dcHasta.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        if (desde == null && hasta == null) {
            JOptionPane.showMessageDialog(this,
                    "Debe seleccionar al menos una fecha (Desde o Hasta).",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Pago> lista = pagoService.buscarPagosPorFecha(desde, hasta);
            actualizarTabla(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error buscando pagos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dcDesde = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        dcHasta = new com.toedter.calendar.JDateChooser();
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
        jPanel1.add(dcDesde);

        jLabel2.setText("Hasta:");
        jPanel1.add(jLabel2);
        jPanel1.add(dcHasta);

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
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Pago", "Nº Expediente", "Fecha Pago", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class
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
