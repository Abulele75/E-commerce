package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.controller.FeedbackController;
import cput.ac.za.ecommerce.domain.Feedback;
import cput.ac.za.ecommerce.domain.ProductReview;
import cput.ac.za.ecommerce.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    private Feedback feedback;

    @BeforeEach
    void setUp() {

        feedback = new ProductReview.Builder()
                .setFeedbackId("F001")
                .setTargetProductId("P001")
                .setReviewerCustomerId("C001")
                .build();
    }

    @Test
    void create(){

        when(feedbackService.create(feedback)).thenReturn(feedback);

        Feedback result = feedbackController.create(feedback);

        assertNotNull(result);
        assertEquals(feedback,result);

    }
    @Test
    void read() {
        when(feedbackService.read("F001")).thenReturn(feedback);

        Feedback found = feedbackController.read("F001");

        assertNotNull(found);
        assertEquals(feedback, found);
    }
    @Test
    void getAll() {
        List<Feedback> feedbackList = new ArrayList<>();
        feedbackList.add(feedback);

        when(feedbackService.getAll()).thenReturn(feedbackList);

        List<Feedback> result = feedbackController.getAll();

        assertEquals(1, result.size());
        assertEquals(feedback, result.get(0));
    }

    }

