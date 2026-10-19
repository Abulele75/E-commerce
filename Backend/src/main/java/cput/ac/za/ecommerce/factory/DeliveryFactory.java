package cput.ac.za.ecommerce.factory;

/*
 DeliveryFactory.java
 Factory for Delivery
 Author: Sinazo Ntsimbi (222765208)
 Date: 27 June 2026
 */

import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.domain.DeliveryAddress;

public class DeliveryFactory {

    public static Delivery createDelivery(String deliveryId,
                                          String orderId,
                                          String trackingNumber,
                                          String deliveryStatus,
                                          DeliveryAddress deliveryAddress) {

        if (deliveryId == null || deliveryId.isEmpty()
                || orderId == null || orderId.isEmpty()
                || trackingNumber == null || trackingNumber.isEmpty()
                || deliveryStatus == null || deliveryStatus.isEmpty()
                || deliveryAddress == null) {
            return null;
        }

        return new Delivery.Builder()
                .setDeliveryId(deliveryId)
                .setOrderId(orderId)
                .setTrackingNumber(trackingNumber)
                .setDeliveryStatus(deliveryStatus)
                .setDeliveryAddress(deliveryAddress)
                .build();
    }
}
