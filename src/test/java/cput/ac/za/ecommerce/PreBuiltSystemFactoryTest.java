/* PreBuiltSystemFactoryTest.java
   Factory Test class for PreBuiltSystem
   Author: Nomhle Njengele (216227488)
   Date: 28 June 2026 */

package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.PreBuiltSystemFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PreBuiltSystemFactoryTest {

    @Test
    void testCreatePreBuiltSystem() {
        DimensionSpecs dimensions = new DimensionSpecs.Builder()
                .setPackageHeightCm(35.0)
                .setPackageWidthCm(24.0)
                .setPackageDepthCm(2.0)
                .setPackageWeightKg(2.5)
                .build();

        PreBuiltSystem system = PreBuiltSystemFactory.createPreBuiltSystem(
                "PBS001",
                Brand.ASUS,
                "TUF Gaming F15",
                25000.00,
                "High performance gaming laptop",
                "https://via.placeholder.com/300x300?text=ASUS+TUF",
                20,
                "Gray",
                "512GB",
                ProductCategory.LAPTOP,
                dimensions,
                "High-End",
                24,
                true
        );

        assertNotNull(system);
    }
}