package com.smartoccupation.utilidades;

import javax.swing.JOptionPane;
import java.sql.*;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos MySQL.
 * <p>
 * Se encarga de:
 * <ul>
 * <li>Establecer la conexión con el servidor MySQL.</li>
 * <li>Verificar si la base de datos existe; si no, ofrece crearla.</li>
 * <li>Crear las tablas de la aplicación si no existen.</li>
 * <li>Insertar los datos iniciales necesarios (estados de cobro).</li>
 * </ul>
 * Esta clase es esencial para el inicio y la configuración de la aplicación.
 * </p>
 *
 * @author Alex Fernández
 * @version 1.0
 * @since 2025-11-27
 */
public class ConexionBBDD {

    private static final String DB_NAME = "smartOccupation_db";
    private static final String URL_SERVER = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String URL_DB = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "User1234";

    // Bandera para asegurar que la estructura (DB y tablas) solo se verifica una vez por ejecución.
    private static boolean estructuraVerificada = false;

    /**
     * Establece una conexión con la base de datos.
     * <p>
     * Este método gestiona el ciclo de vida de la conexión, incluyendo la
     * verificación y creación de la base de datos y las tablas si es necesario.
     * </p>
     *
     * @return Una conexión activa a la base de datos {@code smartOccupation_db}, o {@code null} si la conexión falla o es cancelada por el usuario.
     */
    public static Connection conectar() {
        LogManager.info("Aplicación iniciada"); // <-- LOG

        try {
            // 1. Conectar al servidor sin especificar DB para verificar su existencia
            Connection serverConnection = DriverManager.getConnection(URL_SERVER, USER, PASSWORD);
            LogManager.info("Conexión a MySQL correcta"); // <-- LOG

            if (!estructuraVerificada) {

                // 2. Verificar y/o crear la base de datos
                if (!existeBaseDeDatos(serverConnection)) {
                    LogManager.info("Base de datos no encontrada"); // <-- LOG

                    int respuesta = JOptionPane.showConfirmDialog(
                            null,
                            "La base de datos '" + DB_NAME + "' no existe.\n¿Deseas crearla ahora?",
                            "Base de datos no encontrada",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (respuesta == JOptionPane.YES_OPTION) {
                        crearBaseDeDatos(serverConnection);
                        LogManager.info("Base de datos creada correctamente"); // <-- LOG
                    } else {
                        LogManager.error("El usuario canceló la creación de la base de datos"); // <-- LOG
                        serverConnection.close();
                        return null;
                    }
                }
            }
            
            // Cerrar conexión al servidor antes de abrir la específica a la DB
            if (serverConnection != null && !serverConnection.isClosed()) {
                serverConnection.close();
            }

            // 3. Conectar a la base de datos específica
            Connection dbConnection = DriverManager.getConnection(URL_DB, USER, PASSWORD);

            if (!estructuraVerificada) {
                // 4. Crear tablas e insertar datos iniciales si no existen
                crearTablasSiNoExisten(dbConnection);
                insertarEstadosCobroIniciales(dbConnection);
                estructuraVerificada = true;
                LogManager.info("Estructura de la base de datos verificada y actualizada"); // <-- LOG
            }

            return dbConnection;

        } catch (SQLException e) {
            LogManager.error("Error al conectar a MySQL", e); // <-- LOG

            // Mostrar error amigable al usuario
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar a MySQL.\n"
                    + "Verifica que MySQL esté instalado y en ejecución, y que las credenciales sean correctas.\n\n"
                    + "Detalles: " + e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
    }

    /**
     * Verifica si la base de datos {@code DB_NAME} ya existe en el servidor MySQL.
     *
     * @param conn La conexión al servidor MySQL (sin especificar base de datos).
     * @return {@code true} si la base de datos existe, {@code false} en caso contrario.
     * @throws SQLException Si ocurre un error al obtener metadatos.
     */
    private static boolean existeBaseDeDatos(Connection conn) throws SQLException {
        ResultSet rs = conn.getMetaData().getCatalogs();
        while (rs.next()) {
            if (DB_NAME.equalsIgnoreCase(rs.getString(1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea la base de datos {@code DB_NAME} en el servidor MySQL.
     *
     * @param conn La conexión al servidor MySQL (sin especificar base de datos).
     */
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

    /**
     * Crea todas las tablas de la aplicación si no existen, utilizando `CREATE TABLE IF NOT EXISTS`.
     *
     * @param conn La conexión a la base de datos {@code DB_NAME}.
     */
    public static void crearTablasSiNoExisten(Connection conn) {
        try (Statement stmt = conn.createStatement()) {

            // Tabla CLIENTES
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

            // Tabla VIVIENDAS
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

            // Tabla ESTADOS_COBRO (Tabla de Catálogo)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS estados_cobro (
                    id_estado INT AUTO_INCREMENT PRIMARY KEY,
                    nombre_estado ENUM('pagado','pendiente','retrasado') UNIQUE NOT NULL
                );
            """);

            // Tabla ALQUILERES (Depende de clientes, viviendas y estados_cobro)
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

            // Tabla PAGOS (Depende de alquileres)
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

    /**
     * Inserta los valores iniciales para la tabla de catálogo {@code estados_cobro}
     * si aún no existen (utiliza {@code INSERT IGNORE}).
     *
     * @param conn La conexión a la base de datos {@code DB_NAME}.
     */
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
    
    /**
     * Método auxiliar público para fines de prueba (tests) que permite
     * forzar la inserción de estados iniciales.
     *
     * @param conn Conexión a la base de datos.
     */
    public static void insertarEstadosInicialesPublic(Connection conn) {
        ConexionBBDD.insertarEstadosCobroIniciales(conn);
    }
}