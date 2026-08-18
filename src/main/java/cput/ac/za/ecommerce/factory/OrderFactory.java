package cput.ac.za.ecommerce.factory;

/*
 * OrderFactory.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

import cput.ac.za.ecommerce.domain.FinancialBreakdown;
import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFactory {

    public static Order createOrder(UUID customerId,
                                    String currentOrderStatus,
                                    List<OrderItem> orderLineItems,
                                    FinancialBreakdown dynamicTotals) {

        // Check if the customer id is empty.
        if (customerId == null)
            return null;

        // Check if the order status is empty.
        if (currentOrderStatus == null || currentOrderStatus.isBlank())
            return null;

        // Check if there are no order items.
        if (orderLineItems == null || orderLineItems.isEmpty())
            return null;

        // Check if the financial breakdown is empty.
        if (dynamicTotals == null)
            return null;

        return new Order.Builder()
                .setOrderId(UUID.randomUUID())
                .setCustomerId(customerId)
                .setDateCreated(LocalDateTime.now())
                .setCurrentOrderStatus(currentOrderStatus)
                .setOrderLineItems(orderLineItems)
                .setDynamicTotals(dynamicTotals)
                .build();
    }
}