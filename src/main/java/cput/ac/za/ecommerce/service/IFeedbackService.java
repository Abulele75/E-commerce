package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Feedback;

import java.util.List;
/*IFeedback.java
    Interface for Feedback service
    Author: Abulele Ntwanambi(218276400)
    Date: 12 July 2026 */

public interface IFeedbackService {
    Feedback create(Feedback feedback);
    Feedback read(String feedbackId);
    List<Feedback> getAll();
    List<Feedback> getProductReviews(String productId);
    Feedback update(Feedback feedback);
    void delete(String feedbackId);
}
