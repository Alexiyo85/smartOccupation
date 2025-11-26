package com.smartoccupation.utilidades;

import javax.swing.JOptionPane;
import java.sql.*;

public class ConexionBBDD {

    private static final String DB_NAME = "smartOccupation_db";
    private static final String URL_SERVER = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String URL_DB = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "User1234";

    private static boolean estructuraVerificada = false;

    public static Connection conectar() {
        LogManager.info("Aplicación iniciada"); // <-- LOG

        try {
            Connection serverConnection = DriverManager.getConnection(URL_SERVER, USER, PASSWORD);
            LogManager.info("Conexión a MySQL correcta"); // <-- LOG

            if (!estructuraVerificada) {

                if (!existeBaseDeDatos(serverConnection)) {
                    LogManager.info("Base de datos no encontrada"); // <-- LOG

                    int respuesta = JOptionPane.showConfirmDialog(
                            null,
                            "La base de datos no existe.\n¿Deseas crearla ahora?",
                            "Base de datos no encontrada",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (respuesta == JOptionPane.YES_OPTION) {
                        crearBaseDeDatos(serverConnection);
                        LogManager.info("Base de datos creada correctamente"); // <-- LOG
                    } else {
                        LogManager.error("El usuario canceló la creación de la base de datos"); // <-- LOG
                        return null;
                    }
                }
            }

            Connection dbConnection = DriverManager.getConnection(URL_DB, USER, PASSWORD);

            if (!estructuraVerificada) {
                crearTablasSiNoExisten(dbConnection);
                insertarEstadosCobroIniciales(dbConnection);
                estructuraVerificada = true;
                LogManager.info("Tablas verificadas"); // <-- LOG
            }

            return dbConnection;

        } catch (SQLException e) {
            LogManager.error("Error al conectar a MySQL", e); // <-- LOG

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar a MySQL.\n"
                    + "Verifica que MySQL esté instalado y en ejecución.\n\n"
                    + "Detalles: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    private static boolean existeBaseDeDatos(Connection conn) throws SQLException {
        ResultSet rs = conn.getMetaData().getCatalogs();
        while (rs.next()) {
            if (DB_NAME.equalsIgnoreCase(rs.getString(1))) {
                return true;
            }
        }
        return false;
    }

    private static void crearBaseDeDatos(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE " + DB_NAME);
        } catch (SQLException e) {
            LogManager.error("Error al crear la base de datos", e); // <-- LOG

            JOptionPane.showMessageDialog(null,
                    "Error al crear la base de datos:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void crearTablasSiNoExisten(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clientes (
                    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(45) NOT NULL,
                    primer_apellido VARCHAR(30),
                    segundo_apellido VARCHAR(30),
                    dni_nif VARCHAR(15) UNIQUE,
                    telefono VARCHAR(15),
                    email VARCHAR(60),
                    direccion VARCHAR(80),
                    ciudad VARCHAR(40),
                    provincia VARCHAR(40),
                    codigo_postal VARCHAR(10)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS viviendas (
                    id_vivienda INT AUTO_INCREMENT PRIMARY KEY,
                    codigo_referencia VARCHAR(20) UNIQUE NOT NULL,
                    direccion VARCHAR(80) NOT NULL,
                    ciudad VARCHAR(40),
                    provincia VARCHAR(40),
                    codigo_postal VARCHAR(10),
                    metros_cuadrados INT,
                    numero_habitaciones INT,
                    numero_banios INT,
                    precio_mensual DECIMAL(10,2) NOT NULL,
                    estado ENUM('disponible','reservado','ocupado') DEFAULT 'disponible'
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS estados_cobro (
                    id_estado INT AUTO_INCREMENT PRIMARY KEY,
                    nombre_estado ENUM('pagado','pendiente','retrasado') UNIQUE NOT NULL
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS alquileres (
                    numero_expediente INT AUTO_INCREMENT PRIMARY KEY,
                    fecha_inicio DATE NOT NULL,
                    tiempo_meses INT DEFAULT 0,
                    tiempo_dias INT DEFAULT 0,
                    fecha_fin_estimada DATE,
                    precio_total_estimado DECIMAL(10,2),
                    id_cliente INT NOT NULL,
                    id_vivienda INT NOT NULL,
                    id_estado_cobro INT NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
                    FOREIGN KEY (id_vivienda) REFERENCES viviendas(id_vivienda),
                    FOREIGN KEY (id_estado_cobro) REFERENCES estados_cobro(id_estado)
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pagos (
                    id_pago INT AUTO_INCREMENT PRIMARY KEY,
                    numero_expediente INT NOT NULL,
                    fecha_pago DATE NOT NULL,
                    cantidad DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (numero_expediente) REFERENCES alquileres(numero_expediente)
                );
            """);

        } catch (SQLException e) {
            LogManager.error("Error al crear/verificar las tablas", e); // <-- LOG

            JOptionPane.showMessageDialog(null,
                    "Error al crear/verificar las tablas:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void insertarEstadosCobroIniciales(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
                INSERT IGNORE INTO estados_cobro (id_estado, nombre_estado)
                VALUES 
                    (1, 'pagado'),
                    (2, 'pendiente'),
                    (3, 'retrasado');
            """);

            LogManager.info("Estados de cobro iniciales insertados"); // <-- LOG

        } catch (SQLException e) {
            LogManager.error("Error al insertar estados de cobro iniciales", e); // <-- LOG

            JOptionPane.showMessageDialog(null,
                    "Error al insertar estados de cobro iniciales:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
