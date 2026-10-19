package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.PreBuiltSystem;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.domain.ProductStatus;

import java.math.BigDecimal;

public record WishlistProductResponse(
        String productId,
        String sku,
        String productName,
        String description,
        Brand brand,
        ProductCategory category,
        BigDecimal price,
        BigDecimal discountPrice,
        BigDecimal effectivePrice,
        int stockQuantity,
        ProductStatus productStatus,
        String primaryImageUrl,
        String secondaryImageUrl,
        String colour,
        String storage,
        String memory,
        String processor,
        String specifications,
        String graphicsCard,
        String operatingSystem,
        Integer warrantyPeriodMonths,
        boolean featured,
        boolean active,
        boolean available
) {
    public static WishlistProductResponse from(ProductCatalog product) {
        PreBuiltSystem system = product instanceof PreBuiltSystem value ? value : null;
        return new WishlistProductResponse(
                product.getProductId(), product.getSku(), product.getProductName(), product.getDescription(),
                product.getBrand(), product.getCategory(), product.getPrice(), product.getDiscountPrice(),
                product.getEffectivePrice(), product.getStockQuantity(), product.getProductStatus(),
                product.getPrimaryImageUrl(), product.getSecondaryImageUrl(), product.getColour(),
                product.getStorage(), product.getMemory(), product.getProcessor(), product.getSpecifications(),
                system == null ? null : system.getGraphicsCard(),
                system == null ? null : system.getOperatingSystem(),
                system == null ? null : system.getWarrantyPeriodMonths(),
                product.isFeatured(), product.isActive(), product.isAvailable()
        );
    }
}
