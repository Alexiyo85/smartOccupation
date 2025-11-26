package smartoccupationTest.dao;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ViviendaDAOTest {

    private ViviendaDAO viviendaDAO;

    private Connection mockConn;
    private PreparedStatement mockPs;
    private Statement mockStatement;
    private ResultSet mockRs;

    private MockedStatic<ConexionBBDD> conexionMock;

    @BeforeEach
    void setUp() throws Exception {
        viviendaDAO = new ViviendaDAO();

        mockConn = mock(Connection.class);
        mockPs = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);
        mockRs = mock(ResultSet.class);

        conexionMock = mockStatic(ConexionBBDD.class);
        conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
    }

    @AfterEach
    void tearDown() {
        conexionMock.close();
    }

    // ------------------------------------------------------------------
    // INSERTAR
    // ------------------------------------------------------------------
    @Test
    void testInsertar_exito() throws Exception {
        Vivienda v = crearVivienda();

        when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPs);

        when(mockPs.executeUpdate()).thenReturn(1);

        when(mockPs.getGeneratedKeys()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(99);

        boolean resultado = viviendaDAO.insertar(v);

        assertTrue(resultado);
        assertEquals(99, v.getId_vivienda());
        verify(mockPs).executeUpdate();
    }

    @Test
    void testInsertar_errorSQLException() throws Exception {
        Vivienda v = crearVivienda();

        when(mockConn.prepareStatement(anyString(), anyInt()))
                .thenThrow(new SQLException("Simulado"));

        boolean resultado = viviendaDAO.insertar(v);

        assertFalse(resultado);
    }

    // ------------------------------------------------------------------
    // ACTUALIZAR
    // ------------------------------------------------------------------
    @Test
    void testActualizar_exito() throws Exception {
        Vivienda v = crearVivienda();
        v.setId_vivienda(10);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);

        boolean resultado = viviendaDAO.actualizar(v);

        assertTrue(resultado);
        verify(mockPs).executeUpdate();
    }

    @Test
    void testActualizar_errorSQLException() throws Exception {
        Vivienda v = crearVivienda();

        when(mockConn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Simulado"));

        boolean resultado = viviendaDAO.actualizar(v);

        assertFalse(resultado);
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    @Test
    void testEliminar_exito() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);

        boolean resultado = viviendaDAO.eliminar(5);

        assertTrue(resultado);
    }

    @Test
    void testEliminar_errorSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Simulado"));

        boolean resultado = viviendaDAO.eliminar(5);

        assertFalse(resultado);
    }

    // ------------------------------------------------------------------
    // OBTENER POR ID
    // ------------------------------------------------------------------
    @Test
    void testObtenerPorId_encontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        mockearViviendaUnica();

        Vivienda v = viviendaDAO.obtenerPorId(10);

        assertNotNull(v);
        assertEquals(10, v.getId_vivienda());
        assertEquals("REF-001", v.getCodigo_referencia());
    }

    @Test
    void testObtenerPorId_noEncontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(false);

        Vivienda v = viviendaDAO.obtenerPorId(10);

        assertNull(v);
    }

    // ------------------------------------------------------------------
    // OBTENER TODOS
    // ------------------------------------------------------------------
    @Test
    void testObtenerTodos_exito() throws Exception {
        when(mockConn.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, true, false);
        mockearViviendaUnica();
        mockearViviendaUnica();

        List<Vivienda> lista = viviendaDAO.obtenerTodos();

        assertEquals(2, lista.size());
    }

    @Test
    void testObtenerTodos_errorSQLException() throws Exception {
        when(mockConn.createStatement())
                .thenThrow(new SQLException("Simulado"));

        List<Vivienda> lista = viviendaDAO.obtenerTodos();

        assertTrue(lista.isEmpty());
    }

    // ------------------------------------------------------------------
    // OBTENER POR ESTADO
    // ------------------------------------------------------------------
    @Test
    void testObtenerPorEstado_exito() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, false);
        mockearViviendaUnica();

        List<Vivienda> lista = viviendaDAO.obtenerPorEstado("LIBRE");

        assertEquals(1, lista.size());
        assertEquals("LIBRE", lista.get(0).getEstado());
    }

    @Test
    void testObtenerPorEstado_errorSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Simulado"));

        List<Vivienda> lista = viviendaDAO.obtenerPorEstado("LIBRE");

        assertTrue(lista.isEmpty());
    }

    // ------------------------------------------------------------------
    // OBTENER POR RANGO DE PRECIO
    // ------------------------------------------------------------------
    @Test
    void testObtenerPorRangoPrecio_exito() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, false);
        mockearViviendaUnica();

        List<Vivienda> lista = viviendaDAO.obtenerPorRangoPrecio(
                new BigDecimal("400"), new BigDecimal("900"));

        assertEquals(1, lista.size());
        assertEquals(new BigDecimal("750.00"), lista.get(0).getPrecio_mensual());
    }

    @Test
    void testObtenerPorRangoPrecio_errorSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Simulado"));

        List<Vivienda> lista = viviendaDAO.obtenerPorRangoPrecio(
                new BigDecimal("400"), new BigDecimal("900"));

        assertTrue(lista.isEmpty());
    }

    // ------------------------------------------------------------------
    // OBTENER POR CÓDIGO REFERENCIA
    // ------------------------------------------------------------------
    @Test
    void testObtenerPorCodigoReferencia_exito() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        mockearViviendaUnica();

        Vivienda v = viviendaDAO.obtenerPorCodigoReferencia("REF-001");

        assertNotNull(v);
        assertEquals("REF-001", v.getCodigo_referencia());
    }

    @Test
    void testObtenerPorCodigoReferencia_noEncontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(false);

        Vivienda v = viviendaDAO.obtenerPorCodigoReferencia("REF-XYZ");

        assertNull(v);
    }

    // ------------------------------------------------------------------
    // MÉTODOS DE APOYO
    // ------------------------------------------------------------------

    private Vivienda crearVivienda() {
        Vivienda v = new Vivienda();
        v.setCodigo_referencia("REF-001");
        v.setDireccion("Calle Falsa 123");
        v.setCiudad("Madrid");
        v.setProvincia("Madrid");
        v.setCodigo_postal("28000");
        v.setMetros_cuadrados(80);
        v.setNumero_habitaciones(3);
        v.setNumero_banios(2);
        v.setPrecio_mensual(new BigDecimal("750.00"));
        v.setEstado("LIBRE");
        return v;
    }

    private void mockearViviendaUnica() throws Exception {
        when(mockRs.next()).thenReturn(true);

        when(mockRs.getInt("id_vivienda")).thenReturn(10);
        when(mockRs.getString("codigo_referencia")).thenReturn("REF-001");
        when(mockRs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(mockRs.getString("ciudad")).thenReturn("Madrid");
        when(mockRs.getString("provincia")).thenReturn("Madrid");
        when(mockRs.getString("codigo_postal")).thenReturn("28000");
        when(mockRs.getInt("metros_cuadrados")).thenReturn(80);
        when(mockRs.getInt("numero_habitaciones")).thenReturn(3);
        when(mockRs.getInt("numero_banios")).thenReturn(2);
        when(mockRs.getBigDecimal("precio_mensual")).thenReturn(new BigDecimal("750.00"));
        when(mockRs.getString("estado")).thenReturn("LIBRE");
    }
}
