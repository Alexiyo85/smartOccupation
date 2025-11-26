package com.smartoccupation.dao;

import com.smartoccupation.modelo.Alquiler;
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

class AlquilerDAOTest {

    private AlquilerDAO dao;

    private Connection connection;
    private PreparedStatement ps;
    private Statement stmt;
    private ResultSet rs;

    @BeforeEach
    void setUp() throws SQLException {
        dao = new AlquilerDAO();

        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
    }

    @Test
    void testInsertarExitoso() throws Exception {
        Alquiler alquiler = new Alquiler();
        alquiler.setFecha_inicio(LocalDate.now());
        alquiler.setTiempo_meses(1);
        alquiler.setTiempo_dias(10);
        alquiler.setPrecio_total_estimado(BigDecimal.valueOf(1000));
        alquiler.setId_cliente(1);
        alquiler.setId_vivienda(2);
        alquiler.setId_estado_cobro(3);

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(42);

        try (MockedStatic<ConexionBBDD> conexionBBDDStatic = mockStatic(ConexionBBDD.class)) {
            conexionBBDDStatic.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.insertar(alquiler);

            assertThat(resultado).isTrue();
            assertThat(alquiler.getNumero_expediente()).isEqualTo(42);
        }

        verify(ps).executeUpdate();
        verify(ps).getGeneratedKeys();
    }

    @Test
    void testInsertarFalloSQLException() throws Exception {
        Alquiler alquiler = new Alquiler();
        alquiler.setFecha_inicio(LocalDate.now());

        when(connection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Error simulada"));

        try (MockedStatic<ConexionBBDD> conexionBBDDStatic = mockStatic(ConexionBBDD.class)) {
            conexionBBDDStatic.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.insertar(alquiler);
            assertThat(resultado).isFalse();
        }
    }

    @Test
    void testActualizarExitoso() throws Exception {
        Alquiler alquiler = new Alquiler();
        alquiler.setNumero_expediente(1);
        alquiler.setFecha_inicio(LocalDate.now());
        alquiler.setTiempo_meses(1);
        alquiler.setTiempo_dias(10);
        alquiler.setPrecio_total_estimado(BigDecimal.valueOf(1000));
        alquiler.setId_cliente(1);
        alquiler.setId_vivienda(2);
        alquiler.setId_estado_cobro(3);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionBBDDStatic = mockStatic(ConexionBBDD.class)) {
            conexionBBDDStatic.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.actualizar(alquiler);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testEliminarExitoso() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionBBDDStatic = mockStatic(ConexionBBDD.class)) {
            conexionBBDDStatic.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.eliminar(1);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("numero_expediente")).thenReturn(1);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.now()));
        when(rs.getInt("tiempo_meses")).thenReturn(1);
        when(rs.getInt("tiempo_dias")).thenReturn(2);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(null);
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(BigDecimal.valueOf(1000));
        when(rs.getInt("id_cliente")).thenReturn(1);
        when(rs.getInt("id_vivienda")).thenReturn(2);
        when(rs.getInt("id_estado_cobro")).thenReturn(3);

        try (MockedStatic<ConexionBBDD> conexionBBDDStatic = mockStatic(ConexionBBDD.class)) {
            conexionBBDDStatic.when(ConexionBBDD::conectar).thenReturn(connection);

            Alquiler alquiler = dao.obtenerPorId(1);
            assertThat(alquiler).isNotNull();
            assertThat(alquiler.getNumero_expediente()).isEqualTo(1);
        }
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("numero_expediente")).thenReturn(1);
        when(rs.getDate("fecha_inicio")).thenReturn(Date.valueOf(LocalDate.now()));
        when(rs.getInt("tiempo_meses")).thenReturn(1);
        when(rs.getInt("tiempo_dias")).thenReturn(2);
        when(rs.getDate("fecha_fin_estimada")).thenReturn(null);
        when(rs.getBigDecimal("precio_total_estimado")).thenReturn(BigDecimal.valueOf(1000));
        when(rs.getInt("id_cliente")).thenReturn(1);
        when(rs.getInt("id_vivienda")).thenReturn(2);
        when(rs.getInt("id_estado_cobro")).thenReturn(3);

        try (MockedStatic<ConexionBBDD> conexionBBDDStatic = mockStatic(ConexionBBDD.class)) {
            conexionBBDDStatic.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Alquiler> lista = dao.obtenerTodos();
            assertThat(lista).hasSize(1);
        }
    }

   
}
