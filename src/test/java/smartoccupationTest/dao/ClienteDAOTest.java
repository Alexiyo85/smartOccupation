package smartoccupationTest.dao;

import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;

    // Mocks JDBC (sin el MockedStatic, que va dentro del try-with-resources)
    private Connection mockConn;
    private PreparedStatement mockPs;
    private Statement mockStatement;
    private ResultSet mockRs;

    @BeforeEach
    void setUp() throws Exception {
        clienteDAO = new ClienteDAO();

        mockConn = mock(Connection.class);
        mockPs = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);
        mockRs = mock(ResultSet.class);
        
        // La inicialización del MockedStatic se mueve a los bloques try
    }

    // --- INSERTAR ---
    @Test
    void testInsertarCliente_exito() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);
            
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
    }

    @Test
    void testInsertarCliente_errorSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);
            
            Cliente c = crearCliente();

            when(mockConn.prepareStatement(anyString(), anyInt()))
                    .thenThrow(new SQLException("Error simulado"));

            boolean resultado = clienteDAO.insertar(c);

            assertFalse(resultado);
        }
    }

    // --- ACTUALIZAR ---
    @Test
    void testActualizarCliente_exito() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            Cliente c = crearCliente();
            c.setId_cliente(10);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            boolean resultado = clienteDAO.actualizar(c);

            assertTrue(resultado);
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void testActualizarCliente_errorSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            Cliente c = crearCliente();

            when(mockConn.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Error simulado"));

            boolean resultado = clienteDAO.actualizar(c);

            assertFalse(resultado);
        }
    }

    // --- ELIMINAR ---
    @Test
    void testEliminarCliente_exito() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeUpdate()).thenReturn(1);

            boolean resultado = clienteDAO.eliminar(5);

            assertTrue(resultado);
        }
    }

    @Test
    void testEliminarCliente_errorSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString()))
                    .thenThrow(new SQLException("Error simulado"));

            boolean resultado = clienteDAO.eliminar(5);

            assertFalse(resultado);
        }
    }

    // --- OBTENER POR ID ---
    @Test
    void testObtenerPorId_encontrado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true); // <<-- CORRECCIÓN 1: Asegura que next() devuelve true
            mockearResultadoUnCliente(); 

            Cliente c = clienteDAO.obtenerPorId(10);

            assertNotNull(c);
            assertEquals(10, c.getId_cliente());
            assertEquals("Juan", c.getNombre());
        }
    }

    @Test
    void testObtenerPorId_noEncontrado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(false);

            Cliente c = clienteDAO.obtenerPorId(10);

            assertNull(c);
        }
    }

    // --- OBTENER TODOS ---
    @Test
    void testObtenerTodos_exito() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockRs);

            // CORRECCIÓN 3: Stubbing secuencial para dos filas completas (para evitar IllegalArgumentException)
            when(mockRs.next()).thenReturn(true, true, false);

            // Primera fila (Cliente 10)
            when(mockRs.getInt("id_cliente")).thenReturn(10).thenReturn(20);
            when(mockRs.getString("nombre")).thenReturn("Juan").thenReturn("Pedro");
            when(mockRs.getString("primer_apellido")).thenReturn("Pérez").thenReturn("López"); 
            when(mockRs.getString("segundo_apellido")).thenReturn("Gómez").thenReturn("García");
            when(mockRs.getString("dni_nif")).thenReturn("12345678A").thenReturn("87654321B");
            when(mockRs.getString("telefono")).thenReturn("600123123").thenReturn("600456456");
            when(mockRs.getString("email")).thenReturn("test1@test.com").thenReturn("test2@test.com");
            when(mockRs.getString("direccion")).thenReturn("Calle Falsa 123").thenReturn("Avenida Real 45");
            when(mockRs.getString("ciudad")).thenReturn("Madrid").thenReturn("Barcelona");
            when(mockRs.getString("provincia")).thenReturn("Madrid").thenReturn("Barcelona");
            when(mockRs.getString("codigo_postal")).thenReturn("28000").thenReturn("08000");

            List<Cliente> lista = clienteDAO.obtenerTodos();

            assertEquals(2, lista.size());
            assertEquals("Pedro", lista.get(1).getNombre());
        }
    }

    @Test
    void testObtenerTodos_errorSQLException() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.createStatement())
                    .thenThrow(new SQLException("Error simulado"));

            List<Cliente> lista = clienteDAO.obtenerTodos();

            assertTrue(lista.isEmpty());
        }
    }

    // --- OBTENER POR DNI ---
    @Test
    void testObtenerPorDni_encontrado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true); // <<-- CORRECCIÓN 2: Asegura que next() devuelve true
            mockearResultadoUnCliente();

            Cliente c = clienteDAO.obtenerPorDni("12345678A");

            assertNotNull(c);
            assertEquals("12345678A", c.getDni());
        }
    }

    @Test
    void testObtenerPorDni_noEncontrado() throws Exception {
        try (MockedStatic<ConexionBBDD> conexionMockStatic = mockStatic(ConexionBBDD.class)) {
            conexionMockStatic.when(ConexionBBDD::conectar).thenReturn(mockConn);

            when(mockConn.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(false);

            Cliente c = clienteDAO.obtenerPorDni("00000000Z");

            assertNull(c);
        }
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
        // NO mockeamos when(mockRs.next()).thenReturn(true); aquí. Se hace en el test.
        // Usamos thenReturn(valor) porque los tests individuales solo esperan una llamada.
        
        // CORRECCIÓN: Los valores de thenReturn() deben ser valores únicos, no cadenas.
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