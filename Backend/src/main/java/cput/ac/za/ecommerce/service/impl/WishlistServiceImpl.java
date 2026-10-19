package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.WishlistItem;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.repository.WishlistItemRepository;
import cput.ac.za.ecommerce.response.WishlistItemResponse;
import cput.ac.za.ecommerce.service.IWishlistService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WishlistServiceImpl implements IWishlistService {

    private final WishlistItemRepository wishlistRepository;
    private final UserManagementRepository userRepository;
    private final ProductCatalogRepository productRepository;

    public WishlistServiceImpl(WishlistItemRepository wishlistRepository,
            UserManagementRepository userRepository,
            ProductCatalogRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItemResponse> list(String customerEmail) {
        Customer customer = customer(customerEmail);
        return wishlistRepository.findAllForCustomer(customer.getUserId()).stream()
                .map(WishlistItemResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public WishlistItemResponse add(String customerEmail, String productId) {
        Customer customer = customer(customerEmail);
        validateProductId(productId);
        WishlistItem existing = wishlistRepository
                .findByCustomer_UserIdAndProduct_ProductId(customer.getUserId(), productId)
                .orElse(null);
        if (existing != null) return WishlistItemResponse.from(existing);

        ProductCatalog product = productRepository.findById(productId)
                .filter(ProductCatalog::isActive)
                .orElseThrow(() -> new EntityNotFoundException("Product was not found"));
        WishlistItem item = new WishlistItem(
                "WI-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase(),
                customer,
                product
        );
        return WishlistItemResponse.from(wishlistRepository.save(item));
    }

    @Override
    @Transactional
    public void remove(String customerEmail, String productId) {
        Customer customer = customer(customerEmail);
        validateProductId(productId);
        wishlistRepository.findByCustomer_UserIdAndProduct_ProductId(customer.getUserId(), productId)
                .ifPresent(wishlistRepository::delete);
    }

    private Customer customer(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Customer email is required");
        Customer customer = userRepository.findCustomerByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Customer account was not found"));
        if (!customer.isActive()) throw new IllegalStateException("Customer account is inactive");
        return customer;
    }

    private void validateProductId(String productId) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID is required");
    }
}
