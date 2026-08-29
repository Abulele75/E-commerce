package cput.ac.za.ecommerce.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AddCartItemRequest {

    @NotBlank(
            message = "Product ID is required"
    )
    private String productId;

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
    private int quantity = 1;

    public AddCartItemRequest() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(
            String productId
    ) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}