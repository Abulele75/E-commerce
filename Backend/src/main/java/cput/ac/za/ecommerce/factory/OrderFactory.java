/*
 * OrderFactory.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 22 June 2026
 */

package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.DeliveryAddress;
import cput.ac.za.ecommerce.domain.FinancialBreakdown;
import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderItem;
import cput.ac.za.ecommerce.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class OrderFactory {

    private OrderFactory() {
    }

    public static Order createOrder(
            Customer customer,
            Cart sourceCart,
            List<OrderItem> orderItems,
            FinancialBreakdown financialBreakdown,
            DeliveryAddress deliveryAddress,
            String deliveryInstructions
    ) {
        if (customer == null
                || !customer.isActive()
                || sourceCart == null
                || sourceCart.isEmpty()
                || orderItems == null
                || orderItems.isEmpty()
                || financialBreakdown == null
                || deliveryAddress == null) {

            return null;
        }

        if (sourceCart.getCustomer() == null
                || !customer.getUserId().equals(
                sourceCart.getCustomer().getUserId()
        )) {

            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        return new Order.Builder()
                .setOrderId(generateOrderId())
                .setCustomer(customer)
                .setSourceCart(sourceCart)
                .setOrderStatus(
                        OrderStatus.PENDING_PAYMENT
                )
                .setOrderItems(orderItems)
                .setFinancialBreakdown(
                        financialBreakdown
                )
                .setDeliveryAddress(
                        deliveryAddress
                )
                .setDeliveryInstructions(
                        trimToNull(deliveryInstructions)
                )
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }

    private static String generateOrderId() {
        return "ORD-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private static String trimToNull(
            String value
    ) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}