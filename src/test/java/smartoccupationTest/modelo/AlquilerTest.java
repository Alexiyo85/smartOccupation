package smartoccupationTest.modelo;

import com.smartoccupation.modelo.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AlquilerTest {

    @Test
    void constructorCompletoAsignacionCorrecta() {
        LocalDate inicio = LocalDate.of(2024, 1, 1);

        Alquiler a = new Alquiler(
                10, inicio, 2, 15,
                inicio.plusMonths(2).plusDays(15),
                new BigDecimal("1000"),
                1, 2, 3
        );

        assertEquals(10, a.getNumeroExpediente());
        assertEquals(inicio, a.getFechaInicio());
        assertEquals(2, a.getTiempoMeses());
        assertEquals(15, a.getTiempoDias());
        assertEquals(1, a.getIdCliente());
        assertEquals(2, a.getIdVivienda());
        assertEquals(3, a.getIdEstadoCobro());
    }

    @Test
    void calcularFechaFinActualizaCorrectamente() {
        Alquiler a = new Alquiler();
        a.setFechaInicio(LocalDate.of(2024, 1, 1));
        a.setTiempoMeses(1);
        a.setTiempoDias(10);

        assertEquals(LocalDate.of(2024, 2, 11), a.getFechaFinEstimada());
    }

    @Test
    void precioTotalEstimadoNoPuedeSerNegativo() {
        Alquiler a = new Alquiler();
        assertThrows(IllegalArgumentException.class,
                () -> a.setPrecioTotalEstimado(new BigDecimal("-1")));
    }

    @Test
    void calcularPrecioTotalFuncionaCorrectamente() {
        Alquiler a = new Alquiler();
        a.setTiempoMeses(1);
        a.setTiempoDias(15);

        a.calcularPrecioTotal(new BigDecimal("300"));

        assertEquals(new BigDecimal("450.00"), a.getPrecioTotalEstimado());
    }

    @Test
    void setClienteActualizaIdCliente() {
        Cliente c = new Cliente();
        c.setIdCliente(50);
        c.setNombre("Juan");
        c.setPrimerApellido("Perez");
        c.setSegundoApellido("Lopez");
        c.setDni("12345678A");
        c.setDireccion("Calle 1");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigoPostal("28001");

        Alquiler a = new Alquiler();
        a.setCliente(c);

        assertEquals(50, a.getIdCliente());
    }

    @Test
    void toComboStringDevuelveFormatoCorrecto() {
        Cliente c = new Cliente();
        c.setIdCliente(1);
        c.setNombre("Ana");
        c.setPrimerApellido("Gomez");
        c.setSegundoApellido("Lopez");
        c.setDni("12345678A");
        c.setDireccion("Calle 1");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigoPostal("28001");

        Alquiler a = new Alquiler();
        a.setNumeroExpediente(40);
        a.setCliente(c);

        assertEquals("Expediente 40 - Ana", a.toComboString());
    }

    @Test
    void toStringDevuelveFormatoCorrecto() {
        Cliente c = new Cliente();
        c.setIdCliente(2);
        c.setNombre("Luis");
        c.setPrimerApellido("Martinez");
        c.setSegundoApellido("Ruiz");
        c.setDni("87654321B");
        c.setDireccion("Calle 3");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigoPostal("28002");

        Vivienda v = new Vivienda();
        v.setIdVivienda(5);
        v.setCodigoReferencia("REF2");
        v.setDireccion("Calle 5");
        v.setCiudad("Madrid");
        v.setProvincia("Madrid");
        v.setCodigoPostal("28002");
        v.setMetrosCuadrados(80);
        v.setNumeroHabitaciones(2);
        v.setNumeroBanios(1);
        v.setPrecioMensual(new BigDecimal("700"));
        v.setEstado("disponible");

        Alquiler a = new Alquiler();
        a.setNumeroExpediente(99);
        a.setCliente(c);
        a.setVivienda(v);

        assertEquals("[Exp. 99] Luis Martinez Ruiz - Calle 5", a.toString());
    }
}
