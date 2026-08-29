package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.WalletProvider;
import cput.ac.za.ecommerce.service.IPaymentGatewayService;
import cput.ac.za.ecommerce.service.PaymentGatewayResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SimulatedPaymentGatewayService implements IPaymentGatewayService {

    private static final String DECLINED_TEST_TOKEN =
            "TEST-PAYMENT-DECLINED";

    @Override
    public PaymentGatewayResult chargeCard(
            String paymentToken,
            BigDecimal amount
    ) {
        return processPayment(
                "CARD",
                paymentToken,
                amount
        );
    }

    @Override
    public PaymentGatewayResult chargeWallet(
            WalletProvider walletProvider,
            String walletToken,
            BigDecimal amount
    ) {
        String providerPrefix =
                walletProvider == null
                        ? "WALLET"
                        : walletProvider.name();

        return processPayment(
                providerPrefix,
                walletToken,
                amount
        );
    }

    private PaymentGatewayResult processPayment(
            String referencePrefix,
            String paymentToken,
            BigDecimal amount
    ) {
        if (paymentToken == null
                || paymentToken.isBlank()) {

            return new PaymentGatewayResult(
                    false,
                    null,
                    "Payment token is invalid"
            );
        }

        if (amount == null
                || amount.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            return new PaymentGatewayResult(
                    false,
                    null,
                    "Payment amount is invalid"
            );
        }

        if (DECLINED_TEST_TOKEN.equalsIgnoreCase(
                paymentToken.trim()
        )) {
            return new PaymentGatewayResult(
                    false,
                    generateReference(
                            referencePrefix
                    ),
                    "Payment was declined"
            );
        }

        return new PaymentGatewayResult(
                true,
                generateReference(
                        referencePrefix
                ),
                null
        );
    }

    private String generateReference(
            String prefix
    ) {
        return prefix
                + "-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }
}