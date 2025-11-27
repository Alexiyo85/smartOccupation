package com.smartoccupation.gui.util;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableColumnModel; 
import java.math.BigDecimal;
import java.text.DecimalFormat; 
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Clase de utilidad estática para la gestión de formularios en la GUI. 🛠️
 * <p>
 * Proporciona métodos para:
 * <ul>
 * <li>Parsear y validar datos de entrada (String a BigDecimal, int, LocalDate).</li>
 * <li>Formatear valores para su presentación (ej: BigDecimal a String monetario).</li>
 * <li>Manejar componentes de la GUI (ComboBox, JTable).</li>
 * <li>Mostrar mensajes de diálogo estandarizados (Error, Info, Advertencia).</li>
 * </ul>
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class FormUtils {

    // Formato para mostrar dinero (ej: 1,234.56, aunque puede variar por Locale)
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    // Patrón regex para validación básica de correo electrónico
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    // ===============================================================
    // PARSEO Y VALIDACIÓN
    // ===============================================================

    /**
     * Convierte una cadena de texto a un {@code BigDecimal}.
     *
     * @param text La cadena de texto a parsear.
     * @param fieldName El nombre del campo para usar en los mensajes de error.
     * @return El objeto {@code BigDecimal} resultante.
     * @throws IllegalArgumentException Si la cadena no representa un número válido.
     */
    public static BigDecimal parseBigDecimal(String text, String fieldName) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
             throw new IllegalArgumentException(fieldName + " no puede estar vacío.");
        }
        try {
            // Reemplaza coma por punto para el parsing internacional y elimina espacios.
            return new BigDecimal(text.trim().replace(",", "."));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cantidad inválida en " + fieldName + ". Debe ser un número.");
        }
    }

    /**
     * Convierte una cadena de texto a un entero primitivo ({@code int}).
     *
     * @param text La cadena de texto a parsear.
     * @param fieldName El nombre del campo para usar en los mensajes de error.
     * @return El valor {@code int} resultante.
     * @throws IllegalArgumentException Si la cadena no representa un entero válido.
     */
    public static int parseInt(String text, String fieldName) throws IllegalArgumentException {
         if (text == null || text.trim().isEmpty()) {
             throw new IllegalArgumentException(fieldName + " no puede estar vacío.");
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Número inválido en " + fieldName + ". Debe ser un número entero.");
        }
    }

    /**
     * Convierte una cadena de texto a un {@code BigDecimal}. Si la cadena es nula o vacía, devuelve {@code null}.
     *
     * @param text La cadena de texto a parsear.
     * @param fieldName El nombre del campo para usar en los mensajes de error si no es nulo/vacío.
     * @return El objeto {@code BigDecimal} resultante, o {@code null}.
     * @throws IllegalArgumentException Si la cadena no está vacía y no representa un número válido.
     */
    public static BigDecimal parseBigDecimalOrNull(String text, String fieldName) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return parseBigDecimal(text, fieldName);
    }

    /**
     * Convierte un objeto {@code java.util.Date} (típico de componentes de selección de fecha) a un {@code java.time.LocalDate}.
     *
     * @param date El objeto {@code Date} a convertir.
     * @param fieldName El nombre del campo para usar en los mensajes de error.
     * @return El objeto {@code LocalDate} resultante.
     * @throws IllegalArgumentException Si el objeto {@code Date} es nulo.
     */
    public static LocalDate parseFecha(Date date, String fieldName) throws IllegalArgumentException {
        if (date == null) {
            throw new IllegalArgumentException("Debe seleccionar " + fieldName);
        }
        // Conversión segura de java.util.Date a java.time.LocalDate
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * Valida si una cadena de texto es un formato de email válido.
     *
     * @param email La cadena de email a validar.
     * @throws IllegalArgumentException Si la cadena de email no cumple con el patrón regex, a menos que esté vacía.
     */
     public static void validarEmail(String email) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            // Permitimos nulo o vacío
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido. Asegúrese de que tenga el formato correcto (ej: nombre@dominio.com).");
        }
    }

    // ===============================================================
    // FORMATO
    // ===============================================================
    
    /**
     * Formatea un {@code BigDecimal} a una cadena con formato monetario
     * (dos decimales y separador de miles/decimal según el Locale definido).
     *
     * @param value El valor {@code BigDecimal} a formatear.
     * @return Una cadena formateada o una cadena vacía si el valor es {@code null}.
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
     * Intenta seleccionar un ítem en un {@code JComboBox} basándose en la instancia del objeto.
     *
     * @param <T> El tipo de objeto contenido en el ComboBox.
     * @param combo El {@code JComboBox} donde realizar la selección.
     * @param item El objeto a seleccionar. Si es nulo, deselecciona el ComboBox.
     */
    public static <T> void seleccionarItem(JComboBox<T> combo, T item) {
        if (item == null) {
            combo.setSelectedIndex(-1); // Deseleccionar
        } else {
            // Esto requiere que la clase T tenga implementado correctamente equals()
            combo.setSelectedItem(item);
        }
    }
    
    /**
     * Oculta una columna específica de un {@code JTable} (útil para ocultar columnas de ID/PK).
     *
     * @param table El {@code JTable} del que se desea ocultar la columna.
     * @param columnIndex El índice (cero basado) de la columna a ocultar.
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

    /**
     * Muestra un diálogo de error al usuario.
     *
     * @param parent El componente padre que determina la posición del diálogo.
     * @param mensaje El texto del mensaje de error.
     */
    public static void mostrarError(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra un diálogo informativo al usuario.
     *
     * @param parent El componente padre que determina la posición del diálogo.
     * @param mensaje El texto del mensaje informativo.
     */
    public static void mostrarInfo(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra un diálogo de advertencia al usuario.
     *
     * @param parent El componente padre que determina la posición del diálogo.
     * @param mensaje El texto del mensaje de advertencia.
     */
    public static void mostrarAdvertencia(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }
}