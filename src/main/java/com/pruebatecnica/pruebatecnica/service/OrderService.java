package com.pruebatecnica.pruebatecnica.service;

import com.pruebatecnica.pruebatecnica.dto.CreateOrderRequest;
import com.pruebatecnica.pruebatecnica.dto.OrderItemRequest;
import com.pruebatecnica.pruebatecnica.exception.InsufficientStockException;
import com.pruebatecnica.pruebatecnica.exception.ProductNotFoundException;
import com.pruebatecnica.pruebatecnica.model.Order;
import com.pruebatecnica.pruebatecnica.model.OrderItem;
import com.pruebatecnica.pruebatecnica.model.OrderStatus;
import com.pruebatecnica.pruebatecnica.model.Product;
import com.pruebatecnica.pruebatecnica.repository.OrderRepository;
import com.pruebatecnica.pruebatecnica.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
    * Gestiona el flujo principal de creación de una orden
    * 
    * La lógica se divide en pasos pequeños y claro
    * validación de entrada, procesamiento de items,
    * reglas de negocio y guardado final
    */

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
       
        validateCreateOrderRequest(request);
        Order order = new Order(request.getCustomerName(), request.getCustomerEmail());
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        Set<Long> uniqueProductIds = new HashSet<>();
        

            total = processOrderItems(
            request.getItems(),
            order,
            orderItems,
            uniqueProductIds
            );

        
        total = applyVarietyDiscount(total, uniqueProductIds);        
        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);
        
        return orderRepository.save(order);
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Aplica la regla de descuento por variedad de productos.
     * Si hay más de 3 tipos distintos, se descuenta el 10% del total.
     */
private BigDecimal applyVarietyDiscount(BigDecimal total, Set<Long> uniqueProductIds) {
    if (uniqueProductIds.size() > 3) {
        BigDecimal discount = total.multiply(BigDecimal.valueOf(0.10));
        return total.subtract(discount);
    }
    return total;
}

    /**
     * Calcula el subtotal de un producto según su cantidad
     */
private BigDecimal calculateItemTotal(Product product, int quantity) {
    return product.getPrice().multiply(BigDecimal.valueOf(quantity));
}

    /**
     * Verifica que exista stock suficiente y lo descuenta
     * Lanza excepción si el stock no alcanza
     */
private void validateAndUpdateStock(Product product, int quantity) {
    if (product.getStock() < quantity) {
        throw new InsufficientStockException(
            product.getName(),
            quantity,
            product.getStock()
        );
    }

    product.setStock(product.getStock() - quantity);
    productRepository.save(product);
}

    /**
     * Valida los datos básicos necesarios para crear una orden
    * Se mantiene separado para evitar lógica mezclada en el flujo principal
    */
private void validateCreateOrderRequest(CreateOrderRequest request) {
    if (request == null) {
        throw new IllegalArgumentException("Request cannot be null");
    }

    if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
        throw new IllegalArgumentException("Customer name is required");
    }

    if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
        throw new IllegalArgumentException("Customer email is required");
    }

    if (request.getItems() == null || request.getItems().isEmpty()) {
        throw new IllegalArgumentException("Order items are required");
    }
}


    /**
     *   Procesa los items del pedido;
     * - valida cada item
     * - verifica y actualiza stock
     * - construye los OrderItem
     * - calcula el total acumulado
     */
private BigDecimal processOrderItems(
        List<OrderItemRequest> items,
        Order order,
        List<OrderItem> orderItems,
        Set<Long> uniqueProductIds
) {
    BigDecimal total = BigDecimal.ZERO;

    for (OrderItemRequest itemRequest : items) {
        validateOrderItemRequest(itemRequest);

        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(itemRequest.getProductId()));

        validateAndUpdateStock(product, itemRequest.getQuantity());

        OrderItem orderItem = new OrderItem(product, itemRequest.getQuantity());
        orderItem.setOrder(order);
        orderItems.add(orderItem);

        total = total.add(calculateItemTotal(product, itemRequest.getQuantity()));

        uniqueProductIds.add(product.getId());
    }

    return total;
}

    /**
     * Valida los datos minimos de un item del pedido
     */
private void validateOrderItemRequest(OrderItemRequest itemRequest) {
    if (itemRequest.getProductId() == null) {
        throw new IllegalArgumentException("Product ID is required");
    }
    if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than 0");
    }
}
}