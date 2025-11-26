package smartoccupationTest.dao;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EstadoCobroDAOTest {

    private EstadoCobroDAO dao;
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        dao = new EstadoCobroDAO();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
    }

    @Test
    void testObtenerTodos() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("id_estado")).thenReturn(1, 2);
        when(rs.getString("nombre_estado")).thenReturn("pendiente", "pagado");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            List<EstadoCobro> lista = dao.obtenerTodos();
            assertThat(lista).hasSize(2);
            assertThat(lista.get(0).getNombre_estado()).isEqualTo("pendiente");
            assertThat(lista.get(1).getNombre_estado()).isEqualTo("pagado");
        }
    }

    @Test
    void testObtenerPorIdExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id_estado")).thenReturn(1);
        when(rs.getString("nombre_estado")).thenReturn("pendiente");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            EstadoCobro estado = dao.obtenerPorId(1);
            assertThat(estado).isNotNull();
            assertThat(estado.getId_estado()).isEqualTo(1);
            assertThat(estado.getNombre_estado()).isEqualTo("pendiente");
        }
    }

    @Test
    void testObtenerPorIdNoExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            EstadoCobro estado = dao.obtenerPorId(99);
            assertThat(estado).isNull();
        }
    }

    @Test
    void testObtenerPorNombreExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id_estado")).thenReturn(2);
        when(rs.getString("nombre_estado")).thenReturn("pagado");

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            EstadoCobro estado = dao.obtenerPorNombre("pagado");
            assertThat(estado).isNotNull();
            assertThat(estado.getId_estado()).isEqualTo(2);
            assertThat(estado.getNombre_estado()).isEqualTo("pagado");
        }
    }

    @Test
    void testObtenerPorNombreNoExistente() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<ConexionBBDD> conexionMock = mockStatic(ConexionBBDD.class)) {
            conexionMock.when(ConexionBBDD::conectar).thenReturn(connection);

            EstadoCobro estado = dao.obtenerPorNombre("inexistente");
            assertThat(estado).isNull();
        }
    }
}
