/*
   Payment.java
   Ngwana Tiyani (231266731)
   Date: 19 June 2026
 */
package cput.ac.za.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Payment {

    @Id
    @Column(
            name = "transaction_id",
            nullable = false,
            updatable = false,
            length = 50
    )
    private String transactionId;

    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @Column(
            name = "captured_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal capturedAmount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_status",
            nullable = false,
            length = 30
    )
    private PaymentStatus paymentStatus;

    @Embedded
    private BillingLocation billingLocation;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(
            name = "failure_reason",
            length = 300
    )
    private String failureReason;

    protected Payment() {
    }

    protected Payment(
            PaymentBuilder<?> builder
    ) {
        this.transactionId =
                builder.transactionId;

        this.order = builder.order;

        this.capturedAmount =
                builder.capturedAmount;

        this.paymentMethod =
                builder.paymentMethod;

        this.paymentStatus =
                builder.paymentStatus;

        this.billingLocation =
                builder.billingLocation;

        this.createdAt =
                builder.createdAt;

        this.completedAt =
                builder.completedAt;

        this.failureReason =
                builder.failureReason;
    }

    public String getTransactionId() {
        return transactionId;
    }

    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    @Transient
    public String getOrderId() {
        return order == null
                ? null
                : order.getOrderId();
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

    public BillingLocation getBillingLocation() {
        return billingLocation;
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

    public void markSuccessful() {
        ensurePending();

        this.paymentStatus =
                PaymentStatus.SUCCESSFUL;

        this.completedAt =
                LocalDateTime.now();

        this.failureReason = null;
    }

    public void markFailed(
            String failureReason
    ) {
        ensurePending();

        this.paymentStatus =
                PaymentStatus.FAILED;

        this.completedAt =
                LocalDateTime.now();

        this.failureReason =
                normalizeReason(failureReason);
    }

    public void cancel() {
        ensurePending();

        this.paymentStatus =
                PaymentStatus.CANCELLED;

        this.completedAt =
                LocalDateTime.now();
    }

    public void markRefunded() {
        if (paymentStatus
                != PaymentStatus.SUCCESSFUL) {
            throw new IllegalStateException(
                    "Only a successful payment may be refunded"
            );
        }

        this.paymentStatus =
                PaymentStatus.REFUNDED;

        this.completedAt =
                LocalDateTime.now();
    }

    private void ensurePending() {
        if (paymentStatus
                != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only a pending payment may be processed"
            );
        }
    }

    private String normalizeReason(
            String reason
    ) {
        if (reason == null
                || reason.isBlank()) {
            return "Payment processing failed";
        }

        String trimmed = reason.trim();

        return trimmed.length() > 300
                ? trimmed.substring(0, 300)
                : trimmed;
    }

    public abstract static class PaymentBuilder<
            T extends PaymentBuilder<T>> {

        protected String transactionId;
        protected Order order;
        protected BigDecimal capturedAmount;
        protected PaymentMethod paymentMethod;

        protected PaymentStatus paymentStatus =
                PaymentStatus.PENDING;

        protected BillingLocation billingLocation;

        protected LocalDateTime createdAt =
                LocalDateTime.now();

        protected LocalDateTime completedAt;
        protected String failureReason;

        protected abstract T self();

        public T setTransactionId(
                String transactionId
        ) {
            this.transactionId = transactionId;
            return self();
        }

        public T setOrder(
                Order order
        ) {
            this.order = order;
            return self();
        }

        public T setCapturedAmount(
                BigDecimal capturedAmount
        ) {
            this.capturedAmount =
                    capturedAmount;

            return self();
        }

        protected T setPaymentMethod(
                PaymentMethod paymentMethod
        ) {
            this.paymentMethod =
                    paymentMethod;

            return self();
        }

        public T setBillingLocation(
                BillingLocation billingLocation
        ) {
            this.billingLocation =
                    billingLocation;

            return self();
        }
    }
}