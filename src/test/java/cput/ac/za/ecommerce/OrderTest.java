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

public class OrderTest {

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
    void copyOrder() {

        // Create order
        Order order = OrderFactory.createOrder(
                UUID.randomUUID(),
                "Pending",
                List.of(
                        OrderItemFactory.createOrderItem(
                                UUID.randomUUID(),
                                2,
                                100.00
                        )
                ),
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                )
        );

        // Copy the order
        Order copy = new Order.Builder()
                .copy(order)
                .build();

        // Check the copy
        assertEquals(order.getOrderId(), copy.getOrderId());
        assertEquals(order.getCustomerId(), copy.getCustomerId());
        assertEquals(order.getDateCreated(), copy.getDateCreated());
        assertEquals(order.getCurrentOrderStatus(), copy.getCurrentOrderStatus());
        assertEquals(order.getOrderLineItems(), copy.getOrderLineItems());
        assertEquals(order.getDynamicTotals(), copy.getDynamicTotals());
    }

    @Test
    void orderToString() {

        // Create order
        Order order = OrderFactory.createOrder(
                UUID.randomUUID(),
                "Pending",
                List.of(
                        OrderItemFactory.createOrderItem(
                                UUID.randomUUID(),
                                1,
                                100.00
                        )
                ),
                FinancialBreakdownFactory.createFinancialBreakdown(
                        100.00,
                        15.00,
                        115.00
                )
        );

        // Check toString
        assertNotNull(order.toString());
    }
}