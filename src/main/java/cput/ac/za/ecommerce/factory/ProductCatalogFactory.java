package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.DimensionSpecs;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;

import java.math.BigDecimal;
import java.util.UUID;

public final class ProductCatalogFactory {

    private ProductCatalogFactory() {
    }

    /**
     * Used when creating a NEW product.
     */
    public static ProductCatalog createProduct(ProductCatalogRequest request) {
        return createProduct(generateProductId(), request);
    }

    /**
     * Used when updating an EXISTING product.
     */
    public static ProductCatalog createProduct(
            String productId,
            ProductCatalogRequest request
    ) {

        validateRequest(request);

        DimensionSpecs dimensions = createDimensions(request);

        return switch (request.getCategory()) {

            case COMPUTER_COMPONENTS ->
                    HardwareComponentFactory.createHardwareComponent(
                            productId,
                            request,
                            dimensions
                    );

            case LAPTOPS,
                 DESKTOP_COMPUTERS ->
                    PreBuiltSystemFactory.createPreBuiltSystem(
                            productId,
                            request,
                            dimensions
                    );

            default ->
                    createGeneralProduct(
                            productId,
                            request,
                            dimensions
                    );
        };
    }

    private static ProductCatalog createGeneralProduct(
            String productId,
            ProductCatalogRequest request,
            DimensionSpecs dimensions
    ) {

        return new ProductCatalog.Builder()
                .setProductId(productId)
                .setSku(request.getSku().trim())
                .setProductName(request.getProductName().trim())
                .setDescription(trimToNull(request.getDescription()))
                .setBrand(request.getBrand())
                .setCategory(request.getCategory())
                .setPrice(request.getPrice())
                .setDiscountPrice(request.getDiscountPrice())
                .setStockQuantity(request.getStockQuantity())
                .setPrimaryImageUrl(trimToNull(request.getPrimaryImageUrl()))
                .setSecondaryImageUrl(trimToNull(request.getSecondaryImageUrl()))
                .setColour(trimToNull(request.getColour()))
                .setStorage(trimToNull(request.getStorage()))
                .setMemory(trimToNull(request.getMemory()))
                .setProcessor(trimToNull(request.getProcessor()))
                .setSpecifications(trimToNull(request.getSpecifications()))
                .setFeatured(request.isFeatured())
                .setActive(true)
                .setPhysicalDimensions(dimensions)
                .build();
    }

    private static DimensionSpecs createDimensions(
            ProductCatalogRequest request
    ) {

        if (request.getPackageHeightCm() == null
                && request.getPackageWidthCm() == null
                && request.getPackageDepthCm() == null
                && request.getPackageWeightKg() == null) {
            return null;
        }

        return new DimensionSpecs.Builder()
                .setPackageHeightCm(valueOrZero(request.getPackageHeightCm()))
                .setPackageWidthCm(valueOrZero(request.getPackageWidthCm()))
                .setPackageDepthCm(valueOrZero(request.getPackageDepthCm()))
                .setPackageWeightKg(valueOrZero(request.getPackageWeightKg()))
                .build();
    }

    private static void validateRequest(
            ProductCatalogRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null.");
        }

        if (isBlank(request.getSku())) {
            throw new IllegalArgumentException("SKU is required.");
        }

        if (isBlank(request.getProductName())) {
            throw new IllegalArgumentException("Product name is required.");
        }

        if (request.getBrand() == null) {
            throw new IllegalArgumentException("Brand is required.");
        }

        if (request.getCategory() == null) {
            throw new IllegalArgumentException("Category is required.");
        }

        if (request.getPrice() == null
                || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        if (request.getDiscountPrice() != null) {

            if (request.getDiscountPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "Discount price must be greater than zero."
                );
            }

            if (request.getDiscountPrice().compareTo(request.getPrice()) >= 0) {
                throw new IllegalArgumentException(
                        "Discount price must be less than the normal price."
                );
            }
        }

        if (request.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative."
            );
        }

        if (request.getCategory() == ProductCategory.COMPUTER_COMPONENTS
                && request.getComponentType() == null) {
            throw new IllegalArgumentException(
                    "Component type is required for computer components."
            );
        }

        if ((request.getCategory() == ProductCategory.LAPTOPS
                || request.getCategory() == ProductCategory.DESKTOP_COMPUTERS)
                && isBlank(request.getOperatingSystem())) {
            throw new IllegalArgumentException(
                    "Operating system is required."
            );
        }
    }

    private static String generateProductId() {

        return "PRD-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private static double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private static String trimToNull(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}