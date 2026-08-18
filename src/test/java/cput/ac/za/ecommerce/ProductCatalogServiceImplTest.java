/* ProductCatalogServiceImplTest.java
   Service Test for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 12 July 2026 */

package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.HardwareComponentFactory;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.service.impl.ProductCatalogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductCatalogServiceImplTest {

    @Mock
    private ProductCatalogRepository repository;

    @InjectMocks
    private ProductCatalogServiceImpl service;

    private HardwareComponent hardware;

    @BeforeEach
    void setUp() {
        DimensionSpecs dimensions = new DimensionSpecs.Builder()
                .setPackageHeightCm(10.0)
                .setPackageWidthCm(8.0)
                .setPackageDepthCm(2.0)
                .setPackageWeightKg(0.2)
                .build();

        hardware = HardwareComponentFactory.createHardwareComponent(
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
        when(repository.save(hardware)).thenReturn(hardware);
        ProductCatalog result = service.saveProductCatalog(hardware);
        assertNotNull(result);
    }

    @Test
    void testGetById() {
        when(repository.findById("HWC001")).thenReturn(Optional.of(hardware));
        ProductCatalog result = service.getProductCatalogById("HWC001");
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        service.deleteProductCatalog("HWC001");
        verify(repository, times(1)).deleteById("HWC001");
    }
}