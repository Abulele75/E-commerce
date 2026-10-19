package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.domain.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        String userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        UserRole role,
        boolean active,
        LocalDateTime createdAt
) {

    public static UserResponse from(
            User user
    ) {
        return new UserResponse(
                user.getUserId(),
                user.getAccountProfile()
                        .getFirstName(),
                user.getAccountProfile()
                        .getLastName(),
                user.getAccountProfile()
                        .getEmail(),
                user.getAccountProfile()
                        .getPhoneNumber(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}