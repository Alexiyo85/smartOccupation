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

        assertEquals(10, a.getNumero_expediente());
        assertEquals(inicio, a.getFecha_inicio());
        assertEquals(2, a.getTiempo_meses());
        assertEquals(15, a.getTiempo_dias());
        assertEquals(1, a.getId_cliente());
        assertEquals(2, a.getId_vivienda());
        assertEquals(3, a.getId_estado_cobro());
    }

    @Test
    void calcularFechaFinActualizaCorrectamente() {
        Alquiler a = new Alquiler();
        a.setFecha_inicio(LocalDate.of(2024, 1, 1));
        a.setTiempo_meses(1);
        a.setTiempo_dias(10);

        assertEquals(LocalDate.of(2024, 2, 11), a.getFecha_fin_estimada());
    }

    @Test
    void precioTotalEstimadoNoPuedeSerNegativo() {
        Alquiler a = new Alquiler();
        assertThrows(IllegalArgumentException.class,
                () -> a.setPrecio_total_estimado(new BigDecimal("-1")));
    }

    @Test
    void calcularPrecioTotalFuncionaCorrectamente() {
        Alquiler a = new Alquiler();
        a.setTiempo_meses(1);
        a.setTiempo_dias(15);

        a.calcularPrecioTotal(new BigDecimal("300"));

        assertEquals(new BigDecimal("450.00"), a.getPrecio_total_estimado());
    }

    @Test
    void setClienteActualizaIdCliente() {
        Cliente c = new Cliente();
        c.setId_cliente(50);
        c.setNombre("Juan");
        c.setPrimer_apellido("Perez");
        c.setSegundo_apellido("Lopez");
        c.setDni("12345678A");
        c.setDireccion("Calle 1");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigo_postal("28001");

        Alquiler a = new Alquiler();
        a.setCliente(c);

        assertEquals(50, a.getId_cliente());
    }

    @Test
    void toComboStringDevuelveFormatoCorrecto() {
        Cliente c = new Cliente();
        c.setId_cliente(1);
        c.setNombre("Ana");
        c.setPrimer_apellido("Gomez");
        c.setSegundo_apellido("Lopez");
        c.setDni("12345678A");
        c.setDireccion("Calle 1");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigo_postal("28001");

        Alquiler a = new Alquiler();
        a.setNumero_expediente(40);
        a.setCliente(c);

        assertEquals("Expediente 40 - Ana", a.toComboString());
    }

    @Test
    void toStringDevuelveFormatoCorrecto() {
        Cliente c = new Cliente();
        c.setId_cliente(2);
        c.setNombre("Luis");
        c.setPrimer_apellido("Martinez");
        c.setSegundo_apellido("Ruiz");
        c.setDni("87654321B");
        c.setDireccion("Calle 3");
        c.setCiudad("Madrid");
        c.setProvincia("Madrid");
        c.setCodigo_postal("28002");

        Vivienda v = new Vivienda();
        v.setId_vivienda(5);
        v.setCodigo_referencia("REF2");
        v.setDireccion("Calle 5");
        v.setCiudad("Madrid");
        v.setProvincia("Madrid");
        v.setCodigo_postal("28002");
        v.setMetros_cuadrados(80);
        v.setNumero_habitaciones(2);
        v.setNumero_banios(1);
        v.setPrecio_mensual(new BigDecimal("700"));
        v.setEstado("disponible");

        Alquiler a = new Alquiler();
        a.setNumero_expediente(99);
        a.setCliente(c);
        a.setVivienda(v);

        assertEquals("[Exp. 99] Luis Martinez Ruiz - Calle 5", a.toString());
    }
}
