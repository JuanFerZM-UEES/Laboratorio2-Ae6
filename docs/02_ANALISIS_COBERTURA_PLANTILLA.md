# Análisis de cobertura (JaCoCo)

## Cómo se generó

`mvn clean test` (el `pom.xml` ya trae `jacoco-maven-plugin` con `prepare-agent` +
`report` en la fase `test`). El reporte queda en `target/site/jacoco/index.html`.
Para esta actividad se generaron dos rondas, antes y después de añadir la
prueba de la sección 11 — ver `docs/evidencia/evidencia_06_jacoco_resumen_antes.txt`
y `evidencia_07_jacoco_resumen_despues.txt` (resumen por clase/método,
equivalente en contenido al HTML).

## Ronda 1 — después de los Stub/Mock de confirmar() (13 pruebas)

| Clase | Líneas | Ramas |
|---|---|---|
| `ReservaService` | 100 % (21/21) | 100 % (12/12) |
| `Reserva` | 64.3 % (9/14) | 50 % (3/6) |

### ¿Qué método tiene menor cobertura?

Ninguno de `ReservaService`: los tres métodos (`puedeCancelar`, `calcularTotal`,
`confirmar`) llegan a 100 % de líneas y ramas en cuanto se agregan los tres
Stub/Mock de `confirmar()`. El método con menor cobertura de todo el proyecto
es `Reserva.cancelar()`, con 0 % — ninguna prueba, ni de la Actividad 1 ni de
la 2, invoca ese método.

### ¿Qué comportamiento falta?

No es un hueco en `ReservaService` (el objetivo explícito de esta actividad),
sino en la clase de dominio que usa: el constructor de `Reserva` valida
`id == null || id.isBlank()` y lanza `IllegalArgumentException("Id
obligatorio")` — esa rama nunca se había ejecutado en ninguna prueba, en
ninguna de las dos actividades. Es la regla más importante del constructor
(evita crear una reserva sin identificador) y no tenía ninguna protección.
`getId()` y `getTipo()` aparecían en 0 % de líneas por la misma razón: ningún
test los llamaba directamente.

### ¿Qué prueba nueva aportaría valor?

El enunciado sugiere como ejemplo `totalNegativoLanzaExcepcion()` — ese caso
ya existe desde la Actividad 1 (`totalNegativoEsInvalido()`, CP-09), así que
repetirlo no habría aportado nada. En su lugar, la prueba con más valor según
el reporte real era la validación del `id` en `Reserva`: se agregó
`ReservaTest.java` con `reservaConIdNuloLanzaExcepcion()`,
`reservaConIdEnBlancoLanzaExcepcion()` y `tipoNuloSeNormalizaComoNormal()`
(esta última cubre además la normalización `tipo == null → "NORMAL"`, otra
regla sin prueba directa).

### ¿Existe código cubierto pero mal probado?

En `ReservaService.confirmar()`, no: las tres pruebas de la sección 6-8 no
solo ejecutan las líneas, también verifican el resultado real (estado
`CONFIRMADA`, o la excepción correcta) y las interacciones con `verify(...)`
— es cobertura con aserciones significativas, no solo "pasar por la línea".
Antes de esta actividad, en cambio, `confirmar()` estaba en 0 % de cobertura
real (nunca se ejecutaba en ninguna prueba), a pesar de ser el método con más
lógica de negocio y más colaboradores del servicio.

## Ronda 2 — después de añadir ReservaTest.java (16 pruebas)

| Clase | Líneas | Ramas | Cambio |
|---|---|---|---|
| `ReservaService` | 100 % (21/21) | 100 % (12/12) | sin cambio (ya estaba completo) |
| `Reserva` | 85.7 % (12/14) | 100 % (6/6) | +21.4 pts líneas, +50 pts ramas |

Con las tres pruebas nuevas, el constructor, `getId()`, `getTipo()`,
`getEstado()` y `confirmar()` de `Reserva` llegan a 100 % de líneas y el 100 %
de las ramas queda cubierto. El único método que sigue en 0 % es
`cancelar()`: es un comportamiento real, documentado y deliberadamente sin
proteger — ninguna prueba en este laboratorio lo necesitaba para validar
`ReservaService`, y se deja señalado aquí en vez de agregar una prueba solo
para subir el porcentaje sin que responda a un caso de la matriz.

## Comportamiento aún no protegido (checklist, sección 15)

`Reserva.cancelar()` no tiene ninguna prueba en todo el proyecto. Si alguien
introdujera un defecto ahí (por ejemplo, que no cambie el estado, o que lo
cambie al valor equivocado), ninguna de las 16 pruebas actuales lo
detectaría.
