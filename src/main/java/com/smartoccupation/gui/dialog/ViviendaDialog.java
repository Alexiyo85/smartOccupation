package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.servicios.ViviendaService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*; // Necesario para Window y Dialog.ModalityType
import java.math.BigDecimal; // Necesario para parsear el precio

/**
 * Diálogo (JDialog) para la creación y edición de entidades Vivienda. 🏠 Hereda
 * de BaseDialog para integrar la lógica de guardar, cancelar y manejar errores.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ViviendaDialog extends BaseDialog {

    private final ViviendaService viviendaService;
    // Referencia a la entidad que se está creando o editando.
    private Vivienda viviendaActual;

    // ===============================================================
    // CONSTRUCTOR PRINCIPAL (Creación)
    // ===============================================================
    /**
     * Constructor para crear una nueva Vivienda.
     *
     * @param parent Ventana padre.
     * @param modal Indicador de modalidad.
     * @param viviendaService Servicio para la persistencia de datos.
     */
    public ViviendaDialog(Window parent, boolean modal, ViviendaService viviendaService) {
        // Llama al constructor de BaseDialog.
        super(parent, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
        this.viviendaService = viviendaService;

        initComponents();

        // INYECTAR los botones del formulario en BaseDialog para que maneje los listeners.
        setBtnGuardar(this.btnGuardar);
        setBtnCancelar(this.btnCancelar);

        // Activar la lógica general de Guardar/Cancelar (llama a guardarEntidad() y dispose()).
        configurarBotonesBase();

        setTitle("Registrar Nueva Vivienda");
        pack(); // Ajustar tamaño de la ventana al contenido.
        setLocationRelativeTo(parent);
    }

    // ===============================================================
    // CONSTRUCTOR PARA EDICIÓN
    // ===============================================================
    /**
     * Constructor para editar una Vivienda existente.
     *
     * @param parent Ventana padre.
     * @param modal Indicador de modalidad.
     * @param viviendaService Servicio para la persistencia de datos.
     * @param vivienda La entidad Vivienda a editar.
     */
    public ViviendaDialog(Window parent, boolean modal, ViviendaService viviendaService, Vivienda vivienda) {
        // Llama al constructor principal para inicializar el servicio y los componentes.
        this(parent, modal, viviendaService);
        this.viviendaActual = vivienda;
        cargarVivienda(vivienda); // Rellenar los campos con los datos de la entidad.
        setTitle("Editar Vivienda");
    }

    /**
     * Rellena los campos del formulario con los datos de una entidad Vivienda.
     *
     * @param vivienda La entidad a cargar.
     */
    public void cargarVivienda(Vivienda vivienda) {
        this.viviendaActual = vivienda;
        if (vivienda != null) {
            txtCodigoReferencia.setText(vivienda.getCodigoReferencia());
            txtDireccion.setText(vivienda.getDireccion());
            txtCiudad.setText(vivienda.getCiudad());
            txtProvincia.setText(vivienda.getProvincia());
            txtCodigoPostal.setText(vivienda.getCodigoPostal());

            // Convertir valores numéricos a String para los JTextFields.
            txtMetrosCuadrados.setText(String.valueOf(vivienda.getMetrosCuadrados()));
            txtNumeroHabitaciones.setText(String.valueOf(vivienda.getNumeroHabitaciones()));
            txtNumeroBanios.setText(String.valueOf(vivienda.getNumeroBanios()));
            // Usamos toPlainString() para asegurar formato de BigDecimal en el campo de texto.
            txtPrecioMensual.setText(vivienda.getPrecio_mensual() != null ? vivienda.getPrecio_mensual().toPlainString() : "");

            // Establecer el estado seleccionado en el JComboBox.
            cmbEstado.setSelectedItem(vivienda.getEstado());
        }
    }

    // ===============================================================
    // VALIDACIÓN
    // ===============================================================
    /**
     * Implementa la validación de campos del formulario. Se llama
     * automáticamente antes de guardar gracias a BaseDialog.
     *
     * @return true si todos los campos son válidos.
     */
    @Override
    protected boolean validarCampos() {
        // 1. Validación de campo obligatorio (Dirección).
        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarAdvertencia("La dirección es obligatoria.");
            return false;
        }

        try {
            // 2. Validar que los campos numéricos tengan el formato correcto (usando FormUtils).
            FormUtils.parseBigDecimal(txtPrecioMensual.getText(), "precio mensual");
            FormUtils.parseInt(txtMetrosCuadrados.getText(), "metros cuadrados");
            FormUtils.parseInt(txtNumeroHabitaciones.getText(), "número de habitaciones");
            FormUtils.parseInt(txtNumeroBanios.getText(), "número de baños");
            return true;
        } catch (IllegalArgumentException ex) {
            // Capturar errores de formato de FormUtils y mostrarlos al usuario.
            mostrarAdvertencia(ex.getMessage());
            return false;
        }
    }

    // ===============================================================
    // GUARDAR ENTIDAD
    // ===============================================================
    /**
     * Implementa la lógica para crear o actualizar la entidad Vivienda. Se
     * llama automáticamente después de la validación exitosa gracias a
     * BaseDialog.
     *
     * @throws Exception Propaga cualquier error del servicio de persistencia.
     */
    @Override
    protected void guardarEntidad() throws Exception {
        // Inicializar la entidad si es un nuevo registro.
        if (viviendaActual == null) {
            viviendaActual = new Vivienda();
        }

        // Mapear campos de texto al objeto Vivienda.
        viviendaActual.setCodigoReferencia(txtCodigoReferencia.getText().trim());
        viviendaActual.setDireccion(txtDireccion.getText().trim());
        viviendaActual.setCiudad(txtCiudad.getText().trim());
        viviendaActual.setProvincia(txtProvincia.getText().trim());
        viviendaActual.setCodigoPostal(txtCodigoPostal.getText().trim());

        // Mapear campos numéricos usando los parseadores seguros.
        viviendaActual.setMetrosCuadrados(FormUtils.parseInt(txtMetrosCuadrados.getText(), "metros cuadrados"));
        viviendaActual.setNumeroHabitaciones(FormUtils.parseInt(txtNumeroHabitaciones.getText(), "número de habitaciones"));
        viviendaActual.setNumeroBanios(FormUtils.parseInt(txtNumeroBanios.getText(), "número de baños"));
        viviendaActual.setPrecioMensual(FormUtils.parseBigDecimal(txtPrecioMensual.getText(), "precio mensual"));
        viviendaActual.setEstado((String) cmbEstado.getSelectedItem());

        // Lógica de persistencia: Crear si ID es 0, Actualizar si ID > 0.
        if (viviendaActual.getIdVivienda() <= 0) {
            // Crear nueva vivienda.
            boolean exito = viviendaService.crearVivienda(viviendaActual);
            if (!exito) {
                throw new Exception("No se pudo crear la vivienda.");
            }
        } else {
            // Actualizar vivienda existente.
            viviendaService.actualizarVivienda(viviendaActual);
        }

        // El BaseDialog se encarga de cerrar el diálogo (dispose()) después de esta llamada si no hay excepción.
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
