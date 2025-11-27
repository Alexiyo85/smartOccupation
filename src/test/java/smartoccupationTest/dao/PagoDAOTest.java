package smartoccupationTest.dao;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PagoDAOTest {

    private PagoDAO dao;
    private Connection mockConn;
    private PreparedStatement mockPs;
    private ResultSet mockRs;
    private MockedStatic<ConexionBBDD> conexionMock;

    @BeforeEach
    void setUp() {
        dao = new PagoDAO();
        mockConn = mock(Connection.class);
        mockPs = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        conexionMock = mockStatic(ConexionBBDD.class);
        conexionMock.when(ConexionBBDD::conectar).thenReturn(mockConn);
    }

    @AfterEach
    void tearDown() {
        conexionMock.close();
    }

    private Pago crearPago() {
        Pago pago = new Pago();
        pago.setNumeroExpediente(1);
        pago.setFechaPago(LocalDate.of(2025, 11, 26));
        pago.setCantidad(BigDecimal.valueOf(150));
        return pago;
    }

    private void mockPagoResultSet(ResultSet rs) throws SQLException {
        when(rs.getInt("id_pago")).thenReturn(1);
        when(rs.getInt("numero_expediente")).thenReturn(1);
        when(rs.getDate("fecha_pago")).thenReturn(Date.valueOf("2025-11-26"));
        when(rs.getBigDecimal("cantidad")).thenReturn(BigDecimal.valueOf(150));
    }

    // ----------------------------
    // INSERTAR
    // ----------------------------
    @Test
    void testInsertarExitoso() throws Exception {
        Pago pago = crearPago();
        when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);
        when(mockPs.getGeneratedKeys()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(99);

        boolean resultado = dao.insertar(pago);

        assertThat(resultado).isTrue();
        assertThat(pago.getIdPago()).isEqualTo(99);
    }

    @Test
    void testInsertarSQLException() throws Exception {
        Pago pago = crearPago();
        when(mockConn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.insertar(pago))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error insertando pago");
    }

    // ----------------------------
    // OBTENER TODOS
    // ----------------------------
    @Test
    void testObtenerTodosExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        mockPagoResultSet(mockRs);

        List<Pago> lista = dao.obtenerTodos();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getCantidad()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void testObtenerTodosSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.obtenerTodos())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error consultando pagos");
    }

    // ----------------------------
    // BUSCAR POR RANGO DE FECHAS
    // ----------------------------
    @Test
    void testBuscarPorRangoFechasExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        mockPagoResultSet(mockRs);

        List<Pago> lista = dao.buscarPorRangoFechas(LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 30));
        assertThat(lista).hasSize(1);
    }

    @Test
    void testBuscarPorRangoFechasSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.buscarPorRangoFechas(LocalDate.now(), LocalDate.now()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error consultando pagos por rango");
    }

    // ----------------------------
    // ELIMINAR
    // ----------------------------
    @Test
    void testEliminarExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);

        boolean resultado = dao.eliminar(5);
        assertThat(resultado).isTrue();
    }

    @Test
    void testEliminarSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.eliminar(5))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error eliminando pago");
    }

    // ----------------------------
    // OBTENER POR EXPEDIENTE
    // ----------------------------
    @Test
    void testObtenerPorExpedienteExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, false);
        mockPagoResultSet(mockRs);

        List<Pago> lista = dao.obtenerPorExpediente(1);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNumeroExpediente()).isEqualTo(1);
    }

    @Test
    void testObtenerPorExpedienteSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.obtenerPorExpediente(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error consultando pagos por expediente");
    }
}
