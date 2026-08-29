package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository
        extends JpaRepository<CartItem, String> {

    List<CartItem> findAllByCart_CartId(
            String cartId
    );

    boolean existsByCart_CartIdAndProduct_ProductId(
            String cartId,
            String productId
    );
}