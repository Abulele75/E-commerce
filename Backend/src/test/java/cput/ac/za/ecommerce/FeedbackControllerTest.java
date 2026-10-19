package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.controller.FeedbackController;
import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.domain.ProductReview;
import cput.ac.za.ecommerce.request.ProductReviewRequest;
import cput.ac.za.ecommerce.service.IFeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackControllerTest {

    @Mock
    private IFeedbackService feedbackService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FeedbackController feedbackController;

    private Feedback feedback;

    @BeforeEach
    void setUp() {
        feedback = new ProductReview.Builder()
                .setFeedbackId("F001")
                .setTargetProductId("P001")
                .setReviewerCustomerId("customer@example.com")
                .build();
    }

    @Test
    void createProductReviewReturnsCreatedFeedback() {
        ProductReviewRequest request = new ProductReviewRequest();
        request.setTargetProductId("P001");
        request.setHardwareStarRating(5);
        request.setComprehensiveReviewText("Excellent product.");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(" Customer@Example.com ");
        when(feedbackService.createProductReview(
                anyString(),
                any(ProductReviewRequest.class)
        )).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            ProductReviewRequest submitted = invocation.getArgument(1);
            return new ProductReview.Builder()
                    .setFeedbackId("F001")
                    .setTargetProductId(submitted.getTargetProductId())
                    .setReviewerCustomerId(email)
                    .setHardwareStarRating(submitted.getHardwareStarRating())
                    .setComprehensiveReviewText(submitted.getComprehensiveReviewText())
                    .build();
        });

        ResponseEntity<Feedback> response =
                feedbackController.createProductReview(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        ProductReview created = assertInstanceOf(ProductReview.class, response.getBody());
        assertEquals("P001", created.getTargetProductId());
        assertEquals("customer@example.com", created.getReviewerCustomerId());
        assertEquals(5, created.getHardwareStarRating());
    }

    @Test
    void getFeedbackReturnsRequestedFeedback() {
        when(feedbackService.read("F001")).thenReturn(feedback);

        ResponseEntity<Feedback> response = feedbackController.getFeedback("F001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(feedback, response.getBody());
    }

    @Test
    void getAllFeedbackReturnsServiceResults() {
        when(feedbackService.getAll()).thenReturn(List.of(feedback));

        ResponseEntity<List<Feedback>> response = feedbackController.getAllFeedback();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(List.of(feedback), response.getBody());
    }
}
