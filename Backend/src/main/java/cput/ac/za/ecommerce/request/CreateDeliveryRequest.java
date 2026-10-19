package cput.ac.za.ecommerce.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateDeliveryRequest {
    @NotBlank(message = "Order ID is required")
    private String orderId;
    @NotBlank(message = "Tracking number is required")
    @Size(max = 100)
    private String trackingNumber;
    @NotBlank(message = "Carrier is required")
    @Size(max = 100)
    private String carrier;
    @Size(max = 150)
    private String carrierContact;

    public CreateDeliveryRequest() {}
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getCarrierContact() { return carrierContact; }
    public void setCarrierContact(String carrierContact) { this.carrierContact = carrierContact; }
}
