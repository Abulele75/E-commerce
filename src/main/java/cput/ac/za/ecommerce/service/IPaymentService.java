/*
   IPaymentService
   Ngwana Tiyani (231266731)
   Date: 07 July 2026
 */

package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Payment;
import cput.ac.za.ecommerce.request.CardPaymentRequest;
import cput.ac.za.ecommerce.request.DigitalWalletPaymentRequest;

import java.util.List;

public interface IPaymentService {

    Payment processCardPayment(
            String customerEmail,
            CardPaymentRequest request
    );

    Payment processDigitalWalletPayment(
            String customerEmail,
            DigitalWalletPaymentRequest request
    );

    Payment getPaymentForCustomer(
            String transactionId,
            String customerEmail
    );

    List<Payment> getPaymentsForOrder(
            String orderId,
            String customerEmail
    );

    List<Payment> getCustomerPayments(
            String customerEmail
    );

    Payment savePayment(Payment payment);

    Payment getPaymentById(String paymentId);

    List<Payment> getAllPayments();

    Payment updatePayment(Payment payment);

    void deletePayment(String paymentId);

    Payment refundPayment(
            String transactionId
    );
}