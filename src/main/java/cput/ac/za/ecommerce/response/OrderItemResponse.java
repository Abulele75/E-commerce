//OrderItemResponse.java
//Sinethemba Nyimbinya (220085870)
//Date: 17 August 2026

package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        String orderItemId,
        String productId,
        String sku,
        String productName,
        Brand brand,
        String imageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {

    public static OrderItemResponse from(
            OrderItem item
    ) {
        return new OrderItemResponse(
                item.getOrderItemId(),
                item.getProductIdSnapshot(),
                item.getSkuSnapshot(),
                item.getProductNameSnapshot(),
                item.getBrandSnapshot(),
                item.getImageUrlSnapshot(),
                item.getUnitPriceSnapshot(),
                item.getQuantityPurchased(),
                item.getLineTotalSnapshot()
        );
    }
}