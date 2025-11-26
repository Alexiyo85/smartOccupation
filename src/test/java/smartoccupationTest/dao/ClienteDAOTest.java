package smartoccupationTest.dao;

import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;

    // Mocks JDBC
    private Connection mockConn;
    private PreparedStatement mockPs;
    private Statement mockStatement;
    private ResultSet mockRs;

    // Mock para método estático
    private MockedStatic<ConexionBBDD> conexionMock;

    @BeforeEach
    void setUp() throws Exception {
        clienteDAO = new ClienteDAO();

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

    // --- INSERTAR ---
    @Test
    void testInsertarCliente_exito() throws Exception {
        Cliente c = crearCliente();

        when(mockConn.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);

        when(mockPs.getGeneratedKeys()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getInt(1)).thenReturn(123);

        boolean resultado = clienteDAO.insertar(c);

        assertTrue(resultado);
        assertEquals(123, c.getId_cliente());
        verify(mockPs, times(1)).executeUpdate();
    }

    @Test
    void testInsertarCliente_errorSQLException() throws Exception {
        Cliente c = crearCliente();

        when(mockConn.prepareStatement(anyString(), anyInt()))
                .thenThrow(new SQLException("Error simulado"));

        boolean resultado = clienteDAO.insertar(c);

        assertFalse(resultado);
    }

    // --- ACTUALIZAR ---
    @Test
    void testActualizarCliente_exito() throws Exception {
        Cliente c = crearCliente();
        c.setId_cliente(10);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);

        boolean resultado = clienteDAO.actualizar(c);

        assertTrue(resultado);
        verify(mockPs).executeUpdate();
    }

    @Test
    void testActualizarCliente_errorSQLException() throws Exception {
        Cliente c = crearCliente();

        when(mockConn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Error simulado"));

        boolean resultado = clienteDAO.actualizar(c);

        assertFalse(resultado);
    }

    // --- ELIMINAR ---
    @Test
    void testEliminarCliente_exito() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);

        boolean resultado = clienteDAO.eliminar(5);

        assertTrue(resultado);
    }

    @Test
    void testEliminarCliente_errorSQLException() throws Exception {
        when(mockConn.prepareStatement(anyString()))
                .thenThrow(new SQLException("Error simulado"));

        boolean resultado = clienteDAO.eliminar(5);

        assertFalse(resultado);
    }

    // --- OBTENER POR ID ---
    @Test
    void testObtenerPorId_encontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        mockearResultadoUnCliente();

        Cliente c = clienteDAO.obtenerPorId(10);

        assertNotNull(c);
        assertEquals(10, c.getId_cliente());
        assertEquals("Juan", c.getNombre());
    }

    @Test
    void testObtenerPorId_noEncontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(false);

        Cliente c = clienteDAO.obtenerPorId(10);

        assertNull(c);
    }

    // --- OBTENER TODOS ---
    @Test
    void testObtenerTodos_exito() throws Exception {
        when(mockConn.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockRs);

        // Dos filas
        when(mockRs.next()).thenReturn(true, true, false);
        mockearResultadoUnCliente();
        mockearResultadoUnCliente(); // segunda fila

        List<Cliente> lista = clienteDAO.obtenerTodos();

        assertEquals(2, lista.size());
    }

    @Test
    void testObtenerTodos_errorSQLException() throws Exception {
        when(mockConn.createStatement())
                .thenThrow(new SQLException("Error simulado"));

        List<Cliente> lista = clienteDAO.obtenerTodos();

        assertTrue(lista.isEmpty());
    }

    // --- OBTENER POR DNI ---
    @Test
    void testObtenerPorDni_encontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        mockearResultadoUnCliente();

        Cliente c = clienteDAO.obtenerPorDni("12345678A");

        assertNotNull(c);
        assertEquals("12345678A", c.getDni());
    }

    @Test
    void testObtenerPorDni_noEncontrado() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(false);

        Cliente c = clienteDAO.obtenerPorDni("00000000Z");

        assertNull(c);
    }

    // -----------------------
    // MÉTODOS DE APOYO
    // -----------------------

    private Cliente crearCliente() {
        Cliente c = new Cliente();
        c.setNombre("Juan");
        c.setPrimer_apellido("Pérez");
        c.setSegundo_apellido("Gómez");
        c.setDni("12345678A");
        c.setTelefono("600123123");
        c.setEmail("test@test.com");
        c.setDireccion("Calle Falsa 123");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigo_postal("28000");
        return c;
    }

    private void mockearResultadoUnCliente() throws Exception {
        when(mockRs.next()).thenReturn(true);

        when(mockRs.getInt("id_cliente")).thenReturn(10);
        when(mockRs.getString("nombre")).thenReturn("Juan");
        when(mockRs.getString("primer_apellido")).thenReturn("Pérez");
        when(mockRs.getString("segundo_apellido")).thenReturn("Gómez");
        when(mockRs.getString("dni_nif")).thenReturn("12345678A");
        when(mockRs.getString("telefono")).thenReturn("600123123");
        when(mockRs.getString("email")).thenReturn("test@test.com");
        when(mockRs.getString("direccion")).thenReturn("Calle Falsa 123");
        when(mockRs.getString("ciudad")).thenReturn("Madrid");
        when(mockRs.getString("provincia")).thenReturn("Madrid");
        when(mockRs.getString("codigo_postal")).thenReturn("28000");
    }
}
