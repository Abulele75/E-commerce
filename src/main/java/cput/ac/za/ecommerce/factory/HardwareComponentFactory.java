/* HardwareComponentFactory.java
   Factory class for HardwareComponent
   Author: Nomhle Njengele (216227488)
   Date: 27 June 2026 */

package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.*;

public class HardwareComponentFactory {

    public static HardwareComponent createHardwareComponent(
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
            String hardwareCategory,
            int powerRequirementWatts,
            String componentFormFactor) {

        return (HardwareComponent) new HardwareComponent.Builder()
                .setHardwareCategory(hardwareCategory)
                .setPowerRequirementWatts(powerRequirementWatts)
                .setComponentFormFactor(componentFormFactor)
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