package cput.ac.za.ecommerce.service;

import cput.ac.za.ecommerce.request.ForgotPasswordRequest;
import cput.ac.za.ecommerce.request.LoginRequest;
import cput.ac.za.ecommerce.request.ResetPasswordRequest;
import cput.ac.za.ecommerce.response.AuthResponse;
import cput.ac.za.ecommerce.response.ForgotPasswordResponse;

public interface IAuthService {

    AuthResponse login(LoginRequest request);

    ForgotPasswordResponse forgotPassword(
            ForgotPasswordRequest request
    );

    void resetPassword(
            ResetPasswordRequest request
    );
}
