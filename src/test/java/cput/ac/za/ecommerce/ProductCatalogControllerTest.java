/* ProductCatalogControllerTest.java
   Controller Test for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 24 July 2026 */

package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.controller.ProductCatalogController;
import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.HardwareComponentFactory;
import cput.ac.za.ecommerce.service.IProductCatalogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductCatalogControllerTest {

    @Mock
    private IProductCatalogService service;

    @InjectMocks
    private ProductCatalogController controller;

    private HardwareComponent createTestProduct() {
        DimensionSpecs dimensions = new DimensionSpecs.Builder()
                .setPackageHeightCm(10.0)
                .setPackageWidthCm(8.0)
                .setPackageDepthCm(2.0)
                .setPackageWeightKg(0.2)
                .build();

        return HardwareComponentFactory.createHardwareComponent(
                "HWC001",
                Brand.SAMSUNG,
                "970 EVO Plus",
                1500.00,
                "High performance NVMe SSD",
                "https://via.placeholder.com/300x300?text=Samsung+SSD",
                50,
                "Black",
                "1TB",
                ProductCategory.SSD,
                dimensions,
                "Storage",
                5,
                "M.2 NVMe"
        );
    }

    @Test
    void testSave() {
        HardwareComponent product = createTestProduct();
        when(service.saveProductCatalog(product)).thenReturn(product);
        ProductCatalog result = controller.save(product);
        assertNotNull(result);
        verify(service).saveProductCatalog(product);
    }

    @Test
    void testGetById() {
        HardwareComponent product = createTestProduct();
        when(service.getProductCatalogById("HWC001")).thenReturn(product);
        ProductCatalog result = controller.getById("HWC001");
        assertNotNull(result);
        verify(service).getProductCatalogById("HWC001");
    }

    @Test
    void testGetById_NotFound() {
        when(service.getProductCatalogById("INVALID")).thenReturn(null);
        ProductCatalog result = controller.getById("INVALID");
        assertNull(result);
        verify(service).getProductCatalogById("INVALID");
    }

    @Test
    void testGetAll() {
        when(service.getAllProductCatalogs()).thenReturn(List.of());
        List<ProductCatalog> result = controller.getAll();
        assertNotNull(result);
        verify(service).getAllProductCatalogs();
    }
}