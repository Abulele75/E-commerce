/*
   ProductCatalog.java
   Abstract base class for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 21 June 2026
 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "product_catalog")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProductCatalog {

    @Id
    private String productId;

    @Enumerated(EnumType.STRING)
    private Brand brand;

    private String modelName;
    private double standardRetailPrice;
    private String description;
    private String imageUrl;
    private int stockQuantity;
    private String color;
    private String storage;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Embedded
    private DimensionSpecs physicalDimensions;

    protected ProductCatalog(Builder builder) {
        this.productId = builder.productId;
        this.brand = builder.brand;
        this.modelName = builder.modelName;
        this.standardRetailPrice = builder.standardRetailPrice;
        this.description = builder.description;
        this.imageUrl = builder.imageUrl;
        this.stockQuantity = builder.stockQuantity;
        this.color = builder.color;
        this.storage = builder.storage;
        this.productCategory = builder.productCategory;
        this.physicalDimensions = builder.physicalDimensions;
    }

    protected ProductCatalog() {
    }

    public String getProductId() { return productId; }
    public Brand getBrand() { return brand; }
    public String getModelName() { return modelName; }
    public double getStandardRetailPrice() { return standardRetailPrice; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getStockQuantity() { return stockQuantity; }
    public String getColor() { return color; }
    public String getStorage() { return storage; }
    public ProductCategory getProductCategory() { return productCategory; }
    public DimensionSpecs getPhysicalDimensions() { return physicalDimensions; }

    public abstract static class Builder {
        private String productId;
        private Brand brand;
        private String modelName;
        private double standardRetailPrice;
        private String description;
        private String imageUrl;
        private int stockQuantity;
        private String color;
        private String storage;
        private ProductCategory productCategory;
        private DimensionSpecs physicalDimensions;

        public Builder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public Builder setBrand(Brand brand) {
            this.brand = brand;
            return this;
        }

        public Builder setBrandName(String brandName) {
            return this;
        }

        public Builder setModelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder setStandardRetailPrice(double standardRetailPrice) {
            this.standardRetailPrice = standardRetailPrice;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setStockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public Builder setProductCategory(ProductCategory productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        public Builder setPhysicalDimensions(DimensionSpecs physicalDimensions) {
            this.physicalDimensions = physicalDimensions;
            return this;
        }

        public abstract ProductCatalog build();
    }
}
