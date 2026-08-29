/*
   PaymentFactory.java
   Ngwana Tiyani (231266731)
   Date: 21 June 2026
 */

package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.*;
import cput.ac.za.ecommerce.request.BillingLocationRequest;
import cput.ac.za.ecommerce.util.ValidationPatterns;

import java.math.BigDecimal;
import java.util.UUID;

public final class PaymentFactory {

    private PaymentFactory() {
    }

    public static CardPayment createCardPayment(
            Order order,
            BigDecimal capturedAmount,
            BillingLocation billingLocation,
            String paymentGatewayReference,
            CardBrand cardBrand,
            String cardholderName,
            String cardLastFourDigits
    ) {
        if (!isValidCommonPayment(
                order,
                capturedAmount,
                billingLocation,
                paymentGatewayReference
        )) {
            return null;
        }

        String normalizedCardholderName =
                ValidationPatterns.normalizeName(
                        cardholderName
                );

        if (!ValidationPatterns.isValidName(
                normalizedCardholderName
        )) {
            return null;
        }

        if (cardBrand == null
                || cardLastFourDigits == null
                || !cardLastFourDigits.matches(
                "^\\d{4}$"
        )) {
            return null;
        }

        return new CardPayment.Builder()
                .setTransactionId(
                        generateTransactionId()
                )
                .setOrder(order)
                .setCapturedAmount(
                        capturedAmount
                )
                .setBillingLocation(
                        billingLocation
                )
                .setPaymentGatewayReference(
                        paymentGatewayReference
                )
                .setCardBrand(cardBrand)
                .setCardholderName(
                        normalizedCardholderName
                )
                .setCardLastFourDigits(
                        cardLastFourDigits
                )
                .build();
    }

    public static DigitalWalletPayment
    createDigitalWalletPayment(
            Order order,
            BigDecimal capturedAmount,
            BillingLocation billingLocation,
            WalletProvider walletProvider,
            String providerTransactionReference
    ) {
        if (!isValidCommonPayment(
                order,
                capturedAmount,
                billingLocation,
                providerTransactionReference
        )) {
            return null;
        }

        if (walletProvider == null) {
            return null;
        }

        return new DigitalWalletPayment.Builder()
                .setTransactionId(
                        generateTransactionId()
                )
                .setOrder(order)
                .setCapturedAmount(
                        capturedAmount
                )
                .setBillingLocation(
                        billingLocation
                )
                .setWalletProvider(
                        walletProvider
                )
                .setProviderTransactionReference(
                        providerTransactionReference
                )
                .build();
    }

    public static BillingLocation
    createBillingLocation(BillingLocationRequest request)
    {
        if (request == null)
        {
            return null;
        }

        String normalizedBillingName = ValidationPatterns.normalizeName(request.getBillingName());

        if (!ValidationPatterns.isValidName(normalizedBillingName))
        {
            return null;
        }

        if (isBlank(request.getStreetAddress())
                || isBlank(request.getCity())
                || isBlank(request.getProvince())
                || request.getPostalCode() == null
                || !request.getPostalCode()
                .matches("^\\d{4}$")) {

            return null;
        }

        return new BillingLocation.Builder()
                .setBillingName(normalizedBillingName)
                .setStreetAddress(request.getStreetAddress().trim())
                .setSuburb(trimToNull(request.getSuburb()))
                .setCity(request.getCity().trim())
                .setProvince(request.getProvince().trim())
                .setPostalCode(request.getPostalCode().trim())
                .setCountry(isBlank(request.getCountry()) ? "South Africa" : request.getCountry().trim()
                ).build();
    }

    private static boolean isValidCommonPayment(
            Order order,
            BigDecimal capturedAmount,
            BillingLocation billingLocation,
            String providerReference
    ) {
        return order != null
                && capturedAmount != null
                && capturedAmount.compareTo(
                BigDecimal.ZERO
        ) > 0
                && billingLocation != null
                && !isBlank(providerReference);
    }

    private static String generateTransactionId() {
        return "PAY-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 14)
                .toUpperCase();
    }

    private static boolean isBlank(
            String value
    ) {
        return value == null
                || value.isBlank();
    }

    private static String trimToNull(
            String value
    ) {
        return isBlank(value)
                ? null
                : value.trim();
    }
}