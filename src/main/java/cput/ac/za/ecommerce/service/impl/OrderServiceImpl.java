package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/*
 * OrderServiceImpl.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cput.ac.za.ecommerce.domain.Cart;
import cput.ac.za.ecommerce.domain.CartItem;
import cput.ac.za.ecommerce.domain.CartStatus;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.DeliveryAddress;
import cput.ac.za.ecommerce.domain.FinancialBreakdown;
import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderItem;
import cput.ac.za.ecommerce.domain.OrderStatus;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.factory.DeliveryAddressFactory;
import cput.ac.za.ecommerce.factory.FinancialBreakdownFactory;
import cput.ac.za.ecommerce.factory.OrderFactory;
import cput.ac.za.ecommerce.factory.OrderItemFactory;
import cput.ac.za.ecommerce.repository.CartRepository;
import cput.ac.za.ecommerce.repository.OrderRepository;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.request.CheckoutRequest;
import cput.ac.za.ecommerce.service.IOrderService;
import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderServiceImpl
        implements IOrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    private final ProductCatalogRepository
            productRepository;

    private final BigDecimal deliveryFee;
    private final BigDecimal vatRate;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            ProductCatalogRepository productRepository,

            @Value(
                    "${store.checkout.delivery-fee:0.00}"
            )
            BigDecimal deliveryFee,

            @Value(
                    "${store.checkout.vat-rate:0.00}"
            )
            BigDecimal vatRate
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.deliveryFee = deliveryFee;
        this.vatRate = vatRate;
    }

    @Override
    @Transactional
    public Order checkout(
            String customerEmail,
            CheckoutRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Checkout details are required"
            );
        }

        String normalizedEmail =
                normalizeEmail(customerEmail);

        Cart cart =
                cartRepository
                        .findCartForCustomerByStatusForUpdate(
                                normalizedEmail,
                                CartStatus.ACTIVE
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Active cart was not found"
                                )
                        );

        if (cart.isEmpty()) {
            throw new IllegalStateException(
                    "Your cart is empty"
            );
        }

        DeliveryAddress deliveryAddress =
                DeliveryAddressFactory
                        .createDeliveryAddress(
                                request.getRecipientName(),
                                request.getRecipientPhone(),
                                request.getStreetAddress(),
                                request.getSuburb(),
                                request.getCity(),
                                request.getProvince(),
                                request.getPostalCode(),
                                request.getCountry()
                        );

        if (deliveryAddress == null) {
            throw new IllegalArgumentException(
                    "Delivery address is invalid"
            );
        }

        List<OrderItem> orderItems =
                createOrderItems(cart);

        FinancialBreakdown breakdown =
                FinancialBreakdownFactory
                        .createFinancialBreakdown(
                                orderItems,
                                deliveryFee,
                                BigDecimal.ZERO,
                                vatRate
                        );

        if (breakdown == null) {
            throw new IllegalStateException(
                    "Order totals could not be calculated"
            );
        }

        /*
         * Checkout is retry-safe: one pending order per cart. If a pending
         * order already exists for this cart we refresh it to match the
         * current cart contents and delivery details (so the amount and items
         * are never stale); if it was already paid we block a re-checkout.
         */
        Order existingOrder =
                orderRepository
                        .findBySourceCartId(
                                cart.getCartId()
                        )
                        .orElse(null);

        if (existingOrder != null) {
            if (existingOrder.getOrderStatus()
                    != OrderStatus.PENDING_PAYMENT) {

                throw new IllegalStateException(
                        "This cart has already been checked out"
                );
            }

            existingOrder.reviseCheckout(
                    orderItems,
                    breakdown,
                    deliveryAddress,
                    request.getDeliveryInstructions()
            );

            return orderRepository.save(
                    existingOrder
            );
        }

        Customer customer =
                cart.getCustomer();

        Order order =
                OrderFactory.createOrder(
                        customer,
                        cart,
                        orderItems,
                        breakdown,
                        deliveryAddress,
                        request.getDeliveryInstructions()
                );

        if (order == null) {
            throw new IllegalStateException(
                    "Order could not be created"
            );
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order getCustomerOrder(
            String customerEmail,
            String orderId
    ) {
        validateIdentifier(
                orderId,
                "Order ID"
        );

        return orderRepository
                .findForCustomer(
                        orderId,
                        normalizeEmail(
                                customerEmail
                        )
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Order was not found"
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(
            String customerEmail
    ) {
        return orderRepository
                .findAllForCustomer(
                        normalizeEmail(
                                customerEmail
                        )
                );
    }

    @Override
    @Transactional
    public Order cancelCustomerOrder(
            String customerEmail,
            String orderId
    ) {
        validateIdentifier(
                orderId,
                "Order ID"
        );

        Order order =
                orderRepository
                        .findForCustomerForUpdate(
                                orderId,
                                normalizeEmail(
                                        customerEmail
                                )
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Order was not found"
                                )
                        );

        if (order.getOrderStatus()
                != OrderStatus.PENDING_PAYMENT) {

            throw new IllegalStateException(
                    "Only an unpaid order may be cancelled by the customer"
            );
        }

        order.cancel();

        Cart sourceCart =
                order.getSourceCart();

        if (sourceCart != null
                && sourceCart.getCartStatus()
                == CartStatus.ACTIVE) {

            sourceCart.markAbandoned();
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository
                .findAllWithDetails();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(
            OrderStatus orderStatus
    ) {
        if (orderStatus == null) {
            throw new IllegalArgumentException(
                    "Order status is required"
            );
        }

        return orderRepository
                .findAllByStatusWithDetails(
                        orderStatus
                );
    }

    @Override
    @Transactional
    public Order updateOrderStatus(
            String orderId,
            OrderStatus newStatus
    ) {
        validateIdentifier(
                orderId,
                "Order ID"
        );

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "New order status is required"
            );
        }

        Order order =
                orderRepository
                        .findByIdForUpdate(orderId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Order was not found"
                                )
                        );

        applyStatusTransition(
                order,
                newStatus
        );

        return orderRepository.save(order);
    }

    private List<OrderItem> createOrderItems(
            Cart cart
    ) {
        List<OrderItem> orderItems =
                new ArrayList<>();

        for (CartItem cartItem
                : cart.getItems()) {

            ProductCatalog currentProduct =
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

            validateProductForCheckout(
                    currentProduct,
                    cartItem.getQuantity()
            );

            OrderItem orderItem =
                    OrderItemFactory
                            .createOrderItem(
                                    currentProduct,
                                    cartItem.getQuantity()
                            );

            if (orderItem == null) {
                throw new IllegalStateException(
                        "Order item could not be created for "
                                + currentProduct
                                .getProductName()
                );
            }

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private void validateProductForCheckout(
            ProductCatalog product,
            int quantity
    ) {
        if (!product.isActive()
                || !product.isAvailable()) {

            throw new IllegalStateException(
                    product.getProductName()
                            + " is no longer available"
            );
        }

        if (quantity <= 0) {
            throw new IllegalStateException(
                    "Invalid cart quantity for "
                            + product.getProductName()
            );
        }

        if (quantity
                > product.getStockQuantity()) {

            throw new IllegalStateException(
                    "Only "
                            + product.getStockQuantity()
                            + " unit(s) of "
                            + product.getProductName()
                            + " are available"
            );
        }
    }

    private void applyStatusTransition(
            Order order,
            OrderStatus newStatus
    ) {
        if (order.getOrderStatus() == newStatus) {
            return;
        }

        switch (newStatus) {

            case PROCESSING ->
                    order.startProcessing();

            case SHIPPED ->
                    order.markShipped();

            case DELIVERED ->
                    order.markDelivered();

            case CANCELLED -> {
                if (order.getOrderStatus()
                        != OrderStatus.PENDING_PAYMENT) {

                    throw new IllegalStateException(
                            "Paid orders must use the refund workflow instead of direct cancellation"
                    );
                }

                order.cancel();

                Cart sourceCart =
                        order.getSourceCart();

                if (sourceCart != null
                        && sourceCart.getCartStatus()
                        == CartStatus.ACTIVE) {

                    sourceCart.markAbandoned();
                }
            }

            case PAID ->
                    throw new IllegalStateException(
                            "Orders become paid only through a successful payment"
                    );

            case REFUNDED ->
                    throw new IllegalStateException(
                            "Orders become refunded only through the payment refund endpoint"
                    );

            case PENDING_PAYMENT ->
                    throw new IllegalStateException(
                            "An order cannot be returned to pending payment"
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