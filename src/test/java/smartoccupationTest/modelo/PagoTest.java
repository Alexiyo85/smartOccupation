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

        assertEquals(1, p.getIdPago());
        assertEquals(999, p.getNumeroExpediente());
        assertEquals(fecha, p.getFechaPago());
        assertEquals(new BigDecimal("100"), p.getCantidad());
    }

    @Test
    void settersFuncionanCorrectamente() {
        Pago p = new Pago();
        p.setId_pago(10);
        p.setNumeroExpediente(200);
        p.setFechaPago(LocalDate.of(2024, 5, 5));
        p.setCantidad(new BigDecimal("250"));

        assertEquals(10, p.getIdPago());
        assertEquals(200, p.getNumeroExpediente());
        assertEquals(LocalDate.of(2024, 5, 5), p.getFechaPago());
        assertEquals(new BigDecimal("250"), p.getCantidad());
    }
}
