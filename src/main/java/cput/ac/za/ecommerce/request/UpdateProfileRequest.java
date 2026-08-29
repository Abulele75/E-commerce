package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Pattern(
            regexp = ValidationPatterns.PERSON_NAME,
            message =
                    "First name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    @Pattern(
            regexp = ValidationPatterns.PERSON_NAME,
            message =
                    "Last name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Pattern(
            regexp = ValidationPatterns.ALLOWED_EMAIL,
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message =
                    "Use Gmail, Outlook, Hotmail, Live or a .co.za email"
    )
    private String email;

    @NotBlank(
            message = "Cellphone number is required"
    )
    @Pattern(
            regexp =
                    ValidationPatterns.SOUTH_AFRICAN_MOBILE,
            message =
                    "Enter a valid 10-digit South African cellphone number"
    )
    private String phoneNumber;

    public UpdateProfileRequest() {
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
}