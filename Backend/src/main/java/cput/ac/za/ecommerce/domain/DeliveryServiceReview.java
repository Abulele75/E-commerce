package cput.ac.za.ecommerce.domain;

/*DeliveryServiceReview.java
    Entity for delivery service review
    Author: Abulele Ntwanambi(218276400)
    Date: 20 June 2026
 */
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("DeliveryServiceReview")
public class DeliveryServiceReview extends Feedback{
    private int courierFulfillmentRating;
    private String packageConditionFeedback;

    protected DeliveryServiceReview() {
    }

    public int getCourierFulfillmentRating() {
        return courierFulfillmentRating;
    }

    public String getPackageConditionFeedback() {
        return packageConditionFeedback;
    }
    public DeliveryServiceReview(Builder builder) {
        super(builder);
        this.courierFulfillmentRating = builder.courierFulfillmentRating;
        this.packageConditionFeedback = builder.packageConditionFeedback;
    }
    public String toString() {
        return super.toString() + "courierFulfillmentRating='" + courierFulfillmentRating + '\'' +
                ", packageConditionFeedback='" + packageConditionFeedback + '\'' +
                '}';
    }
    public static class Builder extends Feedback.Builder{
        private int courierFulfillmentRating;
        private String packageConditionFeedback;

        public Builder setCourierFulfillmentRating(int courierFulfillmentRating) {
            this.courierFulfillmentRating = courierFulfillmentRating;
            return this;
        }

        public Builder setPackageConditionFeedback(String packageConditionFeedback) {
            this.packageConditionFeedback = packageConditionFeedback;
            return this;
        }
        @Override
        public Builder setFeedbackId(String feedbackId) {
            super.setFeedbackId(feedbackId);
            return this;
        }
        @Override
        public Builder setTargetProductId(String targetProductId) {
            super.setTargetProductId(targetProductId);
            return this;
        }
        @Override
        public Builder setReviewerCustomerId(String reviewerCustomerId) {
            super.setReviewerCustomerId(reviewerCustomerId);
            return this;
        }
        @Override
        public Builder setDateSubmitted(LocalDate dateSubmitted) {
            super.setDateSubmitted(dateSubmitted);
            return this;
        }
        @Override
        public Builder setReviewStatus(ContentModeration reviewStatus){
            super.setReviewStatus(reviewStatus);
            return this;
        }
        @Override
        public Builder setProductCatalog(ProductCatalog productCatalog) {
            super.setProductCatalog(productCatalog);
            return this;
        }
        @Override
        public Builder setCustomer(Customer customer) {
            super.setCustomer(customer);
            return this;
        }

        @Override
        public DeliveryServiceReview build() {
            return new DeliveryServiceReview(this);
        }
    }
}
