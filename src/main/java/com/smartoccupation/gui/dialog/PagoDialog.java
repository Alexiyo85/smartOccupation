package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.plaf.basic.BasicComboBoxRenderer; // Necesario para el Renderizador

public class PagoDialog extends BaseDialog {

    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private Pago pagoActual;

    public PagoDialog(Window parent, boolean modal, PagoService pagoService, AlquilerService alquilerService) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.pagoService = pagoService;
        this.alquilerService = alquilerService;

        initComponents();


        setBtnGuardar(txtGuardar);
        setBtnCancelar(btnCancelar);
        configurarBotonesBase();

        setTitle("Registrar Pago");
        setLocationRelativeTo(parent);

        cargarAlquileres();

        // 🔹 Listener para actualizar importes cuando cambia el alquiler seleccionado
        cbAlquiler.addActionListener(e -> actualizarImportesAlquiler());

        // 🔹 Listener para actualizar pendiente al escribir cantidad
        txtCantidadPagada.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                recalcularPendiente();
            }
        });

        // 🔹 Inicializar txtCantidadAlquiler después de renderizar
        SwingUtilities.invokeLater(() -> {
            if (cbAlquiler.getItemCount() > 0) {
                cbAlquiler.setSelectedIndex(0); // selecciona el primer alquiler
                actualizarImportesAlquiler();
            }
        });
    }

    // ----------------------------------------------------------------------
    // CLASE AUXILIAR PARA RENDERIZAR EL COMBOBOX CON ANCHURA DINÁMICA
    // ----------------------------------------------------------------------
    private class AnchoFijoComboBoxRenderer extends BasicComboBoxRenderer {

        private final JComboBox<?> comboBox;

        public AnchoFijoComboBoxRenderer(JComboBox<?> comboBox) {
            this.comboBox = comboBox;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            // Llamar al método base para obtener el JLabel con el texto y estilo
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (index == -1) {
                // Cuando no está desplegado, solo usa la anchura estándar del componente
                return this;
            }

            // Lógica para calcular y aplicar la anchura máxima
            int maxWidth = 0;
            // Recorre todos los ítems para encontrar el más ancho
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                Component c = super.getListCellRendererComponent(list, comboBox.getItemAt(i), i, false, false);
                maxWidth = Math.max(maxWidth, c.getPreferredSize().width);
            }

            // Establece la anchura preferida del renderizador
            // Añadimos un pequeño margen de 15 píxeles
            setPreferredSize(new Dimension(maxWidth + 15, getPreferredSize().height));

            return this;
        }
    }
    // ----------------------------------------------------------------------
    // FIN CLASE AUXILIAR
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // CARGA INICIAL DE ALQUILERES
    // ----------------------------------------------------------------------
    private void cargarAlquileres() {
        try {
            List<Alquiler> lista = alquilerService.obtenerTodos();
            DefaultComboBoxModel<Alquiler> modelo = new DefaultComboBoxModel<>();
            for (Alquiler a : lista) {
                modelo.addElement(a);
            }
            cbAlquiler.setModel(modelo);
        } catch (Exception ex) {
            mostrarError("Error cargando alquileres: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // ACTUALIZAR IMPORTES AL SELECCIONAR UN ALQUILER
    // ----------------------------------------------------------------------
    private void actualizarImportesAlquiler() {
        Alquiler alquiler = (Alquiler) cbAlquiler.getSelectedItem();
        if (alquiler == null) {
            return;
        }

        try {
            BigDecimal total = alquiler.getPrecio_total_estimado();
            if (total == null) {
                total = BigDecimal.ZERO;
            }

            List<Pago> pagos = pagoService.obtenerPagosPorExpediente(alquiler.getNumero_expediente());
            BigDecimal pagado = pagos.stream().map(Pago::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal pendiente = total.subtract(pagado);

            // Mostrar el pendiente actual en txtCantidadAlquiler
            txtCantidadAlquiler.setText(pendiente.toPlainString());

            // Inicializar txtCantidadPagada
            txtCantidadPagada.setText("");

            // txtCantidadPendiente inicialmente igual al pendiente
            txtCantidadPediente.setText(pendiente.toPlainString());

        } catch (Exception ex) {
            mostrarError("Error calculando importes: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // RECALCULAR PENDIENTE
    // ----------------------------------------------------------------------
    private void recalcularPendiente() {
        try {
            BigDecimal alquilerPendiente = new BigDecimal(txtCantidadAlquiler.getText());
            BigDecimal cantidadPagada = txtCantidadPagada.getText().isBlank()
                    ? BigDecimal.ZERO
                    : new BigDecimal(txtCantidadPagada.getText());

            BigDecimal nuevoPendiente = alquilerPendiente.subtract(cantidadPagada);
            if (nuevoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                nuevoPendiente = BigDecimal.ZERO;
            }

            txtCantidadPediente.setText(nuevoPendiente.toPlainString());

        } catch (Exception ignored) {
            // Ignorar si el usuario está escribiendo algo temporalmente inválido
        }
    }

    // ----------------------------------------------------------------------
    // VALIDACIÓN
    // ----------------------------------------------------------------------
    @Override
    protected boolean validarCampos() {
        if (cbAlquiler.getSelectedItem() == null) {
            mostrarAdvertencia("Debe seleccionar un alquiler.");
            return false;
        }
        try {
            FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago");
            FormUtils.parseBigDecimal(txtCantidadPagada.getText(), "cantidad a pagar");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------------
    // GUARDAR PAGO
    // ----------------------------------------------------------------------
    @Override
    protected void guardarEntidad() throws Exception {
        Pago pago = new Pago();
        Alquiler alquiler = (Alquiler) cbAlquiler.getSelectedItem();

        BigDecimal cantidadPagada = new BigDecimal(txtCantidadPagada.getText());

        pago.setNumero_expediente(alquiler.getNumero_expediente());
        pago.setFecha_pago(FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago"));
        pago.setCantidad(cantidadPagada);

        pagoService.registrarPago(pago);
        pagoActual = pago;

        // 🔥 Actualizar importes: txtCantidadAlquiler = nuevo pendiente
        actualizarImportesAlquiler();
    }

    public Pago getPagoActual() {
        return pagoActual;
    }

    // ----------------------------------------------------------------------
    // SIMPLE DOCUMENT LISTENER PARA txtCantidadPagada
    // ----------------------------------------------------------------------
    private abstract class SimpleDocumentListener implements javax.swing.event.DocumentListener {

        public abstract void update();

        @Override
        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        @Override
        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        @Override
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
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
        txtCantidadAlquiler = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCantidadPagada = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCantidadPediente = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(580, 200));

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
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(cbAlquiler, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Cantidad Por Pagar");
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
        gridBagConstraints.weightx = 1.0;
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
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(txtCantidadAlquiler, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Cantidad A Pagar:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(txtCantidadPagada, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Cantidad Pendiente:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        panelCampos.add(jLabel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelCampos.add(txtCantidadPediente, gridBagConstraints);

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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JTextField txtCantidadAlquiler;
    private javax.swing.JTextField txtCantidadPagada;
    private javax.swing.JTextField txtCantidadPediente;
    private javax.swing.JButton txtGuardar;
    // End of variables declaration//GEN-END:variables
}
