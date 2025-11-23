package smartoccupationTest.dao;

import com.smartoccupation.dao.ClienteDAO;
import com.smartoccupation.modelo.Cliente;
import com.smartoccupation.utilidades.ConexionBBDD;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClienteDAOTest {

    private ClienteDAO clienteDAO;

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private Statement mockStatement;

    private MockedStatic<ConexionBBDD> conexionBBDDStaticMock;

    @BeforeEach
    void setUp() throws SQLException {
        clienteDAO = new ClienteDAO();

        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockStatement = mock(Statement.class);

        conexionBBDDStaticMock = mockStatic(ConexionBBDD.class);
        conexionBBDDStaticMock.when(ConexionBBDD::conectar).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        conexionBBDDStaticMock.close();
    }

    private Cliente crearClienteCompleto() {
        Cliente c = new Cliente();
        c.setId_cliente(1);
        c.setNombre("Juan");
        c.setPrimer_apellido("Pérez");
        c.setSegundo_apellido("Gómez");
        c.setDni("12345678A");
        c.setTelefono("600123456");
        c.setEmail("juan.perez@example.com");
        c.setDireccion("Calle Falsa 123");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigo_postal("28080");
        return c;
    }

    // -------------------------------
    // Test: insertar cliente
    // -------------------------------
    @Test
    void insertarCliente_exitoso() throws SQLException {
        Cliente c = crearClienteCompleto();

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        boolean resultado = clienteDAO.insertar(c);

        assertThat(resultado).isTrue();
        assertThat(c.getId_cliente()).isEqualTo(1);
        verify(mockPreparedStatement).executeUpdate();
    }

    // -------------------------------
    // Test: actualizar cliente
    // -------------------------------
    @Test
    void actualizarCliente_exitoso() throws SQLException {
        Cliente c = crearClienteCompleto();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean resultado = clienteDAO.actualizar(c);

        assertThat(resultado).isTrue();
        verify(mockPreparedStatement).executeUpdate();
    }

    // -------------------------------
    // Test: eliminar cliente
    // -------------------------------
    @Test
    void eliminarCliente_exitoso() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean resultado = clienteDAO.eliminar(1);

        assertThat(resultado).isTrue();
        verify(mockPreparedStatement).executeUpdate();
    }

    // -------------------------------
    // Test: obtener cliente por ID
    // -------------------------------
    @Test
    void obtenerClientePorId_exitoso() throws SQLException {
        Cliente c = crearClienteCompleto();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id_cliente")).thenReturn(c.getId_cliente());
        when(mockResultSet.getString("nombre")).thenReturn(c.getNombre());
        when(mockResultSet.getString("primer_apellido")).thenReturn(c.getPrimer_apellido());
        when(mockResultSet.getString("segundo_apellido")).thenReturn(c.getSegundo_apellido());
        when(mockResultSet.getString("dni_nif")).thenReturn(c.getDni());
        when(mockResultSet.getString("telefono")).thenReturn(c.getTelefono());
        when(mockResultSet.getString("email")).thenReturn(c.getEmail());
        when(mockResultSet.getString("direccion")).thenReturn(c.getDireccion());
        when(mockResultSet.getString("ciudad")).thenReturn(c.getCiudad());
        when(mockResultSet.getString("provincia")).thenReturn(c.getProvincia());
        when(mockResultSet.getString("codigo_postal")).thenReturn(c.getCodigo_postal());

        Cliente resultado = clienteDAO.obtenerPorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo(c.getNombre());
    }

    // -------------------------------
    // Test: obtener todos los clientes
    // -------------------------------
    @Test
    void obtenerTodosClientes_exitoso() throws SQLException {
        Cliente c = crearClienteCompleto();

        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);

        when(mockResultSet.getInt("id_cliente")).thenReturn(c.getId_cliente());
        when(mockResultSet.getString("nombre")).thenReturn(c.getNombre());
        when(mockResultSet.getString("primer_apellido")).thenReturn(c.getPrimer_apellido());
        when(mockResultSet.getString("segundo_apellido")).thenReturn(c.getSegundo_apellido());
        when(mockResultSet.getString("dni_nif")).thenReturn(c.getDni());
        when(mockResultSet.getString("telefono")).thenReturn(c.getTelefono());
        when(mockResultSet.getString("email")).thenReturn(c.getEmail());
        when(mockResultSet.getString("direccion")).thenReturn(c.getDireccion());
        when(mockResultSet.getString("ciudad")).thenReturn(c.getCiudad());
        when(mockResultSet.getString("provincia")).thenReturn(c.getProvincia());
        when(mockResultSet.getString("codigo_postal")).thenReturn(c.getCodigo_postal());

        List<Cliente> lista = clienteDAO.obtenerTodos();

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).getNombre()).isEqualTo(c.getNombre());
    }

    // -------------------------------
    // Test: obtener cliente por DNI
    // -------------------------------
    @Test
    void obtenerClientePorDni_exitoso() throws SQLException {
        Cliente c = crearClienteCompleto();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id_cliente")).thenReturn(c.getId_cliente());
        when(mockResultSet.getString("nombre")).thenReturn(c.getNombre());
        when(mockResultSet.getString("primer_apellido")).thenReturn(c.getPrimer_apellido());
        when(mockResultSet.getString("segundo_apellido")).thenReturn(c.getSegundo_apellido());
        when(mockResultSet.getString("dni_nif")).thenReturn(c.getDni());
        when(mockResultSet.getString("telefono")).thenReturn(c.getTelefono());
        when(mockResultSet.getString("email")).thenReturn(c.getEmail());
        when(mockResultSet.getString("direccion")).thenReturn(c.getDireccion());
        when(mockResultSet.getString("ciudad")).thenReturn(c.getCiudad());
        when(mockResultSet.getString("provincia")).thenReturn(c.getProvincia());
        when(mockResultSet.getString("codigo_postal")).thenReturn(c.getCodigo_postal());

        Cliente resultado = clienteDAO.obtenerPorDni("12345678A");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getDni()).isEqualTo("12345678A");
    }

    // -------------------------------
    // Test: manejar SQLException en insertar
    // -------------------------------
    @Test
    void insertarCliente_sqlException() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Error de prueba"));

        Cliente c = crearClienteCompleto();
        boolean resultado = clienteDAO.insertar(c);

        assertThat(resultado).isFalse();
    }

    // -------------------------------
    // Test: manejar SQLException en actualizar
    // -------------------------------
    @Test
    void actualizarCliente_sqlException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Error de prueba"));

        Cliente c = crearClienteCompleto();
        boolean resultado = clienteDAO.actualizar(c);

        assertThat(resultado).isFalse();
    }

    // -------------------------------
    // Test: manejar SQLException en eliminar
    // -------------------------------
    @Test
    void eliminarCliente_sqlException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Error de prueba"));

        boolean resultado = clienteDAO.eliminar(1);

        assertThat(resultado).isFalse();
    }

    // -------------------------------
    // Test: manejar SQLException en obtenerPorId
    // -------------------------------
    @Test
    void obtenerPorId_sqlException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Error de prueba"));

        Cliente resultado = clienteDAO.obtenerPorId(1);

        assertThat(resultado).isNull();
    }

    // -------------------------------
    // Test: manejar SQLException en obtenerTodos
    // -------------------------------
    @Test
    void obtenerTodos_sqlException() throws SQLException {
        when(mockConnection.createStatement()).thenThrow(new SQLException("Error de prueba"));

        List<Cliente> lista = clienteDAO.obtenerTodos();

        assertThat(lista).isEmpty();
    }

    // -------------------------------
    // Test: manejar SQLException en obtenerPorDni
    // -------------------------------
    @Test
    void obtenerPorDni_sqlException() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Error de prueba"));

        Cliente resultado = clienteDAO.obtenerPorDni("12345678A");

        assertThat(resultado).isNull();
    }
}
