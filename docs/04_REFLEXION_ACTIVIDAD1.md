# Reflexión — Actividad 1 (Semana 7, Laboratorio 1)

## ¿Qué prueba detectó mejor un posible error de frontera y por qué?

`dosHorasEsElLimitePermitido()` fue la prueba que detectó mejor el error de
frontera. Al modificar temporalmente `puedeCancelar()` de
`horasAnticipacion >= 2` a `horasAnticipacion > 2`
(`docs/evidencia/evidencia_02_bug_detectado.txt`), esa fue la única de las
diez pruebas que falló, con el mensaje real
`expected: <true> but was: <false>`. Las demás pruebas de cancelación
(`cincoHorasPermitenCancelar()`, `unaHoraNoPermiteCancelar()`,
`ceroHorasNoPermiteCancelar()`) no habrían detectado este defecto porque
ninguna de ellas ejercita exactamente el valor `2`, que es el único punto
donde `>=` y `>` producen resultados distintos.

## Evidencia de la ejecución (docs/evidencia/)

| Archivo | Momento | Resultado |
|---|---|---|
| `evidencia_01_suite_verde_inicial.txt` | Suite completa, código original (`>= 2`) | 10/10 pruebas exitosas |
| `evidencia_02_bug_detectado.txt` | Bug introducido temporalmente (`> 2`) | 9/10 exitosas — falla `dosHorasEsElLimitePermitido()` con `expected: <true> but was: <false>` |
| `evidencia_03_restaurado_verde.txt` | Código restaurado a `>= 2` | 10/10 pruebas exitosas de nuevo |

El código productivo fue restaurado a su versión original inmediatamente
después de capturar la evidencia del fallo; el cambio nunca se mantuvo.

## Checklist — Producto formativo (sección 9)

- [x] Matriz con al menos 8 casos diseñados (9 casos: CP-01 a CP-09, ver `docs/01_MATRIZ_CASOS_PLANTILLA.md`).
- [x] Suite JUnit con al menos 7 pruebas implementadas (10 pruebas: `entornoJUnitFunciona()` + 9 nuevas, una por cada caso de la matriz).
- [x] Evidencia de `mvn clean test` exitoso — ejecutado con JUnit Platform Console Standalone 5.10.1 por restricción de red del entorno; ver `docs/evidencia/evidencia_01_suite_verde_inicial.txt`.
- [x] Respuesta breve a la pregunta de frontera (ver arriba).

## Checklist — Antes de finalizar (sección 10)

- [x] Puedo explicar cada caso de mi matriz (cada fila en `01_MATRIZ_CASOS_PLANTILLA.md` incluye justificación en la columna Riesgo).
- [x] Incluí al menos un caso límite (CP-02, CP-03, CP-08).
- [x] Incluí al menos un caso inválido (CP-09 — total negativo, `IllegalArgumentException`).
- [x] Las pruebas tienen nombres descriptivos (ej. `dosHorasEsElLimitePermitido()`, `vipRecibeQuincePorCiento()`; ninguna se llama `test1()` ni similar).
- [x] Las pruebas pasan con la suite completa (10/10, ver evidencia).
- [x] No se alteró el resultado esperado solo para obtener verde (el bug de la sección 7 se introdujo, se documentó el fallo real y se revirtió; ninguna aserción fue modificada para maquillar un resultado).
