package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.FinancialBreakdown;
import cput.ac.za.ecommerce.factory.FinancialBreakdownFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialBreakdownFactoryTest {

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
    void createFinancialBreakdownWithNegativeSubtotal() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        -200.00,
                        30.00,
                        -170.00
                );

        // Check if breakdown is null
        assertNull(financialBreakdown);
    }

    @Test
    void createFinancialBreakdownWithNegativeVat() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        -30.00,
                        170.00
                );

        // Check if breakdown is null
        assertNull(financialBreakdown);
    }

    @Test
    void createFinancialBreakdownWithWrongTotal() {

        // Create financial breakdown
        FinancialBreakdown financialBreakdown =
                FinancialBreakdownFactory.createFinancialBreakdown(
                        200.00,
                        30.00,
                        220.00
                );

        // Check if breakdown is null
        assertNull(financialBreakdown);
    }
}