package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(
            message = "Email address is required"
    )
    @Email(
            message = "Enter a valid email address"
    )
    @Size(
            max = 254,
            message = "Email address is too long"
    )
    @Pattern(
            regexp = ValidationPatterns.ALLOWED_EMAIL,
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message =
                    "Use a Gmail, Outlook, Hotmail, Live or .co.za email address"
    )
    private String email;

    @NotBlank(
            message = "Password is required"
    )
    @Size(
            max = 64,
            message = "Password cannot exceed 64 characters"
    )
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(
            String email,
            String password
    ) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }
}