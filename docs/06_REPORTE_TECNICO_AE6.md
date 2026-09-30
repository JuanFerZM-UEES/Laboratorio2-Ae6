# Reporte técnico — Ae6: Suite de pruebas, cobertura y Pull Request

**Estudiante:** Juan Fernando Zhingri Medina
**Materia:** Diseño de Software · UCOM0310
**Semana:** 7 · Ae6
**Repositorio:** github.com/JuanFerZM-UEES/Laboratorio2
**Rama:** `ae6/suite-pruebas` → Pull Request hacia `main`

## 1. Objetivo de Ae6

Integrar todo lo trabajado en los Laboratorios 1 y 2 de la Semana 7: 
diseño de casos de prueba, una suite JUnit 5 con estructura AAA, dobles 
de prueba (Stub y Mock) para la única regla de `ReservaService` que depende 
de colaboradores externos, un reporte de cobertura JaCoCo interpretado 
(no solo leído como porcentaje), un historial Git con commits pequeños y 
descriptivos en una rama separada, y un Pull Request documentado que le permita 
a otra persona reconstruir por qué existe cada prueba y qué protege.

## 2. Reglas y matriz de casos

`ReservaService` expone tres comportamientos con riesgo real:

| Método | Regla | Riesgo si falla |
|---|---|---|
| `puedeCancelar(int)` | Se permite cancelar con 2 o más horas de anticipación. | Error de frontera en `>= 2`. |
| `calcularTotal(String, double)` | VIP 15 %, ESTUDIANTE 10 %, NORMAL sin descuento; total negativo es inválido. | Reglas alternativas cruzadas y total negativo aceptado. |
| `confirmar(Reserva)` | Solo confirma si hay disponibilidad; guarda y notifica solo si confirma. | Depende de tres colaboradores externos; un rechazo con efectos secundarios a medias. |

La matriz completa (`docs/01_MATRIZ_CASOS_PLANTILLA.md`) tiene **12 casos**
— por encima del mínimo de 10 — distribuidos en los tres comportamientos:
4 de cancelación (CP-01 a CP-04: normal, límite válido, límite inválido,
extremo), 5 de descuentos (CP-05 a CP-09: NORMAL, VIP, ESTUDIANTE, cero,
negativo) y 3 de confirmación (CP-10 a CP-12: disponible, no disponible,
nula). Cada fila incluye el riesgo concreto que justifica el caso, no solo
el dato de entrada.

## 3. Implementación JUnit y AAA

Las 16 pruebas (`ReservaServiceTest`: 13: `ReservaTest`: 3) siguen
Arrange-Act-Assert de forma explícita y tienen nombres que describen el
escenario, no un número de caso: `dosHorasEsElLimitePermitido()`,
`vipRecibeQuincePorCiento()`, `reservaNoDisponibleNoSeGuardaNiNotifica()`,
`reservaConIdNuloLanzaExcepcion()`. Ninguna se llama `test1()`, `caso2()` ni
similar. Cada prueba de `puedeCancelar()`/`calcularTotal()` referencia su ID
de la matriz (CP-xx) en un comentario, para que la trazabilidad regla →
caso → prueba sea explícita y no implícita.

`ReservaTest.java` (dominio) se agregó a partir del reporte JaCoCo, no del
enunciado original: es la evidencia de que el diseño de casos siguió
evolucionando con datos reales y no se detuvo en la lista inicial del
laboratorio (ver sección 6).

## 4. Stub/Mock utilizados y justificación

`confirmar(Reserva)` es el único método que depende de colaboradores
externos (`DisponibilidadClient`, `ReservaRepository`, `Notificador`), así
que es el único que necesita dobles de prueba — los otros dos métodos son
puros y se prueban con `new ReservaService(null, null, null)`.

- **Stub — `DisponibilidadClient`**: se controla con
  `when(disponibilidad.estaDisponible(any())).thenReturn(true|false)`.
  Es Stub y no Mock porque lo relevante es la respuesta que produce, no si
  fue invocado: esa respuesta es la que decide qué rama de `confirmar()`
  se ejecuta.
- **Mock — `ReservaRepository` y `Notificador`**: se verifican con
  `verify(repository).guardar(reserva)`,
  `verify(notificador).enviarConfirmacion(reserva)` y, en los casos de
  rechazo, `verify(repository, never()).guardar(any())` /
  `verify(notificador, never()).enviarConfirmacion(any())`. Son Mock
  porque ambos métodos son `void`: no hay nada que afirmar sobre un valor
  de retorno, solo se puede verificar si la interacción ocurrió.

