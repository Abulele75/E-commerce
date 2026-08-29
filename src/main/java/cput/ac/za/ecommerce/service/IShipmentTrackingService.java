
/*
 Service for Shipment tracking
 Author: Sinazo Ntsimbi (222765208)
 Date: 10 July 2026
 */
package cput.ac.za.ecommerce.service;

import java.util.List;

import cput.ac.za.ecommerce.domain.DeliveryStatus;
import cput.ac.za.ecommerce.domain.ShipmentTracking;

public interface IShipmentTrackingService {

    ShipmentTracking createTrackingEvent(
            String deliveryId,
            DeliveryStatus deliveryStatus,
            String currentLocation,
            String statusMessage
    );

    ShipmentTracking getTrackingEvent(
            String trackingId
    );

    List<ShipmentTracking>
    getDeliveryTrackingHistory(
            String deliveryId
    );

    List<ShipmentTracking>
    getTrackingHistoryByTrackingNumber(
            String trackingNumber
    );

    List<ShipmentTracking>
    getTrackingHistoryByOrderId(
            String orderId
    );
}