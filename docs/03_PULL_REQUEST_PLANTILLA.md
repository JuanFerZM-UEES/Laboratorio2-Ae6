# Pull Request — Ae6: completar suite, cobertura y evidencia

## Objetivo
Proteger las reglas críticas del módulo de reservas (cancelación,
descuentos y confirmación) con una suite de pruebas reproducible, dobles
de prueba justificados y cobertura interpretada — no solo medida.

## Cambios realizados
- Matriz consolidada de 12 casos (`docs/01_MATRIZ_CASOS_PLANTILLA.md`):
  cancelación, descuentos y confirmación.
- 16 pruebas JUnit 5 con AAA: `ReservaServiceTest` (13, incluye 3 con
  Stub/Mock de `confirmar()`) y `ReservaTest` (3, nuevas, a partir del
  hallazgo de cobertura).
- Reporte de cobertura JaCoCo generado e interpretado en dos rondas,
  con el hueco encontrado y la prueba añadida a partir de él.
- README y reporte técnico actualizados (`docs/06_REPORTE_TECNICO_AE6.md`).

## Casos de prueba
- Cancelación: anticipación normal, límite válido (2h), límite inválido
  (1h), extremo (0h) — CP-01 a CP-04.
- Descuentos: NORMAL, VIP, ESTUDIANTE, total cero, total negativo
  (excepción) — CP-05 a CP-09.
- Confirmación (Stub/Mock): disponible (confirma, guarda, notifica), no
  disponible (excepción, sin guardar ni notificar), reserva nula
  (excepción, sin tocar ningún colaborador) — CP-10 a CP-12.

## Cómo verificar
```bash
mvn clean test
```
Deben pasar 16/16 pruebas. Para ver la cobertura:
```bash
mvn clean test
# abrir target/site/jacoco/index.html
```

## Cobertura
`ReservaService` queda en 100 % de líneas y ramas tras los Stub/Mock de
`confirmar()`. El reporte también reveló que `Reserva` (la clase de
dominio) estaba en 64.3 % líneas / 50 % ramas — el constructor nunca
había sido probado con un `id` inválido. Se agregó `ReservaTest.java`
específicamente para ese hueco, subiendo `Reserva` a 85.7 % líneas / 100 %
ramas. `Reserva.cancelar()` queda deliberadamente en 0 % y documentado
como limitación (ver abajo), en vez de agregar una prueba solo para subir
el número.

## Limitaciones
- `Reserva.cancelar()` no tiene ninguna prueba en el proyecto.
- Los dobles de `confirmar()` se crean manualmente (`mock(...)` por
  prueba), sin `@ExtendWith(MockitoExtension.class)`.
- No se cubren fallos propios de los colaboradores externos (timeouts,
  excepciones de infraestructura) — el enunciado no lo pedía, pero es un
  escenario real de confiabilidad.

## Autorrevisión
- [x] Compila
- [x] Pruebas en verde (16/16)
- [x] Sin archivos accidentales (`target/` en `.gitignore`, verificado)
- [x] Commits descriptivos (ver `git log --oneline --decorate`)
- [x] Documentación actualizada (matriz, README, reporte técnico)

## Uso de IA
Usé Claude como apoyo para interpretar el reporte de cobertura JaCoCo,
identificar el hueco en `Reserva` y redactar este PR y el reporte técnico
a partir de la evidencia real que generé corriendo `mvn clean test` en mi
máquina. El diseño de las pruebas y las decisiones de qué cubrir quedan
documentadas como mías en `docs/06_REPORTE_TECNICO_AE6.md`, sección 10.
