package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.FinancialBreakdown;
import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderItem;
import cput.ac.za.ecommerce.factory.FinancialBreakdownFactory;
import cput.ac.za.ecommerce.factory.OrderFactory;
import cput.ac.za.ecommerce.factory.OrderItemFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderFactoryTest {


    @Test
    void createOrder() {

        // Create customer id
        UUID customerId = UUID.randomUUID();

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                UUID.randomUUID(),
                2,
                100.00
        );

        // Add item to list
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Create order
        Order order = OrderFactory.createOrder(
                customerId,
                "Pending",
                orderItems,
                financialBreakdown
        );

        // Check the order
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals("Pending", order.getCurrentOrderStatus());
        assertEquals(orderItems, order.getOrderLineItems());
        assertEquals(financialBreakdown, order.getDynamicTotals());
    }

    @Test
    void createOrderWithNullCustomerId() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();

        // Create order
        Order order = OrderFactory.createOrder(
                null,
                "Pending",
                orderItems,
                financialBreakdown
        );

        // Check if order is null
        assertNull(order);
    }

    @Test
    void createOrderWithEmptyStatus() {

        // Create customer id
        UUID customerId = UUID.randomUUID();

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Create order item
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(
                OrderItemFactory.createOrderItem(
                        UUID.randomUUID(),
                        2,
                        100.00
                )
        );

        // Create order
        Order order = OrderFactory.createOrder(
                customerId,
                "",
                orderItems,
                financialBreakdown
        );

        // Check if order is null
        assertNull(order);
    }

    @Test
    void createOrderWithNoOrderItems() {

        // Create customer id
        UUID customerId = UUID.randomUUID();

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Create order
        Order order = OrderFactory.createOrder(
                customerId,
                "Pending",
                new ArrayList<>(),
                financialBreakdown
        );

        // Check if order is null
        assertNull(order);
    }

    @Test
    void createOrderWithNullFinancialBreakdown() {

        // Create customer id
        UUID customerId = UUID.randomUUID();

        // Create order item
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(
                OrderItemFactory.createOrderItem(
                        UUID.randomUUID(),
                        2,
                        100.00
                )
        );

        // Create order
        Order order = OrderFactory.createOrder(
                customerId,
                "Pending",
                orderItems,
                null
        );

        // Check if order is null
        assertNull(order);
    }
}

