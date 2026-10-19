package cput.ac.za.ecommerce.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class UpdateCartItemRequest {

    @Min(
            value = 1,
            message =
                    "Quantity must be at least 1"
    )
    @Max(
            value = 99,
            message =
                    "Quantity cannot exceed 99"
    )
    private int quantity;

    public UpdateCartItemRequest() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}