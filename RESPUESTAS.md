# Respuestas - Prueba Técnica Backend Developer

## 1. Escenario de Concurrencia (Black Friday) 🏃‍♂️

### Problema
Es Black Friday y el sistema recibe 50 pedidos por segundo del iPhone 15 que solo tiene 10 unidades en stock. El resultado es un inventario negativo (-5 unidades).

### Pregunta
¿Qué mecanismo de base de datos o de Spring Boot utilizarías para asegurar que nunca se venda más stock del que existe, asumiendo múltiples instancias de la API corriendo en paralelo?

### Tu Respuesta
```
Usaría control de concurrencia a nivel de base de datos, específicamente Optimistic Locking con @Version en la entidad del producto. De esta forma, si dos pedidos intentan descontar stock al mismo tiempo, uno de ellos falla y se evita que el inventario quede negativo. Es una solución simple, funciona bien con múltiples instancias de la API y escala mejor que bloquear todo el tiempo la base de datos.

Posibles enfoques a considerar:
- Transacciones y niveles de aislamiento
- Bloqueos (locks) en base de datos
- Bloqueos optimistas vs pesimistas
- Uso de @Version para Optimistic Locking
- SELECT FOR UPDATE
- Implementación de un sistema de colas
- Otros mecanismos...

Explica cuál elegirías y por qué.
```

---

## 2. Pregunta Trampa de Arquitectura 🎯

### Propuesta del Junior Developer
Configurar TODAS las relaciones JPA (`@OneToMany`, `@ManyToOne`) con `FetchType.EAGER` para:
- Traer toda la data en una sola consulta
- Evitar `LazyInitializationException`
- Mejorar el rendimiento

### Pregunta
¿Aceptarías este Pull Request? ¿Por qué sí o por qué no? ¿Qué impacto tendría con millones de registros?

### Tu Respuesta
```
No aceptaría ese Pull Request. Usar FetchType.EAGER en todas las relaciones puede generar un consumo excesivo de memoria y problemas de rendimiento cuando hay muchos datos. Lo correcto es usar LAZY por defecto y traer solo lo necesario según el caso, usando consultas específicas o DTOs. Cambiar todo a EAGER puede funcionar al inicio, pero es un problema serio cuando el sistema crece.

Considera estos puntos:
- Problema N+1 vs Carga excesiva de memoria
- Impacto en el rendimiento con grandes volúmenes de datos
- Alternativas mejores (DTO projection, fetch joins específicos, etc.)
- Cuándo usar EAGER vs LAZY
- Mejores prácticas para manejar LazyInitializationException

¿Aceptarías la propuesta? ¿Qué alternativas sugerirías?
```

---

## 3. Reflexiones Adicionales (Opcional) 💭

### Sobre el Refactoring Realizado
```
El refactor se enfocó en hacer la lógica más clara, fácil de entender y más simple de probar, separando mejor responsabilidades y evitando lógica innecesariamente compleja.
```

### Patrones de Diseño Aplicados
```
Se aplicaron principios básicos como separación de capas y responsabilidad única, sin forzar patrones complejos que no eran necesarios para el tamaño del problema.
```

### Posibles Mejoras Futuras
```
Con más tiempo se podrían agregar más validaciones, pruebas de integración y un manejo más avanzado de concurrencia para escenarios de alta carga.
```