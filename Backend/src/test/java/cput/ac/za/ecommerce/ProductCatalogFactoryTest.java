package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.ComponentType;
import cput.ac.za.ecommerce.domain.HardwareComponent;
import cput.ac.za.ecommerce.domain.PreBuiltSystem;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.factory.ProductCatalogFactory;
import cput.ac.za.ecommerce.request.ProductCatalogRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductCatalogFactoryTest {

    @Test
    void shouldCreateHardwareComponent() {
        ProductCatalogRequest request =
                new ProductCatalogRequest();

        request.setSku("COR-RAM-048");
        request.setProductName(
                "Corsair Vengeance 48GB DDR5 RAM"
        );
        request.setBrand(Brand.CORSAIR);
        request.setCategory(
                ProductCategory.COMPUTER_COMPONENTS
        );
        request.setComponentType(
                ComponentType.MEMORY
        );
        request.setPrice(
                new BigDecimal("3499.00")
        );
        request.setStockQuantity(15);
        request.setMemory("48GB DDR5");
        request.setComponentFormFactor("DIMM");

        ProductCatalog product =
                ProductCatalogFactory
                        .createProduct(request);

        assertNotNull(product);
        assertInstanceOf(
                HardwareComponent.class,
                product
        );
        assertEquals(
                Brand.CORSAIR,
                product.getBrand()
        );
        assertEquals(
                ProductCategory.COMPUTER_COMPONENTS,
                product.getCategory()
        );
        assertTrue(
                product.getProductId()
                        .startsWith("PRD-")
        );
    }

    @Test
    void shouldCreatePreBuiltSystem() {
        ProductCatalogRequest request =
                new ProductCatalogRequest();

        request.setSku("IT-LAP-001");
        request.setProductName(
                "Incredible Tech Pro Laptop"
        );
        request.setBrand(
                Brand.INCREDIBLE_TECH
        );
        request.setCategory(
                ProductCategory.LAPTOPS
        );
        request.setPrice(
                new BigDecimal("24999.00")
        );
        request.setStockQuantity(8);
        request.setProcessor(
                "Intel Core Ultra 7"
        );
        request.setMemory("32GB");
        request.setStorage("1TB SSD");
        request.setOperatingSystem(
                "Windows 11"
        );
        request.setWarrantyPeriodMonths(24);

        ProductCatalog product =
                ProductCatalogFactory
                        .createProduct(request);

        assertNotNull(product);
        assertInstanceOf(
                PreBuiltSystem.class,
                product
        );
    }

    @Test
    void shouldRejectInvalidDiscount() {
        ProductCatalogRequest request =
                new ProductCatalogRequest();

        request.setSku("PHONE-001");
        request.setProductName("Smartphone");
        request.setBrand(Brand.APPLE);
        request.setCategory(
                ProductCategory.SMARTPHONES
        );
        request.setPrice(
                new BigDecimal("10000.00")
        );
        request.setDiscountPrice(
                new BigDecimal("12000.00")
        );
        request.setStockQuantity(5);

        assertThrows(
                IllegalArgumentException.class,
                () -> ProductCatalogFactory
                        .createProduct(request)
        );
    }
}