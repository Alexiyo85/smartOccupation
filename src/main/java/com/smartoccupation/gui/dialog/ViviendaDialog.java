package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*; // Necesario para Window y Dialog.ModalityType

public class ViviendaDialog extends BaseDialog {

    private final ViviendaService viviendaService;
    private Vivienda viviendaActual;

    // ===============================================================
    // CONSTRUCTOR PRINCIPAL
    // ===============================================================
    public ViviendaDialog(Window parent, boolean modal, ViviendaService viviendaService) {
        super(parent, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
        this.viviendaService = viviendaService;

        initComponents(); // init limpio sin botones
    }

    // ===============================================================
    // CONSTRUCTOR PARA EDICIÓN
    // ===============================================================
    public ViviendaDialog(Window parent, boolean modal, ViviendaService viviendaService, Vivienda vivienda) {
        this(parent, modal, viviendaService);
        this.viviendaActual = vivienda;
        cargarVivienda(vivienda);
        setTitle("Editar Vivienda");
    }

    public void cargarVivienda(Vivienda vivienda) {
        this.viviendaActual = vivienda;
        if (vivienda != null) {
            txtCodigoReferencia.setText(vivienda.getCodigo_referencia());
            txtDireccion.setText(vivienda.getDireccion());
            txtCiudad.setText(vivienda.getCiudad());
            txtProvincia.setText(vivienda.getProvincia());
            txtCodigoPostal.setText(vivienda.getCodigo_postal());

            // Manejo seguro de números (si son 0 se ven como 0, si quieres vacío usa lógica condicional)
            txtMetrosCuadrados.setText(String.valueOf(vivienda.getMetros_cuadrados()));
            txtNumeroHabitaciones.setText(String.valueOf(vivienda.getNumero_habitaciones()));
            txtNumeroBanios.setText(String.valueOf(vivienda.getNumero_banios()));
            txtPrecioMensual.setText(String.valueOf(vivienda.getPrecio_mensual()));

            cmbEstado.setSelectedItem(vivienda.getEstado());
        }
    }

    @Override
    protected boolean validarCampos() {
        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarAdvertencia("La dirección es obligatoria.");
            return false;
        }

        try {
            // Validamos formatos usando FormUtils
            FormUtils.parseBigDecimal(txtPrecioMensual.getText(), "precio mensual");
            FormUtils.parseInt(txtMetrosCuadrados.getText(), "metros cuadrados");
            FormUtils.parseInt(txtNumeroHabitaciones.getText(), "número de habitaciones");
            FormUtils.parseInt(txtNumeroBanios.getText(), "número de baños");
            return true;
        } catch (IllegalArgumentException ex) {
            mostrarAdvertencia(ex.getMessage()); // Usamos método de BaseDialog
            return false;
        }
    }

    @Override
    protected void guardarEntidad() throws Exception {
        if (viviendaActual == null) {
            viviendaActual = new Vivienda();
        }

        viviendaActual.setCodigo_referencia(txtCodigoReferencia.getText().trim());
        viviendaActual.setDireccion(txtDireccion.getText().trim());
        viviendaActual.setCiudad(txtCiudad.getText().trim());
        viviendaActual.setProvincia(txtProvincia.getText().trim());
        viviendaActual.setCodigo_postal(txtCodigoPostal.getText().trim());

        viviendaActual.setMetros_cuadrados(FormUtils.parseInt(txtMetrosCuadrados.getText(), "metros cuadrados"));
        viviendaActual.setNumero_habitaciones(FormUtils.parseInt(txtNumeroHabitaciones.getText(), "número de habitaciones"));
        viviendaActual.setNumero_banios(FormUtils.parseInt(txtNumeroBanios.getText(), "número de baños"));
        viviendaActual.setPrecio_mensual(FormUtils.parseBigDecimal(txtPrecioMensual.getText(), "precio mensual"));
        viviendaActual.setEstado((String) cmbEstado.getSelectedItem());

        // Asumimos lógica estándar: ID <= 0 es crear, ID > 0 es actualizar
        // Nota: Si ViviendaService no tiene actualizarVivienda, usa solo crearVivienda,
        // pero idealmente deberías tener ambos.
        if (viviendaActual.getId_vivienda() <= 0) {
            boolean exito = viviendaService.crearVivienda(viviendaActual);
            if (!exito) {
                throw new Exception("No se pudo crear la vivienda.");
            }
        } else {
            // Asumiendo que existe actualizarVivienda. Si no existe, descomenta la línea de crear y comenta esta.
            // viviendaService.crearVivienda(viviendaActual); 
            viviendaService.actualizarVivienda(viviendaActual);
        }
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
