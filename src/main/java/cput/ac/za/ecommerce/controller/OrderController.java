package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
 * OrderController.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    // Create order
    @PostMapping("/create")
    public Order create(@RequestBody Order order) {
        return orderService.create(order);
    }

    // Find order
    @GetMapping("/read/{orderId}")
    public Order read(@PathVariable UUID orderId) {
        return orderService.read(orderId);
    }

    // Get all orders
    @GetMapping("/getAll")
    public List<Order> getAll() {
        return orderService.getAll();
    }

    // Update order
    @PutMapping("/update")
    public Order update(@RequestBody Order order) {
        return orderService.update(order);
    }

    // Delete order
    @DeleteMapping("/delete/{orderId}")
    public boolean delete(@PathVariable UUID orderId) {
        return orderService.delete(orderId);
    }
}