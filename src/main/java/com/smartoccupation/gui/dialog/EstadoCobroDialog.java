package com.smartoccupation.gui.dialog;

import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.servicios.EstadoCobroService;
import com.smartoccupation.gui.util.FormUtils;

import javax.swing.*;
import java.awt.*;

public class EstadoCobroDialog extends BaseDialog {

    private final EstadoCobroService estadoCobroService;
    private EstadoCobro estadoCobroActual;

    // ===============================================================
    // CONSTRUCTORES
    // ===============================================================

    /**
     * Constructor principal para inyección de dependencias.
     * @param parent Ventana padre.
     * @param modal Indica si es modal.
     * @param estadoCobroService Servicio para interactuar con la BBDD.
     */
    public EstadoCobroDialog(Window parent, boolean modal, 
                             EstadoCobroService estadoCobroService) {
        // Usar Window en lugar de Frame
        super(parent, modal ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS);

        this.estadoCobroService = estadoCobroService;
        
        initComponents();
        
        pack(); // Ajustar tamaño
        setLocationRelativeTo(parent);
    }
    
    /**
     * Constructor para editar un EstadoCobro existente.
     */
    public EstadoCobroDialog(Window parent, boolean modal,
                             EstadoCobroService service,
                             EstadoCobro estadoCobro) {
        this(parent, modal, service);
        cargarEstadoCobro(estadoCobro);
    }

    // ===============================================================
    // LÓGICA DE CARGA Y GESTIÓN
    // ===============================================================

    public void cargarEstadoCobro(EstadoCobro estado) {
        this.estadoCobroActual = estado;
        if (estado != null) {
            setTitle("Editar Estado de Cobro ID #" + estado.getId_estado());
            txtNombre.setText(estado.getNombre_estado());
        } else {
            setTitle("Nuevo Estado de Cobro");
            // Limpiar campos si es necesario (aunque ya están vacíos al inicio)
            txtNombre.setText("");
        }
    }

        // ===============================================================
    // MÉTODOS DE BASEDIALOG
    // ===============================================================

    @Override
    protected boolean validarCampos() {
        try {
            String nombre = txtNombre.getText();
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del estado de cobro es obligatorio.");
            }
            return true;
        } catch (IllegalArgumentException ex) {
            FormUtils.mostrarAdvertencia(this, ex.getMessage());
            return false;
        }
    }

    @Override
    protected void guardarEntidad() throws Exception {
        if (estadoCobroActual == null) {
            estadoCobroActual = new EstadoCobro();
        }
        
        // El setter del modelo ya valida que no sea nulo/vacío y limpia espacios.
        estadoCobroActual.setNombre_estado(txtNombre.getText());

        if (estadoCobroActual.getId_estado() == null || estadoCobroActual.getId_estado() <= 0) {
            // Es un nuevo registro
            estadoCobroService.crearEstadoCobro(estadoCobroActual);
            FormUtils.mostrarInfo(this, "Estado de cobro creado correctamente.");
        } else {
            // Es una actualización
            estadoCobroService.actualizarEstadoCobro(estadoCobroActual);
            FormUtils.mostrarInfo(this, "Estado de cobro actualizado correctamente.");
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panelCampos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnGuardar.setText("Guardar");
        panelBotones.add(btnGuardar);

        btnCancelar.setText("Cancelar");
        panelBotones.add(btnCancelar);

        getContentPane().add(panelBotones, java.awt.BorderLayout.PAGE_END);

        panelCampos.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Nombre Estado:");
        panelCampos.add(jLabel1, new java.awt.GridBagConstraints());
        panelCampos.add(txtNombre, new java.awt.GridBagConstraints());

        getContentPane().add(panelCampos, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCampos;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
}
