package smartoccupationTest.utilidades;

import com.smartoccupation.utilidades.LogManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class LogManagerTest {

    private static final String LOG_FILE = "app.log";

    @BeforeEach
    void limpiarLog() throws IOException {
        File file = new File(LOG_FILE);
        if (file.exists()) {
            Files.delete(file.toPath());
        }
    }

    @Test
    void info_escribeSinExcepcion() {
        assertDoesNotThrow(() -> LogManager.info("Mensaje de info"));
        File file = new File(LOG_FILE);
        assertTrue(file.exists());
    }

    @Test
    void error_escribeSinExcepcion() {
        assertDoesNotThrow(() -> LogManager.error("Mensaje de error"));
        File file = new File(LOG_FILE);
        assertTrue(file.exists());
    }

    @Test
    void error_conException_escribeSinExcepcion() {
        Exception ex = new Exception("Simulado");
        assertDoesNotThrow(() -> LogManager.error("Mensaje error con excepción", ex));
        File file = new File(LOG_FILE);
        assertTrue(file.exists());
    }
}
