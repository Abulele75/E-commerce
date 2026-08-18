package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.OrderItem;
import cput.ac.za.ecommerce.factory.OrderItemFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemFactoryTest {

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
        assertNotNull(orderItem.getOrderItemId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(2, orderItem.getQuantityPurchased());
        assertEquals(100.00, orderItem.getItemPriceSnapshot());
    }

    @Test
    void createOrderItemWithNullProductId() {

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                null,
                2,
                100.00
        );

        // Check if order item is null
        assertNull(orderItem);
    }

    @Test
    void createOrderItemWithInvalidQuantity() {

        // Create product id
        UUID productId = UUID.randomUUID();

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                productId,
                0,
                100.00
        );

        // Check if order item is null
        assertNull(orderItem);
    }

    @Test
    void createOrderItemWithInvalidPrice() {

        // Create product id
        UUID productId = UUID.randomUUID();

        // Create order item
        OrderItem orderItem = OrderItemFactory.createOrderItem(
                productId,
                2,
                0
        );

        // Check if order item is null
        assertNull(orderItem);
    }
}