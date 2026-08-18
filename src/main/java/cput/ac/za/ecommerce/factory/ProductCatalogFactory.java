/* ProductCatalogFactory.java
   Factory class for ProductCatalog Service
   Author: Nomhle Njengele (216227488)
   Date: 28 June 2026 */

package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.*;

public class ProductCatalogFactory {

    public static ProductCatalog createProduct(
            String type,
            String productId,
            Brand brand,
            String modelName,
            double standardRetailPrice,
            String description,
            String imageUrl,
            int stockQuantity,
            String color,
            String storage,
            ProductCategory productCategory,
            DimensionSpecs physicalDimensions) {

        if (type.equals("hardware")) {
            return (HardwareComponent) new HardwareComponent.Builder()
                    .setHardwareCategory("General")
                    .setPowerRequirementWatts(0)
                    .setComponentFormFactor("Standard")
                    .setProductId(productId)
                    .setBrand(brand)
                    .setModelName(modelName)
                    .setStandardRetailPrice(standardRetailPrice)
                    .setDescription(description)
                    .setImageUrl(imageUrl)
                    .setStockQuantity(stockQuantity)
                    .setColor(color)
                    .setStorage(storage)
                    .setProductCategory(productCategory)
                    .setPhysicalDimensions(physicalDimensions)
                    .build();
        } else if (type.equals("system")) {
            return (PreBuiltSystem) new PreBuiltSystem.Builder()
                    .setSystemTierClassification("Standard")
                    .setWarrantyPeriodMonths(12)
                    .setIsLiquidCooled(false)
                    .setProductId(productId)
                    .setBrand(brand)
                    .setModelName(modelName)
                    .setStandardRetailPrice(standardRetailPrice)
                    .setDescription(description)
                    .setImageUrl(imageUrl)
                    .setStockQuantity(stockQuantity)
                    .setColor(color)
                    .setStorage(storage)
                    .setProductCategory(productCategory)
                    .setPhysicalDimensions(physicalDimensions)
                    .build();
        }
        throw new IllegalArgumentException("Invalid product type");
    }
}