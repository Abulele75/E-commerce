package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.CartItem;
import cput.ac.za.ecommerce.domain.PreBuiltSystem;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.ProductCategory;

import java.math.BigDecimal;

public record CartItemResponse(
        String cartItemId,
        String productId,
        String sku,
        String productName,
        String description,
        Brand brand,
        ProductCategory category,
        BigDecimal price,
        int stockQuantity,
        String colour,
        String storage,
        String memory,
        String processor,
        String graphicsCard,
        String operatingSystem,
        Integer warrantyPeriodMonths,
        boolean featured,
        String imageUrl,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal,
        int availableStock
) {

    public static CartItemResponse from(
            CartItem item
    ) {
        ProductCatalog product = item.getProduct();
        PreBuiltSystem system =
                product instanceof PreBuiltSystem value
                        ? value
                        : null;

        return new CartItemResponse(
                item.getCartItemId(),
                product.getProductId(),
                product.getSku(),
                product.getProductName(),
                product.getDescription(),
                product.getBrand(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getColour(),
                product.getStorage(),
                product.getMemory(),
                product.getProcessor(),
                system == null
                        ? null
                        : system.getGraphicsCard(),
                system == null
                        ? null
                        : system.getOperatingSystem(),
                system == null
                        ? null
                        : system.getWarrantyPeriodMonths(),
                product.isFeatured(),
                product.getPrimaryImageUrl(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal(),
                product.getStockQuantity()
        );
    }
}
