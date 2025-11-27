package smartoccupationTest.modelo;

import com.smartoccupation.modelo.Vivienda;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ViviendaTest {

    @Test
    void constructorCompletoAsignaValoresCorrectamente() {
        Vivienda v = new Vivienda(
                1, "REF123", "Calle 1", "Madrid", "Madrid",
                "28080", 80, 3, 1,
                new BigDecimal("750.50"), "disponible"
        );

        assertEquals(1, v.getIdVivienda());
        assertEquals("REF123", v.getCodigoReferencia());
        assertEquals("Calle 1", v.getDireccion());
        assertEquals("Madrid", v.getCiudad());
        assertEquals("Madrid", v.getProvincia());
        assertEquals("28080", v.getCodigoPostal());
        assertEquals(80, v.getMetrosCuadrados());
        assertEquals(3, v.getNumeroHabitaciones());
        assertEquals(1, v.getNumeroBanios());
        assertEquals(new BigDecimal("750.50"), v.getPrecio_mensual());
        assertEquals("disponible", v.getEstado());
    }

    @Test
    void codigoReferenciaNoPuedeSerVacio() {
        Vivienda v = new Vivienda();
        assertThrows(IllegalArgumentException.class, () -> v.setCodigoReferencia(""));
    }

    @Test
    void precioMensualNegativoDebeLanzarExcepcion() {
        Vivienda v = new Vivienda();
        assertThrows(IllegalArgumentException.class, () -> v.setPrecioMensual(new BigDecimal("-1")));
    }

    @Test
    void estadoInvalidoDebeLanzarExcepcion() {
        Vivienda v = new Vivienda();
        assertThrows(IllegalArgumentException.class, () -> v.setEstado("xyz"));
    }

    @Test
    void toStringDebeRetornarFormatoCorrecto() {
        Vivienda v = new Vivienda(
                5, "REF10", "Calle 5", "Madrid", "Madrid",
                "28001", 90, 4, 2,
                new BigDecimal("1000"), "ocupado"
        );

        assertEquals("5, REF10, Calle 5, ocupado", v.toString());
    }
}
