package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*; // Necesario para Window
import java.util.List;

public class AlquilerDialog extends BaseDialog {

    // Quitamos los 'new Service()' y los recibimos en el constructor
    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;

    private Alquiler alquilerActual;

    // ===============================================================
    // CONSTRUCTOR: Acepta Window y Servicios
    // ===============================================================
    public AlquilerDialog(Window parent, boolean modal,
            AlquilerService alquilerService,
            ClienteService clienteService,
            ViviendaService viviendaService) {

        // Llamada correcta al padre BaseDialog
        super(parent, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);

        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;

        setTitle("Gestión de Alquiler");
        initComponents(); // initComponents LIMPIO (sin botones)
        cargarClientes();
        cargarViviendas();
    }

    // Constructor opcional para editar directamente
    public AlquilerDialog(Window parent, boolean modal,
            AlquilerService as, ClienteService cs, ViviendaService vs,
            Alquiler alquiler) {
        this(parent, modal, as, cs, vs);
        cargarAlquiler(alquiler);
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteService.obtenerTodos();
            DefaultComboBoxModel<Cliente> modelo = new DefaultComboBoxModel<>();
            modelo.addElement(null); // Opción vacía
            clientes.forEach(modelo::addElement);
            cbCliente.setModel(modelo);
        } catch (Exception e) {
            mostrarError("Error al cargar clientes: " + e.getMessage());
        }
    }

    private void cargarViviendas() {
        try {
            List<Vivienda> viviendas = viviendaService.obtenerTodas();
            DefaultComboBoxModel<Vivienda> modelo = new DefaultComboBoxModel<>();
            modelo.addElement(null);
            viviendas.forEach(modelo::addElement);
            cbVivienda.setModel(modelo);
        } catch (Exception e) {
            mostrarError("Error al cargar viviendas: " + e.getMessage());
        }
    }

    public void cargarAlquiler(Alquiler alquiler) {
        this.alquilerActual = alquiler;
        if (alquiler != null) {
            FormUtils.seleccionarItem(cbCliente, alquiler.getCliente());
            FormUtils.seleccionarItem(cbVivienda, alquiler.getVivienda());
            cbEstado.setSelectedItem(alquiler.getEstado()); // Cambio directo si es String

            dcFechaInicio.setDate(alquiler.getFecha_inicio() != null
                    ? java.util.Date.from(alquiler.getFecha_inicio().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
                    : null);

            txtTiempoEnMeses.setText(alquiler.getTiempo_meses() > 0 ? String.valueOf(alquiler.getTiempo_meses()) : "");
            txtTiempoEnDias.setText(alquiler.getTiempo_dias() > 0 ? String.valueOf(alquiler.getTiempo_dias()) : "");
            txtPrecioTotalEstimado.setText(alquiler.getPrecio_total_estimado() != null ? alquiler.getPrecio_total_estimado().toString() : "");
        }
    }

    @Override
    protected boolean validarCampos() {
        try {
            FormUtils.parseFecha(dcFechaInicio.getDate(), "Fecha de inicio");
            if (cbCliente.getSelectedItem() == null) {
                throw new IllegalArgumentException("Debe seleccionar un cliente.");
            }
            if (cbVivienda.getSelectedItem() == null) {
                throw new IllegalArgumentException("Debe seleccionar una vivienda.");
            }
            // Validamos formatos numéricos
            if (!txtTiempoEnMeses.getText().isEmpty()) {
                FormUtils.parseInt(txtTiempoEnMeses.getText(), "Tiempo en meses");
            }
            if (!txtTiempoEnDias.getText().isEmpty()) {
                FormUtils.parseInt(txtTiempoEnDias.getText(), "Tiempo en días");
            }
            if (!txtPrecioTotalEstimado.getText().isEmpty()) {
                FormUtils.parseBigDecimalOrNull(txtPrecioTotalEstimado.getText(), "Precio total estimado");
            }

            return true;
        } catch (IllegalArgumentException ex) {
            mostrarAdvertencia(ex.getMessage()); // Usamos el método de BaseDialog
            return false;
        }
    }

    @Override
    protected void guardarEntidad() throws Exception {
        if (alquilerActual == null) {
            alquilerActual = new Alquiler();
        }

        alquilerActual.setCliente((Cliente) cbCliente.getSelectedItem());
        alquilerActual.setVivienda((Vivienda) cbVivienda.getSelectedItem());
        alquilerActual.setFecha_inicio(FormUtils.parseFecha(dcFechaInicio.getDate(), "Fecha de inicio"));

        // Manejo seguro de campos vacíos para números
        String meses = txtTiempoEnMeses.getText().trim();
        alquilerActual.setTiempo_meses(meses.isEmpty() ? 0 : Integer.parseInt(meses));

        String dias = txtTiempoEnDias.getText().trim();
        alquilerActual.setTiempo_dias(dias.isEmpty() ? 0 : Integer.parseInt(dias));

        alquilerActual.setPrecio_total_estimado(FormUtils.parseBigDecimalOrNull(txtPrecioTotalEstimado.getText(), "Precio total estimado"));
        alquilerActual.setEstado((String) cbEstado.getSelectedItem());

        if (alquilerActual.getNumero_expediente() <= 0) {
            alquilerService.crearAlquiler(alquilerActual);
        } else {
            alquilerService.actualizarAlquiler(alquilerActual);
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
        dcFechaInicio = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        txtTiempoEnMeses = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTiempoEnDias = new javax.swing.JTextField();
        cbEstado = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        txtPrecioTotalEstimado = new javax.swing.JTextField();
        cbCliente = new javax.swing.JComboBox<>();
        cbVivienda = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnGuardar.setText("Guardar");
        panelBotones.add(btnGuardar);

        btnCancelar.setText("Cancelar");
        panelBotones.add(btnCancelar);

        getContentPane().add(panelBotones, java.awt.BorderLayout.PAGE_END);

        panelCampos.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Cliente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Vivienda:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Fecha Inicio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Tiempo en meses:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(dcFechaInicio, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Tiempo en días:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel5, gridBagConstraints);

        txtTiempoEnMeses.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(txtTiempoEnMeses, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Precio Total Estimado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel6, gridBagConstraints);

        txtTiempoEnDias.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(txtTiempoEnDias, gridBagConstraints);

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Activo", "Finalizado" }));
        cbEstado.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(cbEstado, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Estado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(txtPrecioTotalEstimado, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(cbCliente, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panelCampos.add(cbVivienda, gridBagConstraints);

        getContentPane().add(panelCampos, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<com.smartoccupation.modelo.Cliente> cbCliente;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<com.smartoccupation.modelo.Vivienda> cbVivienda;
    private com.toedter.calendar.JDateChooser dcFechaInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JTextField txtPrecioTotalEstimado;
    private javax.swing.JTextField txtTiempoEnDias;
    private javax.swing.JTextField txtTiempoEnMeses;
    // End of variables declaration//GEN-END:variables
}
