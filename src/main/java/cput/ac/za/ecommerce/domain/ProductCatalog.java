/*
   ProductCatalog.java
   Abstract base class for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 21 June 2026
 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_catalog")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_record_type")
@DiscriminatorValue("GENERAL_PRODUCT")
public class ProductCatalog {

    @Id
    @Column(
            name = "product_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String productId;

    @Column(
            nullable = false,
            unique = true,
            length = 60
    )
    private String sku;

    @Column(
            name = "product_name",
            nullable = false,
            length = 150
    )
    private String productName;

    @Column(
            length = 2000
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 40
    )
    private Brand brand;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private ProductCategory category;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal price;

    @Column(
            name = "discount_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal discountPrice;

    @Column(
            name = "stock_quantity",
            nullable = false
    )
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "product_status",
            nullable = false,
            length = 30
    )
    private ProductStatus productStatus;

    @Column(
            name = "primary_image_url",
            length = 1000
    )
    private String primaryImageUrl;

    @Column(
            name = "secondary_image_url",
            length = 1000
    )
    private String secondaryImageUrl;

    @Column(length = 50)
    private String colour;

    @Column(length = 50)
    private String storage;

    @Column(length = 50)
    private String memory;

    @Column(length = 150)
    private String processor;

    @Column(length = 1000)
    private String specifications;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean active;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Embedded
    private DimensionSpecs physicalDimensions;

    protected ProductCatalog() {
    }

    protected ProductCatalog(
            ProductBuilder<?> builder
    ) {
        this.productId = builder.productId;
        this.sku = builder.sku;
        this.productName = builder.productName;
        this.description = builder.description;
        this.brand = builder.brand;
        this.category = builder.category;
        this.price = builder.price;
        this.discountPrice = builder.discountPrice;
        this.stockQuantity = builder.stockQuantity;
        this.productStatus =
                resolveStatus(
                        builder.stockQuantity,
                        builder.productStatus
                );

        this.primaryImageUrl =
                builder.primaryImageUrl;

        this.secondaryImageUrl =
                builder.secondaryImageUrl;

        this.colour = builder.colour;
        this.storage = builder.storage;
        this.memory = builder.memory;
        this.processor = builder.processor;
        this.specifications = builder.specifications;
        this.featured = builder.featured;
        this.active = builder.active;
        this.createdAt = builder.createdAt;
        this.physicalDimensions =
                builder.physicalDimensions;
    }

    public String getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public String getDescription() {
        return description;
    }

    public Brand getBrand() {
        return brand;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public BigDecimal getEffectivePrice() {
        if (discountPrice != null
                && discountPrice.compareTo(
                BigDecimal.ZERO
        ) > 0) {
            return discountPrice;
        }

        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public String getSecondaryImageUrl() {
        return secondaryImageUrl;
    }

    public String getColour() {
        return colour;
    }

    public String getStorage() {
        return storage;
    }

    public String getMemory() {
        return memory;
    }

    public String getProcessor() {
        return processor;
    }

    public String getSpecifications() {
        return specifications;
    }

    public boolean isFeatured() {
        return featured;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public DimensionSpecs getPhysicalDimensions() {
        return physicalDimensions;
    }

    public boolean isAvailable() {
        return active
                && stockQuantity > 0
                && productStatus
                != ProductStatus.DISCONTINUED;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (quantity > stockQuantity) {
            throw new IllegalArgumentException(
                    "Insufficient product stock"
            );
        }

        stockQuantity -= quantity;
        refreshStatus();
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        stockQuantity += quantity;
        refreshStatus();
    }

    public void changePrice(BigDecimal updatedPrice) {
        if (updatedPrice == null
                || updatedPrice.compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero"
            );
        }

        this.price = updatedPrice;
    }

    public void applyDiscount(
            BigDecimal updatedDiscountPrice
    ) {
        if (updatedDiscountPrice == null) {
            this.discountPrice = null;
            return;
        }

        if (updatedDiscountPrice.compareTo(
                BigDecimal.ZERO
        ) <= 0) {
            throw new IllegalArgumentException(
                    "Discount price must be greater than zero"
            );
        }

        if (updatedDiscountPrice.compareTo(price) >= 0) {
            throw new IllegalArgumentException(
                    "Discount price must be lower than the standard price"
            );
        }

        this.discountPrice = updatedDiscountPrice;
    }

    public void markFeatured(boolean featured) {
        this.featured = featured;
    }

    public void deactivate() {
        this.active = false;
        this.productStatus =
                ProductStatus.DISCONTINUED;
    }

    public void activate() {
        this.active = true;
        refreshStatus();
    }

    private void refreshStatus() {
        this.productStatus =
                resolveStatus(stockQuantity, null);
    }

    private static ProductStatus resolveStatus(
            int stockQuantity,
            ProductStatus requestedStatus
    ) {
        if (requestedStatus
                == ProductStatus.DISCONTINUED) {
            return ProductStatus.DISCONTINUED;
        }

        if (stockQuantity <= 0) {
            return ProductStatus.OUT_OF_STOCK;
        }

        if (stockQuantity <= 5) {
            return ProductStatus.LOW_STOCK;
        }

        return ProductStatus.IN_STOCK;
    }

    public static class Builder
            extends ProductBuilder<Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        public ProductCatalog build() {
            return new ProductCatalog(this);
        }
    }

    public abstract static class ProductBuilder<
            T extends ProductBuilder<T>> {

        protected String productId;
        protected String sku;
        protected String productName;
        protected String description;
        protected Brand brand;
        protected ProductCategory category;
        protected BigDecimal price;
        protected BigDecimal discountPrice;
        protected int stockQuantity;
        protected ProductStatus productStatus;
        protected String primaryImageUrl;
        protected String secondaryImageUrl;
        protected String colour;
        protected String storage;
        protected String memory;
        protected String processor;
        protected String specifications;
        protected boolean featured;
        protected boolean active = true;
        protected LocalDateTime createdAt =
                LocalDateTime.now();

        protected DimensionSpecs physicalDimensions;

        protected abstract T self();

        public T setProductId(String productId) {
            this.productId = productId;
            return self();
        }

        public T setSku(String sku) {
            this.sku = sku;
            return self();
        }

        public T setProductName(String productName) {
            this.productName = productName;
            return self();
        }

        public T setDescription(String description) {
            this.description = description;
            return self();
        }

        public T setBrand(Brand brand) {
            this.brand = brand;
            return self();
        }

        public T setCategory(
                ProductCategory category
        ) {
            this.category = category;
            return self();
        }

        public T setPrice(BigDecimal price) {
            this.price = price;
            return self();
        }

        public T setDiscountPrice(
                BigDecimal discountPrice
        ) {
            this.discountPrice = discountPrice;
            return self();
        }

        public T setStockQuantity(
                int stockQuantity
        ) {
            this.stockQuantity = stockQuantity;
            return self();
        }

        public T setProductStatus(
                ProductStatus productStatus
        ) {
            this.productStatus = productStatus;
            return self();
        }

        public T setPrimaryImageUrl(
                String primaryImageUrl
        ) {
            this.primaryImageUrl = primaryImageUrl;
            return self();
        }

        public T setSecondaryImageUrl(
                String secondaryImageUrl
        ) {
            this.secondaryImageUrl =
                    secondaryImageUrl;
            return self();
        }

        public T setColour(String colour) {
            this.colour = colour;
            return self();
        }

        public T setStorage(String storage) {
            this.storage = storage;
            return self();
        }

        public T setMemory(String memory) {
            this.memory = memory;
            return self();
        }

        public T setProcessor(String processor) {
            this.processor = processor;
            return self();
        }

        public T setSpecifications(
                String specifications
        ) {
            this.specifications = specifications;
            return self();
        }

        public T setFeatured(boolean featured) {
            this.featured = featured;
            return self();
        }

        public T setActive(boolean active) {
            this.active = active;
            return self();
        }

        public T setCreatedAt(
                LocalDateTime createdAt
        ) {
            this.createdAt = createdAt;
            return self();
        }

        public T setPhysicalDimensions(
                DimensionSpecs physicalDimensions
        ) {
            this.physicalDimensions =
                    physicalDimensions;
            return self();
        }
    }
}