package smartoccupationTest.utilidades;

import com.smartoccupation.utilidades.ConexionBBDD;
import com.smartoccupation.utilidades.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;
import java.sql.*;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ConexionBBDDTest {

    private Connection mockConn;
    private Statement mockStmt;

    @BeforeEach
    void setUp() throws SQLException {
        mockConn = mock(Connection.class);
        mockStmt = mock(Statement.class);
        when(mockConn.createStatement()).thenReturn(mockStmt);
    }

    @Test
    void testInsertarEstadosInicialesPublic_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> ConexionBBDD.insertarEstadosInicialesPublic(mockConn));
        try {
            verify(mockConn.createStatement(), times(1)).executeUpdate(anyString());
        } catch (SQLException e) {
            fail("No debería lanzar SQLException al verificar executeUpdate");
        }
    }

    @Test
    void testInsertarEstadosInicialesPublic_SQLException() throws SQLException {
        when(mockStmt.executeUpdate(anyString())).thenThrow(new SQLException("Error simulado"));
        assertDoesNotThrow(() -> ConexionBBDD.insertarEstadosInicialesPublic(mockConn));
    }

    @Test
    void testCrearTablasSiNoExisten_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> ConexionBBDD.crearTablasSiNoExisten(mockConn));
    }

    @Test
    void testCrearTablasSiNoExisten_SQLException() throws SQLException {
        when(mockConn.createStatement()).thenThrow(new SQLException("Error simulado"));
        assertDoesNotThrow(() -> ConexionBBDD.crearTablasSiNoExisten(mockConn));
    }

    @Test
    void testConectar_BaseDatosNoExiste_UsuarioCancela() throws Exception {
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<JOptionPane> jOptionPaneMock = mockStatic(JOptionPane.class);
             MockedStatic<LogManager> logMock = mockStatic(LogManager.class)) {

            // Mock conexión al servidor
            Connection mockServerConn = mock(Connection.class);
            driverManagerMock.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true"), 
                eq("root"), 
                eq("User1234")))
                    .thenReturn(mockServerConn);

            // Mock que no existe la BD
            ResultSet mockRs = mock(ResultSet.class);
            DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
            when(mockServerConn.getMetaData()).thenReturn(mockMetaData);
            when(mockMetaData.getCatalogs()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false); // No existe la BD

            // Usuario cancela
            jOptionPaneMock.when(() -> JOptionPane.showConfirmDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn(JOptionPane.NO_OPTION);

            Connection result = ConexionBBDD.conectar();
            assertNull(result, "Debe retornar null cuando el usuario cancela");
        }
    }

    @Test
    void testExisteBaseDeDatos_SQLException() throws Exception {
        try (MockedStatic<LogManager> logMock = mockStatic(LogManager.class)) {
            Connection mockConn = mock(Connection.class);
            DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
            
            when(mockConn.getMetaData()).thenReturn(mockMetaData);
            when(mockMetaData.getCatalogs()).thenThrow(new SQLException("Error simulado"));

            // Usamos reflection para probar el método privado
            Method method = ConexionBBDD.class.getDeclaredMethod("existeBaseDeDatos", Connection.class);
            method.setAccessible(true);
            
            // El método lanza SQLException, así que esperamos la excepción
            Exception exception = assertThrows(Exception.class, () -> {
                method.invoke(null, mockConn);
            });
            
            // Verificamos que la causa es SQLException
            assertTrue(exception.getCause() instanceof SQLException);
            assertEquals("Error simulado", exception.getCause().getMessage());
        }
    }

    @Test
    void testCrearBaseDeDatos_SQLException() throws Exception {
        try (MockedStatic<LogManager> logMock = mockStatic(LogManager.class);
             MockedStatic<JOptionPane> jOptionPaneMock = mockStatic(JOptionPane.class)) {

            Connection mockConn = mock(Connection.class);
            when(mockConn.createStatement()).thenThrow(new SQLException("Error simulado"));

            // Usamos reflection para probar el método privado
            Method method = ConexionBBDD.class.getDeclaredMethod("crearBaseDeDatos", Connection.class);
            method.setAccessible(true);
            
            // El método maneja la excepción internamente, así que no debería lanzarla
            assertDoesNotThrow(() -> method.invoke(null, mockConn));
            
            // Verificar que se llamó a LogManager.error
            logMock.verify(() -> LogManager.error(anyString(), any(SQLException.class)), times(1));
        }
    }

    @Test
    void testConectar_BaseDatosExiste_ConexionExitosa() throws Exception {
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<LogManager> logMock = mockStatic(LogManager.class)) {

            // Mock conexión al servidor
            Connection mockServerConn = mock(Connection.class);
            Connection mockDbConn = mock(Connection.class);

            driverManagerMock.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true"), 
                eq("root"), 
                eq("User1234")))
                    .thenReturn(mockServerConn);
            
            driverManagerMock.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/smartOccupation_db?useSSL=false&allowPublicKeyRetrieval=true"), 
                eq("root"), 
                eq("User1234")))
                    .thenReturn(mockDbConn);

            // Mock que SÍ existe la BD
            ResultSet mockRs = mock(ResultSet.class);
            DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
            when(mockServerConn.getMetaData()).thenReturn(mockMetaData);
            when(mockMetaData.getCatalogs()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false); // Existe la BD
            when(mockRs.getString(1)).thenReturn("smartOccupation_db");

            // Mock Statement para los métodos que se ejecutarán
            Statement mockStmtForTables = mock(Statement.class);
            when(mockDbConn.createStatement()).thenReturn(mockStmtForTables);
            
            Connection result = ConexionBBDD.conectar();
            assertNotNull(result, "Debe retornar conexión cuando BD existe");
        }
    }

    @Test
    void testConectar_SQLException_Inicial() throws Exception {
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<LogManager> logMock = mockStatic(LogManager.class);
             MockedStatic<JOptionPane> jOptionPaneMock = mockStatic(JOptionPane.class)) {

            // Simular error en la primera conexión
            SQLException sqlException = new SQLException("Error de conexión");
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenThrow(sqlException);

            Connection result = ConexionBBDD.conectar();
            assertNull(result, "Debe retornar null cuando hay SQLException inicial");

            // Verificar que se mostró el mensaje de error
            jOptionPaneMock.verify(() -> 
                JOptionPane.showMessageDialog(
                    any(), 
                    contains("No se pudo conectar a MySQL"), 
                    eq("Error de conexión"), 
                    eq(JOptionPane.ERROR_MESSAGE)
                ), 
                times(1)
            );
        }
    }

    @Test 
    void testConectar_BaseDatosNoExiste_UsuarioAcepta() throws Exception {
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<JOptionPane> jOptionPaneMock = mockStatic(JOptionPane.class);
             MockedStatic<LogManager> logMock = mockStatic(LogManager.class)) {

            // Mock conexiones
            Connection mockServerConn = mock(Connection.class);
            Connection mockDbConn = mock(Connection.class);
            
            driverManagerMock.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true"), 
                anyString(), 
                anyString()))
                    .thenReturn(mockServerConn);
            
            driverManagerMock.when(() -> DriverManager.getConnection(
                eq("jdbc:mysql://localhost:3306/smartOccupation_db?useSSL=false&allowPublicKeyRetrieval=true"), 
                anyString(), 
                anyString()))
                    .thenReturn(mockDbConn);

            // Mock que no existe la BD
            ResultSet mockRs = mock(ResultSet.class);
            DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
            when(mockServerConn.getMetaData()).thenReturn(mockMetaData);
            when(mockMetaData.getCatalogs()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            // Usuario acepta crear BD
            jOptionPaneMock.when(() -> JOptionPane.showConfirmDialog(any(), anyString(), anyString(), anyInt()))
                    .thenReturn(JOptionPane.YES_OPTION);

            // Mock Statement para crear BD
            Statement mockStmtForCreate = mock(Statement.class);
            when(mockServerConn.createStatement()).thenReturn(mockStmtForCreate);
            
            // Mock Statement para crear tablas
            Statement mockStmtForTables = mock(Statement.class);
            when(mockDbConn.createStatement()).thenReturn(mockStmtForTables);
                
            Connection result = ConexionBBDD.conectar();
            assertNotNull(result, "Debe retornar conexión cuando usuario acepta crear BD");
            
            // Verificar que se llamó a executeUpdate para crear BD
            verify(mockStmtForCreate, times(1)).executeUpdate("CREATE DATABASE smartOccupation_db");
        }
    }

    // Test para cubrir el caso donde estructuraVerificada = true
    @Test
    void testConectar_EstructuraYaVerificada() throws Exception {
        try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class);
             MockedStatic<LogManager> logMock = mockStatic(LogManager.class)) {

            // Primera llamada - establece estructuraVerificada = true
            Connection mockServerConn = mock(Connection.class);
            Connection mockDbConn = mock(Connection.class);
            
            driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockServerConn)
                    .thenReturn(mockDbConn);

            // BD existe
            ResultSet mockRs = mock(ResultSet.class);
            DatabaseMetaData mockMetaData = mock(DatabaseMetaData.class);
            when(mockServerConn.getMetaData()).thenReturn(mockMetaData);
            when(mockMetaData.getCatalogs()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getString(1)).thenReturn("smartOccupation_db");

            // Mock Statement para la primera llamada
            Statement mockStmtForFirstCall = mock(Statement.class);
            when(mockDbConn.createStatement()).thenReturn(mockStmtForFirstCall);
                
            // Primera llamada - estructura NO verificada
            Connection result1 = ConexionBBDD.conectar();
            assertNotNull(result1);
            
            // Segunda llamada - estructura YA verificada
            Connection result2 = ConexionBBDD.conectar();
            assertNotNull(result2);
        }
    }

    // Test adicional para insertarEstadosCobroIniciales con SQLException
    @Test
    void testInsertarEstadosCobroIniciales_SQLException() throws Exception {
        try (MockedStatic<LogManager> logMock = mockStatic(LogManager.class);
             MockedStatic<JOptionPane> jOptionPaneMock = mockStatic(JOptionPane.class)) {

            Connection mockConn = mock(Connection.class);
            when(mockConn.createStatement()).thenThrow(new SQLException("Error simulado"));

            // Usamos reflection para probar el método privado
            Method method = ConexionBBDD.class.getDeclaredMethod("insertarEstadosCobroIniciales", Connection.class);
            method.setAccessible(true);
            
            // Debe manejar la excepción internamente
            assertDoesNotThrow(() -> method.invoke(null, mockConn));

            // Verificar que se llamó a LogManager.error
            logMock.verify(() -> LogManager.error(anyString(), any(SQLException.class)), times(1));
        }
    }

    // Test para crearBaseDeDatos exitoso
    @Test
    void testCrearBaseDeDatos_Exitoso() throws Exception {
        try (MockedStatic<LogManager> logMock = mockStatic(LogManager.class)) {
            Connection mockConn = mock(Connection.class);
            Statement mockStmt = mock(Statement.class);
            when(mockConn.createStatement()).thenReturn(mockStmt);

            // Usamos reflection para probar el método privado
            Method method = ConexionBBDD.class.getDeclaredMethod("crearBaseDeDatos", Connection.class);
            method.setAccessible(true);
            
            // Debe ejecutarse sin excepciones
            assertDoesNotThrow(() -> method.invoke(null, mockConn));
            
            // Verificar que se llamó a executeUpdate
            verify(mockStmt, times(1)).executeUpdate("CREATE DATABASE smartOccupation_db");
        }
    }
}