//ProductReviewRequest.java
// Abulele Ntwanambi(218276400)
//Date: 17 August 2026
package cput.ac.za.ecommerce.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductReviewRequest {

    @NotBlank(
            message = "Target product is required"
    )
    private String targetProductId;

    @Min(
            value = 1,
            message = "Rating must be at least 1"
    )
    @Max(
            value = 5,
            message = "Rating must be at most 5"
    )
    private int hardwareStarRating;

    @NotBlank(
            message = "Review text is required"
    )
    @Size(
            max = 1000,
            message = "Review text is too long"
    )
    private String comprehensiveReviewText;

    public ProductReviewRequest() {
    }

    public String getTargetProductId() {
        return targetProductId;
    }

    public void setTargetProductId(
            String targetProductId
    ) {
        this.targetProductId = targetProductId;
    }

    public int getHardwareStarRating() {
        return hardwareStarRating;
    }

    public void setHardwareStarRating(
            int hardwareStarRating
    ) {
        this.hardwareStarRating = hardwareStarRating;
    }

    public String getComprehensiveReviewText() {
        return comprehensiveReviewText;
    }

    public void setComprehensiveReviewText(
            String comprehensiveReviewText
    ) {
        this.comprehensiveReviewText =
                comprehensiveReviewText;
    }
}
