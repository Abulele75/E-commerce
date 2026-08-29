//FinancialBreakdownResponse.java
//Sinethemba Nyimbinya (220085870)
//Date: 17 August 2026

package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.FinancialBreakdown;

import java.math.BigDecimal;

public record FinancialBreakdownResponse(
        BigDecimal basketSubtotal,
        BigDecimal discountAmount,
        BigDecimal vatAmount,
        BigDecimal deliveryFee,
        BigDecimal finalInvoiceTotal
) {

    public static FinancialBreakdownResponse from(
            FinancialBreakdown breakdown
    ) {
        if (breakdown == null) {
            return null;
        }

        return new FinancialBreakdownResponse(
                breakdown.getBasketSubtotal(),
                breakdown.getDiscountAmount(),
                breakdown.getVatAmount(),
                breakdown.getDeliveryFee(),
                breakdown.getFinalInvoiceTotal()
        );
    }
}