package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.WishlistItem;

import java.time.LocalDateTime;

public record WishlistItemResponse(
        String wishlistItemId,
        LocalDateTime addedAt,
        WishlistProductResponse product
) {
    public static WishlistItemResponse from(WishlistItem item) {
        return new WishlistItemResponse(
                item.getWishlistItemId(),
                item.getAddedAt(),
                WishlistProductResponse.from(item.getProduct())
        );
    }
}
