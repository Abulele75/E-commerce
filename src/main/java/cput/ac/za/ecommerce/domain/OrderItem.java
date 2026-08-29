/*
 * OrderItem.java
 * Author: Sinethemba Nyimbinya (220085870)
 * 21 June 2026
 */

package cput.ac.za.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @Column(
            name = "order_item_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String orderItemId;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @Column(
            name = "product_id_snapshot",
            nullable = false,
            length = 50
    )
    private String productIdSnapshot;

    @Column(
            name = "sku_snapshot",
            nullable = false,
            length = 60
    )
    private String skuSnapshot;

    @Column(
            name = "product_name_snapshot",
            nullable = false,
            length = 150
    )
    private String productNameSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "brand_snapshot",
            nullable = false,
            length = 40
    )
    private Brand brandSnapshot;

    @Column(
            name = "image_url_snapshot",
            length = 1000
    )
    private String imageUrlSnapshot;

    @Column(
            name = "unit_price_snapshot",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal unitPriceSnapshot;

    @Column(
            name = "quantity_purchased",
            nullable = false
    )
    private int quantityPurchased;

    @Column(
            name = "line_total_snapshot",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal lineTotalSnapshot;

    protected OrderItem() {
    }

    private OrderItem(Builder builder) {
        this.orderItemId = builder.orderItemId;
        this.order = builder.order;
        this.productIdSnapshot =
                builder.productIdSnapshot;

        this.skuSnapshot =
                builder.skuSnapshot;

        this.productNameSnapshot =
                builder.productNameSnapshot;

        this.brandSnapshot =
                builder.brandSnapshot;

        this.imageUrlSnapshot =
                builder.imageUrlSnapshot;

        this.unitPriceSnapshot =
                builder.unitPriceSnapshot;

        this.quantityPurchased =
                builder.quantityPurchased;

        this.lineTotalSnapshot =
                builder.unitPriceSnapshot.multiply(
                        BigDecimal.valueOf(
                                builder.quantityPurchased
                        )
                );
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    public String getProductIdSnapshot() {
        return productIdSnapshot;
    }

    public String getSkuSnapshot() {
        return skuSnapshot;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public Brand getBrandSnapshot() {
        return brandSnapshot;
    }

    public String getImageUrlSnapshot() {
        return imageUrlSnapshot;
    }

    public BigDecimal getUnitPriceSnapshot() {
        return unitPriceSnapshot;
    }

    public int getQuantityPurchased() {
        return quantityPurchased;
    }

    public BigDecimal getLineTotalSnapshot() {
        return lineTotalSnapshot;
    }

    void attachToOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(
                    "Order is required"
            );
        }

        this.order = order;
    }

    public static class Builder {

        private String orderItemId;
        private Order order;
        private String productIdSnapshot;
        private String skuSnapshot;
        private String productNameSnapshot;
        private Brand brandSnapshot;
        private String imageUrlSnapshot;
        private BigDecimal unitPriceSnapshot;
        private int quantityPurchased;

        public Builder setOrderItemId(
                String orderItemId
        ) {
            this.orderItemId = orderItemId;
            return this;
        }

        public Builder setOrder(Order order) {
            this.order = order;
            return this;
        }

        public Builder setProductIdSnapshot(
                String productIdSnapshot
        ) {
            this.productIdSnapshot =
                    productIdSnapshot;

            return this;
        }

        public Builder setSkuSnapshot(
                String skuSnapshot
        ) {
            this.skuSnapshot = skuSnapshot;
            return this;
        }

        public Builder setProductNameSnapshot(
                String productNameSnapshot
        ) {
            this.productNameSnapshot =
                    productNameSnapshot;

            return this;
        }

        public Builder setBrandSnapshot(
                Brand brandSnapshot
        ) {
            this.brandSnapshot =
                    brandSnapshot;

            return this;
        }

        public Builder setImageUrlSnapshot(
                String imageUrlSnapshot
        ) {
            this.imageUrlSnapshot =
                    imageUrlSnapshot;

            return this;
        }

        public Builder setUnitPriceSnapshot(
                BigDecimal unitPriceSnapshot
        ) {
            this.unitPriceSnapshot =
                    unitPriceSnapshot;

            return this;
        }

        public Builder setQuantityPurchased(
                int quantityPurchased
        ) {
            this.quantityPurchased =
                    quantityPurchased;

            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}