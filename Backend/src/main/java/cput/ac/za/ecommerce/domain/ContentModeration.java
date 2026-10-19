package cput.ac.za.ecommerce.domain;

/*ContentModeration.java
    Value Object class
    Author: Abulele Ntwanambi(218276400)
    Date: 20 June 2026
 */

import jakarta.persistence.Embeddable;

@Embeddable
public class ContentModeration {
    private boolean isPubliclyVisible;
    private String filterFlagReason;
    private String reviewStatus;



    protected ContentModeration() {
    }

    public boolean isPubliclyVisible() {
        return isPubliclyVisible;
    }

    public String getFilterFlagReason() {
        return filterFlagReason;
    }
    public String getReviewStatus() {
        return reviewStatus;
    }
    public ContentModeration(Builder builder) {
        this.isPubliclyVisible = builder.isPubliclyVisible;
        this.filterFlagReason = builder.filterFlagReason;
        this.reviewStatus = builder.reviewStatus;
    }
    public static class Builder {
        private boolean isPubliclyVisible;
        private String filterFlagReason;
        private String reviewStatus;

        public Builder setIsPubliclyVisible(boolean isPubliclyVisible) {
            this.isPubliclyVisible = isPubliclyVisible;
            return this;
        }

        public Builder setFilterFlagReason(String filterFlagReason) {
            this.filterFlagReason = filterFlagReason;
            return this;
        }

        public Builder setReviewStatus(String reviewStatus) {
            this.reviewStatus = reviewStatus;
            return this;
        }

        public ContentModeration build() {
            return new ContentModeration(this);
        }
    }
}

