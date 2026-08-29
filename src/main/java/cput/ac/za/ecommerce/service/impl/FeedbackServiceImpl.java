package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.domain.ProductReview;
import cput.ac.za.ecommerce.repository.FeedbackRepository;
import cput.ac.za.ecommerce.service.IFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/*FeedbackService.java
    Service for Feedback entity
    Author: Abulele Ntwanambi(218276400)
    Date: 12 July 2026 */

@Service
public class FeedbackServiceImpl implements IFeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback create(Feedback feedback) {
        return feedbackRepository.save(feedback);

    }
    @Override
    public Feedback read(String feedbackId) {
        return feedbackRepository.findById(feedbackId).orElse(null);
    }

    @Override
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    @Override
    public List<Feedback> getProductReviews(String productId) {
        return feedbackRepository
                .findAllByTargetProductId(productId)
                .stream()
                .filter(feedback -> feedback instanceof ProductReview)
                .toList();
    }

    @Override
    public Feedback update(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public void delete(String feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

}

