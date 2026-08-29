package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.CartItem;
import cput.ac.za.ecommerce.domain.ProductCategory;

import java.math.BigDecimal;

public record CartItemResponse(
        String cartItemId,
        String productId,
        String sku,
        String productName,
        Brand brand,
        ProductCategory category,
        String imageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal,
        int availableStock
) {

    public static CartItemResponse from(
            CartItem item
    ) {
        return new CartItemResponse(
                item.getCartItemId(),
                item.getProduct().getProductId(),
                item.getProduct().getSku(),
                item.getProduct().getProductName(),
                item.getProduct().getBrand(),
                item.getProduct().getCategory(),
                item.getProduct()
                        .getPrimaryImageUrl(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal(),
                item.getProduct()
                        .getStockQuantity()
        );
    }
}