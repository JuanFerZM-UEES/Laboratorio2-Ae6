package edu.uees.testing.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite de la Actividad 1 (Semana 7): diseño de casos + JUnit 5.
 *
 * Solo se prueban puedeCancelar(int) y calcularTotal(String, double),
 * porque son puros y no dependen de DisponibilidadClient, ReservaRepository
 * ni Notificador. Por eso el servicio se construye con esos tres
 * colaboradores en null: es válido únicamente porque estos dos métodos no
 * los consultan (confirmar(Reserva) sí lo haría, y se cubre en el
 * Laboratorio 2 con Stub y Mock).
 *
 * Cada prueba corresponde a un caso de docs/01_MATRIZ_CASOS_PLANTILLA.md
 * (se referencia el ID CP-xx en el comentario).
 */
class ReservaServiceTest {

    private final ReservaService servicio =
            new ReservaService(null, null, null);

    @Test
    void entornoJUnitFunciona() {
        assertTrue(true);
    }

    // ---------- 3.1 Regla de cancelación (puedeCancelar) ----------

    @Test
    void cincoHorasPermitenCancelar() {
        // CP-01 · Anticipación habitual

        // Arrange
        int horas = 5;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void dosHorasEsElLimitePermitido() {
        // CP-02 · Límite permitido (frontera >=)

        // Arrange
        int horas = 2;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void unaHoraNoPermiteCancelar() {
        // CP-03 · Debajo del límite

        // Arrange
        int horas = 1;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertFalse(resultado);
    }

    @Test
    void ceroHorasNoPermiteCancelar() {
        // CP-04 · Sin anticipación (caso extremo)

        // Arrange
        int horas = 0;

        // Act
        boolean resultado = servicio.puedeCancelar(horas);

        // Assert
        assertFalse(resultado);
    }

    // ---------- 3.2 Regla de descuentos (calcularTotal) ----------

    @Test
    void normalNoRecibeDescuento() {
        // CP-05 · Tipo NORMAL

        // Arrange
        String tipo = "NORMAL";
        double totalBase = 100;

        // Act
        double total = servicio.calcularTotal(tipo, totalBase);

        // Assert
        assertEquals(100.0, total, 0.001);
    }

    @Test
    void vipRecibeQuincePorCiento() {
        // CP-06 · Tipo VIP

        // Arrange
        String tipo = "VIP";
        double totalBase = 100;

        // Act
        double total = servicio.calcularTotal(tipo, totalBase);

        // Assert
        assertEquals(85.0, total, 0.001);
    }

    @Test
    void estudianteRecibeDiezPorCiento() {
        // CP-07 · Tipo ESTUDIANTE

        // Arrange
        String tipo = "ESTUDIANTE";
        double totalBase = 100;

        // Act
        double total = servicio.calcularTotal(tipo, totalBase);

        // Assert
        assertEquals(90.0, total, 0.001);
    }

    @Test
    void totalCeroNoProduceDescuentoNegativo() {
        // CP-08 · Total cero (caso límite)

        // Arrange
        String tipo = "VIP";
        double totalBase = 0;

        // Act
        double total = servicio.calcularTotal(tipo, totalBase);

        // Assert
        assertEquals(0.0, total, 0.001);
    }

    @Test
    void totalNegativoEsInvalido() {
        // CP-09 · Total negativo (entrada inválida)

        // Arrange
        String tipo = "NORMAL";
        double totalBase = -1;

        // Act
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.calcularTotal(tipo, totalBase)
        );

        // Assert
        assertEquals("Total base inválido", ex.getMessage());
    }
}
