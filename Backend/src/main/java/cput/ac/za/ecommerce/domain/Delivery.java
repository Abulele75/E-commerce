package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "delivery", uniqueConstraints = {
        @UniqueConstraint(name = "uk_delivery_order", columnNames = "order_id"),
        @UniqueConstraint(name = "uk_delivery_tracking_number", columnNames = "tracking_number")
})
public class Delivery {

    private static final Map<DeliveryStatus, Set<DeliveryStatus>> TRANSITIONS = Map.of(
            DeliveryStatus.PREPARING, EnumSet.of(DeliveryStatus.DISPATCHED, DeliveryStatus.CANCELLED),
            DeliveryStatus.DISPATCHED, EnumSet.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.CANCELLED),
            DeliveryStatus.IN_TRANSIT, EnumSet.of(DeliveryStatus.OUT_FOR_DELIVERY, DeliveryStatus.DELIVERY_FAILED, DeliveryStatus.CANCELLED),
            DeliveryStatus.OUT_FOR_DELIVERY, EnumSet.of(DeliveryStatus.DELIVERED, DeliveryStatus.DELIVERY_FAILED),
            DeliveryStatus.DELIVERY_FAILED, EnumSet.of(DeliveryStatus.OUT_FOR_DELIVERY, DeliveryStatus.CANCELLED)
    );

    @Id
    @Column(name = "delivery_id", nullable = false, updatable = false, length = 50)
    private String deliveryId;

    @Column(name = "order_id", nullable = false, updatable = false, length = 50)
    private String orderId;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 100)
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 30)
    private DeliveryStatus deliveryStatus;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Delivery() {}

    private Delivery(Builder builder) {
        this.deliveryId = builder.deliveryId;
        this.orderId = builder.orderId;
        this.trackingNumber = builder.trackingNumber;
        this.deliveryStatus = builder.deliveryStatus;
        this.deliveryAddress = builder.deliveryAddress;
        this.createdAt = builder.createdAt == null ? LocalDateTime.now() : builder.createdAt;
        this.updatedAt = this.createdAt;
    }

    public String getDeliveryId() { return deliveryId; }
    public String getOrderId() { return orderId; }
    public String getTrackingNumber() { return trackingNumber; }
    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public DeliveryAddress getDeliveryAddress() { return deliveryAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void advanceTo(DeliveryStatus nextStatus) {
        if (nextStatus == null) throw new IllegalArgumentException("Delivery status is required");
        if (nextStatus == deliveryStatus) return;
        if (!TRANSITIONS.getOrDefault(deliveryStatus, Set.of()).contains(nextStatus)) {
            throw new IllegalStateException("Delivery cannot move from " + deliveryStatus + " to " + nextStatus);
        }
        deliveryStatus = nextStatus;
        updatedAt = LocalDateTime.now();
    }

    public static class Builder {
        private String deliveryId;
        private String orderId;
        private String trackingNumber;
        private DeliveryStatus deliveryStatus;
        private DeliveryAddress deliveryAddress;
        private LocalDateTime createdAt;
        public Builder setDeliveryId(String value) { deliveryId = value; return this; }
        public Builder setOrderId(String value) { orderId = value; return this; }
        public Builder setTrackingNumber(String value) { trackingNumber = value; return this; }
        public Builder setDeliveryStatus(DeliveryStatus value) { deliveryStatus = value; return this; }
        public Builder setDeliveryStatus(String value) { deliveryStatus = value == null ? null : DeliveryStatus.valueOf(value); return this; }
        public Builder setDeliveryAddress(DeliveryAddress value) { deliveryAddress = value; return this; }
        public Builder setCreatedAt(LocalDateTime value) { createdAt = value; return this; }
        public Delivery build() { return new Delivery(this); }
    }
}
