package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.DeliveryStatus;
import cput.ac.za.ecommerce.domain.ShipmentTracking;

import java.util.List;

public interface IShipmentTrackingService {
    ShipmentTracking createTrackingEvent(String deliveryId, DeliveryStatus deliveryStatus, String currentLocation, String statusMessage);
    ShipmentTracking getTrackingEvent(String trackingId);
    List<ShipmentTracking> getDeliveryTrackingHistory(String deliveryId);
    List<ShipmentTracking> getTrackingHistoryByTrackingNumber(String trackingNumber, String customerEmail);
    List<ShipmentTracking> getTrackingHistoryByOrderId(String orderId, String customerEmail);
}
