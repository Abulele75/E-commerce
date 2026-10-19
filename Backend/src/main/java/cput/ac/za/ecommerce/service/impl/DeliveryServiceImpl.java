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
import cput.ac.za.ecommerce.request.CreateDeliveryRequest;
import cput.ac.za.ecommerce.service.IDeliveryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DeliveryServiceImpl implements IDeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final ShipmentTrackingRepository trackingRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository,
            OrderRepository orderRepository,
            ShipmentTrackingRepository trackingRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.trackingRepository = trackingRepository;
    }

    @Override
    @Transactional
    public Delivery assign(CreateDeliveryRequest request) {
        if (deliveryRepository.existsByOrderId(request.getOrderId())) {
            throw new IllegalStateException("This order already has a delivery");
        }
        if (deliveryRepository.existsByTrackingNumberIgnoreCase(request.getTrackingNumber())) {
            throw new IllegalStateException("This tracking number is already in use");
        }
        Order order = orderRepository.findByIdForUpdate(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order was not found"));
        if (order.getOrderStatus() != OrderStatus.PAID && order.getOrderStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Only paid or processing orders may be assigned for delivery");
        }
        if (order.getOrderStatus() == OrderStatus.PAID) order.startProcessing();

        Delivery delivery = new Delivery.Builder()
                .setDeliveryId("DLV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase())
                .setOrderId(order.getOrderId())
                .setTrackingNumber(request.getTrackingNumber().trim())
                .setDeliveryStatus(DeliveryStatus.PREPARING)
                .setDeliveryAddress(order.getDeliveryAddress())
                .build();
        deliveryRepository.save(delivery);

        String assignment = "Assigned to " + request.getCarrier().trim()
                + (request.getCarrierContact() == null || request.getCarrierContact().isBlank() ? "" : " · contact " + request.getCarrierContact().trim())
                + ". Your order is being prepared for dispatch.";
        ShipmentTracking event = ShipmentTrackingFactory.createShipmentTracking(
                delivery, DeliveryStatus.PREPARING, request.getCarrier().trim(), assignment
        );
        if (event == null) throw new IllegalStateException("Initial tracking event could not be created");
        trackingRepository.save(event);
        orderRepository.save(order);
        return delivery;
    }

    @Override
    @Transactional
    public Delivery create(Delivery delivery) {
        if (delivery == null) throw new IllegalArgumentException("Delivery is required");
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public Delivery read(String deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery was not found"));
    }

    @Override
    @Transactional
    public Delivery update(Delivery delivery) {
        if (delivery == null || !deliveryRepository.existsById(delivery.getDeliveryId())) {
            throw new EntityNotFoundException("Delivery was not found");
        }
        return deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public boolean delete(String deliveryId) {
        if (!deliveryRepository.existsById(deliveryId)) return false;
        deliveryRepository.deleteById(deliveryId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delivery> getAll() {
        return deliveryRepository.findAll();
    }
}
