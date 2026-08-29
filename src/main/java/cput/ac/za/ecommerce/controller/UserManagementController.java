package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.request.RegisterRequest;
import cput.ac.za.ecommerce.request.UpdateProfileRequest;
import cput.ac.za.ecommerce.response.UserResponse;
import cput.ac.za.ecommerce.service.IUserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final IUserManagementService
            userService;

    public UserManagementController(
            IUserManagementService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse>
    registerCustomer(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {
        Customer customer =
                userService.registerCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        UserResponse.from(customer)
                );
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse>
    getCurrentProfile(
            Authentication authentication
    ) {
        User user =
                userService.getUserByEmail(
                        authenticatedEmail(
                                authentication
                        )
                );

        return ResponseEntity.ok(
                UserResponse.from(user)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse>
    updateCurrentProfile(
            @Valid
            @RequestBody
            UpdateProfileRequest request,

            Authentication authentication
    ) {
        User user =
                userService.updateCurrentProfile(
                        authenticatedEmail(
                                authentication
                        ),
                        request
                );

        return ResponseEntity.ok(
                UserResponse.from(user)
        );
    }

    @PatchMapping("/me/deactivate")
    public ResponseEntity<UserResponse>
    deactivateCurrentAccount(
            Authentication authentication
    ) {
        User user =
                userService
                        .deactivateCurrentAccount(
                                authenticatedEmail(
                                        authentication
                                )
                        );

        return ResponseEntity.ok(
                UserResponse.from(user)
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>>
    getAllUsers() {
        List<UserResponse> users =
                userService.getAllUsers()
                        .stream()
                        .map(UserResponse::from)
                        .toList();

        return ResponseEntity.ok(users);
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<UserResponse>
    activateUser(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(
                UserResponse.from(
                        userService.activateUser(
                                userId
                        )
                )
        );
    }

    @PreAuthorize(
            "hasRole('ADMINISTRATOR')"
    )
    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<UserResponse>
    deactivateUser(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(
                UserResponse.from(
                        userService.deactivateUser(
                                userId
                        )
                )
        );
    }

    private String authenticatedEmail(
            Authentication authentication
    ) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new AccessDeniedException(
                    "Authentication is required"
            );
        }

        return authentication.getName()
                .trim()
                .toLowerCase();
    }
}