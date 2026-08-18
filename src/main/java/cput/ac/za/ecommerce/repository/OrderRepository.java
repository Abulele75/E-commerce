/*
Sinethemba Nyimbinya (220085870)
Date : 2026
 */

package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}

