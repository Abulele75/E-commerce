//ProductCatalogResponse.java
//Author: Nomhle Njengele (216227488)
//Date: 17 August 2026
package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.domain.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductCatalogResponse {

    private String productId;
    private String sku;
    private String productName;
    private String description;

    private Brand brand;
    private ProductCategory category;

    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal effectivePrice;

    private int stockQuantity;
    private ProductStatus productStatus;

    private String primaryImageUrl;
    private String secondaryImageUrl;

    private String colour;
    private String storage;
    private String memory;
    private String processor;
    private String specifications;

    private boolean featured;
    private boolean active;

    private LocalDateTime createdAt;

    public ProductCatalogResponse() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getEffectivePrice() {
        return effectivePrice;
    }

    public void setEffectivePrice(BigDecimal effectivePrice) {
        this.effectivePrice = effectivePrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ProductStatus getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
    }

    public String getSecondaryImageUrl() {
        return secondaryImageUrl;
    }

    public void setSecondaryImageUrl(String secondaryImageUrl) {
        this.secondaryImageUrl = secondaryImageUrl;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}