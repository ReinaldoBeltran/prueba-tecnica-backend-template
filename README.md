Prueba Técnica Backend Developer – Java & Spring Boot
Descripción General

Este proyecto parte de una base existente y el objetivo principal fue mejorar la calidad del código, ordenar la lógica de negocio y agregar una regla de descuento específica, manteniendo la aplicación funcional y fácil de entender.

El foco del trabajo no fue agregar nuevas funcionalidades visibles desde la API, sino refactorizar el flujo interno de creación de pedidos, aplicar una regla de negocio clara y respaldar esa lógica con pruebas unitarias.



##################################################################


Refactorización del flujo de pedidos

El método createOrder originalmente concentraba demasiadas responsabilidades en un solo lugar.
Durante el refactor se reorganizó la lógica para que el flujo sea más claro y fácil de seguir.

En general, el proceso quedó dividido conceptualmente en:

Validación de los datos de entrada

Obtención y validación de los productos

Validación de stock disponible

Cálculo del total del pedido

Aplicación de la regla de descuento

Persistencia de la orden

La intención fue que el código se lea casi como un paso a paso y sea más sencillo de mantener o modificar en el futuro.



##################################################################

Regla de negocio: Descuento por variedad

Se implementó la regla de descuento solicitada:

Si un pedido contiene más de 3 tipos de productos distintos, se aplica un 10% de descuento sobre el total.

La cantidad de unidades no influye en esta regla, solo la cantidad de productos diferentes.

Múltiples ítems del mismo producto cuentan como un solo tipo.

Ejemplos:

10 unidades del mismo producto → no aplica descuento

4 productos distintos → aplica descuento del 10%



##################################################################


Pruebas unitarias

Se agregaron pruebas unitarias usando JUnit 5 y Mockito, enfocadas exclusivamente en validar la lógica del descuento.

Los casos cubiertos incluyen:

Pedido con 3 o menos tipos de productos (no aplica descuento)

Pedido con más de 3 tipos de productos (aplica descuento)

Pedido con múltiples ítems del mismo producto (no se considera variedad)

Las pruebas permiten verificar la lógica sin depender de base de datos real, manteniendo los tests rápidos y simples.

Para ejecutar los tests: "./gradlew test"



##################################################################

Decisiones generales

Se priorizó claridad y legibilidad sobre soluciones complejas.

No se forzaron patrones de diseño innecesarios.

La lógica se mantuvo simple y directa, pensando en mantenimiento y facilidad de prueba.

Se respetó la estructura original del proyecto siempre que fue posible.


##################################################################

Conclusión

El objetivo principal fue entregar un código más ordenado, fácil de entender y alineado con la regla de negocio solicitada, asegurando que la funcionalidad esté correctamente cubierta por pruebas.