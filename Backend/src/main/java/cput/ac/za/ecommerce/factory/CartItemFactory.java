package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.CartItem;
import cput.ac.za.ecommerce.domain.ProductCatalog;

import java.time.LocalDateTime;
import java.util.UUID;

public final class CartItemFactory {

    private CartItemFactory() {
    }

    public static CartItem createCartItem(
            ProductCatalog product,
            int quantity
    ) {
        if (product == null
                || !product.isAvailable()
                || quantity <= 0
                || quantity
                > product.getStockQuantity()) {
            return null;
        }

        return new CartItem.Builder()
                .setCartItemId(
                        generateCartItemId()
                )
                .setProduct(product)
                .setQuantity(quantity)
                .setCreatedAt(
                        LocalDateTime.now()
                )
                .build();
    }

    private static String
    generateCartItemId() {
        return "CI-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 14)
                .toUpperCase();
    }
}