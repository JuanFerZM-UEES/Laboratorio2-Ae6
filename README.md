# UEES UCOM0310 — Semana 7 — Ae6: Suite de pruebas, cobertura y Pull Request

Proyecto Maven de las actividades individuales de Semana 7 (Laboratorio 1,
Laboratorio 2 y Ae6). Protege tres reglas de negocio de `ReservaService`
(cancelación, descuentos, confirmación) con una suite de 16 pruebas JUnit 5,
Stub/Mock con Mockito y cobertura medida con JaCoCo.

## Requisitos
- Java 17
- Maven 3.9+
- Git

## Verificación inicial

```bash
mvn clean test
```

Deben ejecutarse 16 pruebas, todas exitosas:

- `ReservaServiceTest` (13): entorno, 4 de `puedeCancelar()`, 5 de
  `calcularTotal()`, 3 de `confirmar()` con Stub/Mock.
- `ReservaTest` (3): validación del `id` y normalización del `tipo` en el
  constructor de `Reserva`.

## Cobertura

```bash
mvn clean test
```

Luego abrir:
`target/site/jacoco/index.html`

`ReservaService` queda en 100 % de líneas y ramas. El análisis completo,
incluyendo huecos encontrados y cómo se decidió cubrirlos, está en
`docs/02_ANALISIS_COBERTURA_PLANTILLA.md` y en el reporte técnico de Ae6
(`docs/06_REPORTE_TECNICO_AE6.md`).

## Estructura del proyecto

```
src/main/java/edu/uees/testing/
├── availability/DisponibilidadClient.java
├── domain/EstadoReserva.java
├── domain/Reserva.java
├── notification/Notificador.java
├── repository/ReservaRepository.java
└── service/ReservaService.java

src/test/java/edu/uees/testing/
├── domain/ReservaTest.java
└── service/ReservaServiceTest.java

docs/
├── 01_MATRIZ_CASOS_PLANTILLA.md   (12 casos: cancelación, descuentos, confirmación)
├── 02_ANALISIS_COBERTURA_PLANTILLA.md
├── 03_PULL_REQUEST_PLANTILLA.md
├── 04_REFLEXION_ACTIVIDAD1.md
├── 05_REFLEXION_ACTIVIDAD2.md
├── 06_REPORTE_TECNICO_AE6.md
└── evidencia/   (salidas reales de mvn test y JaCoCo, antes y después de cada cambio)
```

## Flujo Git de esta entrega

- `main`: Actividad 1 (matriz + 10 pruebas de cancelación/descuentos).
- `test/lab2-dobles-cobertura`: Actividad 2 (Stub/Mock de `confirmar()` +
  análisis JaCoCo).
- `ae6/suite-pruebas`: rama de la actividad evaluada, construida sobre el
  historial anterior; agrega la matriz consolidada, el reporte técnico y
  este README. Ver Pull Request hacia `main` para la evidencia completa.

## Regla de trabajo

No modifiques el código productivo solo para hacer pasar una prueba sin
justificar el cambio. Primero diseña el caso, luego implementa la prueba y
finalmente interpreta el resultado. `ReservaService.java` no se modificó en
ninguna de las tres actividades.
