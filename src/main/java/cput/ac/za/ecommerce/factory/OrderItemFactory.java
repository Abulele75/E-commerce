package cput.ac.za.ecommerce.factory;

/*
 * OrderItemFactory.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

import cput.ac.za.ecommerce.domain.OrderItem;

import java.util.UUID;

public class OrderItemFactory {

    public static OrderItem createOrderItem(UUID productId,
                                            int quantityPurchased,
                                            double itemPriceSnapshot) {

        // Check if the product id is empty.
        if (productId == null)
            return null;

        // Check if the quantity is valid.
        if (quantityPurchased <= 0)
            return null;

        // Check if the item price is valid.
        if (itemPriceSnapshot <= 0)
            return null;

        return new OrderItem.Builder()
                .setOrderItemId(UUID.randomUUID())
                .setProductId(productId)
                .setQuantityPurchased(quantityPurchased)
                .setItemPriceSnapshot(itemPriceSnapshot)
                .build();
    }
}