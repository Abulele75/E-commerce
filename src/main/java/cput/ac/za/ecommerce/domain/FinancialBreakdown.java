package cput.ac.za.ecommerce.domain;

/*
 * FinancialBreakdown.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

import jakarta.persistence.Embeddable;

@Embeddable
public class FinancialBreakdown {

    private double basketSubTotal;
    private double calculatedVatAmount;
    private double finalInvoiceTotal;

    protected FinancialBreakdown() {
    }

    private FinancialBreakdown(Builder builder) {
        this.basketSubTotal = builder.basketSubTotal;
        this.calculatedVatAmount = builder.calculatedVatAmount;
        this.finalInvoiceTotal = builder.finalInvoiceTotal;
    }

    public double getBasketSubTotal() {
        return basketSubTotal;
    }

    public double getCalculatedVatAmount() {
        return calculatedVatAmount;
    }

    public double getFinalInvoiceTotal() {
        return finalInvoiceTotal;
    }

    @Override
    public String toString() {
        return "FinancialBreakdown{" +
                "basketSubTotal=" + basketSubTotal +
                ", calculatedVatAmount=" + calculatedVatAmount +
                ", finalInvoiceTotal=" + finalInvoiceTotal +
                '}';
    }

    public static class Builder {

        private double basketSubTotal;
        private double calculatedVatAmount;
        private double finalInvoiceTotal;

        public Builder setBasketSubTotal(double basketSubTotal) {
            this.basketSubTotal = basketSubTotal;
            return this;
        }

        public Builder setCalculatedVatAmount(double calculatedVatAmount) {
            this.calculatedVatAmount = calculatedVatAmount;
            return this;
        }

        public Builder setFinalInvoiceTotal(double finalInvoiceTotal) {
            this.finalInvoiceTotal = finalInvoiceTotal;
            return this;
        }

        public Builder copy(FinancialBreakdown financialBreakdown) {
            this.basketSubTotal = financialBreakdown.basketSubTotal;
            this.calculatedVatAmount = financialBreakdown.calculatedVatAmount;
            this.finalInvoiceTotal = financialBreakdown.finalInvoiceTotal;
            return this;
        }

        public FinancialBreakdown build() {
            return new FinancialBreakdown(this);
        }
    }
}