package com.pruebatecnica.pruebatecnica.service;

import com.pruebatecnica.pruebatecnica.dto.CreateOrderRequest;
import com.pruebatecnica.pruebatecnica.dto.OrderItemRequest;
import com.pruebatecnica.pruebatecnica.model.Order;
import com.pruebatecnica.pruebatecnica.model.Product;
import com.pruebatecnica.pruebatecnica.repository.OrderRepository;
import com.pruebatecnica.pruebatecnica.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    /**
     * NOTA IMPORTANTE: Estos tests están incompletos intencionalmente.
     * Los candidatos deben:
     * 1. Completar los tests faltantes para la lógica del descuento
     * 2. Arreglar los tests que no funcionan debido a la refactorización
     * 3. Agregar más casos de prueba según sea necesario
     */

    @Test
    void testCreateOrderWithoutDiscount_ShouldNotApplyVarietyDiscount() {
        //c rear productos de prueba (3 tipos de productos)
        Product product1 = new Product("Manzana", BigDecimal.valueOf(5.00), 10);
        product1.setId(1L);

        Product product2 = new Product("Banana", BigDecimal.valueOf(3.00), 10);
        product2.setId(2L);

        Product product3 = new Product("Pera", BigDecimal.valueOf(4.00), 10);
        product3.setId(3L);

        // Configurar repositorio para devolver los productos
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product3));

        // Simular save del repository
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        // Crear items para la orden
        OrderItemRequest item1 = new OrderItemRequest(1L, 2); // 2 Manzanas
        OrderItemRequest item2 = new OrderItemRequest(2L, 3); // 3 Bananas
        OrderItemRequest item3 = new OrderItemRequest(3L, 1); // 1 Pera

        CreateOrderRequest request = new CreateOrderRequest(
                "Ana Perez",
                "ana@test.com",
                List.of(item1, item2, item3)
        );

       
        
            Order result = orderService.createOrder(request);

            assertNotNull(result, "La orden no debería ser nula");
            assertEquals("Ana Perez", result.getCustomerName(), "El nombre del cliente no coincide");

            // Total esperado SIN descuento 
            BigDecimal expectedTotal = BigDecimal.valueOf(23.00);

            assertEquals(
                    expectedTotal,
                    result.getTotalAmount(),
                    "El total calculado es incorrecto, no debería aplicarse descuento"
            );
    }


   @Test
    void testCreateOrderWithDiscount_ShouldApplyVarietyDiscount() {
        // crear 4 productos distintos para activar el descuento
        Product product1 = new Product("Manzana", BigDecimal.valueOf(5.00), 10);
        product1.setId(1L);

        Product product2 = new Product("Banana", BigDecimal.valueOf(3.00), 10);
        product2.setId(2L);

        Product product3 = new Product("Pera", BigDecimal.valueOf(4.00), 10);
        product3.setId(3L);

        Product product4 = new Product("Uva", BigDecimal.valueOf(2.00), 10);
        product4.setId(4L);

        // Configurar repositorio para devolver los productos
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.findById(3L)).thenReturn(Optional.of(product3));
        when(productRepository.findById(4L)).thenReturn(Optional.of(product4));

        // Simular save del repository
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        // Crear items para la orden
        OrderItemRequest item1 = new OrderItemRequest(1L, 2); // 2 Manzanas
        OrderItemRequest item2 = new OrderItemRequest(2L, 3); // 3 Bananas
        OrderItemRequest item3 = new OrderItemRequest(3L, 1); // 1 Pera
        OrderItemRequest item4 = new OrderItemRequest(4L, 4); // 4 Uvas

        CreateOrderRequest request = new CreateOrderRequest(
                "Carlos López",
                "carlos@test.com",
                List.of(item1, item2, item3, item4)
        );

             Order result = orderService.createOrder(request);
            assertNotNull(result, "La orden no debería ser nula");
            assertEquals("Carlos López", result.getCustomerName(), "El nombre del cliente no coincide");

            // Total esperado con descuento (valor final exacto)
            BigDecimal expectedTotalConDescuento = new BigDecimal("27.90");

            assertEquals(
                    expectedTotalConDescuento,
                    result.getTotalAmount(),
                    "El descuento de variedad no se aplicó correctamente"
            );
    }



    @Test
    void testCreateOrderWithSameProductMultipleTimes_ShouldNotApplyDiscount() {
        // arrange: Crear un solo producto
        Product product = new Product("Manzana", BigDecimal.valueOf(5.00), 50);
        product.setId(1L);

        // configurar repositorio para devolver siempre el mismo producto
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // simular save del repository
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        // Crear varios items del MISMO producto
        OrderItemRequest item1 = new OrderItemRequest(1L, 3); // 3 Manzanas
        OrderItemRequest item2 = new OrderItemRequest(1L, 4); // 4 Manzanas
        OrderItemRequest item3 = new OrderItemRequest(1L, 3); // 3 Manzanas

        CreateOrderRequest request = new CreateOrderRequest(
                "Luis Gómez",
                "luis@test.com",
                List.of(item1, item2, item3)
        );

             Order result = orderService.createOrder(request);
            assertNotNull(result, "La orden no debería ser nula");
            assertEquals("Luis Gómez", result.getCustomerName(), "El nombre del cliente no coincide");

            // total esperado SIN descuento, solo un tipo de producto
            BigDecimal expectedTotal = BigDecimal.valueOf(50.00);

            assertEquals(
                    expectedTotal,
                    result.getTotalAmount(),
                    "No debería aplicarse descuento cuando solo hay un tipo de producto"
            );

        
    }



    @Test
    void testCreateBasicOrder() {
        //crear producto de prueba
        Product product1 = new Product("Test Product", BigDecimal.valueOf(10.00), 5);
        product1.setId(1L);

        // configurar el comportamiento del repository con Mockito
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        when(orderRepository.save(any(Order.class))).thenAnswer(new org.mockito.stubbing.Answer<Order>() {
            @Override
            public Order answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                // Devuelve la misma orden que recibe
                return (Order) invocation.getArguments()[0];
            }
        });

        // Crear request de prueba
        OrderItemRequest item = new OrderItemRequest(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest("John Doe", "john@test.com", List.of(item));

        
            Order result = orderService.createOrder(request);            
            assertNotNull(result, "La orden no debería ser nula");
            assertEquals("John Doe", result.getCustomerName(), "El nombre del cliente no coincide");
            assertEquals(BigDecimal.valueOf(20.00), result.getTotalAmount(), "El total calculado es incorrecto");
       
    }
    

}