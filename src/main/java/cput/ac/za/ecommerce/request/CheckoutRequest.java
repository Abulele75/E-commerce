package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutRequest {

    @NotBlank(
            message =
                    "Recipient name is required"
    )
    @Size(
            min = 2,
            max = 100,
            message =
                    "Recipient name must contain 2 to 100 characters"
    )
    @Pattern(
            regexp =
                    ValidationPatterns.PERSON_NAME,
            message =
                    "Recipient name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String recipientName;

    @NotBlank(
            message =
                    "Recipient cellphone number is required"
    )
    @Pattern(
            regexp =
                    ValidationPatterns
                            .SOUTH_AFRICAN_MOBILE,
            message =
                    "Enter a valid 10-digit South African cellphone number"
    )
    private String recipientPhone;

    @NotBlank(
            message =
                    "Street address is required"
    )
    @Size(max = 200)
    private String streetAddress;

    @Size(max = 100)
    private String suburb;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(
            message = "Province is required"
    )
    @Size(max = 100)
    private String province;

    @NotBlank(
            message =
                    "Postal code is required"
    )
    @Pattern(
            regexp = "^\\d{4}$",
            message =
                    "Postal code must contain exactly 4 digits"
    )
    private String postalCode;

    @Size(max = 100)
    private String country =
            "South Africa";

    @Size(
            max = 500,
            message =
                    "Delivery instructions cannot exceed 500 characters"
    )
    private String deliveryInstructions;

    public CheckoutRequest() {
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(
            String recipientName
    ) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(
            String recipientPhone
    ) {
        this.recipientPhone =
                recipientPhone;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(
            String streetAddress
    ) {
        this.streetAddress =
                streetAddress;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(
            String province
    ) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(
            String postalCode
    ) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(
            String country
    ) {
        this.country = country;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(
            String deliveryInstructions
    ) {
        this.deliveryInstructions =
                deliveryInstructions;
    }
}