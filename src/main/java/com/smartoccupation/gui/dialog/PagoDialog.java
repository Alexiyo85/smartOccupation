package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.modelo.EstadoCobro; // 👈 NUEVO: Importar EstadoCobro para actualizar el estado
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.servicios.AlquilerService;
import com.smartoccupation.servicios.PagoService;
import com.smartoccupation.servicios.EstadoCobroService; // 👈 NUEVO: Servicio para obtener el estado "pagado"
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Diálogo (JDialog) para el registro de un nuevo Pago a un Alquiler. 💵
 * Extiende BaseDialog para manejar la lógica de Guardar/Validar/Cancelar.
 * Incluye lógica para calcular importes pendientes y actualizar el estado del
 * Alquiler.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class PagoDialog extends BaseDialog {

    // Servicios inyectados para la capa de negocio.
    private final PagoService pagoService;
    private final AlquilerService alquilerService;
    private final EstadoCobroService estadoCobroService; // 👈 NUEVO: Campo del servicio
    // El pago recién registrado (para retorno si es necesario).
    private Pago pagoActual;

    // ===============================================================
    // CONSTRUCTOR Y CONFIGURACIÓN
    // ===============================================================
    /**
     * Constructor del diálogo de registro de pago.
     *
     * @param parent Ventana padre.
     * @param modal Indicador de modalidad.
     * @param pagoService Servicio para gestionar pagos.
     * @param alquilerService Servicio para gestionar alquileres.
     * @param estadoCobroService Servicio para gestionar estados de cobro.
     */
    public PagoDialog(Window parent, boolean modal, PagoService pagoService, AlquilerService alquilerService, EstadoCobroService estadoCobroService) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.pagoService = pagoService;
        this.alquilerService = alquilerService;
        this.estadoCobroService = estadoCobroService; // 👈 Inicializar el nuevo servicio

        initComponents();

        // Inyección de referencias de los botones al BaseDialog.
        setBtnGuardar(txtGuardar);
        setBtnCancelar(btnCancelar);
        // Configura los listeners de Guardar/Cancelar en BaseDialog.
        configurarBotonesBase();

        setTitle("Registrar Pago");
        setLocationRelativeTo(parent);

        cargarAlquileres(); // Carga la lista de alquileres pendientes.

        // 🔹 Listener para actualizar importes cuando cambia el alquiler seleccionado.
        cbAlquiler.addActionListener(e -> actualizarImportesAlquiler());

        // 🔹 Listener para actualizar el pendiente al escribir en el campo de cantidad pagada.
        txtCantidadPagada.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                recalcularPendiente();
            }
        });

        // 🔹 Inicializar el JComboBox y los campos de texto una vez que la GUI esté visible.
        SwingUtilities.invokeLater(() -> {
            if (cbAlquiler.getItemCount() > 0) {
                cbAlquiler.setSelectedIndex(0); // Selecciona el primer alquiler pendiente.
                actualizarImportesAlquiler();
            }
        });
    }

    // ----------------------------------------------------------------------
    // CARGA INICIAL DE ALQUILERES (MODIFICADO PARA FILTRAR PAGADOS)
    // ----------------------------------------------------------------------
    /**
     * Carga todos los alquileres **que tengan saldo pendiente** en el
     * JComboBox.
     */
    private void cargarAlquileres() {
        try {
            List<Alquiler> todosAlquileres = alquilerService.obtenerTodos();
            DefaultComboBoxModel<Alquiler> modelo = new DefaultComboBoxModel<>();

            for (Alquiler a : todosAlquileres) {
                BigDecimal total = a.getPrecioTotalEstimado();
                if (total == null) {
                    continue; // Ignorar si el total es nulo.
                }

                // Calcular pagos realizados
                List<Pago> pagos = pagoService.obtenerPagosPorExpediente(a.getNumeroExpediente());
                BigDecimal pagado = pagos.stream().map(Pago::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);

                // Calcular total pendiente.
                BigDecimal pendiente = total.subtract(pagado);

                // Solo se añade si el pendiente es mayor que cero (Alquiler no pagado en su totalidad).
                if (pendiente.compareTo(BigDecimal.ZERO) > 0) {
                    modelo.addElement(a);
                }
            }
            cbAlquiler.setModel(modelo);
        } catch (Exception ex) {
            logger.severe("Error cargando alquileres: " + ex.getMessage());
            mostrarError("Error cargando alquileres: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // ACTUALIZAR IMPORTES AL SELECCIONAR UN ALQUILER
    // ----------------------------------------------------------------------
    /**
     * Calcula y muestra el saldo pendiente del alquiler seleccionado y sugiere
     * el pago completo.
     */
    private void actualizarImportesAlquiler() {
        Alquiler alquiler = (Alquiler) cbAlquiler.getSelectedItem();
        if (alquiler == null) {
            // Limpiar campos si no hay alquileres disponibles.
            txtCantidadAlquiler.setText(""); // Cantidad por Pagar (Pendiente actual)
            txtCantidadPagada.setText("");
            txtCantidadPediente.setText(""); // Cantidad Pendiente (Después de este pago)
            return;
        }

        try {
            BigDecimal total = alquiler.getPrecioTotalEstimado();
            if (total == null) {
                total = BigDecimal.ZERO;
            }

            List<Pago> pagos = pagoService.obtenerPagosPorExpediente(alquiler.getNumeroExpediente());
            BigDecimal pagado = pagos.stream().map(Pago::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal pendiente = total.subtract(pagado);

            // Mostrar el pendiente actual en txtCantidadAlquiler. Este es el máximo a pagar.
            txtCantidadAlquiler.setText(pendiente.toPlainString());

            // Inicializar txtCantidadPagada: se sugiere el pago completo.
            txtCantidadPagada.setText(pendiente.toPlainString());

            // txtCantidadPendiente: Asumiendo que paga el total sugerido, el pendiente es 0.00.
            txtCantidadPediente.setText("0.00");

        } catch (Exception ex) {
            logger.severe("Error calculando importes: " + ex.getMessage());
            mostrarError("Error calculando importes: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------------
    // RECALCULAR PENDIENTE
    // ----------------------------------------------------------------------
    /**
     * Recalcula el saldo que quedará pendiente después de que el usuario
     * introduzca la cantidad a pagar.
     */
    private void recalcularPendiente() {
        try {
            // Usamos el texto de txtCantidadAlquiler, que es el "pendiente inicial" del alquiler.
            BigDecimal alquilerPendiente = new BigDecimal(txtCantidadAlquiler.getText());

            // Obtener la cantidad pagada por el usuario.
            BigDecimal cantidadPagada = txtCantidadPagada.getText().isBlank()
                    ? BigDecimal.ZERO
                    : new BigDecimal(txtCantidadPagada.getText());

            // Calcular el nuevo pendiente.
            BigDecimal nuevoPendiente = alquilerPendiente.subtract(cantidadPagada);

            // Si el nuevo pendiente es negativo (pago en exceso), lo forzamos a cero.
            if (nuevoPendiente.compareTo(BigDecimal.ZERO) < 0) {
                nuevoPendiente = BigDecimal.ZERO;
            }

            // Muestra el resultado.
            txtCantidadPediente.setText(nuevoPendiente.toPlainString());

        } catch (Exception ignored) {
            // Ignorar excepciones de formato mientras el usuario está escribiendo (ej: solo un punto decimal).
        }
    }

    // ----------------------------------------------------------------------
    // VALIDACIÓN
    // ----------------------------------------------------------------------
    /**
     * Valida que el formulario esté correctamente completado. Incluye la
     * validación de que la cantidad a pagar no sea negativa y no exceda el
     * pendiente.
     *
     * @return true si los campos son válidos.
     */
    @Override
    protected boolean validarCampos() {
        if (cbAlquiler.getSelectedItem() == null) {
            mostrarAdvertencia("Debe seleccionar un alquiler.");
            return false;
        }
        try {
            // 1. Validar fecha
            FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago");

            // 2. Obtener y validar cantidades
            BigDecimal pendienteActual = new BigDecimal(txtCantidadAlquiler.getText());
            BigDecimal cantidadPagada = FormUtils.parseBigDecimal(txtCantidadPagada.getText(), "cantidad a pagar");

            // 3. Validación de cantidad positiva
            if (cantidadPagada.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("La cantidad a pagar debe ser positiva.");
                return false;
            }
            // 4. Advertencia de pago excesivo (la lógica de guardar lo limitará)
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
    /**
     * Implementa la lógica de persistencia: registra el pago y, si el saldo
     * queda a 0, actualiza el estado del alquiler a "pagado".
     *
     * @throws Exception Si ocurre un error en la capa de servicio.
     */
    @Override
    protected void guardarEntidad() throws Exception {
        Pago pago = new Pago();
        Alquiler alquiler = (Alquiler) cbAlquiler.getSelectedItem();

        // 1. Limitar la cantidad pagada al pendiente si el usuario se excede (solo se registra el pendiente).
        BigDecimal pendienteActual = new BigDecimal(txtCantidadAlquiler.getText());
        BigDecimal cantidadPagada = new BigDecimal(txtCantidadPagada.getText());
        cantidadPagada = cantidadPagada.min(pendienteActual); // Asegura que la cantidad pagada no sea mayor al pendiente.

        // Mapear campos al objeto Pago.
        pago.setNumeroExpediente(alquiler.getNumeroExpediente());
        pago.setFechaPago(FormUtils.parseFecha(dcFechaPago.getDate(), "fecha de pago"));
        pago.setCantidad(cantidadPagada);

        // 2. Registrar el pago en la base de datos.
        pagoService.registrarPago(pago);
        pagoActual = pago;

        // 3. Comprobar el nuevo estado pendiente después del pago.
        // Se usa el valor que se calculó en la interfaz, ya limitado.
        BigDecimal nuevoPendiente = new BigDecimal(txtCantidadPediente.getText());

        // 4. Si el nuevo pendiente es 0 (o menos), actualizamos el estado del alquiler a "pagado".
        if (nuevoPendiente.compareTo(BigDecimal.ZERO) <= 0) {

            // Obtener el objeto EstadoCobro 'pagado' desde la base de datos.
            EstadoCobro estadoPagado = estadoCobroService.obtenerPorNombre("pagado");

            if (estadoPagado == null) {
                // Guardia de seguridad si la BD no tiene el estado necesario.
                throw new IllegalStateException("Error de configuración: No se pudo encontrar el estado 'pagado' en la base de datos.");
            }

            // Actualizar el ID de estado del Alquiler.
            alquiler.setIdEstadoCobro(estadoPagado.getIdEstado());
            // Guardar el alquiler actualizado en la BD.
            alquilerService.actualizarAlquiler(alquiler);

            // Recargar la lista de alquileres para eliminar el recién pagado del JComboBox.
            cargarAlquileres();
        }

        // 5. Actualizar la interfaz para reflejar el nuevo estado (refrescar el combobox).
        SwingUtilities.invokeLater(() -> {
            if (cbAlquiler.getItemCount() > 0) {
                cbAlquiler.setSelectedIndex(0); // Selecciona el primer pendiente restante.
                actualizarImportesAlquiler();
            } else {
                // Si no quedan alquileres pendientes, limpiar los campos.
                txtCantidadAlquiler.setText("");
                txtCantidadPagada.setText("");
                txtCantidadPediente.setText("");
            }
        });
    }

    /**
     * Devuelve el objeto Pago recién registrado.
     *
     * @return El objeto Pago.
     */
    public Pago getPagoActual() {
        return pagoActual;
    }

    // ----------------------------------------------------------------------
    // SIMPLE DOCUMENT LISTENER PARA txtCantidadPagada
    // ----------------------------------------------------------------------
    /**
     * Clase auxiliar abstracta para simplificar la implementación de
     * DocumentListener, permitiendo reaccionar a cualquier cambio en el campo
     * de texto.
     */
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

    /**
     * Método autogenerado por el diseñador de GUI (NetBeans).
     */
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
