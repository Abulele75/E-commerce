package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.domain.AccountProfile;
import cput.ac.za.ecommerce.domain.Administrator;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.service.IUserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {
    private final IUserManagementService userService;

    @Autowired
    public UserManagementController(IUserManagementService userService) {
        this.userService = userService;
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody CreateCustomerRequest request) {
        return userService.registerCustomer(request.toProfile(), request.customerNumber());
    }

    @PostMapping("/administrators")
    public Administrator createAdministrator(@RequestBody CreateAdministratorRequest request) {
        return userService.registerAdministrator(request.toProfile(), request.employeeNumber(), request.department());
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.listUsers();
    }

    @PutMapping("/{userId}/profile")
    public User updateProfile(@PathVariable String userId, @RequestBody AccountProfile profile) {
        return userService.updateProfile(userId, profile);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.removeUser(userId);
    }

    public record CreateCustomerRequest(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String customerNumber
    ) {
        private AccountProfile toProfile() {
            return AccountProfile.builder()
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setEmail(email)
                    .setPhoneNumber(phoneNumber)
                    .build();
        }
    }

    public record CreateAdministratorRequest(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String employeeNumber,
            String department
    ) {
        private AccountProfile toProfile() {
            return AccountProfile.builder()
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setEmail(email)
                    .setPhoneNumber(phoneNumber)
                    .build();
        }
    }
}
