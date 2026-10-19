package cput.ac.za.ecommerce.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminDashboardResponse(
        LocalDateTime generatedAt,
        LocalDateTime activityPeriodStart,
        long totalUsers,
        long enabledUsers,
        long registrationsInPeriod,
        long recentLoginsInPeriod,
        long activeCarts,
        long totalOrders,
        long pendingOrders,
        long successfulPayments,
        BigDecimal successfulRevenue,
        long totalFeedback,
        List<PaymentResponse> recentPayments
) {
}
