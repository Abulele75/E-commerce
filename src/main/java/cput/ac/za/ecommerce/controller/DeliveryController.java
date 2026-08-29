package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Delivery;
import cput.ac.za.ecommerce.domain.ShipmentTracking;
import cput.ac.za.ecommerce.request.ShipmentTrackingRequest;
import cput.ac.za.ecommerce.service.IDeliveryService;
import cput.ac.za.ecommerce.service.IShipmentTrackingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final IDeliveryService deliveryService;

    private final IShipmentTrackingService
            trackingService;

    public DeliveryController(
            IDeliveryService deliveryService,
            IShipmentTrackingService trackingService
    ) {
        this.deliveryService = deliveryService;
        this.trackingService = trackingService;
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PostMapping
    public ResponseEntity<Delivery>
    createDelivery(
            @RequestBody Delivery delivery
    ) {
        Delivery created =
                deliveryService.create(delivery);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<Delivery>
    getDelivery(
            @PathVariable String deliveryId
    ) {
        Delivery delivery =
                deliveryService.read(deliveryId);

        if (delivery == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        return ResponseEntity.ok(delivery);
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @GetMapping
    public ResponseEntity<List<Delivery>>
    getAllDeliveries() {
        return ResponseEntity.ok(
                deliveryService.getAll()
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PutMapping("/{deliveryId}")
    public ResponseEntity<Delivery>
    updateDelivery(
            @PathVariable String deliveryId,
            @RequestBody Delivery delivery
    ) {
        if (!deliveryId.equals(
                delivery.getDeliveryId()
        )) {
            throw new IllegalArgumentException(
                    "Delivery ID does not match the URL"
            );
        }

        return ResponseEntity.ok(
                deliveryService.update(delivery)
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<Void>
    deleteDelivery(
            @PathVariable String deliveryId
    ) {
        boolean deleted =
                deliveryService.delete(deliveryId);

        return deleted
                ? ResponseEntity.noContent()
                .build()
                : ResponseEntity.notFound()
                .build();
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PostMapping("/{deliveryId}/tracking")
    public ResponseEntity<ShipmentTracking>
    addTrackingEvent(
            @PathVariable String deliveryId,

            @Valid
            @RequestBody
            ShipmentTrackingRequest request
    ) {
        ShipmentTracking tracking =
                trackingService
                        .createTrackingEvent(
                                deliveryId,
                                request.getDeliveryStatus(),
                                request.getCurrentLocation(),
                                request.getStatusMessage()
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tracking);
    }

    @GetMapping("/{deliveryId}/tracking")
    public ResponseEntity<List<ShipmentTracking>>
    getTrackingHistory(
            @PathVariable String deliveryId
    ) {
        return ResponseEntity.ok(
                trackingService
                        .getDeliveryTrackingHistory(
                                deliveryId
                        )
        );
    }

    @GetMapping(
            "/tracking-number/{trackingNumber}"
    )
    public ResponseEntity<List<ShipmentTracking>>
    trackByTrackingNumber(
            @PathVariable String trackingNumber
    ) {
        return ResponseEntity.ok(
                trackingService
                        .getTrackingHistoryByTrackingNumber(
                                trackingNumber
                        )
        );
    }

    @GetMapping(
            "/order/{orderId}/tracking"
    )
    public ResponseEntity<List<ShipmentTracking>>
    trackByOrderId(
            @PathVariable String orderId
    ) {
        return ResponseEntity.ok(
                trackingService
                        .getTrackingHistoryByOrderId(
                                orderId
                        )
        );
    }
}