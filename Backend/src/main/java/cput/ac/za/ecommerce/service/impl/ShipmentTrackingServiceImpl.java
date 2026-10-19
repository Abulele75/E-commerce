package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.domain.DeliveryStatus;
import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderStatus;
import cput.ac.za.ecommerce.domain.ShipmentTracking;
import cput.ac.za.ecommerce.factory.ShipmentTrackingFactory;
import cput.ac.za.ecommerce.repository.DeliveryRepository;
import cput.ac.za.ecommerce.repository.OrderRepository;
import cput.ac.za.ecommerce.repository.ShipmentTrackingRepository;
import cput.ac.za.ecommerce.service.IShipmentTrackingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShipmentTrackingServiceImpl implements IShipmentTrackingService {

    private final ShipmentTrackingRepository trackingRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public ShipmentTrackingServiceImpl(ShipmentTrackingRepository trackingRepository,
            DeliveryRepository deliveryRepository,
            OrderRepository orderRepository) {
        this.trackingRepository = trackingRepository;
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public ShipmentTracking createTrackingEvent(String deliveryId, DeliveryStatus deliveryStatus,
            String currentLocation, String statusMessage) {
        Delivery delivery = deliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery was not found"));
        delivery.advanceTo(deliveryStatus);

        Order order = orderRepository.findByIdForUpdate(delivery.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery order was not found"));
        synchronizeOrder(order, deliveryStatus);

        ShipmentTracking tracking = ShipmentTrackingFactory.createShipmentTracking(
                delivery, deliveryStatus, currentLocation, statusMessage
        );
        if (tracking == null) throw new IllegalArgumentException("Tracking details are invalid");
        deliveryRepository.save(delivery);
        orderRepository.save(order);
        return trackingRepository.save(tracking);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentTracking getTrackingEvent(String trackingId) {
        return trackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Tracking event was not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTracking> getDeliveryTrackingHistory(String deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) throw new EntityNotFoundException("Delivery was not found");
        return trackingRepository.findAllByDelivery_DeliveryIdOrderByUpdateTimestampAsc(deliveryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTracking> getTrackingHistoryByTrackingNumber(String trackingNumber, String customerEmail) {
        List<ShipmentTracking> events = trackingRepository
                .findAllByDelivery_TrackingNumberIgnoreCaseOrderByUpdateTimestampAsc(trackingNumber);
        if (!events.isEmpty()) verifyOwner(events.get(0).getDelivery().getOrderId(), customerEmail);
        return events;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentTracking> getTrackingHistoryByOrderId(String orderId, String customerEmail) {
        verifyOwner(orderId, customerEmail);
        return trackingRepository.findAllByDelivery_OrderIdOrderByUpdateTimestampAsc(orderId);
    }

    private void verifyOwner(String orderId, String email) {
        if (orderRepository.findForCustomer(orderId, email).isEmpty()) {
            throw new AccessDeniedException("You cannot view tracking for this order");
        }
    }

    private void synchronizeOrder(Order order, DeliveryStatus status) {
        if ((status == DeliveryStatus.DISPATCHED || status == DeliveryStatus.IN_TRANSIT)
                && order.getOrderStatus() == OrderStatus.PROCESSING) order.markShipped();
        if (status == DeliveryStatus.DELIVERED) {
            if (order.getOrderStatus() == OrderStatus.PROCESSING) order.markShipped();
            if (order.getOrderStatus() == OrderStatus.SHIPPED) order.markDelivered();
        }
    }
}
