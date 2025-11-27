package smartoccupationTest.dao;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AlquilerDAOTest {

    private AlquilerDAO dao;

    // Mocks JDBC necesarios para stubbing
    private Connection mockConn;
    private PreparedStatement mockPs;
    private Statement mockStatement;
    private ResultSet mockRs; // General ResultSet (para SELECTs)
    private ResultSet mockRsGen; // Generated Keys ResultSet (para INSERTs)

    @BeforeEach
    void setUp() {
        dao = new AlquilerDAO();

        // Inicializar los mocks de JDBC
        mockConn = mock(Connection.class);
        mockPs = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);
        mockRs = mock(ResultSet.class);
        mockRsGen = mock(ResultSet.class);
    }

    // Helper: construir un Alquiler básico
    private Alquiler createAlquilerBase() {
        Alquiler a = new Alquiler();
        a.setFechaInicio(LocalDate.of(2024, 1, 1));
        a.setTiempoMeses(1);
        a.setTiempoDias(10);
        a.setFechaFinEstimada(a.getFechaInicio().plusMonths(1).plusDays(10)); // 2024-02-11
        a.setPrecioTotalEstimado(new BigDecimal("500.00"));
        a.setIdCliente(2);
        a.setIdVivienda(3);
        a.setIdEstadoCobro(1);
        return a;
    }

    // Helper: Mockear los resultados de una fila de Alquiler para el ResultSet
    private void mockResultadoAlquiler(ResultSet rs, int expediente, LocalDate fechaInicio, LocalDate fechaFinEstimada, BigDecimal precio, int idCliente, int idVivienda, int idEstado) throws SQLException {
        when(rs.getInt("numero_expediente")).thenReturn(expediente);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(fechaInicio));
        when(rs.getInt("tiempo_meses")).thenReturn(1);
        when(rs.getInt("tiempo_dias")).thenReturn(10);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(fechaFinEstimada != null ? Date.valueOf(fechaFinEstimada) : null);
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(precio);
        when(rs.getInt("id_cliente")).thenReturn(idCliente);
        when(rs.getInt("id_vivienda")).thenReturn(idVivienda);
        when(rs.getInt("id_estado_cobro")).thenReturn(idEstado);
    }

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================

    @Test
    void insertar_devuelveTrue_y_seteaNumeroExpediente_conFechaFin() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);
            when(mockPs.getGeneratedKeys()).thenReturn(mockRsGen);
            when(mockRsGen.next()).thenReturn(true);
            when(mockRsGen.getInt(1)).thenReturn(123);

            Alquiler a = createAlquilerBase();
            boolean result = dao.insertar(a);

            assertTrue(result);
            assertEquals(123, a.getNumeroExpediente());
            verify(mockPs).setDate(eq(4), eq(Date.valueOf(a.getFechaFinEstimada()))); // Verifica fecha fin NO nula
        }
    }

    @Test
    void insertar_devuelveTrue_y_manejaFechaFinNull() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);
            when(mockPs.getGeneratedKeys()).thenReturn(mockRsGen);
            when(mockRsGen.next()).thenReturn(true);
            when(mockRsGen.getInt(1)).thenReturn(124);

            Alquiler a = createAlquilerBase();
            a.setFechaFinEstimada(null); // Establece a NULL
            boolean result = dao.insertar(a);

            assertTrue(result);
            assertEquals(124, a.getNumeroExpediente());
            verify(mockPs).setNull(eq(4), eq(Types.DATE)); // Verifica setNull
        }
    }

    @Test
    void insertar_devuelveFalse_siExecuteUpdateCero() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(0); // Cero filas afectadas
            when(mockPs.getGeneratedKeys()).thenReturn(mockRsGen);
            when(mockRsGen.next()).thenReturn(false); // No generated key

            Alquiler a = createAlquilerBase();
            boolean result = dao.insertar(a);

            assertFalse(result);
            assertEquals(0, a.getNumeroExpediente()); // No se debe setear el expediente
        }
    }

    @Test
    void insertar_devuelveFalse_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Simulated insert error"));

            Alquiler a = createAlquilerBase();
            boolean result = dao.insertar(a);

            assertFalse(result);
        }
    }

    // =========================================================================
    // 2. ACTUALIZAR
    // =========================================================================

    @Test
    void actualizar_devuelveTrue_conFechaFin() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            Alquiler a = createAlquilerBase();
            a.setNumeroExpediente(55);

            boolean result = dao.actualizar(a);

            assertTrue(result);
            verify(mockPs).setDate(eq(4), eq(Date.valueOf(a.getFechaFinEstimada())));
            verify(mockPs).setInt(eq(9), eq(55)); // Verifica el WHERE clause
        }
    }

    @Test
    void actualizar_devuelveTrue_y_manejaFechaFinNull() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            Alquiler a = createAlquilerBase();
            a.setNumeroExpediente(56);
            a.setFechaFinEstimada(null); // Establece a NULL

            boolean result = dao.actualizar(a);

            assertTrue(result);
            verify(mockPs).setNull(eq(4), eq(Types.DATE)); // Verifica setNull
        }
    }

    @Test
    void actualizar_devuelveFalse_siExecuteUpdateCero() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(0); // Cero filas afectadas

            Alquiler a = createAlquilerBase();
            a.setNumeroExpediente(66);

            boolean result = dao.actualizar(a);

            assertFalse(result);
        }
    }

    @Test
    void actualizar_devuelveFalse_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulated update error"));

            Alquiler a = createAlquilerBase();
            boolean result = dao.actualizar(a);

            assertFalse(result);
        }
    }

    // =========================================================================
    // 3. ELIMINAR
    // =========================================================================

    @Test
    void eliminar_devuelveTrue_siExecuteUpdatePositivo() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            boolean result = dao.eliminar(10);

            assertTrue(result);
            verify(mockPs).setInt(1, 10);
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void eliminar_devuelveFalse_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenThrow(new SQLException("Simulated delete error"));

            boolean result = dao.eliminar(11);

            assertFalse(result);
        }
    }

    // =========================================================================
    // 4. OBTENER POR ID
    // =========================================================================

    @Test
    void obtenerPorId_devuelveAlquiler_siResultSetTieneFila() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false); // Una fila

            LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
            LocalDate fechaFin = LocalDate.of(2024, 3, 6);
            BigDecimal precio = new BigDecimal("1000.00");

            mockResultadoAlquiler(mockRs, 42, fechaInicio, fechaFin, precio, 7, 8, 1);

            Alquiler a = dao.obtenerPorId(42);

            assertNotNull(a);
            assertEquals(42, a.getNumeroExpediente());
            assertEquals(fechaFin, a.getFechaFinEstimada());
            verify(mockPs).setInt(1, 42);
        }
    }

    @Test
    void obtenerPorId_devuelveNull_siNoHayFilas() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            Alquiler a = dao.obtenerPorId(999);
            assertNull(a);
        }
    }

    @Test
    void obtenerPorId_devuelveNull_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulated select error"));

            Alquiler a = dao.obtenerPorId(1);
            assertNull(a);
        }
    }
    
    // =========================================================================
    // 5. OBTENER TODOS
    // =========================================================================

    @Test
    void obtenerTodos_devuelveLista_conVariasFilas() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockRs);

            // Simular dos filas
            when(mockRs.next()).thenReturn(true, true, false);

            LocalDate fecha1 = LocalDate.of(2024, 1, 1);
            LocalDate fecha2 = LocalDate.of(2024, 2, 1);
            BigDecimal precio1 = new BigDecimal("300.00");
            BigDecimal precio2 = new BigDecimal("400.00");

            // Configurar el comportamiento secuencial para las dos filas
            when(mockRs.getInt("numero_expediente")).thenReturn(1, 2);
            when(mockRs.getDate("fecha_inicio")).thenReturn(Date.valueOf(fecha1), Date.valueOf(fecha2));
            when(mockRs.getInt("tiempo_meses")).thenReturn(1, 2);
            when(mockRs.getInt("tiempo_dias")).thenReturn(0, 0);
            when(mockRs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(fecha1.plusMonths(1)), Date.valueOf(fecha2.plusMonths(2)));
            when(mockRs.getBigDecimal("precio_total_estimado")).thenReturn(precio1, precio2);
            when(mockRs.getInt("id_cliente")).thenReturn(10, 20);
            when(mockRs.getInt("id_vivienda")).thenReturn(3, 4);
            when(mockRs.getInt("id_estado_cobro")).thenReturn(1, 1);

            List<Alquiler> lista = dao.obtenerTodos();

            assertNotNull(lista);
            assertEquals(2, lista.size());
            assertEquals(2, lista.get(1).getNumeroExpediente());
        }
    }
    
    @Test
    void obtenerTodos_devuelveListaVacia_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.createStatement()).thenThrow(new SQLException("Simulated statement error"));

            List<Alquiler> lista = dao.obtenerTodos();

            assertNotNull(lista);
            assertTrue(lista.isEmpty());
        }
    }

    // =========================================================================
    // 6. OBTENER POR CLIENTE/VIVIENDA/RANGO FECHAS/ESTADO
    // =========================================================================

    @Test
    void obtenerPorCliente_devuelveListaSegunResultado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            mockResultadoAlquiler(mockRs, 77, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 11), new BigDecimal("100.00"), 99, 88, 1);

            List<Alquiler> lista = dao.obtenerPorCliente(99);

            assertNotNull(lista);
            assertEquals(1, lista.size());
            verify(mockPs).setInt(1, 99);
        }
    }
    
    @Test
    void obtenerPorVivienda_devuelveListaSegunResultado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            mockResultadoAlquiler(mockRs, 77, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 11), new BigDecimal("100.00"), 99, 88, 1);

            List<Alquiler> lista = dao.obtenerPorVivienda(88);

            assertNotNull(lista);
            assertEquals(1, lista.size());
            verify(mockPs).setInt(1, 88);
        }
    }
    
    @Test
    void obtenerPorRangoFechas_convierteLocalDate_aSqlDate_yDevuelveLista() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            mockResultadoAlquiler(mockRs, 101, LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 6), new BigDecimal("200.00"), 55, 44, 1);

            LocalDate desde = LocalDate.of(2024, 7, 1);
            LocalDate hasta = LocalDate.of(2024, 7, 31);
            
            List<Alquiler> lista = dao.obtenerPorRangoFechas(desde, hasta);
            
            assertNotNull(lista);
            assertEquals(1, lista.size());
            verify(mockPs).setDate(eq(1), eq(Date.valueOf(desde)));
            verify(mockPs).setDate(eq(2), eq(Date.valueOf(hasta)));
        }
    }
    
    @Test
    void obtenerPorEstado_devuelveListaSegunId() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            mockResultadoAlquiler(mockRs, 201, LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 21), new BigDecimal("400.00"), 11, 22, 2);

            List<Alquiler> lista = dao.obtenerPorEstado(2);
            
            assertNotNull(lista);
            assertEquals(1, lista.size());
            verify(mockPs).setInt(1, 2);
        }
    }
    
    // Cobertura de las ramas de SQLException en todos los métodos de consulta
    @Test
    void obtenerPorCliente_devuelveListaVacia_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulated client select error"));

            List<Alquiler> lista = dao.obtenerPorCliente(99);

            assertTrue(lista.isEmpty());
        }
    }

    // =========================================================================
    // 7. OBTENER POR ESTADO NOMBRE (Pendientes y Pagados)
    // =========================================================================

    @Test
    void obtenerPendientesPago_llamaAEstadoNombre_conPendiente() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            // Mockeamos la fila con un estado (aunque la consulta SQL usa el JOIN)
            mockResultadoAlquiler(mockRs, 301, LocalDate.of(2024, 9, 1), LocalDate.of(2024, 9, 2), new BigDecimal("50.00"), 7, 8, 3);

            List<Alquiler> lista = dao.obtenerPendientesPago();
            
            assertNotNull(lista);
            assertEquals(1, lista.size());
            verify(mockPs).setString(1, "pendiente");
        }
    }
    
    @Test
    void obtenerPagados_llamaAEstadoNombre_conPagado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            mockResultadoAlquiler(mockRs, 302, LocalDate.of(2024, 10, 1), LocalDate.of(2024, 11, 1), new BigDecimal("600.00"), 1, 2, 2);

            List<Alquiler> lista = dao.obtenerPagados();
            
            assertNotNull(lista);
            assertEquals(1, lista.size());
            verify(mockPs).setString(1, "pagado");
        }
    }

    // =========================================================================
    // 8. OBTENER ALQUILER ACTIVO POR VIVIENDA
    // =========================================================================

    @Test
    void obtenerAlquilerActivoPorVivienda_devuelveAlquiler_siHayFila() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true, false);
            
            // Usamos id_estado_cobro 1 (asumimos que es 'pendiente' o 'retrasado' según la lógica del SQL)
            mockResultadoAlquiler(mockRs, 401, LocalDate.of(2024, 11, 1), LocalDate.of(2024, 12, 1), new BigDecimal("800.00"), 3, 50, 1);

            Alquiler a = dao.obtenerAlquilerActivoPorVivienda(50);
            
            assertNotNull(a);
            assertEquals(401, a.getNumeroExpediente());
            assertEquals(50, a.getIdVivienda());
            verify(mockPs).setInt(1, 50);
        }
    }

    @Test
    void obtenerAlquilerActivoPorVivienda_devuelveNull_siNoHayFila() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            Alquiler a = dao.obtenerAlquilerActivoPorVivienda(999);
            assertNull(a);
        }
    }
    
    @Test
    void obtenerAlquilerActivoPorVivienda_devuelveNull_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulated active select error"));

            Alquiler a = dao.obtenerAlquilerActivoPorVivienda(10);
            assertNull(a);
        }
    }
}