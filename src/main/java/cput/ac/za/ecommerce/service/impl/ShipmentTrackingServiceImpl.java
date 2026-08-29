
/*
 Service Impl for Shipment tracking
 Author: Sinazo Ntsimbi (222765208)
 Date: 10 July 2026
 */

package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.domain.DeliveryStatus;
import cput.ac.za.ecommerce.domain.ShipmentTracking;
import cput.ac.za.ecommerce.factory.ShipmentTrackingFactory;
import cput.ac.za.ecommerce.repository.DeliveryRepository;
import cput.ac.za.ecommerce.repository.ShipmentTrackingRepository;
import cput.ac.za.ecommerce.service.IShipmentTrackingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShipmentTrackingServiceImpl
        implements IShipmentTrackingService {

    private final ShipmentTrackingRepository
            trackingRepository;

    private final DeliveryRepository
            deliveryRepository;

    public ShipmentTrackingServiceImpl(
            ShipmentTrackingRepository
                    trackingRepository,
            DeliveryRepository deliveryRepository
    ) {
        this.trackingRepository =
                trackingRepository;
        this.deliveryRepository =
                deliveryRepository;
    }

    @Override
    @Transactional
    public ShipmentTracking createTrackingEvent(
            String deliveryId,
            DeliveryStatus deliveryStatus,
            String currentLocation,
            String statusMessage
    ) {
        Delivery delivery =
                deliveryRepository
                        .findById(deliveryId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Delivery was not found"
                                )
                        );

        ShipmentTracking tracking =
                ShipmentTrackingFactory
                        .createShipmentTracking(
                                delivery,
                                deliveryStatus,
                                currentLocation,
                                statusMessage
                        );

        if (tracking == null) {
            throw new IllegalArgumentException(
                    "Tracking details are invalid"
            );
        }

        return trackingRepository.save(tracking);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentTracking getTrackingEvent(
            String trackingId
    ) {
        return trackingRepository
                .findById(trackingId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Tracking event was not found"
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTracking>
    getDeliveryTrackingHistory(
            String deliveryId
    ) {
        return trackingRepository
                .findAllByDelivery_DeliveryIdOrderByUpdateTimestampAsc(
                        deliveryId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTracking>
    getTrackingHistoryByTrackingNumber(
            String trackingNumber
    ) {
        return trackingRepository
                .findAllByDelivery_TrackingNumberIgnoreCaseOrderByUpdateTimestampAsc(
                        trackingNumber
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTracking>
    getTrackingHistoryByOrderId(
            String orderId
    ) {
        return trackingRepository
                .findAllByDelivery_OrderIdOrderByUpdateTimestampAsc(
                        orderId
                );
    }
}