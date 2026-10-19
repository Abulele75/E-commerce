package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.CartStatus;
import cput.ac.za.ecommerce.domain.OrderStatus;
import cput.ac.za.ecommerce.domain.PaymentStatus;
import cput.ac.za.ecommerce.repository.CartRepository;
import cput.ac.za.ecommerce.repository.FeedbackRepository;
import cput.ac.za.ecommerce.repository.OrderRepository;
import cput.ac.za.ecommerce.repository.PaymentRepository;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.response.AdminDashboardResponse;
import cput.ac.za.ecommerce.response.PaymentResponse;
import cput.ac.za.ecommerce.service.IAdminDashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminDashboardServiceImpl
        implements IAdminDashboardService {

    private final UserManagementRepository userRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminDashboardServiceImpl(
            UserManagementRepository userRepository,
            CartRepository cartRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            FeedbackRepository feedbackRepository
    ) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime periodStart = now.minusDays(30);

        return new AdminDashboardResponse(
                now,
                periodStart,
                userRepository.count(),
                userRepository.countByActiveTrue(),
                userRepository.countByCreatedAtAfter(periodStart),
                userRepository.countByLastLoginAtAfter(periodStart),
                cartRepository.countByCartStatus(CartStatus.ACTIVE),
                orderRepository.count(),
                orderRepository.countByOrderStatus(
                        OrderStatus.PENDING_PAYMENT
                ),
                paymentRepository.countByPaymentStatus(
                        PaymentStatus.SUCCESSFUL
                ),
                paymentRepository.sumCapturedAmountByStatus(
                        PaymentStatus.SUCCESSFUL
                ),
                feedbackRepository.count(),
                paymentRepository
                        .findTop6ByOrderByCreatedAtDesc()
                        .stream()
                        .map(PaymentResponse::from)
                        .toList()
        );
    }
}
