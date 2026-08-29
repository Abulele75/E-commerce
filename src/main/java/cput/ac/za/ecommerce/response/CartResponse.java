package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.domain.CartStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        String cartId,
        CartStatus cartStatus,
        List<CartItemResponse> items,
        int totalItemQuantity,
        BigDecimal subtotal,
        LocalDateTime updatedAt
) {

    public static CartResponse from(
            Cart cart
    ) {
        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(CartItemResponse::from)
                        .toList();

        return new CartResponse(
                cart.getCartId(),
                cart.getCartStatus(),
                items,
                cart.getTotalItemQuantity(),
                cart.getSubtotal(),
                cart.getUpdatedAt()
        );
    }
}