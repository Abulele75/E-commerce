package cput.ac.za.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "shopping_cart")
public class Cart {

    @Id
    @Column(
            name = "cart_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String cartId;

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
            name = "cart_status",
            nullable = false,
            length = 30
    )
    private CartStatus cartStatus;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @OrderBy("createdAt ASC")
    private List<CartItem> items;

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

    protected Cart() {
        this.items = new ArrayList<>();
    }

    private Cart(Builder builder) {
        this.cartId = builder.cartId;
        this.customer = builder.customer;
        this.cartStatus = builder.cartStatus;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.items = new ArrayList<>();

        if (builder.items != null) {
            builder.items.forEach(this::addItem);
        }
    }

    public String getCartId() {
        return cartId;
    }

    public CartStatus getCartStatus() {
        return cartStatus;
    }

    public List<CartItem> getItems() {
        return List.copyOf(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonIgnore
    public Customer getCustomer() {
        return customer;
    }

    @Transient
    public BigDecimal getSubtotal() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    @Transient
    public int getTotalItemQuantity() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    @Transient
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Optional<CartItem> findItemByProductId(
            String productId
    ) {
        if (productId == null) {
            return Optional.empty();
        }

        return items.stream()
                .filter(item ->
                        productId.equals(
                                item.getProduct()
                                        .getProductId()
                        )
                )
                .findFirst();
    }

    public Optional<CartItem> findItemById(
            String cartItemId
    ) {
        if (cartItemId == null) {
            return Optional.empty();
        }

        return items.stream()
                .filter(item ->
                        cartItemId.equals(
                                item.getCartItemId()
                        )
                )
                .findFirst();
    }

    public void addItem(CartItem newItem) {
        ensureActive();

        if (newItem == null
                || newItem.getProduct() == null) {
            throw new IllegalArgumentException(
                    "A valid cart item is required"
            );
        }

        Optional<CartItem> existingItem =
                findItemByProductId(
                        newItem.getProduct()
                                .getProductId()
                );

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();

            item.changeQuantity(
                    item.getQuantity()
                            + newItem.getQuantity()
            );
        } else {
            newItem.attachToCart(this);
            items.add(newItem);
        }

        touch();
    }

    public void updateItemQuantity(
            String cartItemId,
            int quantity
    ) {
        ensureActive();

        CartItem item = findItemById(cartItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cart item not found"
                        )
                );

        item.changeQuantity(quantity);
        touch();
    }

    public void removeItem(String cartItemId) {
        ensureActive();

        boolean removed = items.removeIf(item ->
                cartItemId != null
                        && cartItemId.equals(
                        item.getCartItemId()
                )
        );

        if (!removed) {
            throw new IllegalArgumentException(
                    "Cart item not found"
            );
        }

        touch();
    }

    public void clearItems() {
        ensureActive();
        items.clear();
        touch();
    }

    public void markCheckedOut() {
        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "An empty cart cannot be checked out"
            );
        }

        this.cartStatus = CartStatus.CHECKED_OUT;
        touch();
    }

    public void markAbandoned() {
        if (cartStatus == CartStatus.CHECKED_OUT) {
            throw new IllegalStateException(
                    "A checked-out cart cannot be abandoned"
            );
        }

        this.cartStatus = CartStatus.ABANDONED;
        touch();
    }

    private void ensureActive() {
        if (cartStatus != CartStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only an active cart may be changed"
            );
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public static class Builder {

        private String cartId;
        private Customer customer;
        private CartStatus cartStatus =
                CartStatus.ACTIVE;

        private List<CartItem> items =
                new ArrayList<>();

        private LocalDateTime createdAt =
                LocalDateTime.now();

        private LocalDateTime updatedAt =
                LocalDateTime.now();

        public Builder setCartId(String cartId) {
            this.cartId = cartId;
            return this;
        }

        public Builder setCustomer(
                Customer customer
        ) {
            this.customer = customer;
            return this;
        }

        public Builder setCartStatus(
                CartStatus cartStatus
        ) {
            this.cartStatus = cartStatus;
            return this;
        }

        public Builder setItems(
                List<CartItem> items
        ) {
            this.items = items;
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

        public Cart build() {
            return new Cart(this);
        }
    }
}