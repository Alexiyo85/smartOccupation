package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.servicios.ClienteService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo (JDialog) para la creación y edición de entidades Cliente. 👤
 * Extiende BaseDialog para aprovechar la lógica común de guardado y validación.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ClienteDialog extends BaseDialog {

    // Servicio de negocio para interactuar con la capa de datos de Cliente.
    private final ClienteService clienteService;
    // Entidad actual en edición o la nueva entidad a crear.
    private Cliente clienteActual;

// ===============================================================
// CONSTRUCTORES Y CONFIGURACIÓN (La Inyección de Referencias)
// ===============================================================
    /**
     * Constructor para la creación de un **nuevo** cliente.
     *
     * @param parent Ventana padre.
     * @param modal Tipo de modalidad.
     * @param clienteService Servicio de Cliente inyectado.
     */
    public ClienteDialog(Window parent, boolean modal, ClienteService clienteService) {
        // Llama al constructor de BaseDialog.
        super(parent, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);
        this.clienteService = clienteService;

        // 1. Ejecuta el código generado. Esto crea las instancias de los componentes privados (incluyendo los botones).
        initComponents();

        // 2. PASO CLAVE: INYECTAMOS las referencias de los botones privados al padre (BaseDialog).
        // Esto permite que BaseDialog configure los listeners en los objetos correctos (btnGuardar, btnCancelar).
        setBtnGuardar(this.btnGuardar);
        setBtnCancelar(this.btnCancelar);

        // 3. LLAMADA CLAVE: BaseDialog aplica la lógica de guardar/cancelar a las referencias inyectadas.
        configurarBotonesBase();

        // Finaliza la configuración de la ventana.
        pack(); // Ajusta el tamaño del diálogo a sus componentes.
        setLocationRelativeTo(parent); // Centra el diálogo en la ventana padre.
    }

    /**
     * Constructor para la **edición** de un cliente existente.
     *
     * @param parent Ventana padre.
     * @param modal Tipo de modalidad.
     * @param clienteService Servicio de Cliente inyectado.
     * @param cliente Cliente a cargar para edición.
     */
    public ClienteDialog(Window parent, boolean modal, ClienteService clienteService, Cliente cliente) {
        // Llama al constructor de creación para inicializar componentes y servicios.
        this(parent, modal, clienteService);
        this.clienteActual = cliente;
        cargarCliente(cliente); // Carga los datos del cliente en los campos.
        setTitle("Editar Cliente"); // Cambia el título del diálogo.
    }

// -----------------------------------------------------------------------------------
// MÉTODOS DE NEGOCIO Y LÓGICA ABSTRACTA
// -----------------------------------------------------------------------------------
    /**
     * Carga los datos de un objeto Cliente en los campos de texto del
     * formulario.
     *
     * @param cliente El objeto Cliente cuyos datos se van a mostrar.
     */
    public void cargarCliente(Cliente cliente) {
        this.clienteActual = cliente;
        // Uso de ternario para manejar posibles nulos y evitar NullPointerException
        txtNombre.setText(cliente.getNombre() != null ? cliente.getNombre() : "");
        txtApellido1.setText(cliente.getPrimerApellido() != null ? cliente.getPrimerApellido() : "");
        txtApellido2.setText(cliente.getSegundoApellido() != null ? cliente.getSegundoApellido() : "");
        txtDni.setText(cliente.getDni() != null ? cliente.getDni() : "");
        txtCiudad.setText(cliente.getCiudad() != null ? cliente.getCiudad() : "");
        txtProvincia.setText(cliente.getProvincia() != null ? cliente.getProvincia() : "");
        txtEmail.setText(cliente.getEmail() != null ? cliente.getEmail() : "");
        txtTelefono.setText(cliente.getTelefono() != null ? cliente.getTelefono() : "");
        txtDireccion.setText(cliente.getDireccion() != null ? cliente.getDireccion() : "");
        txtCodigoPostal.setText(cliente.getCodigo_postal() != null ? cliente.getCodigo_postal() : "");
    }

    /**
     * Implementación del método abstracto de validación de BaseDialog.
     *
     * @return true si los campos obligatorios están llenos y el email es válido
     * (si se proporciona).
     */
    @Override
    protected boolean validarCampos() {
        // Lógica de validación
        try {
            // Validación de campos obligatorios
            if (txtNombre.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Falta nombre.");
            }
            if (txtApellido1.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Falta primer apellido.");
            }
            if (txtDni.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Falta DNI.");
            }
            // Validación de email solo si se ha introducido algo.
            if (!txtEmail.getText().trim().isEmpty()) {
                FormUtils.validarEmail(txtEmail.getText().trim());
            }
            return true;
        } catch (IllegalArgumentException ex) {
            // Captura las excepciones de validación y muestra una advertencia.
            mostrarAdvertencia(ex.getMessage());
            return false;
        }
    }

    /**
     * Implementación del método abstracto de persistencia (Guardar/Actualizar)
     * de BaseDialog.
     *
     * @throws Exception Si ocurre un error en la capa de servicio al
     * crear/actualizar.
     */
    @Override
    protected void guardarEntidad() throws Exception {
        // Lógica de negocio (CRUD)
        if (clienteActual == null) {
            // Si es null, estamos en modo creación.
            clienteActual = new Cliente();
        }

        // Mapeo de campos del formulario a la entidad Cliente.
        clienteActual.setNombre(txtNombre.getText().trim());
        clienteActual.setPrimerApellido(txtApellido1.getText().trim());
        // El segundo apellido y otros campos opcionales se guardan como null si están vacíos.
        clienteActual.setSegundoApellido(txtApellido2.getText().trim().isEmpty() ? null : txtApellido2.getText().trim());
        clienteActual.setDni(txtDni.getText().trim());
        clienteActual.setCiudad(txtCiudad.getText().trim());
        clienteActual.setProvincia(txtProvincia.getText().trim());
        clienteActual.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        clienteActual.setDireccion(txtDireccion.getText().trim());
        clienteActual.setTelefono(txtTelefono.getText().trim().isEmpty() ? null : txtTelefono.getText().trim());
        clienteActual.setCodigoPostal(txtCodigoPostal.getText().trim());

        // Decide si crear o actualizar basándose en si el ID ya existe.
        if (clienteActual.getIdCliente() <= 0) {
            // Crea el cliente.
            clienteService.crearCliente(clienteActual);
        } else {
            // Actualiza el cliente.
            clienteService.actualizarCliente(clienteActual);
        }
    }

    /**
     * Método autogenerado por el diseñador de GUI (NetBeans).
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtApellido1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtApellido2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCiudad = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtProvincia = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtCodigoPostal = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Nuevo Cliente");

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWeights = new double[] {0.0, 1.0};
        jPanel1.setLayout(jPanel1Layout);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel1, gridBagConstraints);

        txtNombre.setColumns(20);
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtNombre, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Primer Apelldio:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel3, gridBagConstraints);

        txtApellido1.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtApellido1, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Segundo Apellido:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel4, gridBagConstraints);

        txtApellido2.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtApellido2, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("DNI:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel5, gridBagConstraints);

        txtDni.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtDni, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Teléfono:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel6, gridBagConstraints);

        txtTelefono.setColumns(20);
        txtTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefonoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtTelefono, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Email:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel7, gridBagConstraints);

        txtEmail.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtEmail, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Ciudad:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel8, gridBagConstraints);

        txtCiudad.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtCiudad, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Provincia:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel2, gridBagConstraints);

        txtProvincia.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtProvincia, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Dirección:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel9, gridBagConstraints);

        txtDireccion.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(txtDireccion, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Código Postal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel1.add(jLabel10, gridBagConstraints);

        txtCodigoPostal.setColumns(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.ipadx = 120;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(txtCodigoPostal, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        btnGuardar.setText("Guardar");
        jPanel3.add(btnGuardar);

        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setText("Cancelar");
        jPanel3.add(btnCancelar);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void txtTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField txtApellido1;
    private javax.swing.JTextField txtApellido2;
    private javax.swing.JTextField txtCiudad;
    private javax.swing.JTextField txtCodigoPostal;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtDni;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtProvincia;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