Estas interacciones son relevantes porque son el único rastro observable
de que `confirmar()` hizo lo correcto: sin `verify(..., never())`, una
prueba que solo comprobara la excepción lanzada no detectaría un defecto
donde el servicio guarda o notifica *antes* de confirmar que hay
disponibilidad.

## 5. Resultado de `mvn clean test`

Ejecución real, desde cero, en la máquina del estudiante (Windows, Maven
con Java 17, dependencias resueltas contra Maven Central):

```
[INFO] Running edu.uees.testing.domain.ReservaTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.083 s
[INFO] Running edu.uees.testing.service.ReservaServiceTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.072 s
[INFO]
[INFO] Results:
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

16/16 pruebas exitosas. La evidencia completa de las rondas intermedias
(antes y después de cada cambio, incluida la detección real de un bug de
frontera introducido deliberadamente en el Laboratorio 1) está en
`docs/evidencia/`.

## 6. Cobertura JaCoCo e interpretación

El `pom.xml` incluye `jacoco-maven-plugin` con `prepare-agent` + `report`
en la fase `test`; el reporte queda en `target/site/jacoco/index.html`
(no se versiona — está en `.gitignore`).

**Métricas observadas** (dos rondas, ver `docs/evidencia/evidencia_06_*` y
`evidencia_07_*`):

| Clase | Antes del Stub/Mock | Después del Stub/Mock | Después de `ReservaTest` |
|---|---|---|---|
| `ReservaService` (líneas / ramas) | ~67 % / ~67 % (`confirmar()` en 0 %) | 100 % / 100 % | 100 % / 100 % |
| `Reserva` (líneas / ramas) | no ejercitada directamente | 64.3 % / 50 % | 85.7 % / 100 % |

**Método con menor cobertura:** antes de esta actividad, `confirmar()` en
`ReservaService` estaba en 0 % real (nunca se ejecutaba en ninguna
prueba), a pesar de ser el método con más lógica y más colaboradores. Tras
las tres pruebas de Stub/Mock, `ReservaService` completo llega a 100 %; el
método con menor cobertura de todo el proyecto pasa a ser
`Reserva.cancelar()`, en 0 %.

**Hueco relevante encontrado:** el reporte mostró que el constructor de
`Reserva` — `if (id == null || id.isBlank()) throw new
IllegalArgumentException("Id obligatorio")` — nunca se había ejecutado en
ninguna prueba de ninguna de las dos actividades anteriores, a pesar de
ser la regla que evita crear una reserva sin identificador.

**Prueba añadida a partir del análisis:** `ReservaTest.java`
(`reservaConIdNuloLanzaExcepcion()`, `reservaConIdEnBlancoLanzaExcepcion()`,
`tipoNuloSeNormalizaComoNormal()`), dirigida exactamente a ese hueco, no al
ejemplo genérico del enunciado (`totalNegativoLanzaExcepcion()`, que ya
existía desde la Actividad 1 como `totalNegativoEsInvalido()`).

**Por qué una cobertura alta no garantiza corrección:** cobertura mide
si una línea o rama se *ejecutó*, no si el resultado se *verificó*. Antes
de esta actividad, si `confirmar()` hubiera tenido una prueba que solo
llamara al método sin ningún `assertEquals`/`verify`, JaCoCo habría
marcado 100 % de todas formas — la herramienta no sabe si hubo una
aserción significativa. La prueba de que las tres pruebas de `confirmar()`
sí son confiables no es el porcentaje: es que verifican el estado final
(`EstadoReserva.CONFIRMADA`) y las interacciones concretas
(`verify(repository).guardar(...)`). Por la misma razón se decidió **no**
agregar una prueba para `Reserva.cancelar()` solo para subir el
porcentaje: ese método queda documentado como comportamiento aún no
protegido (sección 9), que es una afirmación más honesta que maquillar la
cifra con una prueba que no responde a ningún caso real de la matriz.

## 7. Flujo Git y commits

Historial real en `ae6/suite-pruebas` (ver `git log --oneline --decorate`
en la evidencia entregada):

```
<hash> docs: completar reporte tecnico y evidencia ae6
<hash> docs: actualizar readme para ae6
<hash> docs: completar matriz de casos con confirmacion (ae6)
923ed6f docs: analizar cobertura jacoco y registrar reflexion stub vs mock
ce29efb test: cubrir validacion de id y tipo en Reserva
faf004e test: agregar pruebas de confirmar con stub y mock
686e046 feat: matriz de casos y suite JUnit para ReservaService (Actividad 1, Semana 7)
```

La rama `ae6/suite-pruebas` se creó directamente sobre el historial real
de `test/lab2-dobles-cobertura` (no se re-hizo el trabajo con commits
nuevos): eso conserva la evolución auténtica — primero cancelación y
descuentos (Actividad 1), después Stub/Mock de confirmación y el hallazgo
de cobertura (Actividad 2) — y Ae6 agrega solo lo que le es propio: la
matriz consolidada, el README y este reporte.

## 8. Pull Request y autorrevisión

**Base:** `main` · **Compare:** `ae6/suite-pruebas`

El PR (ver `docs/03_PULL_REQUEST_PLANTILLA.md` para la plantilla y la
sección de entrega de este reporte para el texto final usado) documenta
el objetivo, los cambios, cómo verificar (`mvn clean test`), el resumen de
cobertura y las limitaciones conocidas.

Autorrevisión realizada antes de publicar la rama:

- Se revisaron los "Files changed" línea por línea: solo cambian
  `src/test/**`, `docs/**` y `README.md`; `ReservaService.java` y el resto
  del código productivo permanecen idénticos al original.
- No hay `target/` ni archivos generados en el repositorio (está en
  `.gitignore` y se verificó que ningún commit lo incluye).
- No hay credenciales, contraseñas ni datos sensibles en ningún archivo.
- Los nombres de las 16 pruebas son expresivos y describen el escenario.
- Todas las aserciones comprueban comportamiento real (estado final o
  interacciones verificadas), no solo que el método no lance una
  excepción inesperada.
- Los casos límite están presentes en las tres áreas (2h/1h en
  cancelación, total = 0 en descuentos, id en blanco en `Reserva`).
- Los mocks verifican exactamente las dos interacciones que importan
  (`guardar`, `enviarConfirmacion`), nunca detalles internos irrelevantes
  del servicio.
- La cobertura está interpretada método por método, no solo citada como
  porcentaje (sección 6).
- Los commits explican la evolución del trabajo, no un único commit
  "final".

## 9. Conclusiones, limitaciones y trabajo futuro

La suite protege las tres reglas de negocio pedidas con evidencia
ejecutable y reproducible: 16 pruebas, 100 % de líneas y ramas en
`ReservaService`, y un hallazgo de cobertura real (la validación del `id`
en `Reserva`) que se cubrió específicamente por eso, no por generar
pruebas al azar.

**Limitaciones conocidas:**

- `Reserva.cancelar()` no tiene ninguna prueba en todo el proyecto; si se
  introdujera un defecto ahí, ninguna de las 16 pruebas actuales lo
  detectaría.
- Los dobles de `confirmar()` se crean manualmente con `mock(...)` en cada
  prueba (sin `@ExtendWith(MockitoExtension.class)` ni anotaciones
  `@Mock`); es más explícito para este laboratorio, pero en un proyecto
  más grande convendría adoptar la extensión de Mockito para reducir la
  repetición.
- No se probó el camino donde `DisponibilidadClient`, `ReservaRepository`
  o `Notificador` lanzan una excepción propia (por ejemplo, un timeout de
  red simulado): el enunciado no lo pedía, pero es un escenario de
  confiabilidad real que un servicio de producción debería manejar.

**Trabajo futuro:** cubrir `cancelar()`, evaluar `@Mock`/`@InjectMocks`
para reducir la repetición en `confirmar()`, y considerar pruebas de
resiliencia ante fallos de los colaboradores externos.

## 10. Declaración de uso de IA

Utilicé Claude como apoyo en esta actividad: para revisar la cobertura de
JaCoCo y ayudarme a identificar qué comportamientos de `Reserva` no
estaban protegidos, para redactar este reporte técnico a partir de la
evidencia que generé ejecutando `mvn clean test` en mi propia máquina, y
para plantear los comandos de Git. El diseño de las reglas de negocio
(`ReservaService`) es el proporcionado por el proyecto base del curso; las
pruebas, la matriz de casos y las decisiones sobre qué cubrir y qué dejar
documentado como limitación son mías.