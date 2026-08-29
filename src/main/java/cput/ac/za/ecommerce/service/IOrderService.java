package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderStatus;
import cput.ac.za.ecommerce.request.CheckoutRequest;

import java.util.List;

public interface IOrderService {

    Order checkout(
            String customerEmail,
            CheckoutRequest request
    );

    Order getCustomerOrder(
            String customerEmail,
            String orderId
    );

    List<Order> getCustomerOrders(
            String customerEmail
    );

    Order cancelCustomerOrder(
            String customerEmail,
            String orderId
    );

    List<Order> getAllOrders();

    List<Order> getOrdersByStatus(
            OrderStatus orderStatus
    );

    Order updateOrderStatus(
            String orderId,
            OrderStatus newStatus
    );
}