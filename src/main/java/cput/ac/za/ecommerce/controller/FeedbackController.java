
/*FeedbackController.java
    Controller for Feedback entity
    Author: Abulele Ntwanambi(218276400)
    Date: 12 July 2026 */

package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.ContentModeration;
import cput.ac.za.ecommerce.domain.DeliveryServiceReview;
import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.domain.ProductReview;
import cput.ac.za.ecommerce.request.DeliveryReviewRequest;
import cput.ac.za.ecommerce.request.ProductReviewRequest;
import cput.ac.za.ecommerce.service.IFeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final IFeedbackService feedbackService;

    public FeedbackController(
            IFeedbackService feedbackService
    ) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/product-reviews")
    public ResponseEntity<Feedback>
    createProductReview(
            @Valid
            @RequestBody
            ProductReviewRequest request,

            Authentication authentication
    ) {
        ProductReview productReview =
                new ProductReview.Builder()
                        .setFeedbackId(
                                UUID.randomUUID().toString()
                        )
                        .setTargetProductId(
                                request.getTargetProductId()
                        )
                        .setReviewerCustomerId(
                                getAuthenticatedEmail(
                                        authentication
                                )
                        )
                        .setDateSubmitted(
                                LocalDate.now()
                        )
                        .setReviewStatus(
                                defaultModeration()
                        )
                        .setHardwareStarRating(
                                request.getHardwareStarRating()
                        )
                        .setComprehensiveReviewText(
                                request.getComprehensiveReviewText()
                        )
                        .build();

        Feedback created =
                feedbackService.create(
                        productReview
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PostMapping("/delivery-reviews")
    public ResponseEntity<Feedback>
    createDeliveryReview(
            @Valid
            @RequestBody
            DeliveryReviewRequest request,

            Authentication authentication
    ) {
        DeliveryServiceReview deliveryReview =
                new DeliveryServiceReview.Builder()
                        .setFeedbackId(
                                UUID.randomUUID().toString()
                        )
                        .setTargetProductId(
                                request.getTargetProductId()
                        )
                        .setReviewerCustomerId(
                                getAuthenticatedEmail(
                                        authentication
                                )
                        )
                        .setDateSubmitted(
                                LocalDate.now()
                        )
                        .setReviewStatus(
                                defaultModeration()
                        )
                        .setCourierFulfillmentRating(
                                request.getCourierFulfillmentRating()
                        )
                        .setPackageConditionFeedback(
                                request.getPackageConditionFeedback()
                        )
                        .build();

        Feedback created =
                feedbackService.create(
                        deliveryReview
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Feedback>>
    getProductReviews(
            @PathVariable String productId
    ) {
        return ResponseEntity.ok(
                feedbackService.getProductReviews(
                        productId
                )
        );
    }

    @GetMapping("/{feedbackId}")
    public ResponseEntity<Feedback>
    getFeedback(
            @PathVariable String feedbackId
    ) {
        Feedback feedback =
                feedbackService.read(feedbackId);

        if (feedback == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        return ResponseEntity.ok(feedback);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>>
    getAllFeedback() {
        return ResponseEntity.ok(
                feedbackService.getAll()
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PutMapping("/{feedbackId}")
    public ResponseEntity<Feedback>
    updateFeedback(
            @PathVariable String feedbackId,
            @RequestBody Feedback feedback
    ) {
        if (!feedbackId.equals(
                feedback.getFeedbackId()
        )) {
            throw new IllegalArgumentException(
                    "Feedback ID does not match the URL"
            );
        }

        return ResponseEntity.ok(
                feedbackService.update(feedback)
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void>
    deleteFeedback(
            @PathVariable String feedbackId
    ) {
        feedbackService.delete(feedbackId);

        return ResponseEntity.noContent()
                .build();
    }

    private ContentModeration defaultModeration() {
        return new ContentModeration.Builder()
                .setIsPubliclyVisible(true)
                .setReviewStatus("PUBLISHED")
                .build();
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