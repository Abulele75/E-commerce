package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.request.ForgotPasswordRequest;
import cput.ac.za.ecommerce.request.LoginRequest;
import cput.ac.za.ecommerce.request.ResetPasswordRequest;
import cput.ac.za.ecommerce.response.AuthResponse;
import cput.ac.za.ecommerce.response.ForgotPasswordResponse;
import cput.ac.za.ecommerce.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse>
    forgotPassword(
            @Valid @RequestBody
            ForgotPasswordRequest request
    ) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(
                        authService.forgotPassword(request)
                );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody
            ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.noContent()
                .cacheControl(CacheControl.noStore())
                .build();
    }
}
