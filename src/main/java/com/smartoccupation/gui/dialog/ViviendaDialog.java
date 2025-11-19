package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ViviendaDialog extends JDialog {

    private final ViviendaService viviendaService;
    private Vivienda viviendaActual;

    public ViviendaDialog(Frame parent, boolean modal, ViviendaService service) {
        super(parent, modal);
        this.viviendaService = service;
        initComponents();
        inicializarEventos();
    }

    public ViviendaDialog(Frame parent, boolean modal, ViviendaService service, Vivienda vivienda) {
        this(parent, modal, service);
        this.viviendaActual = vivienda;
        cargarDatosVivienda(vivienda);
    }

    private void inicializarEventos() {
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar.addActionListener(e -> {
            if (validarCampos()) {
                try {
                    if (viviendaActual == null) {
                        // Crear nueva vivienda
                        Vivienda vNueva = obtenerViviendaDesdeCampos();
                        viviendaService.crearVivienda(vNueva);
                        JOptionPane.showMessageDialog(this, "Vivienda creada correctamente.");
                    } else {
                        // Editar existente
                        actualizarViviendaDesdeCampos(viviendaActual);
                        viviendaService.actualizarVivienda(viviendaActual);
                        JOptionPane.showMessageDialog(this, "Vivienda actualizada correctamente.");
                    }
                    dispose();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void cargarDatosVivienda(Vivienda v) {
        txtCodigoReferencia.setText(v.getCodigo_referencia());
        txtDireccion.setText(v.getDireccion());
        txtCiudad.setText(v.getCiudad());
        txtProvincia.setText(v.getProvincia());
        txtCodigoPostal.setText(v.getCodigo_postal());
        txtMetrosCuadrados.setText(String.valueOf(v.getMetros_cuadrados()));
        txtNumeroHabitaciones.setText(String.valueOf(v.getNumero_habitaciones()));
        txtNumeroBanios.setText(String.valueOf(v.getNumero_banios()));
        txtPrecioMensual.setText(v.getPrecio_mensual().toPlainString());
        cmbEstado.setSelectedItem(v.getEstado());
    }

    private boolean validarCampos() {
        // Verificar campos vacíos
        if (txtCodigoReferencia.getText().trim().isEmpty()
                || txtDireccion.getText().trim().isEmpty()
                || txtCiudad.getText().trim().isEmpty()
                || txtProvincia.getText().trim().isEmpty()
                || txtCodigoPostal.getText().trim().isEmpty()
                || txtMetrosCuadrados.getText().trim().isEmpty()
                || txtNumeroHabitaciones.getText().trim().isEmpty()
                || txtNumeroBanios.getText().trim().isEmpty()
                || txtPrecioMensual.getText().trim().isEmpty()
                || cmbEstado.getSelectedIndex() == -1) {

            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Atención", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            int metros = Integer.parseInt(txtMetrosCuadrados.getText());
            int habitaciones = Integer.parseInt(txtNumeroHabitaciones.getText());
            int banios = Integer.parseInt(txtNumeroBanios.getText());
            BigDecimal precio = new BigDecimal(txtPrecioMensual.getText());

            if (metros < 0 || habitaciones < 0 || banios < 0 || precio.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Los valores numéricos no pueden ser negativos.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (!txtCodigoPostal.getText().matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El código postal solo puede contener números.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String estado = cmbEstado.getSelectedItem().toString().toLowerCase();
            if (!estado.equals("disponible") && !estado.equals("reservado") && !estado.equals("ocupado")) {
                JOptionPane.showMessageDialog(this, "Estado inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Campos numéricos inválidos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private Vivienda obtenerViviendaDesdeCampos() {
        Vivienda v = new Vivienda();
        v.setCodigo_referencia(txtCodigoReferencia.getText());
        v.setDireccion(txtDireccion.getText());
        v.setCiudad(txtCiudad.getText());
        v.setProvincia(txtProvincia.getText());
        v.setCodigo_postal(txtCodigoPostal.getText());
        v.setMetros_cuadrados(Integer.parseInt(txtMetrosCuadrados.getText()));
        v.setNumero_habitaciones(Integer.parseInt(txtNumeroHabitaciones.getText()));
        v.setNumero_banios(Integer.parseInt(txtNumeroBanios.getText()));
        v.setPrecio_mensual(new BigDecimal(txtPrecioMensual.getText()));
        v.setEstado(cmbEstado.getSelectedItem().toString());
        return v;
    }

    private void actualizarViviendaDesdeCampos(Vivienda v) {
        v.setCodigo_referencia(txtCodigoReferencia.getText());
        v.setDireccion(txtDireccion.getText());
        v.setCiudad(txtCiudad.getText());
        v.setProvincia(txtProvincia.getText());
        v.setCodigo_postal(txtCodigoPostal.getText());
        v.setMetros_cuadrados(Integer.parseInt(txtMetrosCuadrados.getText()));
        v.setNumero_habitaciones(Integer.parseInt(txtNumeroHabitaciones.getText()));
        v.setNumero_banios(Integer.parseInt(txtNumeroBanios.getText()));
        v.setPrecio_mensual(new BigDecimal(txtPrecioMensual.getText()));
        v.setEstado(cmbEstado.getSelectedItem().toString());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelCampos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCodigoReferencia = new javax.swing.JTextField();
        txtDireccion = new javax.swing.JTextField();
        txtCiudad = new javax.swing.JTextField();
        txtProvincia = new javax.swing.JTextField();
        txtCodigoPostal = new javax.swing.JTextField();
        txtMetrosCuadrados = new javax.swing.JTextField();
        txtNumeroHabitaciones = new javax.swing.JTextField();
        txtNumeroBanios = new javax.swing.JTextField();
        txtPrecioMensual = new javax.swing.JTextField();
        cmbEstado = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnGuardar.setText("Guardar");
        panelBotones.add(btnGuardar);

        btnCancelar.setText("Cancelar");
        panelBotones.add(btnCancelar);

        getContentPane().add(panelBotones, java.awt.BorderLayout.PAGE_END);

        panelCampos.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Codigo de Referencia:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Dirección");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Ciudad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Provincia:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Código Postal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Metros²:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Número de habitaciones:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Número de baños:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Precio Mensual:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Estado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel10, gridBagConstraints);

        txtCodigoReferencia.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panelCampos.add(txtCodigoReferencia, gridBagConstraints);

        txtDireccion.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panelCampos.add(txtDireccion, gridBagConstraints);

        txtCiudad.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        panelCampos.add(txtCiudad, gridBagConstraints);

        txtProvincia.setColumns(15);
        txtProvincia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtProvinciaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        panelCampos.add(txtProvincia, gridBagConstraints);

        txtCodigoPostal.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        panelCampos.add(txtCodigoPostal, gridBagConstraints);

        txtMetrosCuadrados.setColumns(15);
        txtMetrosCuadrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMetrosCuadradosActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        panelCampos.add(txtMetrosCuadrados, gridBagConstraints);

        txtNumeroHabitaciones.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        panelCampos.add(txtNumeroHabitaciones, gridBagConstraints);

        txtNumeroBanios.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        panelCampos.add(txtNumeroBanios, gridBagConstraints);

        txtPrecioMensual.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        panelCampos.add(txtPrecioMensual, gridBagConstraints);

        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "disponible", "reservado", "ocupado" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        panelCampos.add(cmbEstado, gridBagConstraints);

        getContentPane().add(panelCampos, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtProvinciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtProvinciaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProvinciaActionPerformed

    private void txtMetrosCuadradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMetrosCuadradosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMetrosCuadradosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JTextField txtCiudad;
    private javax.swing.JTextField txtCodigoPostal;
    private javax.swing.JTextField txtCodigoReferencia;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtMetrosCuadrados;
    private javax.swing.JTextField txtNumeroBanios;
    private javax.swing.JTextField txtNumeroHabitaciones;
    private javax.swing.JTextField txtPrecioMensual;
    private javax.swing.JTextField txtProvincia;
    // End of variables declaration//GEN-END:variables
}
