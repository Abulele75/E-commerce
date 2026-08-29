package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.domain.CartItem;
import cput.ac.za.ecommerce.domain.CartStatus;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.factory.CartFactory;
import cput.ac.za.ecommerce.factory.CartItemFactory;
import cput.ac.za.ecommerce.repository.CartRepository;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.request.AddCartItemRequest;
import cput.ac.za.ecommerce.request.UpdateCartItemRequest;
import cput.ac.za.ecommerce.response.CartResponse;
import cput.ac.za.ecommerce.service.ICartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;

    private final ProductCatalogRepository
            productRepository;

    private final UserManagementRepository
            userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            ProductCatalogRepository productRepository,
            UserManagementRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Cart getActiveCart(
            String customerEmail
    ) {
        String normalizedEmail =
                normalizeEmail(customerEmail);

        return cartRepository
                .findCartForCustomerByStatus(
                        normalizedEmail,
                        CartStatus.ACTIVE
                )
                .orElseGet(() ->
                        createActiveCart(
                                normalizedEmail
                        )
                );
    }

    @Override
    public CartResponse updateItem(String customerId, String itemId, UpdateCartItemRequest request) {
        return null;
    }

    @Override
    @Transactional
    public Cart addItem(
            String customerEmail,
            AddCartItemRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Cart-item request is required"
            );
        }

        String normalizedEmail =
                normalizeEmail(customerEmail);

        Cart cart =
                getActiveCartForUpdate(
                        normalizedEmail
                );

        ProductCatalog product =
                productRepository
                        .findByIdForUpdate(
                                request.getProductId()
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Product was not found"
                                )
                        );

        validateProductAvailability(
                product,
                request.getQuantity()
        );

        CartItem cartItem =
                CartItemFactory.createCartItem(
                        product,
                        request.getQuantity()
                );

        if (cartItem == null) {
            throw new IllegalArgumentException(
                    "Cart item could not be created"
            );
        }

        cart.addItem(cartItem);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateItemQuantity(
            String customerEmail,
            String cartItemId,
            UpdateCartItemRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Quantity request is required"
            );
        }

        validateIdentifier(
                cartItemId,
                "Cart item ID"
        );

        Cart cart =
                getActiveCartForUpdate(
                        normalizeEmail(
                                customerEmail
                        )
                );

        CartItem cartItem =
                cart.findItemById(cartItemId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Cart item was not found"
                                )
                        );

        ProductCatalog product =
                productRepository
                        .findByIdForUpdate(
                                cartItem
                                        .getProduct()
                                        .getProductId()
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Product was not found"
                                )
                        );

        validateProductAvailability(
                product,
                request.getQuantity()
        );

        cart.updateItemQuantity(
                cartItemId,
                request.getQuantity()
        );

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItem(
            String customerEmail,
            String cartItemId
    ) {
        validateIdentifier(
                cartItemId,
                "Cart item ID"
        );

        Cart cart =
                getActiveCartForUpdate(
                        normalizeEmail(
                                customerEmail
                        )
                );

        cart.removeItem(cartItemId);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart clearCart(
            String customerEmail
    ) {
        Cart cart =
                getActiveCartForUpdate(
                        normalizeEmail(
                                customerEmail
                        )
                );

        cart.clearItems();

        return cartRepository.save(cart);
    }

    @Override
    public CartResponse markCheckedOut(String customerId) {
        return null;
    }

    private Cart getActiveCartForUpdate(
            String customerEmail
    ) {
        return cartRepository
                .findCartForCustomerByStatusForUpdate(
                        customerEmail,
                        CartStatus.ACTIVE
                )
                .orElseGet(() ->
                        createActiveCart(
                                customerEmail
                        )
                );
    }

    private Cart createActiveCart(
            String customerEmail
    ) {
        Customer customer =
                userRepository
                        .findCustomerByEmailIgnoreCase(
                                customerEmail
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Customer account was not found"
                                )
                        );

        if (!customer.isActive()) {
            throw new IllegalStateException(
                    "Customer account is inactive"
            );
        }

        Cart cart =
                CartFactory.createCart(customer);

        if (cart == null) {
            throw new IllegalStateException(
                    "Shopping cart could not be created"
            );
        }

        return cartRepository.save(cart);
    }

    private void validateProductAvailability(
            ProductCatalog product,
            int requestedQuantity
    ) {
        if (product == null) {
            throw new EntityNotFoundException(
                    "Product was not found"
            );
        }

        if (!product.isActive()
                || !product.isAvailable()) {

            throw new IllegalStateException(
                    "Product is currently unavailable"
            );
        }

        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        if (requestedQuantity
                > product.getStockQuantity()) {

            throw new IllegalArgumentException(
                    "Only "
                            + product.getStockQuantity()
                            + " unit(s) are available"
            );
        }
    }

    private String normalizeEmail(
            String email
    ) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated customer email is required"
            );
        }

        return email.trim().toLowerCase();
    }

    private void validateIdentifier(
            String identifier,
            String fieldName
    ) {
        if (identifier == null
                || identifier.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName + " is required"
            );
        }
    }
}