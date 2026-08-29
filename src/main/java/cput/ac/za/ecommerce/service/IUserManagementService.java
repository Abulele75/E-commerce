package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;
import cput.ac.za.ecommerce.request.RegisterRequest;
import cput.ac.za.ecommerce.request.UpdateProfileRequest;

import java.util.List;

public interface IUserManagementService {

    Customer registerCustomer(
            RegisterRequest request
    );

    User getUserByEmail(
            String email
    );

    User updateCurrentProfile(
            String email,
            UpdateProfileRequest request
    );

    User deactivateCurrentAccount(
            String email
    );

    List<User> getAllUsers();

    User activateUser(
            String userId
    );

    User deactivateUser(
            String userId
    );
}