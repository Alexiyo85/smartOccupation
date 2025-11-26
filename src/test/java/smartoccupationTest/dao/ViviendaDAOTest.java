/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smartoccupationTest.dao;

import com.smartoccupation.dao.ViviendaDAO;
import com.smartoccupation.modelo.Vivienda;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ViviendaDAOTest {

    private ViviendaDAO dao;
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;
    private Statement stmt;

    @BeforeEach
    void setUp() {
        dao = new ViviendaDAO();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
        stmt = mock(Statement.class);
    }

    @Test
    void testInsertar() throws Exception {
        Vivienda v = new Vivienda();
        v.setCodigo_referencia("REF123");

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(10);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.insertar(v);
            assertThat(resultado).isTrue();
            assertThat(v.getId_vivienda()).isEqualTo(10);
        }
    }

    @Test
    void testActualizar() throws Exception {
        Vivienda v = new Vivienda();
        v.setId_vivienda(5);
        v.setCodigo_referencia("REF456");

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.actualizar(v);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testEliminar() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.eliminar(3);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id_vivienda")).thenReturn(1);
        when(rs.getString("codigo_referencia")).thenReturn("REF001");
        when(rs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28001");
        when(rs.getInt("metros_cuadrados")).thenReturn(80);
        when(rs.getInt("numero_habitaciones")).thenReturn(3);
        when(rs.getInt("numero_banios")).thenReturn(2);
        when(rs.getBigDecimal("precio_mensual")).thenReturn(BigDecimal.valueOf(1000));
        when(rs.getString("estado")).thenReturn("disponible");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            Vivienda v = dao.obtenerPorId(1);
            assertThat(v).isNotNull();
            assertThat(v.getCodigo_referencia()).isEqualTo("REF001");
            assertThat(v.getPrecio_mensual()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        }
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id_vivienda")).thenReturn(1);
        when(rs.getString("codigo_referencia")).thenReturn("REF001");
        when(rs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28001");
        when(rs.getInt("metros_cuadrados")).thenReturn(80);
        when(rs.getInt("numero_habitaciones")).thenReturn(3);
        when(rs.getInt("numero_banios")).thenReturn(2);
        when(rs.getBigDecimal("precio_mensual")).thenReturn(BigDecimal.valueOf(1000));
        when(rs.getString("estado")).thenReturn("disponible");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Vivienda> lista = dao.obtenerTodos();
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getCodigo_referencia()).isEqualTo("REF001");
        }
    }

    @Test
    void testObtenerPorEstado() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString("estado")).thenReturn("disponible");
        when(rs.getInt("id_vivienda")).thenReturn(1);
        when(rs.getString("codigo_referencia")).thenReturn("REF001");
        when(rs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28001");
        when(rs.getInt("metros_cuadrados")).thenReturn(80);
        when(rs.getInt("numero_habitaciones")).thenReturn(3);
        when(rs.getInt("numero_banios")).thenReturn(2);
        when(rs.getBigDecimal("precio_mensual")).thenReturn(BigDecimal.valueOf(1000));

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Vivienda> lista = dao.obtenerPorEstado("disponible");
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getEstado()).isEqualTo("disponible");
        }
    }

    @Test
    void testObtenerPorRangoPrecio() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getBigDecimal("precio_mensual")).thenReturn(BigDecimal.valueOf(1200));
        when(rs.getInt("id_vivienda")).thenReturn(2);
        when(rs.getString("codigo_referencia")).thenReturn("REF002");
        when(rs.getString("direccion")).thenReturn("Calle Real 5");
        when(rs.getString("ciudad")).thenReturn("Barcelona");
        when(rs.getString("provincia")).thenReturn("Barcelona");
        when(rs.getString("codigo_postal")).thenReturn("08001");
        when(rs.getInt("metros_cuadrados")).thenReturn(100);
        when(rs.getInt("numero_habitaciones")).thenReturn(4);
        when(rs.getInt("numero_banios")).thenReturn(2);
        when(rs.getString("estado")).thenReturn("ocupado");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Vivienda> lista = dao.obtenerPorRangoPrecio(BigDecimal.valueOf(1000), BigDecimal.valueOf(1500));
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getPrecio_mensual()).isEqualByComparingTo(BigDecimal.valueOf(1200));
        }
    }

    @Test
    void testObtenerPorCodigoReferencia() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("codigo_referencia")).thenReturn("REF123");
        when(rs.getInt("id_vivienda")).thenReturn(3);
        when(rs.getString("direccion")).thenReturn("Calle Ejemplo 10");
        when(rs.getString("ciudad")).thenReturn("Sevilla");
        when(rs.getString("provincia")).thenReturn("Sevilla");
        when(rs.getString("codigo_postal")).thenReturn("41001");
        when(rs.getInt("metros_cuadrados")).thenReturn(90);
        when(rs.getInt("numero_habitaciones")).thenReturn(3);
        when(rs.getInt("numero_banios")).thenReturn(2);
        when(rs.getBigDecimal("precio_mensual")).thenReturn(BigDecimal.valueOf(900));
        when(rs.getString("estado")).thenReturn("disponible");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            Vivienda v = dao.obtenerPorCodigoReferencia("REF123");
            assertThat(v).isNotNull();
            assertThat(v.getCodigo_referencia()).isEqualTo("REF123");
        }
    }
}
