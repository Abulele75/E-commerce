package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.WalletProvider;

import java.math.BigDecimal;

public interface IPaymentGatewayService {

    PaymentGatewayResult chargeCard(
            String paymentToken,
            BigDecimal amount
    );

    PaymentGatewayResult chargeWallet(
            WalletProvider walletProvider,
            String walletToken,
            BigDecimal amount
    );
}