/* ProductCatalogRepository.java
   Repository for Product Catalog Service
   Author: Nomhle Njengele (216227488)
   Date: 12 July 2026 */
package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Brand;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.ProductCategory;
import cput.ac.za.ecommerce.domain.ProductStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductCatalogRepository
        extends JpaRepository<ProductCatalog, String> {

    Optional<ProductCatalog> findBySkuIgnoreCase(
            String sku
    );

    boolean existsBySkuIgnoreCase(
            String sku
    );

    List<ProductCatalog>
    findAllByActiveTrueOrderByProductNameAsc();

    List<ProductCatalog>
    findAllByFeaturedTrueAndActiveTrueOrderByCreatedAtDesc();

    List<ProductCatalog>
    findAllByBrandAndActiveTrueOrderByProductNameAsc(
            Brand brand
    );

    List<ProductCatalog>
    findAllByCategoryAndActiveTrueOrderByProductNameAsc(
            ProductCategory category
    );

    List<ProductCatalog>
    findAllByBrandAndCategoryAndActiveTrueOrderByProductNameAsc(
            Brand brand,
            ProductCategory category
    );

    List<ProductCatalog>
    findAllByProductStatusAndActiveTrueOrderByProductNameAsc(
            ProductStatus productStatus
    );

    List<ProductCatalog>
    findAllByPriceBetweenAndActiveTrueOrderByPriceAsc(
            BigDecimal minimumPrice,
            BigDecimal maximumPrice
    );

    @Query("""
            SELECT p
            FROM ProductCatalog p
            WHERE p.active = true
              AND (
                    LOWER(p.productName)
                        LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(p.sku)
                        LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(p.description)
                        LIKE LOWER(CONCAT('%', :query, '%'))
              )
            ORDER BY p.productName ASC
            """)
    List<ProductCatalog> searchActiveProducts(
            @Param("query")
            String query
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM ProductCatalog p
            WHERE p.productId = :productId
            """)
    Optional<ProductCatalog> findByIdForUpdate(
            @Param("productId")
            String productId
    );
}