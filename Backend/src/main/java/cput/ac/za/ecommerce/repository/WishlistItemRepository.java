package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, String> {

    @Query("""
            SELECT item
            FROM WishlistItem item
            JOIN FETCH item.product
            WHERE item.customer.userId = :customerId
            ORDER BY item.addedAt DESC
            """)
    List<WishlistItem> findAllForCustomer(@Param("customerId") String customerId);

    Optional<WishlistItem> findByCustomer_UserIdAndProduct_ProductId(
            String customerId,
            String productId
    );
}
