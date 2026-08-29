/*
   PaymentServiceImpl
   Ngwana Tiyani (231266731)
   Date: 07 July 2026
 */
package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.PaymentFactory;
import cput.ac.za.ecommerce.repository.OrderRepository;
import cput.ac.za.ecommerce.repository.PaymentRepository;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.request.CardPaymentRequest;
import cput.ac.za.ecommerce.request.DigitalWalletPaymentRequest;
import cput.ac.za.ecommerce.service.IPaymentGatewayService;
import cput.ac.za.ecommerce.service.IPaymentService;
import cput.ac.za.ecommerce.service.PaymentGatewayResult;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private final ProductCatalogRepository
            productRepository;

    private final IPaymentGatewayService
            paymentGatewayService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            ProductCatalogRepository
                    productRepository,
            IPaymentGatewayService
                    paymentGatewayService
    ) {
        this.paymentRepository =
                paymentRepository;

        this.orderRepository =
                orderRepository;

        this.productRepository =
                productRepository;

        this.paymentGatewayService =
                paymentGatewayService;
    }

    @Override
    @Transactional
    public Payment processCardPayment(
            String customerEmail,
            CardPaymentRequest request
    ) {
        validateCustomerEmail(customerEmail);

        Order order = loadPaymentOrder(
                request.getOrderId(),
                customerEmail
        );

        ensureOrderCanBePaid(order);

        List<StockAllocation> stockAllocations =
                lockAndValidateStock(order);

        BigDecimal amount =
                getOrderPaymentAmount(order);

        PaymentGatewayResult gatewayResult =
                paymentGatewayService.chargeCard(
                        request.getPaymentToken(),
                        amount
                );

        BillingLocation billingLocation =
                PaymentFactory
                        .createBillingLocation(
                                request
                                        .getBillingLocation()
                        );

        if (billingLocation == null) {
            throw new IllegalArgumentException(
                    "Billing location is invalid"
            );
        }

        CardPayment cardPayment =
                PaymentFactory.createCardPayment(
                        order,
                        amount,
                        billingLocation,
                        requireProviderReference(
                                gatewayResult
                        ),
                        request.getCardBrand(),
                        request.getCardholderName(),
                        request
                                .getCardLastFourDigits()
                );

        if (cardPayment == null) {
            throw new IllegalArgumentException(
                    "Card payment details are invalid"
            );
        }

        return completePaymentAttempt(
                order,
                cardPayment,
                gatewayResult,
                stockAllocations
        );
    }

    @Override
    @Transactional
    public Payment processDigitalWalletPayment(
            String customerEmail,
            DigitalWalletPaymentRequest request
    ) {
        validateCustomerEmail(customerEmail);

        Order order = loadPaymentOrder(
                request.getOrderId(),
                customerEmail
        );

        ensureOrderCanBePaid(order);

        List<StockAllocation> stockAllocations =
                lockAndValidateStock(order);

        BigDecimal amount =
                getOrderPaymentAmount(order);

        PaymentGatewayResult gatewayResult =
                paymentGatewayService.chargeWallet(
                        request.getWalletProvider(),
                        request.getWalletToken(),
                        amount
                );

        BillingLocation billingLocation =
                PaymentFactory
                        .createBillingLocation(
                                request
                                        .getBillingLocation()
                        );

        if (billingLocation == null) {
            throw new IllegalArgumentException(
                    "Billing location is invalid"
            );
        }

        DigitalWalletPayment walletPayment =
                PaymentFactory
                        .createDigitalWalletPayment(
                                order,
                                amount,
                                billingLocation,
                                request.getWalletProvider(),
                                requireProviderReference(
                                        gatewayResult
                                )
                        );

        if (walletPayment == null) {
            throw new IllegalArgumentException(
                    "Digital-wallet payment details are invalid"
            );
        }

        return completePaymentAttempt(
                order,
                walletPayment,
                gatewayResult,
                stockAllocations
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentForCustomer(
            String transactionId,
            String customerEmail
    ) {
        validateIdentifier(
                transactionId,
                "Transaction ID"
        );

        validateCustomerEmail(customerEmail);

        return paymentRepository
                .findForCustomer(
                        transactionId,
                        customerEmail
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Payment was not found"
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsForOrder(
            String orderId,
            String customerEmail
    ) {
        validateIdentifier(
                orderId,
                "Order ID"
        );

        validateCustomerEmail(customerEmail);

        return paymentRepository
                .findAllForOrderAndCustomer(
                        orderId,
                        customerEmail
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getCustomerPayments(
            String customerEmail
    ) {
        validateCustomerEmail(customerEmail);

        return paymentRepository
                .findAllForCustomer(
                        customerEmail
                );
    }

    @Override
    public Payment savePayment(Payment payment) {
        return null;
    }

    @Override
    public Payment getPaymentById(String paymentId) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment updatePayment(Payment payment) {
        return null;
    }

    @Override
    public void deletePayment(String paymentId) {

    }

    @Override
    @Transactional
    public Payment refundPayment(
            String transactionId
    ) {
        validateIdentifier(
                transactionId,
                "Transaction ID"
        );

        Payment payment =
                paymentRepository
                        .findByIdForUpdate(
                                transactionId
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Payment was not found"
                                )
                        );

        if (payment.getPaymentStatus()
                != PaymentStatus.SUCCESSFUL) {

            throw new IllegalStateException(
                    "Only a successful payment may be refunded"
            );
        }

        Order order =
                orderRepository
                        .findByIdForUpdate(
                                payment.getOrderId()
                        )
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Payment order was not found"
                                )
                        );

        if (order.getOrderStatus()
                == OrderStatus.SHIPPED
                || order.getOrderStatus()
                == OrderStatus.DELIVERED) {

            throw new IllegalStateException(
                    "A shipped or delivered order cannot be refunded through this endpoint"
            );
        }

        List<ProductCatalog> productsToSave =
                new ArrayList<>();

        for (OrderItem orderItem
                : order.getOrderItems()) {

            ProductCatalog product =
                    productRepository
                            .findByIdForUpdate(
                                    orderItem
                                            .getProductIdSnapshot()
                            )
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Purchased product was not found"
                                    )
                            );

            product.increaseStock(
                    orderItem.getQuantityPurchased()
            );

            productsToSave.add(product);
        }

        payment.markRefunded();
        order.markRefunded();

        productRepository.saveAll(
                productsToSave
        );

        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    private Payment completePaymentAttempt(
            Order order,
            Payment payment,
            PaymentGatewayResult gatewayResult,
            List<StockAllocation> allocations
    ) {
        if (!gatewayResult.successful()) {
            payment.markFailed(
                    gatewayResult.failureReason()
            );

            return paymentRepository.save(payment);
        }

        for (StockAllocation allocation
                : allocations) {

            allocation.product()
                    .decreaseStock(
                            allocation.quantity()
                    );
        }

        productRepository.saveAll(
                allocations.stream()
                        .map(StockAllocation::product)
                        .toList()
        );

        payment.markSuccessful();
        order.markPaid();

        Cart sourceCart =
                order.getSourceCart();

        if (sourceCart != null
                && sourceCart.getCartStatus()
                == CartStatus.ACTIVE) {

            sourceCart.markCheckedOut();
        }

        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    private Order loadPaymentOrder(
            String orderId,
            String customerEmail
    ) {
        validateIdentifier(
                orderId,
                "Order ID"
        );

        Order order =
                orderRepository
                        .findByIdForUpdate(orderId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Order was not found"
                                )
                        );

        String ownerEmail =
                order.getCustomer()
                        .getAccountProfile()
                        .getEmail();

        if (ownerEmail == null
                || !ownerEmail.equalsIgnoreCase(
                customerEmail
        )) {

            throw new AccessDeniedException(
                    "You cannot pay for this order"
            );
        }

        return order;
    }

    private void ensureOrderCanBePaid(
            Order order
    ) {
        if (order.getOrderStatus()
                != OrderStatus.PENDING_PAYMENT) {

            throw new IllegalStateException(
                    "Only an order awaiting payment may be paid"
            );
        }

        boolean alreadyPaid =
                paymentRepository
                        .existsByOrder_OrderIdAndPaymentStatus(
                                order.getOrderId(),
                                PaymentStatus.SUCCESSFUL
                        );

        if (alreadyPaid) {
            throw new IllegalStateException(
                    "This order has already been paid"
            );
        }

        if (order.getOrderItems() == null
                || order.getOrderItems()
                .isEmpty()) {

            throw new IllegalStateException(
                    "An empty order cannot be paid"
            );
        }
    }

    private List<StockAllocation>
    lockAndValidateStock(
            Order order
    ) {
        List<StockAllocation> allocations =
                new ArrayList<>();

        for (OrderItem orderItem
                : order.getOrderItems()) {

            ProductCatalog product =
                    productRepository
                            .findByIdForUpdate(
                                    orderItem
                                            .getProductIdSnapshot()
                            )
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "Product "
                                                    + orderItem
                                                    .getProductNameSnapshot()
                                                    + " was not found"
                                    )
                            );

            int requestedQuantity =
                    orderItem
                            .getQuantityPurchased();

            if (!product.isAvailable()) {
                throw new IllegalStateException(
                        product.getProductName()
                                + " is no longer available"
                );
            }

            if (requestedQuantity
                    > product.getStockQuantity()) {

                throw new IllegalStateException(
                        "Insufficient stock for "
                                + product
                                .getProductName()
                );
            }

            allocations.add(
                    new StockAllocation(
                            product,
                            requestedQuantity
                    )
            );
        }

        return allocations;
    }

    private BigDecimal getOrderPaymentAmount(
            Order order
    ) {
        if (order.getFinancialBreakdown()
                == null) {

            throw new IllegalStateException(
                    "Order financial information is missing"
            );
        }

        BigDecimal amount =
                order.getFinancialBreakdown()
                        .getFinalInvoiceTotal();

        if (amount == null
                || amount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalStateException(
                    "Order payment amount is invalid"
            );
        }

        return amount;
    }

    private String requireProviderReference(
            PaymentGatewayResult result
    ) {
        if (result == null
                || result.providerReference() == null
                || result.providerReference()
                .isBlank()) {

            throw new IllegalStateException(
                    "Payment provider did not return a transaction reference"
            );
        }

        return result.providerReference();
    }

    private void validateCustomerEmail(
            String customerEmail
    ) {
        if (customerEmail == null
                || customerEmail.isBlank()) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }
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

    private record StockAllocation(
            ProductCatalog product,
            int quantity
    ) {
    }
}//End of program