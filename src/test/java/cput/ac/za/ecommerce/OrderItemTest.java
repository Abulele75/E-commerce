package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.OrderItem;
import cput.ac.za.ecommerce.factory.OrderItemFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    @Test
    void createOrderItem() {

        // Create product id
        UUID productId = UUID.randomUUID();

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                productId,
                2,
                100.00
        );

        // Check the order item
        assertNotNull(orderItem);
        assertEquals(productId, orderItem.getProductId());
        assertEquals(2, orderItem.getQuantityPurchased());
        assertEquals(100.00, orderItem.getItemPriceSnapshot());
    }

    @Test
    void copyOrderItem() {

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                UUID.randomUUID(),
                2,
                100.00
        );

        // Copy the order item
        OrderItem copy = new OrderItem.Builder()
                .copy(orderItem)
                .build();

        // Check the copy
        assertEquals(orderItem.getOrderItemId(), copy.getOrderItemId());
        assertEquals(orderItem.getProductId(), copy.getProductId());
        assertEquals(orderItem.getQuantityPurchased(), copy.getQuantityPurchased());
        assertEquals(orderItem.getItemPriceSnapshot(), copy.getItemPriceSnapshot());
    }

    @Test
    void orderItemToString() {

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                UUID.randomUUID(),
                2,
                100.00
        );

        // Check toString
        assertNotNull(orderItem.toString());
    }
}