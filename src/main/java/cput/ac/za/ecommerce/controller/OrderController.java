package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderStatus;
import cput.ac.za.ecommerce.request.CheckoutRequest;
import cput.ac.za.ecommerce.request.UpdateOrderStatusRequest;
import cput.ac.za.ecommerce.response.OrderResponse;
import cput.ac.za.ecommerce.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(
            IOrderService orderService
    ) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse>
    checkout(
            @Valid
            @RequestBody
            CheckoutRequest request,

            Authentication authentication
    ) {
        Order order =
                orderService.checkout(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        OrderResponse.from(order)
                );
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>>
    getMyOrders(
            Authentication authentication
    ) {
        List<OrderResponse> responses =
                orderService
                        .getCustomerOrders(
                                getAuthenticatedEmail(
                                        authentication
                                )
                        )
                        .stream()
                        .map(OrderResponse::from)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse>
    getMyOrder(
            @PathVariable
            String orderId,

            Authentication authentication
    ) {
        Order order =
                orderService.getCustomerOrder(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        orderId
                );

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse>
    cancelMyOrder(
            @PathVariable
            String orderId,

            Authentication authentication
    ) {
        Order order =
                orderService.cancelCustomerOrder(
                        getAuthenticatedEmail(
                                authentication
                        ),
                        orderId
                );

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @GetMapping
    public ResponseEntity<List<OrderResponse>>
    getAllOrders(
            @RequestParam(
                    required = false
            )
            OrderStatus status
    ) {
        List<Order> orders =
                status == null
                        ? orderService.getAllOrders()
                        : orderService
                        .getOrdersByStatus(status);

        List<OrderResponse> responses =
                orders.stream()
                        .map(OrderResponse::from)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse>
    updateOrderStatus(
            @PathVariable
            String orderId,

            @Valid
            @RequestBody
            UpdateOrderStatusRequest request
    ) {
        Order order =
                orderService.updateOrderStatus(
                        orderId,
                        request.getOrderStatus()
                );

        return ResponseEntity.ok(
                OrderResponse.from(order)
        );
    }

    private String getAuthenticatedEmail(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        return authentication
                .getName()
                .trim()
                .toLowerCase();
    }
}