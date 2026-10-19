package cput.ac.za.ecommerce.factory;

/*ContentModerationFactory.java
    Content Moderation factory class
    Author: Abulele Ntwanambi(218276400)
    Date: 20 June 2026
 */
import cput.ac.za.ecommerce.domain.ContentModeration;

public class ContentModerationFactory {
    public static ContentModeration createContentModeration(boolean isPubliclyVisible , String filterFlagReason,String reviewStatus){

        if (!isPubliclyVisible && (filterFlagReason == null || filterFlagReason.isBlank())) {
            throw new IllegalArgumentException("A flag reason is required when content is not publicly visible");
        }
        if (reviewStatus == null || reviewStatus.isBlank()) {
            throw new IllegalArgumentException("Review status cannot be null or empty");
        }

        return new ContentModeration.Builder()
                .setIsPubliclyVisible(isPubliclyVisible)
                .setFilterFlagReason(filterFlagReason)
                .setReviewStatus(reviewStatus)
                .build();
    }
}
