package smartoccupationTest.modelo;

import com.smartoccupation.modelo.EstadoCobro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCobroTest {

    @Test
    void constructorCompletoAsignaValores() {
        EstadoCobro e = new EstadoCobro(1, "pagado");

        assertEquals(1, e.getIdEstado());
        assertEquals("pagado", e.getNombreEstado());
    }

    @Test
    void settersFuncionanCorrectamente() {
        EstadoCobro e = new EstadoCobro();
        e.setIdEstado(2);
        e.setNombreEstado("pendiente");

        assertEquals(2, e.getIdEstado());
        assertEquals("pendiente", e.getNombreEstado());
    }

    @Test
    void toStringRetornaNombreEstado() {
        EstadoCobro e = new EstadoCobro(3, "retrasado");
        assertEquals("retrasado", e.toString());
    }
}
