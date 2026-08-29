//OrderResponse.java
//Sinethemba Nyimbinya (220085870)
//Date: 17 August 2026
package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderId,
        OrderStatus orderStatus,
        List<OrderItemResponse> orderItems,
        FinancialBreakdownResponse financialBreakdown,
        DeliveryAddressResponse deliveryAddress,
        String deliveryInstructions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime paidAt
) {

    public static OrderResponse from(
            Order order
    ) {
        List<OrderItemResponse> items =
                order.getOrderItems()
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderStatus(),
                items,
                FinancialBreakdownResponse.from(
                        order.getFinancialBreakdown()
                ),
                DeliveryAddressResponse.from(
                        order.getDeliveryAddress()
                ),
                order.getDeliveryInstructions(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getPaidAt()
        );
    }
}