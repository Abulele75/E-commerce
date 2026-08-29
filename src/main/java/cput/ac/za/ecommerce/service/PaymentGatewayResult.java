package cput.ac.za.ecommerce.service;

public record PaymentGatewayResult(
        boolean successful,
        String providerReference,
        String failureReason
)

{
}