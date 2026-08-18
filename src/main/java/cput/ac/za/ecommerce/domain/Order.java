package cput.ac.za.ecommerce.domain;

/*
 * Order.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customer_order")
public class Order {

    @Id
    private UUID orderId;

    private UUID customerId;

    private LocalDateTime dateCreated;

    private String currentOrderStatus;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderLineItems;

    @Embedded
    private FinancialBreakdown dynamicTotals;

    protected Order() {
    }

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.dateCreated = builder.dateCreated;
        this.currentOrderStatus = builder.currentOrderStatus;

        // Make a new list so the original list cannot change the Order.
        this.orderLineItems = builder.orderLineItems == null
                ? new ArrayList<>()
                : new ArrayList<>(builder.orderLineItems);

        this.dynamicTotals = builder.dynamicTotals;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public String getCurrentOrderStatus() {
        return currentOrderStatus;
    }

    public List<OrderItem> getOrderLineItems() {
        return new ArrayList<>(orderLineItems);
    }

    public FinancialBreakdown getDynamicTotals() {
        return dynamicTotals;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", dateCreated=" + dateCreated +
                ", currentOrderStatus='" + currentOrderStatus + '\'' +
                ", orderLineItems=" + orderLineItems +
                ", dynamicTotals=" + dynamicTotals +
                '}';
    }

    public static class Builder {

        private UUID orderId;
        private UUID customerId;
        private LocalDateTime dateCreated;
        private String currentOrderStatus;
        private List<OrderItem> orderLineItems;
        private FinancialBreakdown dynamicTotals;

        public Builder setOrderId(UUID orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder setCustomerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setDateCreated(LocalDateTime dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public Builder setCurrentOrderStatus(String currentOrderStatus) {
            this.currentOrderStatus = currentOrderStatus;
            return this;
        }

        public Builder setOrderLineItems(List<OrderItem> orderLineItems) {
            this.orderLineItems = orderLineItems;
            return this;
        }

        public Builder setDynamicTotals(FinancialBreakdown dynamicTotals) {
            this.dynamicTotals = dynamicTotals;
            return this;
        }

        public Builder copy(Order order) {
            this.orderId = order.orderId;
            this.customerId = order.customerId;
            this.dateCreated = order.dateCreated;
            this.currentOrderStatus = order.currentOrderStatus;
            this.orderLineItems = order.orderLineItems;
            this.dynamicTotals = order.dynamicTotals;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}