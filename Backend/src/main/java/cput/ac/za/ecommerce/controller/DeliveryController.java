package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.domain.ShipmentTracking;
import cput.ac.za.ecommerce.request.CreateDeliveryRequest;
import cput.ac.za.ecommerce.request.ShipmentTrackingRequest;
import cput.ac.za.ecommerce.service.IDeliveryService;
import cput.ac.za.ecommerce.service.IShipmentTrackingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final IDeliveryService deliveryService;
    private final IShipmentTrackingService trackingService;

    public DeliveryController(IDeliveryService deliveryService, IShipmentTrackingService trackingService) {
        this.deliveryService = deliveryService;
        this.trackingService = trackingService;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.assign(request));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/{deliveryId}")
    public ResponseEntity<Delivery> getDelivery(@PathVariable String deliveryId) {
        return ResponseEntity.ok(deliveryService.read(deliveryId));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAll());
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PutMapping("/{deliveryId}")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable String deliveryId, @RequestBody Delivery delivery) {
        if (!deliveryId.equals(delivery.getDeliveryId())) throw new IllegalArgumentException("Delivery ID does not match the URL");
        return ResponseEntity.ok(deliveryService.update(delivery));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable String deliveryId) {
        return deliveryService.delete(deliveryId) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping("/{deliveryId}/tracking")
    public ResponseEntity<ShipmentTracking> addTrackingEvent(@PathVariable String deliveryId,
            @Valid @RequestBody ShipmentTrackingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trackingService.createTrackingEvent(
                deliveryId, request.getDeliveryStatus(), request.getCurrentLocation(), request.getStatusMessage()
        ));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/{deliveryId}/tracking")
    public ResponseEntity<List<ShipmentTracking>> getTrackingHistory(@PathVariable String deliveryId) {
        return ResponseEntity.ok(trackingService.getDeliveryTrackingHistory(deliveryId));
    }

    @GetMapping("/tracking-number/{trackingNumber}")
    public ResponseEntity<List<ShipmentTracking>> trackByTrackingNumber(@PathVariable String trackingNumber,
            Authentication authentication) {
        return ResponseEntity.ok(trackingService.getTrackingHistoryByTrackingNumber(trackingNumber, email(authentication)));
    }

    @GetMapping("/order/{orderId}/tracking")
    public ResponseEntity<List<ShipmentTracking>> trackByOrderId(@PathVariable String orderId,
            Authentication authentication) {
        return ResponseEntity.ok(trackingService.getTrackingHistoryByOrderId(orderId, email(authentication)));
    }

    private String email(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AccessDeniedException("Authentication is required");
        }
        return authentication.getName().trim().toLowerCase();
    }
}
