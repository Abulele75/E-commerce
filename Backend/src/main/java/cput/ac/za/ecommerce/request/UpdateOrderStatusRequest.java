//UpdateOrderStatusRequest.java
// Sinethemba Nyimbinya (220085870)
//Date: 17 August 2026
package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {

    @NotNull(
            message = "Order status is required"
    )
    private OrderStatus orderStatus;

    public UpdateOrderStatusRequest() {
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(
            OrderStatus orderStatus
    ) {
        this.orderStatus = orderStatus;
    }
}
