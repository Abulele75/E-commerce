package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.request.AddCartItemRequest;
import cput.ac.za.ecommerce.request.UpdateCartItemRequest;
import cput.ac.za.ecommerce.response.CartResponse;
import cput.ac.za.ecommerce.service.ICartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ICartService cartService;

    public CartController(
            ICartService cartService
    ) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse>
    getActiveCart(
            Authentication authentication
    ) {
        Cart cart =
                cartService.getActiveCart(
                        getAuthenticatedEmail(
                                authentication
                        )
                );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse>
    addItem(
            @Valid
            @RequestBody
            AddCartItemRequest request,

            Authentication authentication
    ) {
        Cart cart =
                cartService.addItem(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        CartResponse.from(cart)
                );
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse>
    updateItemQuantity(
            @PathVariable
            String cartItemId,

            @Valid
            @RequestBody
            UpdateCartItemRequest request,

            Authentication authentication
    ) {
        Cart cart =
                cartService.updateItemQuantity(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        cartItemId,
                        request
                );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse>
    removeItem(
            @PathVariable
            String cartItemId,

            Authentication authentication
    ) {
        Cart cart =
                cartService.removeItem(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        cartItemId
                );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<CartResponse>
    clearCart(
            Authentication authentication
    ) {
        Cart cart =
                cartService.clearCart(
                        getAuthenticatedEmail(
                                authentication
                        )
                );

        return ResponseEntity.ok(
                CartResponse.from(cart)
        );
    }

    private String getAuthenticatedEmail(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        return authentication
                .getName()
                .trim()
                .toLowerCase();
    }
}