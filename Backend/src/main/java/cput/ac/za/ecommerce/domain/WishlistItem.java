package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wishlist_item",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wishlist_customer_product",
                columnNames = {"customer_id", "product_id"}
        )
)
public class WishlistItem {

    @Id
    @Column(name = "wishlist_item_id", nullable = false, updatable = false, length = 50)
    private String wishlistItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductCatalog product;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    protected WishlistItem() {
    }

    public WishlistItem(String wishlistItemId, Customer customer, ProductCatalog product) {
        if (wishlistItemId == null || wishlistItemId.isBlank() || customer == null || product == null) {
            throw new IllegalArgumentException("Wishlist item details are required");
        }
        this.wishlistItemId = wishlistItemId;
        this.customer = customer;
        this.product = product;
        this.addedAt = LocalDateTime.now();
    }

    public String getWishlistItemId() {
        return wishlistItemId;
    }

    public ProductCatalog getProduct() {
        return product;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }
}
