package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*; // Importar para Window
import java.util.List;

public class PagoDialog extends BaseDialog {

    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private Pago pagoActual;

    // Ajustar constructor a Window y ModalityType
    public PagoDialog(Window parent, boolean modal, PagoService pagoService, AlquilerService alquilerService) {
        super(parent, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        setTitle("Registrar Pago");

        initComponents();
        cargarAlquileres();
    }

    private void cargarAlquileres() {
        try {
            List<Alquiler> lista = alquilerService.obtenerTodos();
            DefaultComboBoxModel<Alquiler> modelo = new DefaultComboBoxModel<>();
            lista.forEach(modelo::addElement);
            cbAlquiler.setModel(modelo);
        } catch (Exception ex) {
            mostrarError("Error cargando alquileres.");
        }
    }

    @Override
    protected boolean validarCampos() {
        if (cbAlquiler.getSelectedItem() == null) {
            mostrarAdvertencia("Debe seleccionar un alquiler.");
            return false;
        }
        try {
            FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago");
            FormUtils.parseBigDecimal(txtCantidad.getText(), "cantidad");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected void guardarEntidad() throws Exception {
        Pago pago = new Pago();
        Alquiler alquiler = (Alquiler) cbAlquiler.getSelectedItem();
        pago.setNumero_expediente(alquiler.getNumero_expediente());
        pago.setFecha_pago(FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago"));
        pago.setCantidad(FormUtils.parseBigDecimal(txtCantidad.getText(), "cantidad"));

        pagoService.registrarPago(pago);
        pagoActual = pago;
    }

    public Pago getPagoActual() {
        return pagoActual;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelBotones = new javax.swing.JPanel();
        txtGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelCampos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbAlquiler = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        dcFechaPago = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        txtCantidad = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtGuardar.setText("Guardar");
        panelBotones.add(txtGuardar);

        btnCancelar.setText("Cancelar");
        panelBotones.add(btnCancelar);

        getContentPane().add(panelBotones, java.awt.BorderLayout.PAGE_END);

        panelCampos.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Alquiler:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(cbAlquiler, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Cantidad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(dcFechaPago, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Fecha Pago:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(txtCantidad, gridBagConstraints);

        getContentPane().add(panelCampos, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JComboBox<Alquiler> cbAlquiler;
    private com.toedter.calendar.JDateChooser dcFechaPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JTextField txtCantidad;
    private javax.swing.JButton txtGuardar;
    // End of variables declaration//GEN-END:variables
}
