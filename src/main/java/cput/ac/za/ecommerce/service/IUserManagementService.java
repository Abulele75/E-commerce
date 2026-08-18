package cput.ac.za.ecommerce.service;

/*
   IUserManagementService.java
   Owenkosi Nxasana (230240887)
   Date: 10 August 2026
 */

import cput.ac.za.ecommerce.domain.AccountProfile;
import cput.ac.za.ecommerce.domain.Administrator;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.domain.User;

import java.util.List;

public interface IUserManagementService {
    Customer registerCustomer(AccountProfile profile, String customerNumber);

    Administrator registerAdministrator(AccountProfile profile, String employeeNumber, String department);

    User getUser(String userId);

    List<User> listUsers();

    User updateProfile(String userId, AccountProfile profile);

    void removeUser(String userId);
}
