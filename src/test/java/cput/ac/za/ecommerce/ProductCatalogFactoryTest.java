/* ProductCatalogFactoryTest.java
   Factory Test class for ProductCatalog
   Author: Nomhle Njengele (216227488)
   Date: 28 June 2026 */

package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.ProductCatalogFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductCatalogFactoryTest {

    @Test
    void testCreateHardwareProduct() {
        DimensionSpecs dimensions = new DimensionSpecs.Builder()
                .setPackageHeightCm(10.0)
                .setPackageWidthCm(8.0)
                .setPackageDepthCm(2.0)
                .setPackageWeightKg(0.2)
                .build();

        ProductCatalog product = ProductCatalogFactory.createProduct(
                "hardware",
                "P001",
                Brand.SAMSUNG,
                "990 PRO",
                1500.00,
                "High performance NVMe SSD",
                "https://via.placeholder.com/300x300?text=Samsung+SSD",
                50,
                "Black",
                "1TB",
                ProductCategory.SSD,
                dimensions
        );

        assertNotNull(product);
    }

    @Test
    void testCreateSystemProduct() {
        DimensionSpecs dimensions = new DimensionSpecs.Builder()
                .setPackageHeightCm(35.0)
                .setPackageWidthCm(24.0)
                .setPackageDepthCm(2.0)
                .setPackageWeightKg(2.5)
                .build();

        ProductCatalog product = ProductCatalogFactory.createProduct(
                "system",
                "P002",
                Brand.APPLE,
                "MacBook Pro 14",
                35000.00,
                "Professional laptop for creatives",
                "https://via.placeholder.com/300x300?text=MacBook+Pro",
                15,
                "Silver",
                "512GB",
                ProductCategory.LAPTOP,
                dimensions
        );

        assertNotNull(product);
    }
}