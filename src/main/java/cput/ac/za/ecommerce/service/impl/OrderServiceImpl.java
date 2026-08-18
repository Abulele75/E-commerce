package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/*
 * OrderServiceImpl.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

@Service
public class OrderServiceImpl {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Create order
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    // Find order using order id
    public Order read(UUID orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    // Get all orders
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    // Update order
    public Order update(Order order) {
        if (orderRepository.existsById(order.getOrderId())) {
            return orderRepository.save(order);
        }

        return null;
    }

    // Delete order
    public boolean delete(UUID orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }

        return false;
    }
}