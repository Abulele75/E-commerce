package cput.ac.za.ecommerce.domain;
/*
  ShipmentTracking.java
  Entity for shipment tracking
  Author: Sinazo Ntsimbi(222765208)
  Date: 19 June 2026
 */



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_tracking")
public class ShipmentTracking {

    @Id
    @Column(
            name = "tracking_event_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String trackingId;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "delivery_id",
            nullable = false
    )
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "delivery_status",
            nullable = false,
            length = 30
    )
    private DeliveryStatus deliveryStatus;

    @Column(
            name = "current_location",
            length = 150
    )
    private String currentLocation;

    @Column(
            name = "status_message",
            nullable = false,
            length = 300
    )
    private String statusMessage;

    @Column(
            name = "update_timestamp",
            nullable = false,
            updatable = false
    )
    private LocalDateTime updateTimestamp;

    protected ShipmentTracking() {
    }

    private ShipmentTracking(Builder builder) {
        this.trackingId = builder.trackingId;
        this.delivery = builder.delivery;
        this.deliveryStatus =
                builder.deliveryStatus;
        this.currentLocation =
                builder.currentLocation;
        this.statusMessage =
                builder.statusMessage;
        this.updateTimestamp =
                builder.updateTimestamp;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public LocalDateTime getUpdateTimestamp() {
        return updateTimestamp;
    }

    @JsonIgnore
    public Delivery getDelivery() {
        return delivery;
    }

    public static class Builder {

        private String trackingId;
        private Delivery delivery;
        private DeliveryStatus deliveryStatus;
        private String currentLocation;
        private String statusMessage;
        private LocalDateTime updateTimestamp;

        public Builder setTrackingId(
                String trackingId
        ) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder setDelivery(
                Delivery delivery
        ) {
            this.delivery = delivery;
            return this;
        }

        public Builder setDeliveryStatus(
                DeliveryStatus deliveryStatus
        ) {
            this.deliveryStatus = deliveryStatus;
            return this;
        }

        public Builder setCurrentLocation(
                String currentLocation
        ) {
            this.currentLocation =
                    currentLocation;
            return this;
        }

        public Builder setStatusMessage(
                String statusMessage
        ) {
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder setUpdateTimestamp(
                LocalDateTime updateTimestamp
        ) {
            this.updateTimestamp =
                    updateTimestamp;
            return this;
        }

        public ShipmentTracking build() {
            return new ShipmentTracking(this);
        }
    }
}