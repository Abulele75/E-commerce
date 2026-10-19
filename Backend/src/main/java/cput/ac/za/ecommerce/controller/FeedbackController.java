package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.request.DeliveryReviewRequest;
import cput.ac.za.ecommerce.request.ProductReviewRequest;
import cput.ac.za.ecommerce.service.IFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final IFeedbackService feedbackService;

    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/product-reviews")
    public ResponseEntity<Feedback> createProductReview(
            @Valid @RequestBody ProductReviewRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                feedbackService.createProductReview(email(authentication), request)
        );
    }

    @PostMapping("/delivery-reviews")
    public ResponseEntity<Feedback> createDeliveryReview(
            @Valid @RequestBody DeliveryReviewRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                feedbackService.createDeliveryReview(email(authentication), request)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Feedback>> getProductReviews(@PathVariable String productId) {
        return ResponseEntity.ok(feedbackService.getProductReviews(productId));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/{feedbackId}")
    public ResponseEntity<Feedback> getFeedback(@PathVariable String feedbackId) {
        return ResponseEntity.ok(feedbackService.read(feedbackId));
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAll());
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable String feedbackId) {
        feedbackService.delete(feedbackId);
        return ResponseEntity.noContent().build();
    }

    private String email(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new AccessDeniedException("Authentication is required");
        }
        return authentication.getName().trim().toLowerCase();
    }
}
