package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.response.WishlistItemResponse;
import cput.ac.za.ecommerce.service.IWishlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final IWishlistService wishlistService;

    public WishlistController(IWishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(wishlistService.list(email(authentication)));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<WishlistItemResponse> add(@PathVariable String productId, Authentication authentication) {
        return ResponseEntity.ok(wishlistService.add(email(authentication), productId));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> remove(@PathVariable String productId, Authentication authentication) {
        wishlistService.remove(email(authentication), productId);
        return ResponseEntity.noContent().build();
    }

    private String email(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AccessDeniedException("Authentication is required");
        }
        return authentication.getName().trim().toLowerCase();
    }
}
