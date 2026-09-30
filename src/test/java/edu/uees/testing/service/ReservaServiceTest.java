package edu.uees.testing.service;

import edu.uees.testing.availability.DisponibilidadClient;
import edu.uees.testing.domain.EstadoReserva;
import edu.uees.testing.domain.Reserva;
import edu.uees.testing.notification.Notificador;
import edu.uees.testing.repository.ReservaRepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Suite de las Actividades 1 y 2 (Semana 7).
 *
 * Actividad 1: puedeCancelar(int) y calcularTotal(String, double) son
 * puros y no dependen de DisponibilidadClient, ReservaRepository ni
 * Notificador. Por eso el campo {@code servicio} se construye con esos
 * tres colaboradores en null: es válido únicamente porque estos dos
 * métodos no los consultan.
 *
 * Actividad 2: confirmar(Reserva) sí depende de los tres colaboradores,
 * así que aquí ya no es correcto usar null. Cada prueba de esta sección
 * crea sus propios dobles con Mockito: DisponibilidadClient se usa como
 * Stub (controla la respuesta con when/thenReturn) y ReservaRepository /
 * Notificador se usan como Mock (se verifica la interacción con
 * verify/never).
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

    // ---------- Actividad 2 · confirmar(Reserva) con Stub y Mock ----------

    @Test
    void reservaDisponibleSeConfirmaGuardaYNotifica() {
        // CP-10 · Camino feliz: disponibilidad = true

        // Arrange
        DisponibilidadClient disponibilidad = mock(DisponibilidadClient.class);
        ReservaRepository repository = mock(ReservaRepository.class);
        Notificador notificador = mock(Notificador.class);

        // Stub: controla la respuesta de disponibilidad
        when(disponibilidad.estaDisponible(any())).thenReturn(true);

        ReservaService servicioConDependencias = new ReservaService(
                disponibilidad, repository, notificador);

        Reserva reserva = new Reserva("R-001", "NORMAL");

        // Act
        servicioConDependencias.confirmar(reserva);

        // Assert
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());

        // Mock: verifica las interacciones relevantes
        verify(repository).guardar(reserva);
        verify(notificador).enviarConfirmacion(reserva);
    }

    @Test
    void reservaNoDisponibleNoSeGuardaNiNotifica() {
        // CP-11 · Sin disponibilidad: debe rechazar sin efectos secundarios

        // Arrange
        DisponibilidadClient disponibilidad = mock(DisponibilidadClient.class);
        ReservaRepository repository = mock(ReservaRepository.class);
        Notificador notificador = mock(Notificador.class);

        when(disponibilidad.estaDisponible(any())).thenReturn(false);

        ReservaService servicioConDependencias = new ReservaService(
                disponibilidad, repository, notificador);

        Reserva reserva = new Reserva("R-002", "NORMAL");

        // Act & Assert
        assertThrows(
                IllegalStateException.class,
                () -> servicioConDependencias.confirmar(reserva)
        );

        verify(repository, never()).guardar(any());
        verify(notificador, never()).enviarConfirmacion(any());
    }

    @Test
    void reservaNulaNoConsultaDependencias() {
        // CP-12 · Reserva nula: debe fallar antes de tocar cualquier colaborador

        // Arrange
        DisponibilidadClient disponibilidad = mock(DisponibilidadClient.class);
        ReservaRepository repository = mock(ReservaRepository.class);
        Notificador notificador = mock(Notificador.class);

        ReservaService servicioConDependencias = new ReservaService(
                disponibilidad, repository, notificador);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> servicioConDependencias.confirmar(null)
        );

        verify(disponibilidad, never()).estaDisponible(any());
        verify(repository, never()).guardar(any());
        verify(notificador, never()).enviarConfirmacion(any());
    }
}
