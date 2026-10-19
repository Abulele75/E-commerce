package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.request.DeliveryReviewRequest;
import cput.ac.za.ecommerce.request.ProductReviewRequest;

import java.util.List;

public interface IFeedbackService {
    Feedback create(Feedback feedback);
    Feedback createProductReview(String customerEmail, ProductReviewRequest request);
    Feedback createDeliveryReview(String customerEmail, DeliveryReviewRequest request);
    Feedback read(String feedbackId);
    List<Feedback> getAll();
    List<Feedback> getProductReviews(String productId);
    void delete(String feedbackId);
}
