package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.domain.CartStatus;
import cput.ac.za.ecommerce.domain.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public final class CartFactory {

    private CartFactory() {
    }

    public static Cart createCart(
            Customer customer
    ) {
        if (customer == null
                || !customer.isActive()) {
            return null;
        }

        LocalDateTime now =
                LocalDateTime.now();

        return new Cart.Builder()
                .setCartId(generateCartId())
                .setCustomer(customer)
                .setCartStatus(
                        CartStatus.ACTIVE
                )
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();
    }

    private static String generateCartId() {
        return "CART-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}