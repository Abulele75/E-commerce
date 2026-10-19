package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.ContentModeration;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.DeliveryServiceReview;
import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.domain.Order;
import cput.ac.za.ecommerce.domain.OrderStatus;
import cput.ac.za.ecommerce.domain.ProductCatalog;
import cput.ac.za.ecommerce.domain.ProductReview;
import cput.ac.za.ecommerce.repository.FeedbackRepository;
import cput.ac.za.ecommerce.repository.OrderRepository;
import cput.ac.za.ecommerce.repository.ProductCatalogRepository;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.request.DeliveryReviewRequest;
import cput.ac.za.ecommerce.request.ProductReviewRequest;
import cput.ac.za.ecommerce.service.IFeedbackService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements IFeedbackService {

    private static final List<OrderStatus> PURCHASED_STATUSES = List.of(
            OrderStatus.PAID, OrderStatus.PROCESSING, OrderStatus.SHIPPED, OrderStatus.DELIVERED
    );

    private final FeedbackRepository feedbackRepository;
    private final UserManagementRepository userRepository;
    private final ProductCatalogRepository productRepository;
    private final OrderRepository orderRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
            UserManagementRepository userRepository,
            ProductCatalogRepository productRepository,
            OrderRepository orderRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Feedback create(Feedback feedback) {
        if (feedback == null) throw new IllegalArgumentException("Feedback is required");
        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional
    public Feedback createProductReview(String customerEmail, ProductReviewRequest request) {
        Customer customer = customer(customerEmail);
        ProductCatalog product = productRepository.findById(request.getTargetProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product was not found"));
        if (!orderRepository.hasCustomerPurchasedProduct(customerEmail, product.getProductId(), PURCHASED_STATUSES)) {
            throw new AccessDeniedException("Only customers who purchased this product may review it");
        }
        ensureNotReviewed(product.getProductId(), customerEmail);

        ProductReview review = new ProductReview.Builder()
                .setFeedbackId(id())
                .setTargetProductId(product.getProductId())
                .setReviewerCustomerId(customerEmail)
                .setDateSubmitted(LocalDate.now())
                .setReviewStatus(published())
                .setCustomer(customer)
                .setProductCatalog(product)
                .setHardwareStarRating(request.getHardwareStarRating())
                .setComprehensiveReviewText(request.getComprehensiveReviewText().trim())
                .build();
        return feedbackRepository.save(review);
    }

    @Override
    @Transactional
    public Feedback createDeliveryReview(String customerEmail, DeliveryReviewRequest request) {
        Customer customer = customer(customerEmail);
        Order order = orderRepository.findForCustomer(request.getOrderId(), customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Order was not found"));
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivery can only be reviewed after the order is delivered");
        }
        ensureNotReviewed(order.getOrderId(), customerEmail);

        DeliveryServiceReview review = new DeliveryServiceReview.Builder()
                .setFeedbackId(id())
                .setTargetProductId(order.getOrderId())
                .setReviewerCustomerId(customerEmail)
                .setDateSubmitted(LocalDate.now())
                .setReviewStatus(published())
                .setCustomer(customer)
                .setCourierFulfillmentRating(request.getCourierFulfillmentRating())
                .setPackageConditionFeedback(request.getPackageConditionFeedback().trim())
                .build();
        return feedbackRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Feedback read(String feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback was not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getProductReviews(String productId) {
        return feedbackRepository.findAllByTargetProductId(productId).stream()
                .filter(ProductReview.class::isInstance)
                .filter(feedback -> feedback.getReviewStatus() != null
                        && feedback.getReviewStatus().isPubliclyVisible()
                        && "PUBLISHED".equalsIgnoreCase(feedback.getReviewStatus().getReviewStatus()))
                .toList();
    }

    @Override
    @Transactional
    public void delete(String feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) throw new EntityNotFoundException("Feedback was not found");
        feedbackRepository.deleteById(feedbackId);
    }

    private Customer customer(String email) {
        return userRepository.findCustomerByEmailIgnoreCase(email)
                .filter(Customer::isActive)
                .orElseThrow(() -> new EntityNotFoundException("Customer account was not found"));
    }

    private void ensureNotReviewed(String targetId, String email) {
        if (feedbackRepository.existsByTargetProductIdAndReviewerCustomerId(targetId, email)) {
            throw new IllegalStateException("You have already reviewed this purchase");
        }
    }

    private String id() {
        return "FDB-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();
    }

    private ContentModeration published() {
        return new ContentModeration.Builder().setIsPubliclyVisible(true).setReviewStatus("PUBLISHED").build();
    }
}
