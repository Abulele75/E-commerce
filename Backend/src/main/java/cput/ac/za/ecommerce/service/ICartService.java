package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.request.AddCartItemRequest;
import cput.ac.za.ecommerce.request.UpdateCartItemRequest;

public interface ICartService {

    Cart getActiveCart(
            String customerEmail
    );

    Cart addItem(
            String customerEmail,
            AddCartItemRequest request
    );

    Cart updateItemQuantity(
            String customerEmail,
            String cartItemId,
            UpdateCartItemRequest request
    );

    Cart removeItem(
            String customerEmail,
            String cartItemId
    );

    Cart clearCart(
            String customerEmail
    );
}