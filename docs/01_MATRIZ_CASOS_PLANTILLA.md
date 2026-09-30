# Matriz de casos

Matriz consolidada de Ae6: cubre los tres comportamientos mínimos exigidos
(cancelación, descuentos, confirmación) con 12 casos — por encima del
mínimo de 10 pedido en la sección 4 del enunciado. CP-01 a CP-09 se
diseñaron en la Actividad 1 (Laboratorio 1); CP-10 a CP-12 se añadieron en
la Actividad 2 (Laboratorio 2) junto con los Stub/Mock de `confirmar()`.

| ID | Método | Regla | Escenario | Entrada | Esperado | Tipo | Riesgo |
|---|---|---|---|---|---|---|---|
| CP-01 | puedeCancelar | Cancelación | Anticipación habitual | horas = 5 | true | Normal | Comprueba la regla general de cancelación. |
| CP-02 | puedeCancelar | Cancelación | Límite permitido | horas = 2 | true | Límite | Detecta un uso incorrecto de `>` en vez de `>=` en la frontera. |
| CP-03 | puedeCancelar | Cancelación | Debajo del límite | horas = 1 | false | Límite | Protege la frontera inferior de la regla de cancelación. |
| CP-04 | puedeCancelar | Cancelación | Sin anticipación | horas = 0 | false | Extremo | No debe permitir una cancelación inmediata (sin aviso previo). |
| CP-05 | calcularTotal | Descuento | Tipo NORMAL | tipo = NORMAL, total = 100 | 100 | Normal | Confirma que el caso por defecto no aplica descuento. |
| CP-06 | calcularTotal | Descuento | Tipo VIP | tipo = VIP, total = 100 | 85 | Alternativo | Confirma el 15 % de descuento para clientes VIP. |
| CP-07 | calcularTotal | Descuento | Tipo ESTUDIANTE | tipo = ESTUDIANTE, total = 100 | 90 | Alternativo | Confirma el 10 % de descuento para estudiantes. |
| CP-08 | calcularTotal | Descuento | Total cero | tipo = VIP, total = 0 | 0 | Límite | Verifica que un total base en cero no produce un resultado inesperado (no negativo, no excepción). |
| CP-09 | calcularTotal | Descuento | Total negativo | tipo = NORMAL, total = -1 | IllegalArgumentException | Inválido | Un total base negativo no es un dato válido de negocio; debe rechazarse explícitamente en vez de devolver un número incorrecto. |
| CP-10 | confirmar | Confirmación | Reserva disponible | R-001, disponibilidad = true | Estado CONFIRMADA; se llama repository.guardar() y notificador.enviarConfirmacion() | Normal | Camino feliz: valida estado final y las dos interacciones que producen efectos observables. |
| CP-11 | confirmar | Confirmación | Sin disponibilidad | R-002, disponibilidad = false | IllegalStateException; repository y notificador NUNCA invocados | Alternativo | Un rechazo no debe dejar efectos secundarios a medias (ni guardado ni notificación parcial). |
| CP-12 | confirmar | Confirmación | Reserva nula | null | IllegalArgumentException; disponibilidad, repository y notificador NUNCA invocados | Inválido | La validación de entrada debe ocurrir antes de tocar cualquier colaborador externo. |
