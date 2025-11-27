package com.smartoccupation.utilidades;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase de utilidad estática para la gestión de logs (registros) de la aplicación. 📝
 * <p>
 * Se encarga de formatear mensajes con una marca de tiempo, nivel de severidad (INFO, ERROR)
 * y escribirlos en un archivo de log predefinido (app.log).
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class LogManager {

    private static final String LOG_FILE = "app.log";

    /**
     * Genera una marca de tiempo con formato "yyyy-MM-dd HH:mm:ss".
     *
     * @return El {@code String} que representa la fecha y hora actual formateada.
     */
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Método central para escribir un mensaje en el archivo de log.
     * Utiliza {@code FileWriter(LOG_FILE, true)} para asegurar que los mensajes
     * se añaden al final del archivo existente (modo append).
     *
     * @param level El nivel de severidad del mensaje (e.g., "INFO", "ERROR").
     * @param message El contenido textual del mensaje a registrar.
     */
    private static void write(String level, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(getTimestamp() + " [" + level + "] " + message);
        } catch (Exception e) {
            // Si falla la escritura del log, se imprime un error en la consola del sistema (System.err).
            System.err.println("No se pudo escribir en el log: " + e.getMessage());
        }
    }

    /**
     * Registra un mensaje informativo de nivel INFO.
     *
     * @param message El mensaje a registrar.
     */
    public static void info(String message) {
        write("INFO", message);
    }

    /**
     * Registra un mensaje de error sin detalles de excepción.
     *
     * @param message El mensaje de error a registrar.
     */
    public static void error(String message) {
        write("ERROR", message);
    }

    /**
     * Registra un mensaje de error que incluye detalles de una excepción capturada.
     *
     * @param message El mensaje descriptivo del error.
     * @param ex La excepción que se produjo, cuya información (mensaje) se añade al registro.
     */
    public static void error(String message, Exception ex) {
        write("ERROR", message + " - Causa: " + ex.getMessage());
    }
}
