package cput.ac.za.ecommerce.domain;

/*
 * OrderItem.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class OrderItem {

    @Id
    private UUID orderItemId;

    private UUID productId;

    private int quantityPurchased;

    private double itemPriceSnapshot;

    protected OrderItem() {
    }

    private OrderItem(Builder builder) {
        this.orderItemId = builder.orderItemId;
        this.productId = builder.productId;
        this.quantityPurchased = builder.quantityPurchased;
        this.itemPriceSnapshot = builder.itemPriceSnapshot;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantityPurchased() {
        return quantityPurchased;
    }

    public double getItemPriceSnapshot() {
        return itemPriceSnapshot;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", productId=" + productId +
                ", quantityPurchased=" + quantityPurchased +
                ", itemPriceSnapshot=" + itemPriceSnapshot +
                '}';
    }

    public static class Builder {

        private UUID orderItemId;
        private UUID productId;
        private int quantityPurchased;
        private double itemPriceSnapshot;

        public Builder setOrderItemId(UUID orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public Builder setProductId(UUID productId) {
            this.productId = productId;
            return this;
        }

        public Builder setQuantityPurchased(int quantityPurchased) {
            this.quantityPurchased = quantityPurchased;
            return this;
        }

        public Builder setItemPriceSnapshot(double itemPriceSnapshot) {
            this.itemPriceSnapshot = itemPriceSnapshot;
            return this;
        }

        public Builder copy(OrderItem orderItem) {
            this.orderItemId = orderItem.orderItemId;
            this.productId = orderItem.productId;
            this.quantityPurchased = orderItem.quantityPurchased;
            this.itemPriceSnapshot = orderItem.itemPriceSnapshot;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}