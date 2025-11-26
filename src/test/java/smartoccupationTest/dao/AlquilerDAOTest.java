package smartoccupationTest.dao;

import com.smartoccupation.dao.AlquilerDAO;
import com.smartoccupation.modelo.Alquiler;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.AfterEach;
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
    private MockedStatic<ConexionBBDD> conexionMockStatic;

    @BeforeEach
    void setUp() {
        dao = new AlquilerDAO();
        conexionMockStatic = mockStatic(ConexionBBDD.class);
    }

    @AfterEach
    void tearDown() {
        conexionMockStatic.close();
    }

    // Helper: construir un Alquiler básico
    private Alquiler createAlquilerBase() {
        Alquiler a = new Alquiler();
        a.setFecha_inicio(LocalDate.of(2024, 1, 1));
        a.setTiempo_meses(1);
        a.setTiempo_dias(10);
        a.setFecha_fin_estimada(a.getFecha_inicio().plusMonths(1).plusDays(10));
        a.setPrecio_total_estimado(new BigDecimal("500.00"));
        a.setId_cliente(2);
        a.setId_vivienda(3);
        a.setId_estado_cobro(1);
        return a;
    }

    @Test
    void insertar_devuelveTrue_y_seteaNumeroExpediente_siExecuteUpdatePositivo_yGeneratedKeyPresente() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rsGen = mock(ResultSet.class);

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rsGen);
        when(rsGen.next()).thenReturn(true);
        when(rsGen.getInt(1)).thenReturn(123);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = createAlquilerBase();
        boolean result = dao.insertar(a);

        assertTrue(result);
        assertEquals(123, a.getNumero_expediente());

        verify(ps).setDate(eq(1), any(Date.class));
        verify(ps).setInt(eq(2), eq(a.getTiempo_meses()));
        verify(ps).setInt(eq(3), eq(a.getTiempo_dias()));
        verify(ps).setBigDecimal(eq(5), eq(a.getPrecio_total_estimado()));
        verify(ps).setInt(eq(6), eq(a.getId_cliente()));
        verify(ps).setInt(eq(7), eq(a.getId_vivienda()));
        verify(ps).setInt(eq(8), eq(a.getId_estado_cobro()));
        verify(ps).executeUpdate();
        verify(ps).getGeneratedKeys();
        verify(rsGen).next();
        verify(rsGen).getInt(1);
        // resources closed by try-with-resources in production code, Mockito doesn't check closes here
    }

    @Test
    void insertar_devuelveFalse_siExecuteUpdateCero() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0); // no filas afectadas

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = createAlquilerBase();
        boolean result = dao.insertar(a);

        assertFalse(result);
        verify(ps).executeUpdate();
    }

    @Test
    void actualizar_devuelveTrue_siExecuteUpdatePositivo() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = createAlquilerBase();
        a.setNumero_expediente(55);

        boolean result = dao.actualizar(a);

        assertTrue(result);
        verify(ps).setDate(eq(1), any(Date.class));
        verify(ps).setInt(eq(9), eq(55));
        verify(ps).executeUpdate();
    }

    @Test
    void actualizar_devuelveFalse_siExecuteUpdateCero() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = createAlquilerBase();
        a.setNumero_expediente(66);

        boolean result = dao.actualizar(a);

        assertFalse(result);
    }

    @Test
    void eliminar_devuelveTrue_siExecuteUpdatePositivo() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        boolean result = dao.eliminar(10);

        assertTrue(result);
        verify(ps).setInt(1, 10);
        verify(ps).executeUpdate();
    }

    @Test
    void eliminar_devuelveFalse_siSQLException() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("boom"));

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        boolean result = dao.eliminar(11);

        assertFalse(result);
    }

    @Test
    void obtenerPorId_devuelveAlquiler_siResultSetTieneFila() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // Simular fila en rs
        when(rs.next()).thenReturn(true);
        when(rs.getInt("numero_expediente")).thenReturn(42);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,1,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(2);
        when(rs.getInt("tiempo_dias")).thenReturn(5);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,3,6)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("1000.00"));
        when(rs.getInt("id_cliente")).thenReturn(7);
        when(rs.getInt("id_vivienda")).thenReturn(8);
        when(rs.getInt("id_estado_cobro")).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = dao.obtenerPorId(42);

        assertNotNull(a);
        assertEquals(42, a.getNumero_expediente());
        assertEquals(LocalDate.of(2024,1,1), a.getFecha_inicio());
        assertEquals(2, a.getTiempo_meses());
        assertEquals(5, a.getTiempo_dias());
        assertEquals(LocalDate.of(2024,3,6), a.getFecha_fin_estimada());
        assertEquals(new BigDecimal("1000.00"), a.getPrecio_total_estimado());
        assertEquals(7, a.getId_cliente());
        assertEquals(8, a.getId_vivienda());
        assertEquals(1, a.getId_estado_cobro());

        verify(ps).setInt(1, 42);
        verify(ps).executeQuery();
    }

    @Test
    void obtenerPorId_devuelveNull_siNoHayFilas() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = dao.obtenerPorId(999);
        assertNull(a);
    }

    @Test
    void obtenerTodos_devuelveLista_conVariasFilas() throws Exception {
        Connection conn = mock(Connection.class);
        Statement stmt = mock(Statement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        // Simular dos filas
        when(rs.next()).thenReturn(true, true, false);

        // Primera fila
        when(rs.getInt("numero_expediente")).thenReturn(1, 2); // iteración 1 -> 1, iteración 2 -> 2
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,1,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(1);
        when(rs.getInt("tiempo_dias")).thenReturn(0);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,2,1)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("300.00"));
        when(rs.getInt("id_cliente")).thenReturn(2);
        when(rs.getInt("id_vivienda")).thenReturn(3);
        when(rs.getInt("id_estado_cobro")).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        List<Alquiler> lista = dao.obtenerTodos();

        assertNotNull(lista);
        assertEquals(2, lista.size());
    }

    @Test
    void obtenerPorCliente_devuelveListaSegunResultado() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("numero_expediente")).thenReturn(77);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,6,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(0);
        when(rs.getInt("tiempo_dias")).thenReturn(10);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,6,11)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("100.00"));
        when(rs.getInt("id_cliente")).thenReturn(99);
        when(rs.getInt("id_vivienda")).thenReturn(88);
        when(rs.getInt("id_estado_cobro")).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        List<Alquiler> lista = dao.obtenerPorCliente(99);

        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals(77, lista.get(0).getNumero_expediente());
    }

    @Test
    void obtenerPorRangoFechas_convierteLocalDate_aSqlDate_yDevuelveLista() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("numero_expediente")).thenReturn(101);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,7,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(0);
        when(rs.getInt("tiempo_dias")).thenReturn(5);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,7,6)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("200.00"));
        when(rs.getInt("id_cliente")).thenReturn(55);
        when(rs.getInt("id_vivienda")).thenReturn(44);
        when(rs.getInt("id_estado_cobro")).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        List<Alquiler> lista = dao.obtenerPorRangoFechas(LocalDate.of(2024,7,1), LocalDate.of(2024,7,31));
        assertNotNull(lista);
        assertEquals(1, lista.size());
        verify(ps).setDate(eq(1), eq(Date.valueOf(LocalDate.of(2024,7,1))));
        verify(ps).setDate(eq(2), eq(Date.valueOf(LocalDate.of(2024,7,31))));
    }

    @Test
    void obtenerPorEstado_devuelveListaSegunId() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("numero_expediente")).thenReturn(201);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,8,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(0);
        when(rs.getInt("tiempo_dias")).thenReturn(20);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,8,21)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("400.00"));
        when(rs.getInt("id_cliente")).thenReturn(11);
        when(rs.getInt("id_vivienda")).thenReturn(22);
        when(rs.getInt("id_estado_cobro")).thenReturn(2);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        List<Alquiler> lista = dao.obtenerPorEstado(2);
        assertNotNull(lista);
        assertEquals(1, lista.size());
    }

    @Test
    void obtenerPendientesPago_usaConsultaPorNombreEstado_yDevuelveLista() throws Exception {
        // cubrir obtenerPorEstadoNombre("pendiente")
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("numero_expediente")).thenReturn(301);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,9,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(0);
        when(rs.getInt("tiempo_dias")).thenReturn(1);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,9,2)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("50.00"));
        when(rs.getInt("id_cliente")).thenReturn(7);
        when(rs.getInt("id_vivienda")).thenReturn(8);
        when(rs.getInt("id_estado_cobro")).thenReturn(3);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        List<Alquiler> lista = dao.obtenerPendientesPago();
        assertNotNull(lista);
        assertEquals(1, lista.size());

        verify(ps).setString(1, "pendiente");
        verify(ps).executeQuery();
    }

    @Test
    void obtenerAlquilerActivoPorVivienda_devuelveAlquiler_siHayFila() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getInt("numero_expediente")).thenReturn(401);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.of(2024,10,1)));
        when(rs.getInt("tiempo_meses")).thenReturn(0);
        when(rs.getInt("tiempo_dias")).thenReturn(3);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(Date.valueOf(LocalDate.of(2024,10,4)));
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(new BigDecimal("120.00"));
        when(rs.getInt("id_cliente")).thenReturn(9);
        when(rs.getInt("id_vivienda")).thenReturn(77);
        when(rs.getInt("id_estado_cobro")).thenReturn(1);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = dao.obtenerAlquilerActivoPorVivienda(77);
        assertNotNull(a);
        assertEquals(401, a.getNumero_expediente());
        verify(ps).setInt(1, 77);
    }

    @Test
    void obtenerAlquilerActivoPorVivienda_devuelveNull_siNoHayFilas() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(conn);

        Alquiler a = dao.obtenerAlquilerActivoPorVivienda(9999);
        assertNull(a);
    }
}
