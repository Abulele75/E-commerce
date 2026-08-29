/*
 * FinancialBreakdown.java
 * Author: Sinethemba Nyimbinya (220085870)
 * 21 June 2026
 */

package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class FinancialBreakdown {

    @Column(
            name = "basket_subtotal",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal basketSubtotal;

    @Column(
            name = "discount_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discountAmount;

    @Column(
            name = "vat_amount",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal vatAmount;

    @Column(
            name = "delivery_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal deliveryFee;

    @Column(
            name = "final_invoice_total",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal finalInvoiceTotal;

    protected FinancialBreakdown() {
    }

    private FinancialBreakdown(Builder builder) {
        this.basketSubtotal =
                builder.basketSubtotal;

        this.discountAmount =
                builder.discountAmount;

        this.vatAmount = builder.vatAmount;
        this.deliveryFee = builder.deliveryFee;

        this.finalInvoiceTotal =
                builder.finalInvoiceTotal;
    }

    public BigDecimal getBasketSubtotal() {
        return basketSubtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public BigDecimal getFinalInvoiceTotal() {
        return finalInvoiceTotal;
    }

    public static class Builder {

        private BigDecimal basketSubtotal;
        private BigDecimal discountAmount;
        private BigDecimal vatAmount;
        private BigDecimal deliveryFee;
        private BigDecimal finalInvoiceTotal;

        public Builder setBasketSubtotal(
                BigDecimal basketSubtotal
        ) {
            this.basketSubtotal =
                    basketSubtotal;

            return this;
        }

        public Builder setDiscountAmount(
                BigDecimal discountAmount
        ) {
            this.discountAmount =
                    discountAmount;

            return this;
        }

        public Builder setVatAmount(
                BigDecimal vatAmount
        ) {
            this.vatAmount = vatAmount;
            return this;
        }

        public Builder setDeliveryFee(
                BigDecimal deliveryFee
        ) {
            this.deliveryFee = deliveryFee;
            return this;
        }

        public Builder setFinalInvoiceTotal(
                BigDecimal finalInvoiceTotal
        ) {
            this.finalInvoiceTotal =
                    finalInvoiceTotal;

            return this;
        }

        public FinancialBreakdown build() {
            return new FinancialBreakdown(this);
        }
    }
}