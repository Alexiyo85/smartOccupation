package smartoccupationTest.modelo;

import com.smartoccupation.modelo.Pago;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PagoTest {

    @Test
    void constructorCompletoAsignaValoresCorrectamente() {
        LocalDate fecha = LocalDate.of(2024, 1, 1);

        Pago p = new Pago(1, 999, fecha, new BigDecimal("100"));

        assertEquals(1, p.getId_pago());
        assertEquals(999, p.getNumero_expediente());
        assertEquals(fecha, p.getFecha_pago());
        assertEquals(new BigDecimal("100"), p.getCantidad());
    }

    @Test
    void settersFuncionanCorrectamente() {
        Pago p = new Pago();
        p.setId_pago(10);
        p.setNumero_expediente(200);
        p.setFecha_pago(LocalDate.of(2024, 5, 5));
        p.setCantidad(new BigDecimal("250"));

        assertEquals(10, p.getId_pago());
        assertEquals(200, p.getNumero_expediente());
        assertEquals(LocalDate.of(2024, 5, 5), p.getFecha_pago());
        assertEquals(new BigDecimal("250"), p.getCantidad());
    }
}
