package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Payment;
import cput.ac.za.ecommerce.request.CardPaymentRequest;
import cput.ac.za.ecommerce.request.DigitalWalletPaymentRequest;
import cput.ac.za.ecommerce.response.PaymentResponse;
import cput.ac.za.ecommerce.service.IPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final IPaymentService paymentService;

    public PaymentController(
            IPaymentService paymentService
    ) {
        this.paymentService = paymentService;
    }

    @PostMapping("/card")
    public ResponseEntity<PaymentResponse>
    processCardPayment(
            @Valid
            @RequestBody
            CardPaymentRequest request,

            Authentication authentication
    ) {
        Payment payment =
                paymentService.processCardPayment(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        PaymentResponse.from(
                                payment
                        )
                );
    }

    @PostMapping("/wallet")
    public ResponseEntity<PaymentResponse>
    processWalletPayment(
            @Valid
            @RequestBody
            DigitalWalletPaymentRequest request,

            Authentication authentication
    ) {
        Payment payment =
                paymentService
                        .processDigitalWalletPayment(
                                getAuthenticatedEmail(
                                        authentication
                                ),
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        PaymentResponse.from(
                                payment
                        )
                );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentResponse>
    getPayment(
            @PathVariable
            String transactionId,

            Authentication authentication
    ) {
        Payment payment =
                paymentService
                        .getPaymentForCustomer(
                                transactionId,
                                getAuthenticatedEmail(
                                        authentication
                                )
                        );

        return ResponseEntity.ok(
                PaymentResponse.from(payment)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>>
    getOrderPayments(
            @PathVariable
            String orderId,

            Authentication authentication
    ) {
        List<PaymentResponse> responses =
                paymentService
                        .getPaymentsForOrder(
                                orderId,
                                getAuthenticatedEmail(
                                        authentication
                                )
                        )
                        .stream()
                        .map(PaymentResponse::from)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-payments")
    public ResponseEntity<List<PaymentResponse>>
    getMyPayments(
            Authentication authentication
    ) {
        List<PaymentResponse> responses =
                paymentService
                        .getCustomerPayments(
                                getAuthenticatedEmail(
                                        authentication
                                )
                        )
                        .stream()
                        .map(PaymentResponse::from)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @GetMapping
    public ResponseEntity<List<PaymentResponse>>
    getAllPayments() {
        List<PaymentResponse> responses =
                paymentService
                        .getAllPayments()
                        .stream()
                        .map(PaymentResponse::from)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<PaymentResponse>
    refundPayment(
            @PathVariable
            String transactionId
    ) {
        Payment refundedPayment =
                paymentService.refundPayment(
                        transactionId
                );

        return ResponseEntity.ok(
                PaymentResponse.from(
                        refundedPayment
                )
        );
    }

    private String getAuthenticatedEmail(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication
                .isAuthenticated()
                || authentication.getName()
                == null
                || authentication.getName()
                .isBlank()) {

            throw new org.springframework
                    .security
                    .access
                    .AccessDeniedException(
                    "Authentication is required"
            );
        }

        return authentication
                .getName()
                .trim()
                .toLowerCase();
    }
}