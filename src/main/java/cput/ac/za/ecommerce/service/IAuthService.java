package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.request.LoginRequest;
import cput.ac.za.ecommerce.request.RegisterRequest;
import cput.ac.za.ecommerce.response.AuthResponse;
import cput.ac.za.ecommerce.response.UserResponse;

public interface IAuthService {


    UserResponse register(RegisterRequest request);

    AuthResponse login(
            LoginRequest request
    );

}