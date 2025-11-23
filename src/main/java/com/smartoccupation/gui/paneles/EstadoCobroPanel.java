package com.smartoccupation.gui.paneles;

import com.smartoccupation.modelo.Pago;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.EstadoCobroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class EstadoCobroPanel extends JPanel {

    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private final EstadoCobroService estadoService;

    public EstadoCobroPanel(PagoService pagoService, AlquilerService alquilerService, EstadoCobroService estadoService) {
        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        this.estadoService = estadoService;

        initComponents();
        cargarComboEstados();
        iniciarEventos();
        cargarTabla(); // mostrar por defecto
    }

    private void cargarComboEstados() {
        cbEstado.removeAllItems();
        List<EstadoCobro> estados = estadoService.obtenerTodos();
        for (EstadoCobro e : estados) {
            cbEstado.addItem(e.getNombre());
        }
    }

    private void iniciarEventos() {
        btnRefrescar.addActionListener(e -> cargarTabla());
        cbEstado.addActionListener(e -> cargarTabla());
    }

    private void cargarTabla() {
        if (cbEstado.getSelectedItem() == null) return;
        String estadoSeleccionado = cbEstado.getSelectedItem().toString();

        // Listar todos los pagos y filtrar por estado de su alquiler
        List<Pago> pagosFiltrados = pagoService.listarTodosLosPagos().stream()
                .filter(p -> {
                    Alquiler a = alquilerService.obtenerAlquiler(p.getNumero_expediente());
                    if (a == null) return false;
                    EstadoCobro est = estadoService.obtenerEstadoCobroPorId(a.getId_estado_cobro());
                    return est != null && est.getNombre().equalsIgnoreCase(estadoSeleccionado);
                })
                .collect(Collectors.toList());

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID Pago", "Alquiler", "Fecha Pago", "Cantidad", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Pago p : pagosFiltrados) {
            Alquiler a = alquilerService.obtenerAlquiler(p.getNumero_expediente());
            EstadoCobro est = estadoService.obtenerEstadoCobroPorId(a.getId_estado_cobro());
            model.addRow(new Object[]{
                    p.getId_pago(),
                    a != null ? a.getNumero_expediente() : p.getNumero_expediente(),
                    p.getFecha_pago(),
                    p.getCantidad(),
                    est != null ? est.getNombre() : "Desconocido"
            });
        }

        tablaCobros.setModel(model);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelBusqueda = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbEstado = new javax.swing.JComboBox<>();
        btnRefrescar = new javax.swing.JButton();
        panelFiltros = new javax.swing.JScrollPane();
        tablaCobros = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cbFiltros = new javax.swing.JComboBox<>();
        btnBuscar = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Estado del Cobro:");
        PanelBusqueda.add(jLabel1);

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "Pagado", "Retrasado" }));
        PanelBusqueda.add(cbEstado);

        btnRefrescar.setText("Refrescar");
        PanelBusqueda.add(btnRefrescar);

        add(PanelBusqueda, java.awt.BorderLayout.PAGE_START);

        tablaCobros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Cliente", "Fecha", "Monto", "Estado"
            }
        ));
        panelFiltros.setViewportView(tablaCobros);

        add(panelFiltros, java.awt.BorderLayout.CENTER);

        jLabel2.setText("Filtros");
        jPanel1.add(jLabel2);

        jPanel1.add(cbFiltros);

        btnBuscar.setText("Buscar");
        jPanel1.add(btnBuscar);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelBusqueda;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<String> cbFiltros;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane panelFiltros;
    private javax.swing.JTable tablaCobros;
    // End of variables declaration//GEN-END:variables
}
