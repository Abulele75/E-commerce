//ShipmentTrackingRequest.java
// Sinazo Ntsimbi(222765208)
// Date: 17 August 2026
package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.domain.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShipmentTrackingRequest {

    @NotNull(
            message = "Delivery status is required"
    )
    private DeliveryStatus deliveryStatus;

    @Size(max = 150)
    private String currentLocation;

    @NotBlank(
            message = "Tracking message is required"
    )
    @Size(max = 300)
    private String statusMessage;

    public ShipmentTrackingRequest() {
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(
            DeliveryStatus deliveryStatus
    ) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(
            String currentLocation
    ) {
        this.currentLocation =
                currentLocation;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(
            String statusMessage
    ) {
        this.statusMessage = statusMessage;
    }
}