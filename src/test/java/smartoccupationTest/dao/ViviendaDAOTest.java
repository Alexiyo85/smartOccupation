package smartoccupationTest.dao;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ViviendaDAOTest {

    private ViviendaDAO dao = new ViviendaDAO();

    private Connection mockConn = mock(Connection.class);
    private PreparedStatement mockPs = mock(PreparedStatement.class);
    private Statement mockStatement = mock(Statement.class);
    private ResultSet mockRs = mock(ResultSet.class);
    private ResultSet mockRsGen = mock(ResultSet.class);

    // =========================================================================
    // 1. INSERTAR
    // =========================================================================
    @Test
    void insertar_devuelveTrue_y_seteaIdVivienda() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);
            when(mockPs.getGeneratedKeys()).thenReturn(mockRsGen);
            when(mockRsGen.next()).thenReturn(true);
            when(mockRsGen.getInt(1)).thenReturn(99);

            Vivienda v = crearVivienda();
            boolean result = dao.insertar(v);

            assertTrue(result);
            assertEquals(99, v.getId_vivienda());
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void insertar_devuelveFalse_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString(), anyInt()))
                    .thenThrow(new SQLException("Simulado"));

            Vivienda v = crearVivienda();
            boolean result = dao.insertar(v);

            assertFalse(result);
        }
    }

    // =========================================================================
    // 2. ACTUALIZAR
    // =========================================================================
    @Test
    void actualizar_devuelveTrue_siExecuteUpdatePositivo() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            Vivienda v = crearVivienda();
            v.setId_vivienda(10);

            boolean result = dao.actualizar(v);
            assertTrue(result);
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void actualizar_devuelveFalse_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

            Vivienda v = crearVivienda();
            boolean result = dao.actualizar(v);
            assertFalse(result);
        }
    }

    // =========================================================================
    // 3. ELIMINAR
    // =========================================================================
    @Test
    void eliminar_devuelveTrue_siExecuteUpdatePositivo() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            boolean result = dao.eliminar(5);
            assertTrue(result);
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void eliminar_devuelveFalse_siSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

            boolean result = dao.eliminar(5);
            assertFalse(result);
        }
    }

    // =========================================================================
    // 4. OBTENER POR ID
    // =========================================================================
    @Test
    void obtenerPorId_devuelveVivienda_siExiste() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);

            mockearVivienda(mockRs, 10, "REF-001", "disponible");

            Vivienda v = dao.obtenerPorId(10);
            assertNotNull(v);
            assertEquals(10, v.getId_vivienda());
        }
    }

    @Test
    void obtenerPorId_devuelveNull_siNoExiste() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            Vivienda v = dao.obtenerPorId(99);
            assertNull(v);
        }
    }

    // =========================================================================
    // 5. OBTENER TODOS
    // =========================================================================
    @Test
    void obtenerTodos_devuelveLista() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockRs);

            // Simulamos 2 filas
            when(mockRs.next()).thenReturn(true, true, false);

            // Valores secuenciales para cada fila
            when(mockRs.getInt("id_vivienda")).thenReturn(1, 2);
            when(mockRs.getString("codigo_referencia")).thenReturn("REF-001", "REF-002");
            when(mockRs.getString("direccion")).thenReturn("Calle X", "Calle Y");
            when(mockRs.getString("ciudad")).thenReturn("Madrid", "Barcelona");
            when(mockRs.getString("provincia")).thenReturn("Madrid", "Barcelona");
            when(mockRs.getString("codigo_postal")).thenReturn("28000", "08000");
            when(mockRs.getInt("metros_cuadrados")).thenReturn(80, 100);
            when(mockRs.getInt("numero_habitaciones")).thenReturn(3, 4);
            when(mockRs.getInt("numero_banios")).thenReturn(2, 3);
            when(mockRs.getBigDecimal("precio_mensual")).thenReturn(new BigDecimal("750.00"), new BigDecimal("1200.00"));
            when(mockRs.getString("estado")).thenReturn("disponible", "ocupado");

            List<Vivienda> lista = dao.obtenerTodos();

            assertEquals(2, lista.size());
            assertEquals("REF-001", lista.get(0).getCodigo_referencia());
            assertEquals("REF-002", lista.get(1).getCodigo_referencia());
        }
    }

    // =========================================================================
    // 6. OBTENER POR ESTADO
    // =========================================================================
    @Test
    void obtenerPorEstado_devuelveLista() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true, false);
            mockearVivienda(mockRs, 5, "REF-005", "reservado");

            List<Vivienda> lista = dao.obtenerPorEstado("reservado");
            assertEquals(1, lista.size());
            assertEquals("reservado", lista.get(0).getEstado());
        }
    }

    // =========================================================================
    // 7. OBTENER POR RANGO DE PRECIO
    // =========================================================================
    @Test
    void obtenerPorRangoPrecio_devuelveLista() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true, false);
            mockearVivienda(mockRs, 6, "REF-006", "disponible", new BigDecimal("750.00"));

            List<Vivienda> lista = dao.obtenerPorRangoPrecio(new BigDecimal("400"), new BigDecimal("900"));
            assertEquals(1, lista.size());
            assertEquals(new BigDecimal("750.00"), lista.get(0).getPrecio_mensual());
        }
    }

    // =========================================================================
    // 8. OBTENER POR CÓDIGO REFERENCIA
    // =========================================================================
    @Test
    void obtenerPorCodigoReferencia_devuelveVivienda() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true);
            mockearVivienda(mockRs, 7, "REF-007", "ocupado");

            Vivienda v = dao.obtenerPorCodigoReferencia("REF-007");
            assertNotNull(v);
            assertEquals("REF-007", v.getCodigo_referencia());
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================
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
        v.setEstado("disponible"); // Valor válido
        return v;
    }

    private void mockearVivienda(ResultSet rs, int id, String ref, String estado) throws SQLException {
        mockearVivienda(rs, id, ref, estado, new BigDecimal("500.00"));
    }

    private void mockearVivienda(ResultSet rs, int id, String ref, String estado, BigDecimal precio) throws SQLException {
        when(rs.getInt("id_vivienda")).thenReturn(id);
        when(rs.getString("codigo_referencia")).thenReturn(ref);
        when(rs.getString("direccion")).thenReturn("Calle X");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28000");
        when(rs.getInt("metros_cuadrados")).thenReturn(80);
        when(rs.getInt("numero_habitaciones")).thenReturn(3);
        when(rs.getInt("numero_banios")).thenReturn(2);
        when(rs.getBigDecimal("precio_mensual")).thenReturn(precio);
        when(rs.getString("estado")).thenReturn(estado);
    }
}
