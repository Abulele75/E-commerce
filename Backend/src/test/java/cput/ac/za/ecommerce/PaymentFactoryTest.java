// PaymentFactoryTest.java
// Ngwana Tiyani 231266731
// 28 June 2026


package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.PaymentFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFactoryTest {

    private BillingLocation billingLocation;
    private Order order;

    @BeforeEach
    void setUp() {
        AccountProfile profile =
                AccountProfile.builder()
                        .setFirstName("Tiyani")
                        .setLastName("Ngwana")
                        .setEmail(
                                "tiyani.ngwana@gmail.com"
                        )
                        .setPhoneNumber(
                                "0821234567"
                        )
                        .build();

        Customer customer =
                Customer.builder()
                        .setUserId("USR-001")
                        .setCustomerNumber(
                                "CUST-001"
                        )
                        .setAccountProfile(profile)
                        .setPasswordHash(
                                "encoded-password"
                        )
                        .setActive(true)
                        .build();

        FinancialBreakdown breakdown =
                new FinancialBreakdown.Builder()
                        .setBasketSubtotal(
                                new BigDecimal(
                                        "1000.00"
                                )
                        )
                        .setDiscountAmount(
                                BigDecimal.ZERO
                        )
                        .setVatAmount(
                                BigDecimal.ZERO
                        )
                        .setDeliveryFee(
                                BigDecimal.ZERO
                        )
                        .setFinalInvoiceTotal(
                                new BigDecimal(
                                        "1000.00"
                                )
                        )
                        .build();

        DeliveryAddress deliveryAddress =
                new DeliveryAddress.Builder()
                        .setRecipientName(
                                "Tiyani Ngwana"
                        )
                        .setRecipientPhone(
                                "0821234567"
                        )
                        .setStreetAddress(
                                "22 Keizersgracht Street"
                        )
                        .setCity("Cape Town")
                        .setProvince(
                                "Western Cape"
                        )
                        .setPostalCode("8001")
                        .setCountry(
                                "South Africa"
                        )
                        .build();

        Cart cart =
                new Cart.Builder()
                        .setCartId("CART-001")
                        .setCustomer(customer)
                        .setCartStatus(
                                CartStatus.ACTIVE
                        )
                        .build();

        order = new Order.Builder()
                .setOrderId("ORD-001")
                .setCustomer(customer)
                .setSourceCart(cart)
                .setOrderStatus(
                        OrderStatus.PENDING_PAYMENT
                )
                .setOrderItems(List.of())
                .setFinancialBreakdown(
                        breakdown
                )
                .setDeliveryAddress(
                        deliveryAddress
                )
                .build();

        billingLocation =
                new BillingLocation.Builder()
                        .setBillingName(
                                "Tiyani Ngwana"
                        )
                        .setStreetAddress(
                                "22 Keizersgracht Street"
                        )
                        .setSuburb("Zonnebloem")
                        .setCity("Cape Town")
                        .setProvince(
                                "Western Cape"
                        )
                        .setPostalCode("8001")
                        .setCountry(
                                "South Africa"
                        )
                        .build();
    }

    @Test
    void shouldCreateCardPayment() {
        CardPayment payment =
                PaymentFactory.createCardPayment(
                        order,
                        new BigDecimal("1000.00"),
                        billingLocation,
                        "PAY-GATE-9921X",
                        CardBrand.VISA,
                        "Tiyani Ngwana",
                        "4242"
                );

        assertNotNull(payment);
        assertNotNull(
                payment.getTransactionId()
        );

        assertEquals(
                "ORD-001",
                payment.getOrderId()
        );

        assertEquals(
                "PAY-GATE-9921X",
                payment
                        .getPaymentGatewayReference()
        );

        assertEquals(
                CardBrand.VISA,
                payment.getCardBrand()
        );

        assertEquals(
                PaymentStatus.PENDING,
                payment.getPaymentStatus()
        );
    }

    @Test
    void shouldRejectInvalidAmount() {
        CardPayment payment =
                PaymentFactory.createCardPayment(
                        order,
                        BigDecimal.ZERO,
                        billingLocation,
                        "PAY-GATE-9921X",
                        CardBrand.VISA,
                        "Tiyani Ngwana",
                        "4242"
                );

        assertNull(payment);
    }

    @Test
    void shouldRejectInvalidLastFourDigits() {
        CardPayment payment =
                PaymentFactory.createCardPayment(
                        order,
                        new BigDecimal("1000.00"),
                        billingLocation,
                        "PAY-GATE-9921X",
                        CardBrand.VISA,
                        "Tiyani Ngwana",
                        "42"
                );

        assertNull(payment);
    }
}