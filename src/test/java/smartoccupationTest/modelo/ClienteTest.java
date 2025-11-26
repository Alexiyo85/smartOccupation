package smartoccupationTest.modelo;

import com.smartoccupation.modelo.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {

    @Test
    void constructorCompletoDebeAsignarValoresCorrectamente() {
        Cliente c = new Cliente(
                1, "12345", "Madrid", "Madrid", "Calle Falsa 123",
                "666777888", "test@test.com", "12345678A",
                "Gomez", "Perez", "Juan"
        );

        assertEquals(1, c.getId_cliente());
        assertEquals("Juan", c.getNombre());
        assertEquals("Perez", c.getPrimer_apellido());
        assertEquals("Gomez", c.getSegundo_apellido());
        assertEquals("12345678A", c.getDni());
        assertEquals("test@test.com", c.getEmail());
        assertEquals("666777888", c.getTelefono());
        assertEquals("Madrid", c.getCiudad());
        assertEquals("Madrid", c.getProvincia());
        assertEquals("12345", c.getCodigo_postal());
    }

    @Test
    void nombreNoPuedeSerVacio() {
        Cliente c = new Cliente();
        assertThrows(IllegalArgumentException.class, () -> c.setNombre(""));
    }

    @Test
    void dniDebeTener9Caracteres() {
        Cliente c = new Cliente();
        assertThrows(IllegalArgumentException.class, () -> c.setDni("123"));
    }

    @Test
    void emailInvalidoDebeLanzarExcepcion() {
        Cliente c = new Cliente();
        assertThrows(IllegalArgumentException.class, () -> c.setEmail("correoSinArroba"));
    }

    @Test
    void telefonoDebeTener9Digitos() {
        Cliente c = new Cliente();
        assertThrows(IllegalArgumentException.class, () -> c.setTelefono("123"));
    }

    @Test
    void toStringDebeFormatearCorrectamente() {
        Cliente c = new Cliente();
        c.setId_cliente(1);
        c.setNombre("Juan");
        c.setPrimer_apellido("Perez");
        c.setDni("12345678A");

        assertEquals("Juan Perez (12345678A)", c.toString());
    }
}
