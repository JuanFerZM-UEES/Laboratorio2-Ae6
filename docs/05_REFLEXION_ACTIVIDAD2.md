# Reflexión — Actividad 2 (Semana 7, Laboratorio 2)

## ¿Qué diferencia práctica observaste entre Stub y Mock?

La diferencia no está en la API (`mock(Clase.class)` crea el doble en ambos
casos) sino en qué parte de la prueba depende de él. `DisponibilidadClient`
se usó como Stub: lo único que importa es la respuesta que devuelve
(`when(disponibilidad.estaDisponible(any())).thenReturn(true)` o `false`),
porque esa respuesta es la que decide qué rama de `confirmar()` se ejecuta.
Si `estaDisponible()` nunca se hubiera verificado con `verify(...)`, la
prueba seguiría siendo válida — el Stub es un insumo, no algo que se
inspecciona después.

`ReservaRepository` y `Notificador`, en cambio, se usaron como Mock: no me
interesa qué devuelven (`guardar()` y `enviarConfirmacion()` son `void`), me
interesa si fueron llamados y con qué argumento. Ahí la aserción está en
`verify(repository).guardar(reserva)` y `verify(notificador,
never()).enviarConfirmacion(any())`, no en un `assertEquals` sobre un valor
de retorno. En `reservaNoDisponibleNoSeGuardaNiNotifica()` esto fue clave:
la prueba no solo comprueba que se lanza `IllegalStateException`, sino que
verifica que, cuando eso pasa, ni `repository` ni `notificador` fueron
tocados — esa garantía (que un rechazo no deja efectos secundarios a medias)
solo se puede expresar con Mock, no con un simple assert sobre el resultado.

En resumen: Stub controla lo que entra a la prueba; Mock verifica lo que
sale de ella. `confirmar(Reserva)` necesitaba ambos a la vez porque depende
de un colaborador que decide su comportamiento (disponibilidad) y de dos
colaboradores cuyo único propósito es producir un efecto observable
(guardar, notificar).

## Checklist — Producto formativo (sección 14)

- [x] Al menos 3 pruebas de `confirmar()` usando Mockito (3:
  `reservaDisponibleSeConfirmaGuardaYNotifica()`,
  `reservaNoDisponibleNoSeGuardaNiNotifica()`,
  `reservaNulaNoConsultaDependencias()`).
- [x] Uso de `when(...).thenReturn(...)` (Stub de `DisponibilidadClient`).
- [x] Uso de `verify(...)`, `never()` y `any()` (en las tres pruebas de
  `confirmar()`).
- [x] Reporte JaCoCo generado (dos rondas — ver `docs/evidencia/evidencia_06_*`
  y `evidencia_07_*`; HTML real en `target/site/jacoco/index.html` tras
  correr `mvn clean test`).
- [x] Análisis escrito de al menos un hueco de cobertura (ver
  `docs/02_ANALISIS_COBERTURA_PLANTILLA.md`: validación del `id` en
  `Reserva`, y `cancelar()` como gap restante y deliberado).
- [ ] Rama Git específica y mínimo 3 commits descriptivos — pendiente:
  lo ejecuta el usuario (ver instrucciones de entrega).
- [x] Respuesta breve: diferencia práctica entre Stub y Mock (ver arriba).

## Checklist — Antes de finalizar (sección 15)

- [x] No uso mocks sin una razón clara (`disponibilidad` controla una
  decisión; `repository`/`notificador` verifican un efecto — cada uno se
  usó porque la prueba lo necesitaba, no por costumbre).
- [x] Sé explicar qué respuesta controla mi Stub (`estaDisponible()` →
  true/false, decide si `confirmar()` continúa o lanza `IllegalStateException`).
- [x] Sé explicar qué interacción verifica cada Mock (`repository.guardar()`
  y `notificador.enviarConfirmacion()`, y que NO se llamen cuando la
  reserva se rechaza).
- [x] Las pruebas pasan (16/16, ver `docs/evidencia/evidencia_05_suite_con_reserva_test.txt`).
- [x] JaCoCo genera reporte.
- [x] Puedo señalar al menos un comportamiento aún no protegido
  (`Reserva.cancelar()`, 0 % de cobertura, documentado en el análisis).
- [ ] Mi rama y commits permiten reconstruir el trabajo — pendiente: se
  completa al ejecutar los comandos Git de la sección de entrega.
