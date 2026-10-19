package cput.ac.za.ecommerce.domain;

/*Feedback.java
    Entity for the Feedback
    Author: Abulele Ntwanambi(218276400)
    Date: 20 June 2026
 */

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "feedback")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Feedback {

    @Id
    private String feedbackId;
    private String targetProductId;
    private String reviewerCustomerId;
    private LocalDate dateSubmitted;

    @Embedded
    private ContentModeration reviewStatus;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductCatalog product;

    protected Feedback() {
    }

    protected Feedback(Builder builder) {
        this.feedbackId = builder.feedbackId;
        this.targetProductId = builder.targetProductId;
        this.reviewerCustomerId = builder.reviewerCustomerId;
        this.dateSubmitted = builder.dateSubmitted;
        this.reviewStatus = builder.reviewStatus;
        this.customer = builder.customer;
        this.product = builder.product;
    }


    public String getFeedbackId() {return feedbackId;}

    public String getTargetProductId() {return targetProductId;}

    public String getReviewerCustomerId() {return reviewerCustomerId;}

    public LocalDate getDateSubmitted() {return dateSubmitted;}

    public ContentModeration getReviewStatus() {return reviewStatus;}

    public Customer getCustomer() {return customer;}

    public ProductCatalog getProduct() {return product;}

    public String toString(){
        return "Feedback{" +
                "feedbackId='" + feedbackId + '\'' +
                ", targetProductId='" + targetProductId + '\'' +
                ", reviewerCustomerId='" + reviewerCustomerId + '\'' +
                ", dateSubmitted=" + dateSubmitted +
                ", reviewStatus=" + reviewStatus +
                ", customer=" + customer +
                ", product=" + product +
                '}';
    }
    public abstract static class Builder{
        private String feedbackId;
        private String targetProductId;
        private String reviewerCustomerId;
        private LocalDate dateSubmitted;
        private ContentModeration reviewStatus;
        private Customer customer;
        private ProductCatalog product;

        public Builder setFeedbackId(String feedbackId) {
            this.feedbackId = feedbackId;
            return this;
        }

        public Builder setTargetProductId(String targetProductId) {
            this.targetProductId = targetProductId;
            return this;
        }

        public Builder setReviewerCustomerId(String reviewerCustomerId) {
            this.reviewerCustomerId = reviewerCustomerId;
            return this;
        }

        public Builder setDateSubmitted(LocalDate dateSubmitted) {
            this.dateSubmitted = dateSubmitted;
            return this;
        }

        public Builder setReviewStatus(ContentModeration reviewStatus) {
            this.reviewStatus = reviewStatus;
            return this;
        }

        public Builder setCustomer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder setProductCatalog(ProductCatalog product) {
            this.product = product;
            return this;
        }

        public abstract Feedback build();
    }
}

