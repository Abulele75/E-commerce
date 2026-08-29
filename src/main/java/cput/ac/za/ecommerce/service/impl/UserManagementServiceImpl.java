package cput.ac.za.ecommerce.service.impl;

import cput.ac.za.ecommerce.domain.AccountProfile;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.factory.UserManagementFactory;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.request.RegisterRequest;
import cput.ac.za.ecommerce.request.UpdateProfileRequest;
import cput.ac.za.ecommerce.service.IUserManagementService;
import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserManagementServiceImpl
        implements IUserManagementService {

    private final UserManagementRepository
            userRepository;

    private final PasswordEncoder
            passwordEncoder;

    public UserManagementServiceImpl(
            UserManagementRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Customer registerCustomer(
            RegisterRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Registration details are required"
            );
        }

        String email =
                ValidationPatterns.normalizeEmail(
                        request.getEmail()
                );

        String phone =
                ValidationPatterns
                        .normalizePhoneNumber(
                                request.getPhoneNumber()
                        );

        if (!request.getPassword().equals(
                request.getConfirmPassword()
        )) {
            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        if (userRepository
                .existsByEmailIgnoreCase(email)) {

            throw new IllegalStateException(
                    "An account with this email already exists"
            );
        }

        if (userRepository
                .existsByPhoneNumber(phone)) {

            throw new IllegalStateException(
                    "An account with this cellphone number already exists"
            );
        }

        Customer customer =
                UserManagementFactory
                        .createCustomer(
                                request.getFirstName(),
                                request.getLastName(),
                                email,
                                phone,
                                passwordEncoder.encode(
                                        request.getPassword()
                                )
                        );

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer details are invalid"
            );
        }

        return userRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(
            String email
    ) {
        return userRepository
                .findByEmailIgnoreCase(
                        normalizeEmail(email)
                )
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User account was not found"
                        )
                );
    }

    @Override
    @Transactional
    public User updateCurrentProfile(
            String currentEmail,
            UpdateProfileRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Profile details are required"
            );
        }

        User user = getUserByEmail(currentEmail);

        String newEmail =
                ValidationPatterns.normalizeEmail(
                        request.getEmail()
                );

        String newPhone =
                ValidationPatterns
                        .normalizePhoneNumber(
                                request.getPhoneNumber()
                        );

        String currentStoredEmail =
                user.getAccountProfile()
                        .getEmail();

        String currentStoredPhone =
                user.getAccountProfile()
                        .getPhoneNumber();

        if (!newEmail.equalsIgnoreCase(
                currentStoredEmail
        ) && userRepository
                .existsByEmailIgnoreCase(newEmail)) {

            throw new IllegalStateException(
                    "Another account already uses this email"
            );
        }

        if (!newPhone.equals(currentStoredPhone)
                && userRepository
                .existsByPhoneNumber(newPhone)) {

            throw new IllegalStateException(
                    "Another account already uses this cellphone number"
            );
        }

        AccountProfile profile =
                UserManagementFactory
                        .createAccountProfile(
                                request.getFirstName(),
                                request.getLastName(),
                                newEmail,
                                newPhone
                        );

        if (profile == null) {
            throw new IllegalArgumentException(
                    "Profile details are invalid"
            );
        }

        user.updateProfile(profile);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User deactivateCurrentAccount(
            String email
    ) {
        User user = getUserByEmail(email);
        user.deactivate();
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User activateUser(
            String userId
    ) {
        User user = findById(userId);
        user.activate();
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User deactivateUser(
            String userId
    ) {
        User user = findById(userId);
        user.deactivate();
        return userRepository.save(user);
    }

    private User findById(String userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User account was not found"
                        )
                );
    }

    private String normalizeEmail(
            String email
    ) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required"
            );
        }

        return ValidationPatterns
                .normalizeEmail(email);
    }
}