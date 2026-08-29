
/*
 * Order.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 21 June 2026
 */
package cput.ac.za.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_order")
public class Order {

    @Id
    @Column(
            name = "order_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String orderId;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "order_status",
            nullable = false,
            length = 30
    )
    private OrderStatus orderStatus;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )

    @OrderBy("productNameSnapshot ASC")
    private List<OrderItem> orderItems;

    @Embedded
    private FinancialBreakdown financialBreakdown;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Column(
            name = "delivery_instructions",
            length = 500
    )
    private String deliveryInstructions;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    @JsonIgnore
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "source_cart_id",
            nullable = false,
            unique = true
    )
    private Cart sourceCart;

    @JsonIgnore
    public Cart getSourceCart() {
        return sourceCart;
    }

    protected Order() {
        this.orderItems = new ArrayList<>();
    }
    public void markRefunded() {

        if (orderStatus != OrderStatus.PAID
                && orderStatus != OrderStatus.PROCESSING) {

            throw new IllegalStateException(
                    "Only a paid or processing order may be refunded"
            );
        }

        this.orderStatus = OrderStatus.REFUNDED;
        touch();
    }

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customer = builder.customer;
        this.sourceCart = builder.sourceCart;
        this.orderStatus = builder.orderStatus;
        this.financialBreakdown =
                builder.financialBreakdown;

        this.deliveryAddress =
                builder.deliveryAddress;

        this.deliveryInstructions =
                builder.deliveryInstructions;

        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.paidAt = builder.paidAt;
        this.orderItems = new ArrayList<>();

        if (builder.orderItems != null) {
            builder.orderItems.forEach(
                    this::addOrderItem
            );
        }
    }

    public String getOrderId() {
        return orderId;
    }

    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<OrderItem> getOrderItems() {
        return List.copyOf(orderItems);
    }

    public FinancialBreakdown
    getFinancialBreakdown() {
        return financialBreakdown;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void addOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException(
                    "Order item is required"
            );
        }

        orderItem.attachToOrder(this);
        orderItems.add(orderItem);
        touch();
    }

    /*
     * Re-syncs an unpaid order with the latest cart contents and delivery
     * details. Keeps checkout retry-safe (no duplicate orders per cart) while
     * ensuring the amount and items always reflect what the customer sees now.
     */
    public void reviseCheckout(
            List<OrderItem> newItems,
            FinancialBreakdown financialBreakdown,
            DeliveryAddress deliveryAddress,
            String deliveryInstructions
    ) {
        if (orderStatus != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Only an order awaiting payment may be revised"
            );
        }

        if (newItems == null || newItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "An order must contain at least one item"
            );
        }

        // orphanRemoval deletes the previous item rows on flush.
        this.orderItems.clear();
        newItems.forEach(this::addOrderItem);

        this.financialBreakdown = financialBreakdown;
        this.deliveryAddress = deliveryAddress;
        this.deliveryInstructions = deliveryInstructions;
        touch();
    }

    public void markPaid() {
        if (orderStatus
                != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Only a pending order may be marked as paid"
            );
        }

        this.orderStatus = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
        touch();
    }

    public void startProcessing() {
        if (orderStatus != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Only a paid order may be processed"
            );
        }

        this.orderStatus =
                OrderStatus.PROCESSING;

        touch();
    }

    public void markShipped() {
        if (orderStatus
                != OrderStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only a processing order may be shipped"
            );
        }

        this.orderStatus = OrderStatus.SHIPPED;
        touch();
    }

    public void markDelivered() {
        if (orderStatus
                != OrderStatus.SHIPPED) {
            throw new IllegalStateException(
                    "Only a shipped order may be delivered"
            );
        }

        this.orderStatus =
                OrderStatus.DELIVERED;

        touch();
    }

    public void cancel() {
        if (orderStatus == OrderStatus.SHIPPED
                || orderStatus
                == OrderStatus.DELIVERED
                || orderStatus
                == OrderStatus.CANCELLED) {

            throw new IllegalStateException(
                    "This order cannot be cancelled"
            );
        }

        this.orderStatus =
                OrderStatus.CANCELLED;

        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public static class Builder {

        private String orderId;
        private Customer customer;
        private Cart sourceCart;

        private OrderStatus orderStatus =
                OrderStatus.PENDING_PAYMENT;

        private List<OrderItem> orderItems =
                new ArrayList<>();

        private FinancialBreakdown
                financialBreakdown;

        private DeliveryAddress deliveryAddress;
        private String deliveryInstructions;

        private LocalDateTime createdAt =
                LocalDateTime.now();

        private LocalDateTime updatedAt =
                LocalDateTime.now();

        private LocalDateTime paidAt;

        public Builder setOrderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setCustomer(
                Customer customer
        ) {
            this.customer = customer;
            return this;
        }

        public Builder setOrderStatus(
                OrderStatus orderStatus
        ) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder setOrderItems(
                List<OrderItem> orderItems
        ) {
            this.orderItems = orderItems;
            return this;
        }

        public Builder setFinancialBreakdown(
                FinancialBreakdown
                        financialBreakdown
        ) {
            this.financialBreakdown =
                    financialBreakdown;

            return this;
        }
        public Builder setSourceCart(
                Cart sourceCart
        ) {
            this.sourceCart = sourceCart;
            return this;
        }
        public Builder setDeliveryAddress(
                DeliveryAddress deliveryAddress
        ) {
            this.deliveryAddress =
                    deliveryAddress;

            return this;
        }

        public Builder setDeliveryInstructions(
                String deliveryInstructions
        ) {
            this.deliveryInstructions =
                    deliveryInstructions;

            return this;
        }

        public Builder setCreatedAt(
                LocalDateTime createdAt
        ) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(
                LocalDateTime updatedAt
        ) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setPaidAt(
                LocalDateTime paidAt
        ) {
            this.paidAt = paidAt;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}