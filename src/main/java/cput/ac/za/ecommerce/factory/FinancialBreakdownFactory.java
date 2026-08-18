package cput.ac.za.ecommerce.factory;

/*
 * FinancialBreakdownFactory.java
 * Author: Sinethemba Nyimbinya (220085870)
 * Date: 2026
 */

import cput.ac.za.ecommerce.domain.FinancialBreakdown;

public class FinancialBreakdownFactory {

    public static FinancialBreakdown createFinancialBreakdown(
            double basketSubTotal,
            double calculatedVatAmount,
            double finalInvoiceTotal) {

        // Check if the subtotal is valid.
        if (basketSubTotal < 0)
            return null;

        // Check if the VAT amount is valid.
        if (calculatedVatAmount < 0)
            return null;

        // Check if the final total is correct.
        if (finalInvoiceTotal != basketSubTotal + calculatedVatAmount)
            return null;

        return new FinancialBreakdown.Builder()
                .setBasketSubTotal(basketSubTotal)
                .setCalculatedVatAmount(calculatedVatAmount)
                .setFinalInvoiceTotal(finalInvoiceTotal)
                .build();
    }
}