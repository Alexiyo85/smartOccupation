package com.smartoccupation.dao;

import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private ClienteDAO dao;

    private Connection connection;
    private PreparedStatement ps;
    private Statement stmt;
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        dao = new ClienteDAO();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
    }

    @Test
    void testInsertarExitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setPrimer_apellido("Pérez");
        cliente.setSegundo_apellido("García");
        cliente.setDni("12345678A");
        cliente.setTelefono("600123456");
        cliente.setEmail("juan@mail.com");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setCiudad("Madrid");
        cliente.setProvincia("Madrid");
        cliente.setCodigo_postal("28001");

        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(10);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.insertar(cliente);

            assertThat(resultado).isTrue();
            assertThat(cliente.getId_cliente()).isEqualTo(10);
        }

        verify(ps).executeUpdate();
        verify(ps).getGeneratedKeys();
    }

    @Test
    void testActualizarExitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId_cliente(10);
        cliente.setNombre("Juan");

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.actualizar(cliente);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testEliminarExitoso() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            boolean resultado = dao.eliminar(10);
            assertThat(resultado).isTrue();
        }
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id_cliente")).thenReturn(10);
        when(rs.getString("nombre")).thenReturn("Juan");
        when(rs.getString("primer_apellido")).thenReturn("Pérez");
        when(rs.getString("segundo_apellido")).thenReturn("García");
        when(rs.getString("dni_nif")).thenReturn("12345678A");
        when(rs.getString("telefono")).thenReturn("600123456");
        when(rs.getString("email")).thenReturn("juan@mail.com");
        when(rs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28001");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            Cliente cliente = dao.obtenerPorId(10);
            assertThat(cliente).isNotNull();
            assertThat(cliente.getNombre()).isEqualTo("Juan");
        }
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id_cliente")).thenReturn(10);
        when(rs.getString("nombre")).thenReturn("Juan");
        when(rs.getString("primer_apellido")).thenReturn("Pérez");
        when(rs.getString("segundo_apellido")).thenReturn("García");
        when(rs.getString("dni_nif")).thenReturn("12345678A");
        when(rs.getString("telefono")).thenReturn("600123456");
        when(rs.getString("email")).thenReturn("juan@mail.com");
        when(rs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28001");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<Cliente> lista = dao.obtenerTodos();
            assertThat(lista).hasSize(1);
            assertThat(lista.get(0).getNombre()).isEqualTo("Juan");
        }
    }

    @Test
    void testObtenerPorDniExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id_cliente")).thenReturn(10);
        when(rs.getString("nombre")).thenReturn("Juan");
        when(rs.getString("primer_apellido")).thenReturn("Pérez");
        when(rs.getString("segundo_apellido")).thenReturn("García");
        when(rs.getString("dni_nif")).thenReturn("12345678A");
        when(rs.getString("telefono")).thenReturn("600123456");
        when(rs.getString("email")).thenReturn("juan@mail.com");
        when(rs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(rs.getString("ciudad")).thenReturn("Madrid");
        when(rs.getString("provincia")).thenReturn("Madrid");
        when(rs.getString("codigo_postal")).thenReturn("28001");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            Cliente cliente = dao.obtenerPorDni("12345678A");
            assertThat(cliente).isNotNull();
            assertThat(cliente.getDni()).isEqualTo("12345678A");
        }
    }
}
