// PaymentServiceImplTest.java
// Ngwana Tiyani 231266731
// 07 July 2026
package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.factory.OrderItemFactory;
import cput.ac.za.ecommerce.factory.PaymentFactory;
import cput.ac.za.ecommerce.repository.OrderRepository;
import cput.ac.za.ecommerce.repository.PaymentRepository;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.request.BillingLocationRequest;
import cput.ac.za.ecommerce.request.CardPaymentRequest;
import cput.ac.za.ecommerce.service.IPaymentGatewayService;
import cput.ac.za.ecommerce.service.PaymentGatewayResult;
import cput.ac.za.ecommerce.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductCatalogRepository productRepository;

    @Mock
    private IPaymentGatewayService paymentGatewayService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private ProductCatalog product;
    private Customer customer;
    private Cart cart;
    private Order order;
    private CardPaymentRequest cardPaymentRequest;

    @BeforeEach
    void setUp() {
        AccountProfile accountProfile =
                AccountProfile.builder()
                        .setFirstName("Tiyani")
                        .setLastName("Ngwana")
                        .setEmail(
                                "tiyani.ngwana@gmail.com"
                        )
                        .setPhoneNumber("0821234567")
                        .build();

        customer = Customer.builder()
                .setUserId("USR-001")
                .setCustomerNumber("CUST-001")
                .setAccountProfile(accountProfile)
                .setPasswordHash(
                        "$2a$12$examplePasswordHash"
                )
                .setActive(true)
                .build();

        product = new ProductCatalog.Builder()
                .setProductId("PRD-001")
                .setSku("COR-RAM-048")
                .setProductName(
                        "Corsair Vengeance 48GB DDR5 RAM"
                )
                .setDescription(
                        "48GB high-performance DDR5 memory"
                )
                .setBrand(Brand.CORSAIR)
                .setCategory(
                        ProductCategory
                                .COMPUTER_COMPONENTS
                )
                .setPrice(
                        new BigDecimal("1000.00")
                )
                .setStockQuantity(5)
                .setPrimaryImageUrl(
                        "/images/products/corsair-48gb.png"
                )
                .setMemory("48GB DDR5")
                .setActive(true)
                .build();

        CartItem cartItem =
                new CartItem.Builder()
                        .setCartItemId("CI-001")
                        .setProduct(product)
                        .setQuantity(1)
                        .build();

        cart = new Cart.Builder()
                .setCartId("CART-001")
                .setCustomer(customer)
                .setCartStatus(CartStatus.ACTIVE)
                .setItems(List.of(cartItem))
                .build();

        OrderItem orderItem =
                OrderItemFactory.createOrderItem(
                        product,
                        1
                );

        FinancialBreakdown breakdown =
                new FinancialBreakdown.Builder()
                        .setBasketSubtotal(
                                new BigDecimal("1000.00")
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
                                new BigDecimal("1000.00")
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

        order = new Order.Builder()
                .setOrderId("ORD-001")
                .setCustomer(customer)
                .setSourceCart(cart)
                .setOrderStatus(
                        OrderStatus.PENDING_PAYMENT
                )
                .setOrderItems(
                        List.of(orderItem)
                )
                .setFinancialBreakdown(
                        breakdown
                )
                .setDeliveryAddress(
                        deliveryAddress
                )
                .build();

        BillingLocationRequest billingRequest =
                new BillingLocationRequest();

        billingRequest.setBillingName(
                "Tiyani Ngwana"
        );

        billingRequest.setStreetAddress(
                "22 Keizersgracht Street"
        );

        billingRequest.setSuburb(
                "Zonnebloem"
        );

        billingRequest.setCity(
                "Cape Town"
        );

        billingRequest.setProvince(
                "Western Cape"
        );

        billingRequest.setPostalCode(
                "8001"
        );

        billingRequest.setCountry(
                "South Africa"
        );

        cardPaymentRequest =
                new CardPaymentRequest();

        cardPaymentRequest.setOrderId(
                "ORD-001"
        );

        cardPaymentRequest.setPaymentToken(
                "TEST-CARD-TOKEN-123456"
        );

        cardPaymentRequest.setCardBrand(
                CardBrand.VISA
        );

        cardPaymentRequest.setCardholderName(
                "Tiyani Ngwana"
        );

        cardPaymentRequest.setCardLastFourDigits(
                "4242"
        );

        cardPaymentRequest.setBillingLocation(
                billingRequest
        );
    }

    @Test
    void shouldProcessSuccessfulCardPayment() {
        when(
                orderRepository.findByIdForUpdate(
                        "ORD-001"
                )
        ).thenReturn(Optional.of(order));

        when(
                paymentRepository
                        .existsByOrder_OrderIdAndPaymentStatus(
                                "ORD-001",
                                PaymentStatus.SUCCESSFUL
                        )
        ).thenReturn(false);

        when(
                productRepository.findByIdForUpdate(
                        "PRD-001"
                )
        ).thenReturn(Optional.of(product));

        when(
                paymentGatewayService.chargeCard(
                        eq("TEST-CARD-TOKEN-123456"),
                        eq(new BigDecimal("1000.00"))
                )
        ).thenReturn(
                new PaymentGatewayResult(
                        true,
                        "PAY-GATE-9921X",
                        null
                )
        );

        when(
                paymentRepository.save(
                        any(Payment.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Payment result =
                paymentService.processCardPayment(
                        "tiyani.ngwana@gmail.com",
                        cardPaymentRequest
                );

        assertNotNull(result);
        assertInstanceOf(
                CardPayment.class,
                result
        );

        CardPayment cardPayment =
                (CardPayment) result;

        assertEquals(
                PaymentStatus.SUCCESSFUL,
                cardPayment.getPaymentStatus()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                cardPayment.getCapturedAmount()
        );

        assertEquals(
                "PAY-GATE-9921X",
                cardPayment
                        .getPaymentGatewayReference()
        );

        assertEquals(
                OrderStatus.PAID,
                order.getOrderStatus()
        );

        assertEquals(
                CartStatus.CHECKED_OUT,
                cart.getCartStatus()
        );

        assertEquals(
                4,
                product.getStockQuantity()
        );

        verify(productRepository)
                .saveAll(anyList());

        verify(orderRepository)
                .save(order);

        verify(paymentRepository)
                .save(any(CardPayment.class));
    }

    @Test
    void shouldSaveFailedCardPaymentWithoutReducingStock() {
        when(
                orderRepository.findByIdForUpdate(
                        "ORD-001"
                )
        ).thenReturn(Optional.of(order));

        when(
                paymentRepository
                        .existsByOrder_OrderIdAndPaymentStatus(
                                "ORD-001",
                                PaymentStatus.SUCCESSFUL
                        )
        ).thenReturn(false);

        when(
                productRepository.findByIdForUpdate(
                        "PRD-001"
                )
        ).thenReturn(Optional.of(product));

        when(
                paymentGatewayService.chargeCard(
                        anyString(),
                        any(BigDecimal.class)
                )
        ).thenReturn(
                new PaymentGatewayResult(
                        false,
                        "PAY-GATE-DECLINED-001",
                        "Payment was declined"
                )
        );

        when(
                paymentRepository.save(
                        any(Payment.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        Payment result =
                paymentService.processCardPayment(
                        "tiyani.ngwana@gmail.com",
                        cardPaymentRequest
                );

        assertNotNull(result);

        assertEquals(
                PaymentStatus.FAILED,
                result.getPaymentStatus()
        );

        assertEquals(
                "Payment was declined",
                result.getFailureReason()
        );

        assertEquals(
                OrderStatus.PENDING_PAYMENT,
                order.getOrderStatus()
        );

        assertEquals(
                CartStatus.ACTIVE,
                cart.getCartStatus()
        );

        assertEquals(
                5,
                product.getStockQuantity()
        );

        verify(
                productRepository,
                never()
        ).saveAll(anyList());

        verify(
                orderRepository,
                never()
        ).save(any(Order.class));
    }

    @Test
    void shouldGetCustomerPayment() {
        BillingLocation billingLocation =
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

        when(
                paymentRepository.findForCustomer(
                        payment.getTransactionId(),
                        "tiyani.ngwana@gmail.com"
                )
        ).thenReturn(Optional.of(payment));

        Payment result =
                paymentService
                        .getPaymentForCustomer(
                                payment.getTransactionId(),
                                "tiyani.ngwana@gmail.com"
                        );

        assertNotNull(result);

        assertEquals(
                payment.getTransactionId(),
                result.getTransactionId()
        );
    }

    @Test
    void shouldReturnAllPayments() {
        when(
                paymentRepository.findAll()
        ).thenReturn(List.of());

        List<Payment> results =
                paymentService.getAllPayments();

        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(paymentRepository)
                .findAll();
    }
}