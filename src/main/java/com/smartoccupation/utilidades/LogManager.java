package com.smartoccupation.utilidades;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private static final String LOG_FILE = "app.log";

    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static void write(String level, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(getTimestamp() + " [" + level + "] " + message);
        } catch (Exception e) {
            System.err.println("No se pudo escribir en el log: " + e.getMessage());
        }
    }

    public static void info(String message) {
        write("INFO", message);
    }

    public static void error(String message) {
        write("ERROR", message);
    }

    public static void error(String message, Exception ex) {
        write("ERROR", message + " - " + ex.getMessage());
    }
}
