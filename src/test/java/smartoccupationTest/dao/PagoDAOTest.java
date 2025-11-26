package smartoccupationTest.dao;

import com.smartoccupation.dao.PagoDAO;
import com.smartoccupation.modelo.Pago;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PagoDAOTest {

    private PagoDAO dao;
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        dao = new PagoDAO();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
    }

    @Test
    void testInsertarExitoso() throws Exception {
        Pago pago = new Pago();
        pago.setNumero_expediente(1);
        pago.setFecha_pago(LocalDate.of(2025, 11, 25));
        pago.setCantidad(BigDecimal.valueOf(100));

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(10);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.insertar(pago);
            assertThat(resultado).isTrue();
            assertThat(pago.getId_pago()).isEqualTo(10);
        }
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id_pago")).thenReturn(1);
        when(rs.getInt("numero_expediente")).thenReturn(100);
        when(rs.getDate("fecha_pago")).thenReturn(Date.valueOf("2025-11-25"));
        when(rs.getBigDecimal("cantidad")).thenReturn(BigDecimal.valueOf(200));

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Pago> lista = dao.obtenerTodos();
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getCantidad()).isEqualByComparingTo(BigDecimal.valueOf(200));
        }
    }

    @Test
    void testBuscarPorRangoFechas() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id_pago")).thenReturn(2);
        when(rs.getInt("numero_expediente")).thenReturn(101);
        when(rs.getDate("fecha_pago")).thenReturn(Date.valueOf("2025-11-20"));
        when(rs.getBigDecimal("cantidad")).thenReturn(BigDecimal.valueOf(300));

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Pago> lista = dao.buscarPorRangoFechas(LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 30));
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getNumero_expediente()).isEqualTo(101);
        }
    }

    @Test
    void testEliminarExitoso() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.eliminar(5);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testObtenerPorExpediente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id_pago")).thenReturn(3);
        when(rs.getInt("numero_expediente")).thenReturn(200);
        when(rs.getDate("fecha_pago")).thenReturn(Date.valueOf("2025-11-15"));
        when(rs.getBigDecimal("cantidad")).thenReturn(BigDecimal.valueOf(400));

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Pago> lista = dao.obtenerPorExpediente(200);
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getCantidad()).isEqualByComparingTo(BigDecimal.valueOf(400));
        }
    }
}

