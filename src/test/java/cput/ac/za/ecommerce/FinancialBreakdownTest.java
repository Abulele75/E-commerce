package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.FinancialBreakdown;
import cput.ac.za.ecommerce.factory.FinancialBreakdownFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialBreakdownTest {

    @Test
    void createFinancialBreakdown() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Check the breakdown
        assertNotNull(financialBreakdown);
        assertEquals(200.00, financialBreakdown.getBasketSubTotal());
        assertEquals(30.00, financialBreakdown.getCalculatedVatAmount());
        assertEquals(230.00, financialBreakdown.getFinalInvoiceTotal());
    }

    @Test
    void copyFinancialBreakdown() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Copy the breakdown
        FinancialBreakdown copy = new FinancialBreakdown.Builder()
                .copy(financialBreakdown)
                .build();

        // Check the copy
        assertEquals(
                financialBreakdown.getBasketSubTotal(),
                copy.getBasketSubTotal()
        );

        assertEquals(
                financialBreakdown.getCalculatedVatAmount(),
                copy.getCalculatedVatAmount()
        );

        assertEquals(
                financialBreakdown.getFinalInvoiceTotal(),
                copy.getFinalInvoiceTotal()
        );
    }

    @Test
    void financialBreakdownToString() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        230.00
                );

        // Check toString
        assertNotNull(financialBreakdown.toString());
    }
}