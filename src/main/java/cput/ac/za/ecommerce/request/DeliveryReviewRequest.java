package cput.ac.za.ecommerce.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DeliveryReviewRequest {

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
    private int courierFulfillmentRating;

    @NotBlank(
            message = "Delivery feedback is required"
    )
    @Size(
            max = 1000,
            message = "Delivery feedback is too long"
    )
    private String packageConditionFeedback;

    public DeliveryReviewRequest() {
    }

    public String getTargetProductId() {
        return targetProductId;
    }

    public void setTargetProductId(
            String targetProductId
    ) {
        this.targetProductId = targetProductId;
    }

    public int getCourierFulfillmentRating() {
        return courierFulfillmentRating;
    }

    public void setCourierFulfillmentRating(
            int courierFulfillmentRating
    ) {
        this.courierFulfillmentRating =
                courierFulfillmentRating;
    }

    public String getPackageConditionFeedback() {
        return packageConditionFeedback;
    }

    public void setPackageConditionFeedback(
            String packageConditionFeedback
    ) {
        this.packageConditionFeedback =
                packageConditionFeedback;
    }
}
