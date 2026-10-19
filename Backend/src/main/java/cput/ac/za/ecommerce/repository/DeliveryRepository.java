package cput.ac.za.ecommerce.repository;
/*
 Repository for Delivery
 Author: Sinazo Ntsimbi (222765208)
 Date: 10 July 2026
 */
import cput.ac.za.ecommerce.domain.Delivery;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
    boolean existsByOrderId(String orderId);
    boolean existsByTrackingNumberIgnoreCase(String trackingNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT delivery FROM Delivery delivery WHERE delivery.deliveryId = :deliveryId")
    Optional<Delivery> findByIdForUpdate(@Param("deliveryId") String deliveryId);
}
