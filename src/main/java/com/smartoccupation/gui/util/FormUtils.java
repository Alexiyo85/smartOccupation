package com.smartoccupation.gui.util;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableColumnModel; // Necesario para ocultar columna
import java.math.BigDecimal;
import java.text.DecimalFormat; // Necesario para formatear BigDecimal
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Pattern;

public class FormUtils {

    // Formato para mostrar dinero (ej: 1,234.56)
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    // ===============================================================
    // PARSEO Y VALIDACIÓN
    // ===============================================================

    public static BigDecimal parseBigDecimal(String text, String fieldName) throws IllegalArgumentException {
        try {
            // Reemplaza coma por punto para el parsing internacional
            return new BigDecimal(text.trim().replace(",", "."));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cantidad inválida en " + fieldName);
        }
    }

    public static int parseInt(String text, String fieldName) throws IllegalArgumentException {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Número inválido en " + fieldName);
        }
    }

    public static BigDecimal parseBigDecimalOrNull(String text, String fieldName) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return parseBigDecimal(text, fieldName);
    }

    public static LocalDate parseFecha(Date date, String fieldName) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("Debe seleccionar " + fieldName);
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    // ===============================================================
    // FORMATO
    // ===============================================================
    
    /**
     * Formatea un BigDecimal a una cadena con formato monetario.
     * Ejemplo: 1234.56 -> "1.234,56" o "1,234.56" (depende del locale)
     */
    public static String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "";
        }
        // Usamos el formato definido arriba
        return DECIMAL_FORMAT.format(value);
    }

    // ===============================================================
    // UTILIDADES DE UI
    // ===============================================================

    /**
     * Selecciona un ítem en un JComboBox basándose en el objeto, o lo deja vacío si no lo encuentra.
     */
    public static <T> void seleccionarItem(JComboBox<T> combo, T item) {
        if (item == null) {
            combo.setSelectedIndex(-1);
        } else {
            // Intentamos seleccionar el item directamente
            // Nota: Esto funciona mejor si la clase T (ej: Cliente, Vivienda) tiene 
            // implementado correctamente equals() y hashCode().
            combo.setSelectedItem(item);
            
            // Si el item no fue encontrado, setSelectedItem() no lanza excepción, 
            // simplemente no cambia la selección. No necesitamos el try-catch aquí.
        }
    }
    
    /**
     * Oculta una columna específica de un JTable (ej: para ocultar el ID).
     */
    public static void ocultarColumna(JTable table, int columnIndex) {
        if (table.getColumnModel().getColumnCount() > columnIndex) {
            TableColumnModel tcm = table.getColumnModel();
            // Removemos la columna del modelo de vista
            tcm.removeColumn(tcm.getColumn(columnIndex));
        }
    }

    // ===============================================================
    // MENSAJES DE DIÁLOGO
    // ===============================================================

    public static void mostrarError(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarInfo(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarAdvertencia(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    public static void validarEmail(String email) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            // Permitimos vacío si el campo no es obligatorio, pero si tiene texto, lo validamos.
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

}