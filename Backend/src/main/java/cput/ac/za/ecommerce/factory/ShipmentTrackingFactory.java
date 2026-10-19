

/*
 ShipmentTrackingFactory.java
 Factory for ShipmentTracking
 Author: Sinazo Ntsimbi (222765208)
 Date: 27 June 2026
 */

package cput.ac.za.ecommerce.factory;
import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.domain.DeliveryStatus;
import cput.ac.za.ecommerce.domain.ShipmentTracking;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ShipmentTrackingFactory {

    private ShipmentTrackingFactory() {
    }

    public static ShipmentTracking
    createShipmentTracking(
            Delivery delivery,
            DeliveryStatus deliveryStatus,
            String currentLocation,
            String statusMessage
    ) {
        if (delivery == null
                || deliveryStatus == null
                || statusMessage == null
                || statusMessage.isBlank()) {

            return null;
        }

        return new ShipmentTracking.Builder()
                .setTrackingId(
                        generateTrackingEventId()
                )
                .setDelivery(delivery)
                .setDeliveryStatus(
                        deliveryStatus
                )
                .setCurrentLocation(
                        trimToNull(currentLocation)
                )
                .setStatusMessage(
                        statusMessage.trim()
                )
                .setUpdateTimestamp(
                        LocalDateTime.now()
                )
                .build();
    }

    private static String
    generateTrackingEventId() {
        return "TRK-EVT-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private static String trimToNull(
            String value
    ) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}