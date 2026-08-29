
//ProductCatalogRequest.java
//Author: Nomhle Njengele (216227488)
//Date: 17 August 2026
package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.ComponentType;
import cput.ac.za.ecommerce.domain.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductCatalogRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 60)
    private String sku;

    @NotBlank(
            message = "Product name is required"
    )
    @Size(max = 150)
    private String productName;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Brand is required")
    private Brand brand;

    @NotNull(
            message = "Category is required"
    )
    private ProductCategory category;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message =
                    "Price must be greater than zero"
    )
    private BigDecimal price;

    @DecimalMin(
            value = "0.01",
            message =
                    "Discount price must be greater than zero"
    )
    private BigDecimal discountPrice;

    @Min(
            value = 0,
            message =
                    "Stock quantity cannot be negative"
    )
    private int stockQuantity;

    private String primaryImageUrl;
    private String secondaryImageUrl;
    private String colour;
    private String storage;
    private String memory;
    private String processor;
    private String specifications;
    private boolean featured;

    private Double packageHeightCm;
    private Double packageWidthCm;
    private Double packageDepthCm;
    private Double packageWeightKg;

    private ComponentType componentType;
    private Integer powerRequirementWatts;
    private String componentFormFactor;

    private String graphicsCard;
    private String operatingSystem;
    private Integer warrantyPeriodMonths;
    private boolean liquidCooled;

    public ProductCatalogRequest() {
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

    public void setProductName(
            String productName
    ) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
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

    public void setCategory(
            ProductCategory category
    ) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(
            BigDecimal price
    ) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(
            BigDecimal discountPrice
    ) {
        this.discountPrice = discountPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(
            int stockQuantity
    ) {
        this.stockQuantity = stockQuantity;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(
            String primaryImageUrl
    ) {
        this.primaryImageUrl =
                primaryImageUrl;
    }

    public String getSecondaryImageUrl() {
        return secondaryImageUrl;
    }

    public void setSecondaryImageUrl(
            String secondaryImageUrl
    ) {
        this.secondaryImageUrl =
                secondaryImageUrl;
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

    public void setProcessor(
            String processor
    ) {
        this.processor = processor;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(
            String specifications
    ) {
        this.specifications =
                specifications;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(
            boolean featured
    ) {
        this.featured = featured;
    }

    public Double getPackageHeightCm() {
        return packageHeightCm;
    }

    public void setPackageHeightCm(
            Double packageHeightCm
    ) {
        this.packageHeightCm =
                packageHeightCm;
    }

    public Double getPackageWidthCm() {
        return packageWidthCm;
    }

    public void setPackageWidthCm(
            Double packageWidthCm
    ) {
        this.packageWidthCm =
                packageWidthCm;
    }

    public Double getPackageDepthCm() {
        return packageDepthCm;
    }

    public void setPackageDepthCm(
            Double packageDepthCm
    ) {
        this.packageDepthCm =
                packageDepthCm;
    }

    public Double getPackageWeightKg() {
        return packageWeightKg;
    }

    public void setPackageWeightKg(
            Double packageWeightKg
    ) {
        this.packageWeightKg =
                packageWeightKg;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(
            ComponentType componentType
    ) {
        this.componentType = componentType;
    }

    public Integer getPowerRequirementWatts() {
        return powerRequirementWatts;
    }

    public void setPowerRequirementWatts(
            Integer powerRequirementWatts
    ) {
        this.powerRequirementWatts =
                powerRequirementWatts;
    }

    public String getComponentFormFactor() {
        return componentFormFactor;
    }

    public void setComponentFormFactor(
            String componentFormFactor
    ) {
        this.componentFormFactor =
                componentFormFactor;
    }

    public String getGraphicsCard() {
        return graphicsCard;
    }

    public void setGraphicsCard(
            String graphicsCard
    ) {
        this.graphicsCard = graphicsCard;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(
            String operatingSystem
    ) {
        this.operatingSystem =
                operatingSystem;
    }

    public Integer getWarrantyPeriodMonths() {
        return warrantyPeriodMonths;
    }

    public void setWarrantyPeriodMonths(
            Integer warrantyPeriodMonths
    ) {
        this.warrantyPeriodMonths =
                warrantyPeriodMonths;
    }

    public boolean isLiquidCooled() {
        return liquidCooled;
    }

    public void setLiquidCooled(
            boolean liquidCooled
    ) {
        this.liquidCooled = liquidCooled;
    }
}