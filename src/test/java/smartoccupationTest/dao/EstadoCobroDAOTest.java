package smartoccupationTest.dao;

import com.smartoccupation.dao.EstadoCobroDAO;
import com.smartoccupation.modelo.EstadoCobro;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EstadoCobroDAOTest {

    private EstadoCobroDAO dao;
    private Connection mockConn;
    private PreparedStatement mockPs;
    private ResultSet mockRs;
    private MockedStatic<ConexionBBDD> conexionMock;

    @BeforeEach
    void setUp() {
        dao = new EstadoCobroDAO();
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

    // ----------------------------
    // OBTENER TODOS
    // ----------------------------
    @Test
    void testObtenerTodosExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true, true, false); // Dos registros
        when(mockRs.getInt("id_estado")).thenReturn(1, 2);
        when(mockRs.getString("nombre_estado")).thenReturn("pendiente", "pagado");

        List<EstadoCobro> lista = dao.obtenerTodos();

        assertThat(lista).hasSize(2);
        assertThat(lista.get(0).getNombre_estado()).isEqualTo("pendiente");
        assertThat(lista.get(1).getNombre_estado()).isEqualTo("pagado");
    }

    @Test
    void testObtenerTodosSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.obtenerTodos())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error obteniendo estados de cobro");
    }

    // ----------------------------
    // OBTENER POR ID
    // ----------------------------
    @Test
    void testObtenerPorIdExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("id_estado")).thenReturn(1);
        when(mockRs.getString("nombre_estado")).thenReturn("pendiente");

        EstadoCobro estado = dao.obtenerPorId(1);

        assertThat(estado).isNotNull();
        assertThat(estado.getNombre_estado()).isEqualTo("pendiente");
    }

    @Test
    void testObtenerPorIdNoExiste() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

        EstadoCobro estado = dao.obtenerPorId(99);
        assertThat(estado).isNull();
    }

    @Test
    void testObtenerPorIdSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.obtenerPorId(1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error consultando estado de cobro por id");
    }

    // ----------------------------
    // OBTENER POR NOMBRE
    // ----------------------------
    @Test
    void testObtenerPorNombreExitoso() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt("id_estado")).thenReturn(2);
        when(mockRs.getString("nombre_estado")).thenReturn("pagado");

        EstadoCobro estado = dao.obtenerPorNombre("pagado");

        assertThat(estado).isNotNull();
        assertThat(estado.getId_estado()).isEqualTo(2);
    }

    @Test
    void testObtenerPorNombreNoExiste() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);

        EstadoCobro estado = dao.obtenerPorNombre("inexistente");
        assertThat(estado).isNull();
    }

    @Test
    void testObtenerPorNombreSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Simulado"));

        assertThatThrownBy(() -> dao.obtenerPorNombre("pendiente"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error consultando estado de cobro por nombre");
    }
}
