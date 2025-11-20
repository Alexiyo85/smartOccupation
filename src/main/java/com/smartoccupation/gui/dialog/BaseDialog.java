package com.smartoccupation.gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

public abstract class BaseDialog extends JDialog {

    protected boolean guardado = false;
    protected static final Logger logger = Logger.getLogger(BaseDialog.class.getName());

    protected JButton btnGuardar = new JButton("Guardar");
    protected JButton btnCancelar = new JButton("Cancelar");

    // Constructor para Frame
    public BaseDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initBotones();
    }

    // AGREGA ESTE CONSTRUCTOR para soportar Window (JDialogs padres o SwingUtilities.getWindowAncestor)
    public BaseDialog(Window parent, ModalityType modalityType) {
        super(parent, modalityType);
        initBotones();
    }

    private void initBotones() {
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        // Importante: Agregamos al PAGE_END. 
        // Las clases hijas deben poner su contenido en CENTER.
        getContentPane().add(panelBotones, BorderLayout.PAGE_END);

        btnGuardar.addActionListener(e -> {
            try {
                if (validarCampos()) {
                    guardarEntidad(); // Llama al método abstracto
                    guardado = true;
                    dispose();
                }
            } catch (Exception ex) {
                logger.severe("Error: " + ex.getMessage());
                mostrarError(ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    public boolean isGuardado() { return guardado; }

    protected abstract boolean validarCampos();
    protected abstract void guardarEntidad() throws Exception;

    protected void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    protected void mostrarAdvertencia(String mensaje) { // Agregado para compatibilidad
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}