package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.gui.util.FormUtils;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AlquilerDialog extends BaseDialog {

    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;

    private Alquiler alquilerEnEdicion;

    public AlquilerDialog(Window parent, boolean modal,
            AlquilerService alquilerService,
            ClienteService clienteService,
            ViviendaService viviendaService) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;

        initComponents();

        // 🔹 Inyección de botones
        setBtnGuardar(btnGuardar);
        setBtnCancelar(btnCancelar);
        configurarBotonesBase();

        setLocationRelativeTo(parent);
        cargarCombos();
        iniciarEventos();
    }

    public void cargarAlquiler(Alquiler alquiler) {
        this.alquilerEnEdicion = alquiler;

        if (alquiler != null) {
            // Cliente
            FormUtils.seleccionarItem(cbCliente, clienteService.obtenerCliente(alquiler.getId_cliente()));
            // Vivienda
            Vivienda viviendaActual = viviendaService.obtenerVivienda(alquiler.getId_vivienda());
            if (viviendaActual != null && !viviendaEstaEnCombo(viviendaActual)) {
                cbVivienda.addItem(viviendaActual);
            }
            FormUtils.seleccionarItem(cbVivienda, viviendaActual);

            // Fecha inicio
            dcFechaInicio.setDate(Date.from(alquiler.getFecha_inicio().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            txtTiempoEnMeses.setText(String.valueOf(alquiler.getTiempo_meses()));
            txtTiempoEnDias.setText(String.valueOf(alquiler.getTiempo_dias()));
            txtPrecioTotalEstimado.setText(alquiler.getPrecio_total_estimado().toPlainString());
        }
    }

    private void cargarCombos() {
        cbCliente.removeAllItems();
        for (Cliente c : clienteService.obtenerTodos()) {
            cbCliente.addItem(c);
        }

        cbVivienda.removeAllItems();
        for (Vivienda v : viviendaService.obtenerPorEstado("disponible")) {
            cbVivienda.addItem(v);
        }
    }

    private boolean viviendaEstaEnCombo(Vivienda v) {
        for (int i = 0; i < cbVivienda.getItemCount(); i++) {
            if (cbVivienda.getItemAt(i).getId_vivienda() == v.getId_vivienda()) {
                return true;
            }
        }
        return false;
    }

    private void iniciarEventos() {
        dcFechaInicio.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                recalcularValores();
            }
        });

        KeyAdapter recalculoListener = new KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                recalcularValores();
            }
        };
        txtTiempoEnMeses.addKeyListener(recalculoListener);
        txtTiempoEnDias.addKeyListener(recalculoListener);
        
        // Agregar listener para que el cambio de vivienda también recalcule el precio
        cbVivienda.addActionListener(e -> recalcularValores());
    }

    private void recalcularValores() {
        try {
            // 1. Validar inputs básicos
            LocalDate fechaInicio = obtenerFechaInicio();
            Vivienda vi = (Vivienda) cbVivienda.getSelectedItem();
            if (fechaInicio == null || vi == null) {
                return;
            }

            int meses = parseInt(txtTiempoEnMeses.getText());
            int dias = parseInt(txtTiempoEnDias.getText());
            BigDecimal precioMensual = vi.getPrecio_mensual();
            
            // 🟢 CORRECCIÓN DE PRECISIÓN EN LA GUI
            
            // 2. Precio por meses completos: EXACTO
            BigDecimal precioPorMeses = precioMensual.multiply(BigDecimal.valueOf(meses));

            // 3. Precio diario: Usar alta precisión (8 decimales)
            BigDecimal precioDiario = precioMensual.divide(
                BigDecimal.valueOf(30), 
                8, // Alta precisión para evitar errores en la multiplicación
                RoundingMode.HALF_UP
            ); 

            // 4. Precio por días extra
            BigDecimal precioPorDias = precioDiario.multiply(BigDecimal.valueOf(dias));

            // 5. Total y Redondeo FINAL a 2 decimales para mostrar en la GUI
            BigDecimal total = precioPorMeses.add(precioPorDias)
                .setScale(2, RoundingMode.HALF_UP); 
            
            txtPrecioTotalEstimado.setText(total.toPlainString());
        } catch (Exception e) {
            // Se puede ignorar errores de parseo mientras el usuario escribe
            // System.out.println("Error: " + e.getMessage()); 
        }
    }

    private LocalDate obtenerFechaInicio() {
        Date d = dcFechaInicio.getDate();
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= BaseDialog =================
    @Override
    protected boolean validarCampos() {
        if (cbCliente.getSelectedItem() == null) {
            FormUtils.mostrarAdvertencia(this, "Seleccione un cliente.");
            return false;
        }
        if (cbVivienda.getSelectedItem() == null) {
            FormUtils.mostrarAdvertencia(this, "Seleccione una vivienda.");
            return false;
        }
        if (obtenerFechaInicio() == null) {
            FormUtils.mostrarAdvertencia(this, "Seleccione una fecha de inicio.");
            return false;
        }
        int meses = parseInt(txtTiempoEnMeses.getText());
        int dias = parseInt(txtTiempoEnDias.getText());
        if (meses == 0 && dias == 0) {
            FormUtils.mostrarAdvertencia(this, "Indique una duración válida.");
            return false;
        }
        return true;
    }

    @Override
    protected void guardarEntidad() throws Exception {
        Cliente c = (Cliente) cbCliente.getSelectedItem();
        Vivienda v = (Vivienda) cbVivienda.getSelectedItem();
        LocalDate fechaInicio = obtenerFechaInicio();
        int meses = parseInt(txtTiempoEnMeses.getText());
        int dias = parseInt(txtTiempoEnDias.getText());
        
        BigDecimal precioTotal = null; 

        if (alquilerEnEdicion == null) {
            Alquiler nuevo = new Alquiler();
            nuevo.setId_cliente(c.getId_cliente());
            nuevo.setId_vivienda(v.getId_vivienda());
            nuevo.setFecha_inicio(fechaInicio);
            nuevo.setTiempo_meses(meses);
            nuevo.setTiempo_dias(dias);
            nuevo.setPrecio_total_estimado(precioTotal); // Se envía NULL o el valor calculado
            alquilerService.crearAlquiler(nuevo);
            FormUtils.mostrarInfo(this, "Alquiler creado correctamente.");
        } else {
            alquilerEnEdicion.setId_cliente(c.getId_cliente());
            alquilerEnEdicion.setId_vivienda(v.getId_vivienda());
            alquilerEnEdicion.setFecha_inicio(fechaInicio);
            alquilerEnEdicion.setTiempo_meses(meses);
            alquilerEnEdicion.setTiempo_dias(dias);
            
            BigDecimal precioMostrado = new BigDecimal(txtPrecioTotalEstimado.getText().trim());
            alquilerEnEdicion.setPrecio_total_estimado(precioMostrado);
            
            alquilerService.actualizarAlquiler(alquilerEnEdicion);
            FormUtils.mostrarInfo(this, "Alquiler actualizado correctamente.");
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
        txtPrecioTotalEstimado = new javax.swing.JTextField();
        cbCliente = new javax.swing.JComboBox<>();
        cbVivienda = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 350));

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
        gridBagConstraints.weightx = 1.0;
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
        gridBagConstraints.weightx = 1.0;
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
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(txtTiempoEnDias, gridBagConstraints);

        txtPrecioTotalEstimado.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(txtPrecioTotalEstimado, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(cbCliente, gridBagConstraints);

        cbVivienda.setMaximumRowCount(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(cbVivienda, gridBagConstraints);

        getContentPane().add(panelCampos, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<com.smartoccupation.modelo.Cliente> cbCliente;
    private javax.swing.JComboBox<com.smartoccupation.modelo.Vivienda> cbVivienda;
    private com.toedter.calendar.JDateChooser dcFechaInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JTextField txtPrecioTotalEstimado;
    private javax.swing.JTextField txtTiempoEnDias;
    private javax.swing.JTextField txtTiempoEnMeses;
    // End of variables declaration//GEN-END:variables
}
