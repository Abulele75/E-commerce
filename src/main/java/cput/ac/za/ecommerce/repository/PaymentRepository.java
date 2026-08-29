/*
   PaymentRepository
   Ngwana Tiyani (231266731)
   Date: 19 June 2026
 */

package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Payment;
import cput.ac.za.ecommerce.domain.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, String> {

    boolean existsByOrder_OrderIdAndPaymentStatus(
            String orderId,
            PaymentStatus paymentStatus
    );

    List<Payment>
    findAllByOrder_OrderIdOrderByCreatedAtDesc(
            String orderId
    );

    @Query("""
            SELECT p
            FROM Payment p
            JOIN FETCH p.order o
            JOIN FETCH o.customer c
            WHERE p.transactionId = :transactionId
              AND LOWER(c.accountProfile.email)
                  = LOWER(:customerEmail)
            """)
    Optional<Payment> findForCustomer(
            @Param("transactionId")
            String transactionId,

            @Param("customerEmail")
            String customerEmail
    );

    @Query("""
            SELECT p
            FROM Payment p
            JOIN FETCH p.order o
            JOIN FETCH o.customer c
            WHERE o.orderId = :orderId
              AND LOWER(c.accountProfile.email)
                  = LOWER(:customerEmail)
            ORDER BY p.createdAt DESC
            """)
    List<Payment> findAllForOrderAndCustomer(
            @Param("orderId")
            String orderId,

            @Param("customerEmail")
            String customerEmail
    );

    @Query("""
            SELECT p
            FROM Payment p
            JOIN FETCH p.order o
            JOIN FETCH o.customer c
            WHERE LOWER(c.accountProfile.email)
                  = LOWER(:customerEmail)
            ORDER BY p.createdAt DESC
            """)
    List<Payment> findAllForCustomer(
            @Param("customerEmail")
            String customerEmail
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p
            FROM Payment p
            JOIN FETCH p.order o
            WHERE p.transactionId = :transactionId
            """)
    Optional<Payment> findByIdForUpdate(
            @Param("transactionId")
            String transactionId
    );
}