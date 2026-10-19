/* HardwareComponentFactory.java
   Factory class for HardwareComponent
   Author: Nomhle Njengele (216227488)
   Date: 27 June 2026 */
package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.DimensionSpecs;
import cput.ac.za.ecommerce.domain.HardwareComponent;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;

public final class HardwareComponentFactory {

    private HardwareComponentFactory() {
    }

    public static HardwareComponent createHardwareComponent(
            String productId,
            ProductCatalogRequest request,
            DimensionSpecs dimensions
    ) {

        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID is required.");
        }

        if (request == null) {
            throw new IllegalArgumentException("Product request is required.");
        }

        if (request.getCategory() != ProductCategory.COMPUTER_COMPONENTS) {
            throw new IllegalArgumentException(
                    "HardwareComponentFactory only creates COMPUTER_COMPONENTS."
            );
        }

        if (request.getComponentType() == null) {
            throw new IllegalArgumentException(
                    "Component type is required."
            );
        }

        return new HardwareComponent.Builder()
                .setProductId(productId)
                .setSku(request.getSku().trim())
                .setProductName(request.getProductName().trim())
                .setDescription(trimToNull(request.getDescription()))
                .setBrand(request.getBrand())
                .setCategory(ProductCategory.COMPUTER_COMPONENTS)
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
                .setComponentType(request.getComponentType())
                .setPowerRequirementWatts(
                        request.getPowerRequirementWatts() == null
                                ? 0
                                : request.getPowerRequirementWatts()
                )
                .setComponentFormFactor(
                        trimToNull(request.getComponentFormFactor())
                )
                .build();
    }

    private static String trimToNull(String value)
    {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }
}