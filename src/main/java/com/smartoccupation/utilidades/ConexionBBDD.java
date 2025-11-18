package com.smartoccupation.utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {
    private static final String URL = "jdbc:mysql://localhost:3306/smartOccupation_db";
    private static final String USER = "root";
    private static final String PASSWORD = "User1234";

    /**
     * Método estático para obtener una nuvea conexión a la base de datos.
     * @return Objeto Connection activo
     * @exception SQLException si la conexión es fallida
     */
    public static Connection conectar() {
        try {
            // DriverManager se encarga de buscar el driver adecuado para la conexión
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }

}