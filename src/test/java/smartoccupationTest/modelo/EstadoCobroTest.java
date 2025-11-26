package smartoccupationTest.modelo;

import com.smartoccupation.modelo.EstadoCobro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoCobroTest {

    @Test
    void constructorCompletoAsignaValores() {
        EstadoCobro e = new EstadoCobro(1, "pagado");

        assertEquals(1, e.getId_estado());
        assertEquals("pagado", e.getNombre_estado());
    }

    @Test
    void settersFuncionanCorrectamente() {
        EstadoCobro e = new EstadoCobro();
        e.setId_estado(2);
        e.setNombre_estado("pendiente");

        assertEquals(2, e.getId_estado());
        assertEquals("pendiente", e.getNombre_estado());
    }

    @Test
    void toStringRetornaNombreEstado() {
        EstadoCobro e = new EstadoCobro(3, "retrasado");
        assertEquals("retrasado", e.toString());
    }
}
