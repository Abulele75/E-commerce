//PaymentResponse.java
//Tiyan Ngwana 231266731
//Date: 17 August 2026
package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private final String transactionId;
    private final String orderId;
    private final BigDecimal capturedAmount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus paymentStatus;
    private final String paymentDescription;
    private final String providerReference;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;
    private final String failureReason;

    private PaymentResponse(
            String transactionId,
            String orderId,
            BigDecimal capturedAmount,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            String paymentDescription,
            String providerReference,
            LocalDateTime createdAt,
            LocalDateTime completedAt,
            String failureReason
    ) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.capturedAmount = capturedAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDescription =
                paymentDescription;

        this.providerReference =
                providerReference;

        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.failureReason = failureReason;
    }

    public static PaymentResponse from(
            Payment payment
    ) {
        String description =
                payment.getPaymentMethod().name();

        String providerReference = null;

        if (payment
                instanceof CardPayment cardPayment) {

            description =
                    cardPayment.getCardBrand()
                            .name()
                            + " ending in "
                            + cardPayment
                            .getCardLastFourDigits();

            providerReference =
                    cardPayment
                            .getPaymentGatewayReference();
        }

        if (payment
                instanceof DigitalWalletPayment
                walletPayment) {

            description =
                    walletPayment
                            .getWalletProvider()
                            .name()
                            .replace("_", " ");

            providerReference =
                    walletPayment
                            .getProviderTransactionReference();
        }

        return new PaymentResponse(
                payment.getTransactionId(),
                payment.getOrderId(),
                payment.getCapturedAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                description,
                providerReference,
                payment.getCreatedAt(),
                payment.getCompletedAt(),
                payment.getFailureReason()
        );
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getCapturedAmount() {
        return capturedAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }
}