package com.smartoccupation.gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Clase base abstracta para todos los diálogos de edición/creación. 🏗️
 * Proporciona la estructura común para la gestión de formularios Swing,
 * incluyendo: 1. Manejo de la lógica de Guardar/Cancelar. 2. Validación de
 * campos (método abstracto). 3. Persistencia de la entidad (método abstracto).
 * 4. Manejo de errores centralizado. * AJUSTE CLAVE: Ya no usa botones
 * protected. Usa inyección de referencias (Setters) para obtener las instancias
 * de los botones que son declaradas como 'private' por NetBeans en la clase
 * hija, solucionando problemas de visibilidad.
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public abstract class BaseDialog extends JDialog {

    // Bandera para indicar si el diálogo se cerró después de una operación de guardado exitosa.
    protected boolean guardado = false;
    // Logger estático para registrar errores y eventos importantes.
    protected static final Logger logger = Logger.getLogger(BaseDialog.class.getName());

    // Los botones ahora son privados en BaseDialog. Las clases hijas deben inyectarlos.
    private JButton btnGuardar;
    private JButton btnCancelar;

    // ===============================================================
    // CONSTRUCTORES
    // ===============================================================
    /**
     * Constructor para Frame (Compatibilidad).
     *
     * @param parent El marco padre.
     * @param modal Si el diálogo debe ser modal.
     */
    public BaseDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Constructor para Window (Recomendado para Swing).
     *
     * @param parent La ventana padre.
     * @param modalityType El tipo de modalidad (ej: APPLICATION_MODAL).
     */
    public BaseDialog(Window parent, ModalityType modalityType) {
        super(parent, modalityType);
    }

    // ===============================================================
    // MÉTODOS DE INYECCIÓN DE REFERENCIAS (Setters Públicos)
    // ===============================================================
    /**
     * Permite a la clase hija inyectar la referencia del botón 'Guardar'.
     *
     * @param btnGuardar La instancia del botón Guardar de la clase hija.
     */
    public void setBtnGuardar(JButton btnGuardar) {
        this.btnGuardar = btnGuardar;
    }

    /**
     * Permite a la clase hija inyectar la referencia del botón 'Cancelar'.
     *
     * @param btnCancelar La instancia del botón Cancelar de la clase hija.
     */
    public void setBtnCancelar(JButton btnCancelar) {
        this.btnCancelar = btnCancelar;
    }

    // ===============================================================
    // LÓGICA DE BOTONES Y EVENTOS
    // ===============================================================
    /**
     * Configura los listeners de los botones, usando las referencias que deben
     * ser inyectadas por la clase hija (mediante setBtnGuardar y
     * setBtnCancelar). DEBE SER LLAMADO por la clase hija inmediatamente
     * después de initComponents().
     */
    protected void configurarBotonesBase() {
        // Validación de inyección de botones.
        if (this.btnGuardar == null || this.btnCancelar == null) {
            logger.severe("Las referencias de los botones no fueron inyectadas correctamente por la clase hija.");
            return;
        }

        // Añadir Listener al botón Guardar: gestiona la validación, persistencia y errores.
        this.btnGuardar.addActionListener(e -> {
            try {
                // 1. Validar campos usando el método implementado por la clase hija.
                if (validarCampos()) {
                    guardarEntidad(); // 2. Llama al método abstracto de persistencia.
                    guardado = true; // 3. Marca la operación como exitosa.
                    dispose(); // 4. Cierra el diálogo tras el éxito.
                }
            } catch (Exception ex) {
                // 5. Captura cualquier excepción en la capa de persistencia y la registra/muestra.
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
     * Devuelve true si el diálogo se cerró con éxito después de guardar (es
     * decir, si la entidad fue persistida).
     *
     * @return true si se guardó, false en caso contrario.
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Método abstracto para que la clase hija implemente la validación de
     * campos. Esta es la primera comprobación antes de intentar guardar.
     *
     * @return true si los campos son válidos.
     */
    protected abstract boolean validarCampos();

    /**
     * Método abstracto para que la clase hija implemente la lógica de
     * persistencia (CRUD). Contiene la lógica para crear o actualizar la
     * entidad de negocio.
     *
     * @throws Exception Si ocurre un error en la capa de servicio/DAO.
     */
    protected abstract void guardarEntidad() throws Exception;

    // ===============================================================
    // UTILIDADES DE INTERFAZ
    // ===============================================================
    /**
     * Muestra un mensaje de error estándar al usuario.
     *
     * @param mensaje El texto del error.
     */
    protected void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un mensaje de advertencia estándar al usuario.
     *
     * @param mensaje El texto de la advertencia.
     */
    protected void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}
