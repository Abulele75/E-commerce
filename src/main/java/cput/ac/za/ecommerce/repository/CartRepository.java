package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.domain.CartStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository
        extends JpaRepository<Cart, String> {

    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            JOIN FETCH c.customer customer
            LEFT JOIN FETCH c.items item
            LEFT JOIN FETCH item.product
            WHERE LOWER(customer.accountProfile.email)
                = LOWER(:customerEmail)
              AND c.cartStatus = :cartStatus
            """)
    Optional<Cart> findCartForCustomerByStatus(
            @Param("customerEmail")
            String customerEmail,

            @Param("cartStatus")
            CartStatus cartStatus
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            JOIN FETCH c.customer customer
            LEFT JOIN FETCH c.items item
            LEFT JOIN FETCH item.product
            WHERE LOWER(customer.accountProfile.email)
                = LOWER(:customerEmail)
              AND c.cartStatus = :cartStatus
            """)
    Optional<Cart> findCartForCustomerByStatusForUpdate(
            @Param("customerEmail")
            String customerEmail,

            @Param("cartStatus")
            CartStatus cartStatus
    );

    @Query("""
            SELECT DISTINCT c
            FROM Cart c
            JOIN FETCH c.customer customer
            LEFT JOIN FETCH c.items item
            LEFT JOIN FETCH item.product
            WHERE customer.userId = :customerId
            ORDER BY c.createdAt DESC
            """)
    List<Cart> findAllForCustomer(
            @Param("customerId")
            String customerId
    );
}