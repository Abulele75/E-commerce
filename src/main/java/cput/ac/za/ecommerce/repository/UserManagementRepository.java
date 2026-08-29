package cput.ac.za.ecommerce.repository;

import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserManagementRepository
        extends JpaRepository<User, String> {

    @Query("""
            SELECT u
            FROM User u
            WHERE LOWER(u.accountProfile.email)
                = LOWER(:email)
            """)
    Optional<User> findByEmailIgnoreCase(
            @Param("email")
            String email
    );

    @Query("""
            SELECT c
            FROM Customer c
            WHERE LOWER(c.accountProfile.email)
                = LOWER(:email)
            """)
    Optional<Customer> findCustomerByEmailIgnoreCase(
            @Param("email")
            String email
    );

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0
                        THEN true
                        ELSE false
                   END
            FROM User u
            WHERE LOWER(u.accountProfile.email)
                = LOWER(:email)
            """)
    boolean existsByEmailIgnoreCase(
            @Param("email")
            String email
    );

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0
                        THEN true
                        ELSE false
                   END
            FROM User u
            WHERE u.accountProfile.phoneNumber
                = :phoneNumber
            """)
    boolean existsByPhoneNumber(
            @Param("phoneNumber")
            String phoneNumber
    );
}