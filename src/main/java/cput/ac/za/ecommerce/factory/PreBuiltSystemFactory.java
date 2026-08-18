/* PreBuiltSystemFactory.java
   Factory class for PreBuiltSystem
   Author: Nomhle Njengele (216227488)
   Date: 27 June 2026 */

package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.*;

public class PreBuiltSystemFactory {

    public static PreBuiltSystem createPreBuiltSystem(
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
            DimensionSpecs physicalDimensions,
            String systemTierClassification,
            int warrantyPeriodMonths,
            boolean isLiquidCooled) {

        return (PreBuiltSystem) new PreBuiltSystem.Builder()
                .setSystemTierClassification(systemTierClassification)
                .setWarrantyPeriodMonths(warrantyPeriodMonths)
                .setIsLiquidCooled(isLiquidCooled)
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
}