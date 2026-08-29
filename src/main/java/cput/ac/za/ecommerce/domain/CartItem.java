package cput.ac.za.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "cart_item",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "cart_product",
                        columnNames = {
                                "cart_id",
                                "product_id"
                        }
                )
        }
)
public class CartItem {

    @Id
    @Column(
            name = "cart_item_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String cartItemId;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "cart_id",
            nullable = false
    )
    private Cart cart;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = false
    )
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private ProductCatalog product;

    @Column(nullable = false)
    private int quantity;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    protected CartItem() {
    }

    private CartItem(Builder builder) {
        this.cartItemId = builder.cartItemId;
        this.cart = builder.cart;
        this.product = builder.product;
        this.quantity = builder.quantity;
        this.createdAt = builder.createdAt;
    }

    public String getCartItemId() {
        return cartItemId;
    }

    @JsonIgnore
    public Cart getCart() {
        return cart;
    }

    public ProductCatalog getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Transient
    public BigDecimal getUnitPrice() {
        if (product == null) {
            return BigDecimal.ZERO;
        }

        return product.getEffectivePrice();
    }

    @Transient
    public BigDecimal getLineTotal() {
        return getUnitPrice().multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    void attachToCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException(
                    "Cart is required"
            );
        }

        this.cart = cart;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (product == null) {
            throw new IllegalStateException(
                    "Product is required"
            );
        }

        if (!product.isAvailable()) {
            throw new IllegalStateException(
                    "Product is currently unavailable"
            );
        }

        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }
    }

    public static class Builder {

        private String cartItemId;
        private Cart cart;
        private ProductCatalog product;
        private int quantity;
        private LocalDateTime createdAt =
                LocalDateTime.now();

        public Builder setCartItemId(
                String cartItemId
        ) {
            this.cartItemId = cartItemId;
            return this;
        }

        public Builder setCart(Cart cart) {
            this.cart = cart;
            return this;
        }

        public Builder setProduct(
                ProductCatalog product
        ) {
            this.product = product;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder setCreatedAt(
                LocalDateTime createdAt
        ) {
            this.createdAt = createdAt;
            return this;
        }

        public CartItem build() {
            return new CartItem(this);
        }
    }
}