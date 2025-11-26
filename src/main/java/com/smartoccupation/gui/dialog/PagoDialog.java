package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro; // 👈 NUEVO: Importar EstadoCobro
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.servicios.EstadoCobroService; // 👈 NUEVO: Importar EstadoCobroService
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PagoDialog extends BaseDialog {

    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private final EstadoCobroService estadoCobroService; // 👈 NUEVO: Campo del servicio
    private Pago pagoActual;

    // 🚨 CONSTRUCTOR MODIFICADO: Ahora acepta EstadoCobroService
    public PagoDialog(Window parent, boolean modal, PagoService pagoService, AlquilerService alquilerService, EstadoCobroService estadoCobroService) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        this.estadoCobroService = estadoCobroService; // 👈 Inicializar

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
    // La clase AnchoFijoComboBoxRenderer se ha eliminado para simplificar y centrarse en la lógica.
    // ----------------------------------------------------------------------
    // FIN CLASE AUXILIAR
    // ----------------------------------------------------------------------
    // ----------------------------------------------------------------------
    // CARGA INICIAL DE ALQUILERES (MODIFICADO PARA FILTRAR PAGADOS)
    // ----------------------------------------------------------------------
    private void cargarAlquileres() {
        try {
            List<Alquiler> todosAlquileres = alquilerService.obtenerTodos();
            DefaultComboBoxModel<Alquiler> modelo = new DefaultComboBoxModel<>();

            for (Alquiler a : todosAlquileres) {
                BigDecimal total = a.getPrecio_total_estimado();
                if (total == null) {
                    continue;
                }

                // Calcular pagos realizados y total pendiente
                List<Pago> pagos = pagoService.obtenerPagosPorExpediente(a.getNumero_expediente());
                BigDecimal pagado = pagos.stream().map(Pago::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal pendiente = total.subtract(pagado);

                // Solo se añade si el pendiente es mayor que cero (Alquiler no pagado)
                if (pendiente.compareTo(BigDecimal.ZERO) > 0) {
                    modelo.addElement(a);
                }
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
            // Limpiar campos si no hay alquileres disponibles
            txtCantidadAlquiler.setText("");
            txtCantidadPagada.setText("");
            txtCantidadPediente.setText("");
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
            txtCantidadPagada.setText(pendiente.toPlainString()); // Sugerir el pago completo

            // txtCantidadPendiente inicialmente igual al pendiente
            txtCantidadPediente.setText("0.00"); // Asumiendo que paga el total sugerido

        } catch (Exception ex) {
            mostrarError("Error calculando importes: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // RECALCULAR PENDIENTE
    // ----------------------------------------------------------------------
    private void recalcularPendiente() {
        try {
            // Usamos el texto de txtCantidadAlquiler, que es el "pendiente inicial"
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

            // Validar que la cantidad pagada no sea cero o negativa, y que no supere el pendiente actual
            BigDecimal pendienteActual = new BigDecimal(txtCantidadAlquiler.getText());
            BigDecimal cantidadPagada = FormUtils.parseBigDecimal(txtCantidadPagada.getText(), "cantidad a pagar");

            if (cantidadPagada.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("La cantidad a pagar debe ser positiva.");
                return false;
            }
            if (cantidadPagada.compareTo(pendienteActual) > 0) {
                mostrarAdvertencia("La cantidad pagada (" + cantidadPagada + ") supera la cantidad pendiente (" + pendienteActual + "). Se aplicará un pago máximo del pendiente.");
            }

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------------
    // GUARDAR PAGO (MODIFICADO PARA ACTUALIZAR ESTADO DEL ALQUILER)
    // ----------------------------------------------------------------------
    @Override
    protected void guardarEntidad() throws Exception {
        Pago pago = new Pago();
        Alquiler alquiler = (Alquiler) cbAlquiler.getSelectedItem();

        // Limitar la cantidad pagada al pendiente si el usuario se excede (aunque ya se advirtió)
        BigDecimal pendienteActual = new BigDecimal(txtCantidadAlquiler.getText());
        BigDecimal cantidadPagada = new BigDecimal(txtCantidadPagada.getText());
        cantidadPagada = cantidadPagada.min(pendienteActual);

        pago.setNumero_expediente(alquiler.getNumero_expediente());
        pago.setFecha_pago(FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago"));
        pago.setCantidad(cantidadPagada);

        // 1. Registrar el pago
        pagoService.registrarPago(pago);
        pagoActual = pago;

        // 2. Comprobar el nuevo estado pendiente
        BigDecimal nuevoPendiente = new BigDecimal(txtCantidadPediente.getText());

        // 3. Si el nuevo pendiente es 0, actualizamos el estado del alquiler a "pagado"
        if (nuevoPendiente.compareTo(BigDecimal.ZERO) <= 0) {

            EstadoCobro estadoPagado = estadoCobroService.obtenerPorNombre("pagado");

            if (estadoPagado == null) {
                // Si el estado 'pagado' no existe en la base de datos, lanzamos una excepción
                throw new IllegalStateException("Error de configuración: No se pudo encontrar el estado 'pagado' en la base de datos.");
            }

            // Actualizar el estado del alquiler y guardarlo en la DB
            alquiler.setId_estado_cobro(estadoPagado.getId_estado());
            alquilerService.actualizarAlquiler(alquiler);

            // Recargar la lista para que el alquiler pagado desaparezca del JComboBox
            cargarAlquileres();
        }

        // 4. Actualizar la interfaz (refleja el nuevo pendiente o los nuevos datos del combobox)
        SwingUtilities.invokeLater(() -> {
            if (cbAlquiler.getItemCount() > 0) {
                cbAlquiler.setSelectedIndex(0);
                actualizarImportesAlquiler();
            } else {
                // Si no quedan alquileres pendientes, forzamos la limpieza de campos
                txtCantidadAlquiler.setText("");
                txtCantidadPagada.setText("");
                txtCantidadPediente.setText("");
            }
        });
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

        txtGuardar.setText("Pagar");
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
