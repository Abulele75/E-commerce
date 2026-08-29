package cput.ac.za.ecommerce.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public abstract class PaymentRequest {

    @NotBlank(
            message = "Order ID is required"
    )
    private String orderId;

    @Valid
    @NotNull(
            message = "Billing location is required"
    )
    private BillingLocationRequest billingLocation;

    protected PaymentRequest() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(
            String orderId
    ) {
        this.orderId = orderId;
    }

    public BillingLocationRequest
    getBillingLocation() {
        return billingLocation;
    }

    public void setBillingLocation(
            BillingLocationRequest billingLocation
    ) {
        this.billingLocation = billingLocation;
    }
}