package cput.ac.za.ecommerce;

/*
   UserServiceTest.java
   Owenkosi Nxasana (230240887)
   Updated: 26 July 2026
 */

import cput.ac.za.ecommerce.domain.AccountProfile;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.repository.UserManagementRepository;
import cput.ac.za.ecommerce.request.RegisterRequest;
import cput.ac.za.ecommerce.request.UpdateProfileRequest;
import cput.ac.za.ecommerce.service.impl.UserManagementServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserManagementRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserManagementServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserManagementServiceImpl(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void registerCustomerShouldSaveAndReturnCustomer() {

        RegisterRequest request = createRegisterRequest(
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567"
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(false);

        when(
                userRepository.existsByPhoneNumber(
                        "0821234567"
                )
        ).thenReturn(false);

        when(
                passwordEncoder.encode(
                        "StrongPass#2026"
                )
        ).thenReturn("encoded-password-hash");

        when(
                userRepository.save(
                        any(Customer.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Customer customer =
                userService.registerCustomer(request);

        assertNotNull(customer);
        assertNotNull(customer.getUserId());
        assertTrue(
                customer.getUserId()
                        .startsWith("USR-")
        );

        assertNotNull(customer.getCustomerNumber());
        assertTrue(
                customer.getCustomerNumber()
                        .startsWith("CUST-")
        );

        assertEquals(
                "Ava",
                customer.getAccountProfile()
                        .getFirstName()
        );

        assertEquals(
                "Mokoena",
                customer.getAccountProfile()
                        .getLastName()
        );

        assertEquals(
                "ava.mokoena@gmail.com",
                customer.getAccountProfile()
                        .getEmail()
        );

        assertEquals(
                "0821234567",
                customer.getAccountProfile()
                        .getPhoneNumber()
        );

        assertTrue(customer.isActive());

        verify(passwordEncoder).encode(
                "StrongPass#2026"
        );

        verify(userRepository).save(
                any(Customer.class)
        );
    }

    @Test
    void registerCustomerShouldNormalizeEmailAndNames() {

        RegisterRequest request = createRegisterRequest(
                "  Ava  ",
                "  Mokoena  ",
                "AVA.MOKOENA@GMAIL.COM",
                "082 123 4567"
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(false);

        when(
                userRepository.existsByPhoneNumber(
                        "0821234567"
                )
        ).thenReturn(false);

        when(
                passwordEncoder.encode(
                        "StrongPass#2026"
                )
        ).thenReturn("encoded-password-hash");

        when(
                userRepository.save(
                        any(Customer.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Customer customer =
                userService.registerCustomer(request);

        assertEquals(
                "Ava",
                customer.getAccountProfile()
                        .getFirstName()
        );

        assertEquals(
                "Mokoena",
                customer.getAccountProfile()
                        .getLastName()
        );

        assertEquals(
                "ava.mokoena@gmail.com",
                customer.getAccountProfile()
                        .getEmail()
        );

        assertEquals(
                "0821234567",
                customer.getAccountProfile()
                        .getPhoneNumber()
        );
    }

    @Test
    void registerCustomerShouldRejectDuplicateEmail() {

        RegisterRequest request = createRegisterRequest(
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567"
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(true);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> userService
                                .registerCustomer(request)
                );

        assertEquals(
                "An account with this email already exists",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                passwordEncoder,
                never()
        ).encode(anyString());
    }

    @Test
    void registerCustomerShouldRejectDuplicatePhoneNumber() {

        RegisterRequest request = createRegisterRequest(
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567"
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(false);

        when(
                userRepository.existsByPhoneNumber(
                        "0821234567"
                )
        ).thenReturn(true);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> userService
                                .registerCustomer(request)
                );

        assertEquals(
                "An account with this cellphone number already exists",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));

        verify(
                passwordEncoder,
                never()
        ).encode(anyString());
    }

    @Test
    void registerCustomerShouldRejectMismatchedPasswords() {

        RegisterRequest request = createRegisterRequest(
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567"
        );

        request.setConfirmPassword(
                "DifferentPass#2026"
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService
                                .registerCustomer(request)
                );

        assertEquals(
                "Passwords do not match",
                exception.getMessage()
        );

        verifyNoInteractions(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void getUserByEmailShouldReturnRegisteredUser() {

        Customer customer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                true
        );

        when(
                userRepository.findByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(Optional.of(customer));

        User foundUser =
                userService.getUserByEmail(
                        "AVA.MOKOENA@GMAIL.COM"
                );

        assertNotNull(foundUser);

        assertEquals(
                "USR-001",
                foundUser.getUserId()
        );

        assertEquals(
                "ava.mokoena@gmail.com",
                foundUser.getAccountProfile()
                        .getEmail()
        );
    }

    @Test
    void getUserByEmailShouldThrowWhenUserDoesNotExist() {

        when(
                userRepository.findByEmailIgnoreCase(
                        "missing.user@gmail.com"
                )
        ).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> userService.getUserByEmail(
                                "missing.user@gmail.com"
                        )
                );

        assertEquals(
                "User account was not found",
                exception.getMessage()
        );
    }

    @Test
    void getAllUsersShouldReturnStoredUsers() {

        Customer firstCustomer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                true
        );

        Customer secondCustomer = createCustomer(
                "USR-002",
                "CUST-002",
                "Noah",
                "Dlamini",
                "noah.dlamini@outlook.com",
                "0731234567",
                true
        );

        when(
                userRepository.findAll()
        ).thenReturn(
                List.of(
                        firstCustomer,
                        secondCustomer
                )
        );

        List<User> users =
                userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());

        assertEquals(
                "USR-001",
                users.get(0).getUserId()
        );

        assertEquals(
                "USR-002",
                users.get(1).getUserId()
        );

        verify(userRepository).findAll();
    }

    @Test
    void updateCurrentProfileShouldChangeAccountProfile() {

        Customer existingCustomer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                true
        );

        UpdateProfileRequest request =
                createUpdateProfileRequest(
                        "Ava",
                        "Mokoena",
                        "ava.new@gmail.com",
                        "0831234567"
                );

        when(
                userRepository.findByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(
                Optional.of(existingCustomer)
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "ava.new@gmail.com"
                )
        ).thenReturn(false);

        when(
                userRepository.existsByPhoneNumber(
                        "0831234567"
                )
        ).thenReturn(false);

        when(
                userRepository.save(
                        any(User.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        User updatedUser =
                userService.updateCurrentProfile(
                        "ava.mokoena@gmail.com",
                        request
                );

        assertNotNull(updatedUser);

        assertEquals(
                "Ava",
                updatedUser.getAccountProfile()
                        .getFirstName()
        );

        assertEquals(
                "Mokoena",
                updatedUser.getAccountProfile()
                        .getLastName()
        );

        assertEquals(
                "ava.new@gmail.com",
                updatedUser.getAccountProfile()
                        .getEmail()
        );

        assertEquals(
                "0831234567",
                updatedUser.getAccountProfile()
                        .getPhoneNumber()
        );

        verify(userRepository).save(
                existingCustomer
        );
    }

    @Test
    void updateCurrentProfileShouldRejectDuplicateNewEmail() {

        Customer existingCustomer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                true
        );

        UpdateProfileRequest request =
                createUpdateProfileRequest(
                        "Ava",
                        "Mokoena",
                        "existing.user@gmail.com",
                        "0821234567"
                );

        when(
                userRepository.findByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(
                Optional.of(existingCustomer)
        );

        when(
                userRepository.existsByEmailIgnoreCase(
                        "existing.user@gmail.com"
                )
        ).thenReturn(true);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> userService
                                .updateCurrentProfile(
                                        "ava.mokoena@gmail.com",
                                        request
                                )
                );

        assertEquals(
                "Another account already uses this email",
                exception.getMessage()
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));
    }

    @Test
    void deactivateCurrentAccountShouldSoftDeleteUser() {

        Customer customer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                true
        );

        when(
                userRepository.findByEmailIgnoreCase(
                        "ava.mokoena@gmail.com"
                )
        ).thenReturn(Optional.of(customer));

        when(
                userRepository.save(customer)
        ).thenReturn(customer);

        User deactivatedUser =
                userService.deactivateCurrentAccount(
                        "ava.mokoena@gmail.com"
                );

        assertFalse(deactivatedUser.isActive());

        verify(userRepository).save(customer);

        /*
         * The account is not deleted from MySQL.
         * It is only marked inactive.
         */
        verify(
                userRepository,
                never()
        ).delete(any(User.class));
    }

    @Test
    void deactivateUserShouldDeactivateAccountAsAdministrator() {

        Customer customer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                true
        );

        when(
                userRepository.findById("USR-001")
        ).thenReturn(Optional.of(customer));

        when(
                userRepository.save(customer)
        ).thenReturn(customer);

        User result =
                userService.deactivateUser(
                        "USR-001"
                );

        assertFalse(result.isActive());

        verify(userRepository).save(customer);
    }

    @Test
    void activateUserShouldRestoreInactiveAccount() {

        Customer customer = createCustomer(
                "USR-001",
                "CUST-001",
                "Ava",
                "Mokoena",
                "ava.mokoena@gmail.com",
                "0821234567",
                false
        );

        when(
                userRepository.findById("USR-001")
        ).thenReturn(Optional.of(customer));

        when(
                userRepository.save(customer)
        ).thenReturn(customer);

        User result =
                userService.activateUser(
                        "USR-001"
                );

        assertTrue(result.isActive());

        verify(userRepository).save(customer);
    }

    @Test
    void deactivateUserShouldThrowWhenUserDoesNotExist() {

        when(
                userRepository.findById(
                        "MISSING-USER"
                )
        ).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(
                        EntityNotFoundException.class,
                        () -> userService
                                .deactivateUser(
                                        "MISSING-USER"
                                )
                );

        assertEquals(
                "User account was not found",
                exception.getMessage()
        );
    }

    private RegisterRequest createRegisterRequest(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ) {
        RegisterRequest request =
                new RegisterRequest();

        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);
        request.setPassword(
                "StrongPass#2026"
        );
        request.setConfirmPassword(
                "StrongPass#2026"
        );

        return request;
    }

    private UpdateProfileRequest
    createUpdateProfileRequest(
            String firstName,
            String lastName,
            String email,
            String phoneNumber
    ) {
        UpdateProfileRequest request =
                new UpdateProfileRequest();

        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPhoneNumber(phoneNumber);

        return request;
    }

    private Customer createCustomer(
            String userId,
            String customerNumber,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            boolean active
    ) {
        AccountProfile profile =
                AccountProfile.builder()
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setEmail(email)
                        .setPhoneNumber(phoneNumber)
                        .build();

        return Customer.builder()
                .setUserId(userId)
                .setCustomerNumber(
                        customerNumber
                )
                .setAccountProfile(profile)
                .setPasswordHash(
                        "encoded-password-hash"
                )
                .setActive(active)
                .build();
    }
}