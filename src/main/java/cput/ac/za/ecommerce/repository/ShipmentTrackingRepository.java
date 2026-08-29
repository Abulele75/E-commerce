
/*
 Repository for Shipment tracking
 Author: Sinazo Ntsimbi (222765208)
 Date: 10 July 2026
 */
package cput.ac.za.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cput.ac.za.ecommerce.domain.ShipmentTracking;

public interface ShipmentTrackingRepository
        extends JpaRepository<ShipmentTracking, String> {

    List<ShipmentTracking>
    findAllByDelivery_DeliveryIdOrderByUpdateTimestampAsc(
            String deliveryId
    );

    List<ShipmentTracking>
    findAllByDelivery_TrackingNumberIgnoreCaseOrderByUpdateTimestampAsc(
            String trackingNumber
    );

    List<ShipmentTracking>
    findAllByDelivery_OrderIdOrderByUpdateTimestampAsc(
            String orderId
    );
}