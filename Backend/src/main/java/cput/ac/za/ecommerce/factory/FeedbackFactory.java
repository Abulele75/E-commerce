package cput.ac.za.ecommerce.factory;

import cput.ac.za.ecommerce.domain.*;
/*FeedbackFactory.java
    Feedback factory class
    Author: Abulele Ntwanambi(218276400)
    Date: 27 June 2026
 */
import java.time.LocalDate;

public class FeedbackFactory {
    public static ProductReview createProductReview(String feedbackId, String targetProductId, String reviewerCustomerId,
                                                    LocalDate dateSubmitted, ContentModeration reviewStatus, Customer customer,
                                                    ProductCatalog product, int hardwareStarRating, String comprehensiveReviewText) {
        if (feedbackId == null || feedbackId.isBlank()) {
            throw new IllegalArgumentException("Feedback ID is required");
        }
        if (targetProductId == null || targetProductId.isBlank()) {
            throw new IllegalArgumentException("Target product ID is required");
        }
        if (reviewerCustomerId == null || reviewerCustomerId.isBlank()) {
            throw new IllegalArgumentException("Reviewer customer ID is required");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }

        return new ProductReview.Builder()
                .setFeedbackId(feedbackId)
                .setTargetProductId(targetProductId)
                .setReviewerCustomerId(reviewerCustomerId)
                .setDateSubmitted(dateSubmitted)
                .setReviewStatus(reviewStatus)
                .setCustomer(customer)
                .setProductCatalog(product)
                .setHardwareStarRating(hardwareStarRating)
                .setComprehensiveReviewText(comprehensiveReviewText)
                .build();
    }

    public static DeliveryServiceReview createDeliveryServiceReview(String feedbackId, String targetProductId, String reviewerCustomerId,
                                                                    LocalDate dateSubmitted, ContentModeration reviewStatus, Customer customer,
                                                                    ProductCatalog product, int courierFulfillmentRating, String packageConditionFeedback){

        if (feedbackId == null || feedbackId.isBlank()) {
            throw new IllegalArgumentException("Feedback ID is required");
        }
        if (targetProductId == null || targetProductId.isBlank()) {
            throw new IllegalArgumentException("Target product ID is required");
        }
        if (reviewerCustomerId == null || reviewerCustomerId.isBlank()) {
            throw new IllegalArgumentException("Reviewer customer ID is required");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }

        return new DeliveryServiceReview.Builder()
                .setFeedbackId(feedbackId)
                .setTargetProductId(targetProductId)
                .setReviewerCustomerId(reviewerCustomerId)
                .setDateSubmitted(dateSubmitted)
                .setReviewStatus(reviewStatus)
                .setCustomer(customer)
                .setProductCatalog(product)
                .setCourierFulfillmentRating(courierFulfillmentRating)
                .setPackageConditionFeedback(packageConditionFeedback)
                .build();
    }

}
