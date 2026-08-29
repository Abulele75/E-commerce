package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/*FeedbackRepository.java
    Repository for Feedback entity
    Author: Abulele Ntwanambi(218276400)
    Date: 12 July 2026 */

public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    List<Feedback> findAllByTargetProductId(String targetProductId);
}
