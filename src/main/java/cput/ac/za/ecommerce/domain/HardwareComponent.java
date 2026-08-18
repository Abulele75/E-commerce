/* HardwareComponent.java
   Entity for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 21 June 2026 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "hardware_component")
public class HardwareComponent extends ProductCatalog {

    private String hardwareCategory;
    private int powerRequirementWatts;
    private String componentFormFactor;

    private HardwareComponent(Builder builder) {
        super(builder);
        this.hardwareCategory = builder.hardwareCategory;
        this.powerRequirementWatts = builder.powerRequirementWatts;
        this.componentFormFactor = builder.componentFormFactor;
    }

    protected HardwareComponent() {}

    public String getHardwareCategory() { return hardwareCategory; }
    public int getPowerRequirementWatts() { return powerRequirementWatts; }
    public String getComponentFormFactor() { return componentFormFactor; }

    public static class Builder extends ProductCatalog.Builder {
        private String hardwareCategory;
        private int powerRequirementWatts;
        private String componentFormFactor;

        public Builder setHardwareCategory(String hardwareCategory) {
            this.hardwareCategory = hardwareCategory;
            return this;
        }

        public Builder setPowerRequirementWatts(int powerRequirementWatts) {
            this.powerRequirementWatts = powerRequirementWatts;
            return this;
        }

        public Builder setComponentFormFactor(String componentFormFactor) {
            this.componentFormFactor = componentFormFactor;
            return this;
        }

        @Override
        public Builder setBrand(Brand brand) {
            super.setBrand(brand);
            return this;
        }

        @Override
        public Builder setModelName(String modelName) {
            super.setModelName(modelName);
            return this;
        }

        @Override
        public Builder setStandardRetailPrice(double standardRetailPrice) {
            super.setStandardRetailPrice(standardRetailPrice);
            return this;
        }

        @Override
        public Builder setDescription(String description) {
            super.setDescription(description);
            return this;
        }

        @Override
        public Builder setImageUrl(String imageUrl) {
            super.setImageUrl(imageUrl);
            return this;
        }

        @Override
        public Builder setStockQuantity(int stockQuantity) {
            super.setStockQuantity(stockQuantity);
            return this;
        }

        @Override
        public Builder setColor(String color) {
            super.setColor(color);
            return this;
        }

        @Override
        public Builder setStorage(String storage) {
            super.setStorage(storage);
            return this;
        }

        @Override
        public Builder setProductCategory(ProductCategory productCategory) {
            super.setProductCategory(productCategory);
            return this;
        }

        @Override
        public Builder setPhysicalDimensions(DimensionSpecs physicalDimensions) {
            super.setPhysicalDimensions(physicalDimensions);
            return this;
        }

        @Override
        public HardwareComponent build() {
            return new HardwareComponent(this);
        }
    }
}

