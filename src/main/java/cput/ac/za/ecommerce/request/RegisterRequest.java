package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(
            message = "First name is required"
    )
    @Size(
            min = 2,
            max = 50,
            message =
                    "First name must contain 2 to 50 characters"
    )
    @Pattern(
            regexp =
                    ValidationPatterns.PERSON_NAME,
            message =
                    "First name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String firstName;

    @NotBlank(
            message = "Last name is required"
    )
    @Size(
            min = 2,
            max = 50,
            message =
                    "Last name must contain 2 to 50 characters"
    )
    @Pattern(
            regexp =
                    ValidationPatterns.PERSON_NAME,
            message =
                    "Last name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String lastName;

    @NotBlank(
            message = "Email is required"
    )
    @Email(
            message =
                    "Enter a valid email address"
    )
    @Size(
            max = 254,
            message = "Email is too long"
    )
    @Pattern(
            regexp =
                    ValidationPatterns.ALLOWED_EMAIL,
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message =
                    "Use Gmail, Outlook, Hotmail, Live or a .co.za email address"
    )
    private String email;

    @NotBlank(
            message =
                    "Cellphone number is required"
    )
    @Pattern(
            regexp =
                    ValidationPatterns
                            .SOUTH_AFRICAN_MOBILE,
            message =
                    "Enter a valid 10-digit South African cellphone number"
    )
    private String phoneNumber;

    @NotBlank(
            message = "Password is required"
    )
    @Pattern(
            regexp =
                    ValidationPatterns
                            .STRONG_PASSWORD,
            message =
                    "Password must contain 12-64 characters, uppercase, lowercase, a number and a special character"
    )
    private String password;

    @NotBlank(
            message =
                    "Confirm password is required"
    )
    private String confirmPassword;

    public RegisterRequest() {
    }

    @AssertTrue(
            message =
                    "Password and confirm password must match"
    )
    public boolean isPasswordConfirmed() {
        return password != null
                && password.equals(confirmPassword);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(
            String firstName
    ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(
            String lastName
    ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(
            String phoneNumber
    ) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(
            String confirmPassword
    ) {
        this.confirmPassword =
                confirmPassword;
    }
}