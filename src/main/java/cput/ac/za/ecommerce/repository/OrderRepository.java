package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository
        extends JpaRepository<Order, String> {

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer customer
            JOIN FETCH o.sourceCart sourceCart
            LEFT JOIN FETCH o.orderItems
            WHERE sourceCart.cartId = :cartId
            """)
    Optional<Order> findBySourceCartId(
            @Param("cartId")
            String cartId
    );

    boolean existsBySourceCart_CartId(
            String cartId
    );

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer customer
            JOIN FETCH o.sourceCart sourceCart
            LEFT JOIN FETCH o.orderItems
            WHERE o.orderId = :orderId
              AND LOWER(customer.accountProfile.email)
                  = LOWER(:customerEmail)
            """)
    Optional<Order> findForCustomer(
            @Param("orderId")
            String orderId,

            @Param("customerEmail")
            String customerEmail
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer customer
            JOIN FETCH o.sourceCart sourceCart
            LEFT JOIN FETCH o.orderItems
            WHERE o.orderId = :orderId
              AND LOWER(customer.accountProfile.email)
                  = LOWER(:customerEmail)
            """)
    Optional<Order> findForCustomerForUpdate(
            @Param("orderId")
            String orderId,

            @Param("customerEmail")
            String customerEmail
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer customer
            JOIN FETCH o.sourceCart sourceCart
            LEFT JOIN FETCH o.orderItems
            WHERE o.orderId = :orderId
            """)
    Optional<Order> findByIdForUpdate(
            @Param("orderId")
            String orderId
    );

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer customer
            LEFT JOIN FETCH o.orderItems
            WHERE LOWER(customer.accountProfile.email)
                = LOWER(:customerEmail)
            ORDER BY o.createdAt DESC
            """)
    List<Order> findAllForCustomer(
            @Param("customerEmail")
            String customerEmail
    );

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer
            LEFT JOIN FETCH o.orderItems
            ORDER BY o.createdAt DESC
            """)
    List<Order> findAllWithDetails();

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            JOIN FETCH o.customer
            LEFT JOIN FETCH o.orderItems
            WHERE o.orderStatus = :orderStatus
            ORDER BY o.createdAt DESC
            """)
    List<Order> findAllByStatusWithDetails(
            @Param("orderStatus")
            OrderStatus orderStatus
    );
}