package com.smartoccupation.gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Clase base abstracta para todos los diálogos de edición/creación.
 * Maneja la lógica común de Guardar/Cancelar, la validación y el manejo de errores.
 *
 * AJUSTE CLAVE: Ya no usa botones protected. Usa inyección de referencias (Setters)
 * para obtener las instancias de los botones que son declaradas como 'private' por NetBeans
 * en la clase hija.
 */
public abstract class BaseDialog extends JDialog {

    protected boolean guardado = false;
    protected static final Logger logger = Logger.getLogger(BaseDialog.class.getName());

    // Los botones ahora son privados en BaseDialog.
    private JButton btnGuardar;
    private JButton btnCancelar;

    // ===============================================================
    // CONSTRUCTORES
    // ===============================================================

    // Constructor para Frame (Compatibilidad)
    public BaseDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    // Constructor para Window (Recomendado para Swing)
    public BaseDialog(Window parent, ModalityType modalityType) {
        super(parent, modalityType);
    }

    // ===============================================================
    // MÉTODOS DE INYECCIÓN DE REFERENCIAS (Setters Públicos)
    // ===============================================================
    
    /**
     * Permite a la clase hija inyectar la referencia del botón 'Guardar'.
     */
    public void setBtnGuardar(JButton btnGuardar) {
        this.btnGuardar = btnGuardar;
    }

    /**
     * Permite a la clase hija inyectar la referencia del botón 'Cancelar'.
     */
    public void setBtnCancelar(JButton btnCancelar) {
        this.btnCancelar = btnCancelar;
    }

    // ===============================================================
    // LÓGICA DE BOTONES Y EVENTOS
    // ===============================================================

    /**
     * Configura los listeners de los botones, usando las referencias que deben
     * ser inyectadas por la clase hija.
     * DEBE SER LLAMADO por la clase hija inmediatamente después de initComponents().
     */
    protected void configurarBotonesBase() {
        if (this.btnGuardar == null || this.btnCancelar == null) {
            logger.severe("Las referencias de los botones no fueron inyectadas correctamente por la clase hija.");
            return;
        }
        
        // Añadir Listener al botón Guardar: gestiona la validación, persistencia y errores.
        this.btnGuardar.addActionListener(e -> {
            try {
                if (validarCampos()) {
                    guardarEntidad(); // Llama al método abstracto
                    guardado = true;
                    dispose(); // Cierra el diálogo tras el éxito
                }
            } catch (Exception ex) {
                logger.severe("Error al guardar entidad: " + ex.getMessage());
                mostrarError("Error al guardar: " + ex.getMessage());
            }
        });

        // Añadir Listener al botón Cancelar: simplemente cierra el diálogo.
        this.btnCancelar.addActionListener(e -> dispose());
    }
    
    // ===============================================================
    // MÉTODOS PÚBLICOS Y ABSTRACTOS
    // ===============================================================

    /**
     * Devuelve true si el diálogo se cerró con éxito después de guardar.
     */
    public boolean isGuardado() { return guardado; }

    /**
     * Método abstracto para que la clase hija implemente la validación de campos.
     * @return true si los campos son válidos.
     */
    protected abstract boolean validarCampos();
    
    /**
     * Método abstracto para que la clase hija implemente la lógica de persistencia (CRUD).
     * @throws Exception Si ocurre un error en la capa de servicio/DAO.
     */
    protected abstract void guardarEntidad() throws Exception;

    // ===============================================================
    // UTILIDADES DE INTERFAZ
    // ===============================================================

    protected void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    protected void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}