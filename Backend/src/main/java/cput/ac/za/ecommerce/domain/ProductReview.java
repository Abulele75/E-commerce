package cput.ac.za.ecommerce.domain;

/*ProductReview.java
    Entity for Product review
    Author: Abulele Ntwanambi(218276400)
    Date: 20 June 2026 */

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("ProductReview")
public class ProductReview extends Feedback{
    private int hardwareStarRating;
    private String comprehensiveReviewText;

    protected ProductReview() {
    }
    public ProductReview(Builder builder) {
        super(builder);
        this.hardwareStarRating = builder.hardwareStarRating;
        this.comprehensiveReviewText = builder.comprehensiveReviewText;
    }

    public int getHardwareStarRating() {return hardwareStarRating;}

    public String getComprehensiveReviewText() {return comprehensiveReviewText;}

    public String toString() {
        return super.toString()  +
                "hardwareStarRating=" + hardwareStarRating +
                ", comprehensiveReviewText='" + comprehensiveReviewText + '\'' +
                '}';
    }
    public static class Builder extends Feedback.Builder {
        private int hardwareStarRating;
        private String comprehensiveReviewText;

        public Builder setHardwareStarRating(int hardwareStarRating) {
            this.hardwareStarRating = hardwareStarRating;
            return this;
        }

        public Builder setComprehensiveReviewText(String comprehensiveReviewText) {
            this.comprehensiveReviewText = comprehensiveReviewText;
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
        public ProductReview build() {
            return new ProductReview(this);
        }
    }
}

