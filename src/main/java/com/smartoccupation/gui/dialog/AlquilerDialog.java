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

/**
 * Diálogo (JDialog) para la creación y edición de entidades Alquiler. 🔑
 * Incluye la lógica para calcular el precio total estimado basándose en la
 * vivienda seleccionada y la duración del contrato (meses y días).
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class AlquilerDialog extends BaseDialog {

    // Servicios de negocio inyectados.
    private final AlquilerService alquilerService;
    private final ClienteService clienteService;
    private final ViviendaService viviendaService;

    // Entidad que se está creando o editando. Es null si es una nueva creación.
    private Alquiler alquilerEnEdicion;

    /**
     * Constructor principal para el diálogo.
     */
    public AlquilerDialog(Window parent, boolean modal,
            AlquilerService alquilerService,
            ClienteService clienteService,
            ViviendaService viviendaService) {
        // Llama al constructor de BaseDialog.
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        // Asigna los servicios.
        this.alquilerService = alquilerService;
        this.clienteService = clienteService;
        this.viviendaService = viviendaService;

        initComponents(); // Inicializa los componentes autogenerados.

        // 🔹 Inyección de botones (BaseDialog se encarga de añadir los listeners de Guardar/Cancelar)
        setBtnGuardar(btnGuardar);
        setBtnCancelar(btnCancelar);
        configurarBotonesBase();

        setLocationRelativeTo(parent); // Centra el diálogo en la ventana padre.
        cargarCombos(); // Carga listas de Clientes y Viviendas (disponibles).
        iniciarEventos(); // Configura los listeners de cambio y recálculo.
    }

    /**
     * Carga los datos de un objeto Alquiler existente en los campos del
     * formulario para el modo edición.
     *
     * @param alquiler El objeto Alquiler a cargar.
     */
    public void cargarAlquiler(Alquiler alquiler) {
        this.alquilerEnEdicion = alquiler;

        if (alquiler != null) {
            // Cliente: Selecciona el cliente asociado por ID.
            FormUtils.seleccionarItem(cbCliente, clienteService.obtenerCliente(alquiler.getIdCliente()));

            // Vivienda: Obtiene la vivienda asociada al alquiler.
            Vivienda viviendaActual = viviendaService.obtenerVivienda(alquiler.getIdVivienda());
            // Si la vivienda no está en el combo (porque ya no está "disponible"), la añade temporalmente.
            if (viviendaActual != null && !viviendaEstaEnCombo(viviendaActual)) {
                cbVivienda.addItem(viviendaActual);
            }
            // Selecciona la vivienda.
            FormUtils.seleccionarItem(cbVivienda, viviendaActual);

            // Convierte LocalDate a Date para el JDateChooser.
            dcFechaInicio.setDate(Date.from(alquiler.getFechaInicio().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            // Duración y Precio.
            txtTiempoEnMeses.setText(String.valueOf(alquiler.getTiempoMeses()));
            txtTiempoEnDias.setText(String.valueOf(alquiler.getTiempoDias()));
            txtPrecioTotalEstimado.setText(alquiler.getPrecioTotalEstimado().toPlainString());
        }
    }

    /**
     * Carga los JComboBox con los datos de Clientes y Viviendas.
     */
    private void cargarCombos() {
        cbCliente.removeAllItems();
        // Carga todos los clientes.
        for (Cliente c : clienteService.obtenerTodos()) {
            cbCliente.addItem(c);
        }

        cbVivienda.removeAllItems();
        // Carga solo las viviendas con estado "disponible".
        for (Vivienda v : viviendaService.obtenerPorEstado("disponible")) {
            cbVivienda.addItem(v);
        }
    }

    /**
     * Verifica si una vivienda ya está presente en el JComboBox de viviendas.
     * (Necesario para el modo edición cuando una vivienda ya alquilada debe
     * aparecer).
     *
     * @param v La vivienda a buscar.
     * @return true si ya está en el combo, false en caso contrario.
     */
    private boolean viviendaEstaEnCombo(Vivienda v) {
        for (int i = 0; i < cbVivienda.getItemCount(); i++) {
            if (cbVivienda.getItemAt(i).getIdVivienda() == v.getIdVivienda()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Configura los listeners para desencadenar el recálculo del precio total
     * estimado.
     */
    private void iniciarEventos() {
        // Listener para el cambio de fecha.
        dcFechaInicio.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                recalcularValores();
            }
        });

        // Listener común para los campos de texto (meses y días).
        KeyAdapter recalculoListener = new KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                recalcularValores(); // Se ejecuta al soltar una tecla.
            }
        };
        txtTiempoEnMeses.addKeyListener(recalculoListener);
        txtTiempoEnDias.addKeyListener(recalculoListener);

        // Agregar listener para que el cambio de vivienda también recalcule el precio
        cbVivienda.addActionListener(e -> recalcularValores());
    }

    /**
     * Calcula y actualiza el campo {@code txtPrecioTotalEstimado} basándose en
     * la vivienda, los meses y los días introducidos.
     */
    private void recalcularValores() {
        try {
            // 1. Validar inputs básicos
            LocalDate fechaInicio = obtenerFechaInicio();
            Vivienda vi = (Vivienda) cbVivienda.getSelectedItem();
            if (fechaInicio == null || vi == null) {
                txtPrecioTotalEstimado.setText(""); // Limpiar si faltan datos esenciales.
                return;
            }

            int meses = parseInt(txtTiempoEnMeses.getText());
            int dias = parseInt(txtTiempoEnDias.getText());
            BigDecimal precioMensual = vi.getPrecio_mensual();

            // 🟢 CORRECCIÓN DE PRECISIÓN EN LA GUI
            // 2. Precio por meses completos: EXACTO
            BigDecimal precioPorMeses = precioMensual.multiply(BigDecimal.valueOf(meses));

            // 3. Precio diario: Usar alta precisión (8 decimales)
            // Se asume un mes de 30 días para el cálculo diario.
            BigDecimal precioDiario = precioMensual.divide(
                    BigDecimal.valueOf(30),
                    8, // Alta precisión para evitar errores en la multiplicación
                    RoundingMode.HALF_UP
            );

            // 4. Precio por días extra
            BigDecimal precioPorDias = precioDiario.multiply(BigDecimal.valueOf(dias));

            // 5. Total y Redondeo FINAL a 2 decimales para mostrar en la GUI
            BigDecimal total = precioPorMeses.add(precioPorDias)
                    .setScale(2, RoundingMode.HALF_UP); // Redondeo final para visualización.

            txtPrecioTotalEstimado.setText(total.toPlainString());
        } catch (Exception e) {
            // Se puede ignorar errores de parseo mientras el usuario escribe (ej. si introduce texto).
            // System.out.println("Error: " + e.getMessage()); 
        }
    }

    /**
     * Convierte la fecha seleccionada del JDateChooser a LocalDate.
     *
     * @return El objeto LocalDate o null si no se ha seleccionado fecha.
     */
    private LocalDate obtenerFechaInicio() {
        Date d = dcFechaInicio.getDate();
        // Convierte Date a Instant, luego a ZonedDateTime (con zona horaria por defecto) y finalmente a LocalDate.
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Intenta convertir una cadena a entero, devolviendo 0 si falla (útil para
     * campos vacíos).
     */
    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    // ================= BaseDialog =================
    /**
     * Implementación del método de validación de campos del diálogo base.
     *
     * @return true si todos los campos requeridos son válidos.
     */
    @Override
    protected boolean validarCampos() {
        // Validación de JComboBox Cliente.
        if (cbCliente.getSelectedItem() == null) {
            FormUtils.mostrarAdvertencia(this, "Seleccione un cliente.");
            return false;
        }
        // Validación de JComboBox Vivienda.
        if (cbVivienda.getSelectedItem() == null) {
            FormUtils.mostrarAdvertencia(this, "Seleccione una vivienda.");
            return false;
        }
        // Validación de Fecha de Inicio.
        if (obtenerFechaInicio() == null) {
            FormUtils.mostrarAdvertencia(this, "Seleccione una fecha de inicio.");
            return false;
        }
        // Validación de Duración (meses o días debe ser mayor a cero).
        int meses = parseInt(txtTiempoEnMeses.getText());
        int dias = parseInt(txtTiempoEnDias.getText());
        if (meses == 0 && dias == 0) {
            FormUtils.mostrarAdvertencia(this, "Indique una duración válida.");
            return false;
        }
        return true;
    }

    /**
     * Implementación del método de guardado del diálogo base (creación o
     * actualización).
     */
    @Override
    protected void guardarEntidad() throws Exception {
        // Recopilación de datos del formulario.
        Cliente c = (Cliente) cbCliente.getSelectedItem();
        Vivienda v = (Vivienda) cbVivienda.getSelectedItem();
        LocalDate fechaInicio = obtenerFechaInicio();
        int meses = parseInt(txtTiempoEnMeses.getText());
        int dias = parseInt(txtTiempoEnDias.getText());

        // El precio total se calcula en la GUI (recalcularValores), se obtiene para el guardado.
        BigDecimal precioTotal = null;

        if (alquilerEnEdicion == null) {
            // Modo CREACIÓN
            Alquiler nuevo = new Alquiler();
            nuevo.setIdCliente(c.getIdCliente());
            nuevo.setIdVivienda(v.getIdVivienda());
            nuevo.setFechaInicio(fechaInicio);
            nuevo.setTiempoMeses(meses);
            nuevo.setTiempoDias(dias);
            // El valor a guardar se toma del campo de texto tras el recálculo
            precioTotal = new BigDecimal(txtPrecioTotalEstimado.getText().trim());
            nuevo.setPrecioTotalEstimado(precioTotal);

            alquilerService.crearAlquiler(nuevo);
            FormUtils.mostrarInfo(this, "Alquiler creado correctamente.");
        } else {
            // Modo EDICIÓN
            alquilerEnEdicion.setIdCliente(c.getIdCliente());
            alquilerEnEdicion.setIdVivienda(v.getIdVivienda());
            alquilerEnEdicion.setFechaInicio(fechaInicio);
            alquilerEnEdicion.setTiempoMeses(meses);
            alquilerEnEdicion.setTiempoDias(dias);

            // Asigna el precio calculado y visible en la GUI.
            BigDecimal precioMostrado = new BigDecimal(txtPrecioTotalEstimado.getText().trim());
            alquilerEnEdicion.setPrecioTotalEstimado(precioMostrado);

            alquilerService.actualizarAlquiler(alquilerEnEdicion);
            FormUtils.mostrarInfo(this, "Alquiler actualizado correctamente.");
        }
    }

    /**
     * Método autogenerado por el diseñador de GUI para inicializar los
     * componentes.
     */
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
