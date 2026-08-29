package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.DimensionSpecs;
import cput.ac.za.ecommerce.domain.PreBuiltSystem;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;

public final class PreBuiltSystemFactory {

    private PreBuiltSystemFactory() {
    }

    public static PreBuiltSystem createPreBuiltSystem(
            String productId,
            ProductCatalogRequest request,
            DimensionSpecs dimensions
    ) {
        if (productId == null
                || productId.isBlank()
                || request == null
                || (
                request.getCategory()
                        != ProductCategory.LAPTOPS
                        && request.getCategory()
                        != ProductCategory.DESKTOP_COMPUTERS
        )) {
            return null;
        }

        return new PreBuiltSystem.Builder()
                .setProductId(productId)
                .setSku(request.getSku().trim())
                .setProductName(
                        request.getProductName().trim()
                )
                .setDescription(
                        trimToNull(
                                request.getDescription()
                        )
                )
                .setBrand(request.getBrand())
                .setCategory(request.getCategory())
                .setPrice(request.getPrice())
                .setDiscountPrice(
                        request.getDiscountPrice()
                )
                .setStockQuantity(
                        request.getStockQuantity()
                )
                .setPrimaryImageUrl(
                        trimToNull(
                                request.getPrimaryImageUrl()
                        )
                )
                .setSecondaryImageUrl(
                        trimToNull(
                                request.getSecondaryImageUrl()
                        )
                )
                .setColour(
                        trimToNull(request.getColour())
                )
                .setStorage(
                        trimToNull(request.getStorage())
                )
                .setMemory(
                        trimToNull(request.getMemory())
                )
                .setProcessor(
                        trimToNull(request.getProcessor())
                )
                .setSpecifications(
                        trimToNull(
                                request.getSpecifications()
                        )
                )
                .setFeatured(request.isFeatured())
                .setActive(true)
                .setPhysicalDimensions(dimensions)
                .setGraphicsCard(
                        trimToNull(
                                request.getGraphicsCard()
                        )
                )
                .setOperatingSystem(
                        trimToNull(
                                request.getOperatingSystem()
                        )
                )
                .setWarrantyPeriodMonths(
                        request.getWarrantyPeriodMonths()
                                == null
                                ? 12
                                : request
                                .getWarrantyPeriodMonths()
                )
                .setLiquidCooled(
                        request.isLiquidCooled()
                )
                .build();
    }

    private static String trimToNull(
            String value
    ) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}