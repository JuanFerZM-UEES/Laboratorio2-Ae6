# Matriz de casos

| ID | Regla | Escenario | Entrada | Esperado | Tipo | Riesgo |
|---|---|---|---|---|---|---|
| CP-01 | Cancelación | Anticipación habitual | horas = 5 | true | Normal | Comprueba la regla general de cancelación. |
| CP-02 | Cancelación | Límite permitido | horas = 2 | true | Límite | Detecta un uso incorrecto de `>` en vez de `>=` en la frontera. |
| CP-03 | Cancelación | Debajo del límite | horas = 1 | false | Límite | Protege la frontera inferior de la regla de cancelación. |
| CP-04 | Cancelación | Sin anticipación | horas = 0 | false | Extremo | No debe permitir una cancelación inmediata (sin aviso previo). |
| CP-05 | Descuento | Tipo NORMAL | tipo = NORMAL, total = 100 | 100 | Normal | Confirma que el caso por defecto no aplica descuento. |
| CP-06 | Descuento | Tipo VIP | tipo = VIP, total = 100 | 85 | Alternativo | Confirma el 15 % de descuento para clientes VIP. |
| CP-07 | Descuento | Tipo ESTUDIANTE | tipo = ESTUDIANTE, total = 100 | 90 | Alternativo | Confirma el 10 % de descuento para estudiantes. |
| CP-08 | Descuento | Total cero | tipo = VIP, total = 0 | 0 | Límite | Verifica que un total base en cero no produce un resultado inesperado (no negativo, no excepción). |
| CP-09 | Descuento | Total negativo | tipo = NORMAL, total = -1 | IllegalArgumentException | Inválido | Un total base negativo no es un dato válido de negocio; debe rechazarse explícitamente en vez de devolver un número incorrecto. |
