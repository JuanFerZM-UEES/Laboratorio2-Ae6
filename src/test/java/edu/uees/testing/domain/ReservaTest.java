package edu.uees.testing.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Prueba añadida a partir del reporte JaCoCo de la Actividad 2
 * (docs/02_ANALISIS_COBERTURA_PLANTILLA.md, sección 11).
 *
 * El reporte generado después de agregar los Stub/Mock de confirmar()
 * mostró que ReservaService ya queda en 100% de líneas y ramas, pero
 * Reserva (la clase de dominio que ReservaService.confirmar() usa)
 * solo llegaba a 64.3% de líneas y 50% de ramas: su constructor valida
 * "id == null || id.isBlank()" y ninguna prueba existente ejercitaba
 * esa rama. Es la regla más importante del constructor (evita crear
 * una reserva sin identificador) y no tenía ninguna protección.
 */
class ReservaTest {

    @Test
    void reservaConIdNuloLanzaExcepcion() {
        // Arrange / Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva(null, "NORMAL")
        );

        // Assert
        assertEquals("Id obligatorio", ex.getMessage());
    }

    @Test
    void reservaConIdEnBlancoLanzaExcepcion() {
        // Arrange / Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Reserva("   ", "NORMAL")
        );

        // Assert
        assertEquals("Id obligatorio", ex.getMessage());
    }

    @Test
    void tipoNuloSeNormalizaComoNormal() {
        // Arrange / Act
        Reserva reserva = new Reserva("R-010", null);

        // Assert: el constructor normaliza tipo == null a "NORMAL",
        // otra regla de negocio que no tenía ninguna prueba directa.
        assertEquals("NORMAL", reserva.getTipo());
        assertEquals("R-010", reserva.getId());
    }
}
