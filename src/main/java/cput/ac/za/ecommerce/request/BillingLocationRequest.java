package cput.ac.za.ecommerce.request;

import cput.ac.za.ecommerce.util.ValidationPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BillingLocationRequest {

    @NotBlank(
            message = "Billing name is required"
    )
    @Size(
            min = 2,
            max = 100,
            message =
                    "Billing name must contain 2 to 100 characters"
    )
    @Pattern(
            regexp = ValidationPatterns.PERSON_NAME,
            message =
                    "Billing name may contain letters, spaces, apostrophes and hyphens only"
    )
    private String billingName;

    @NotBlank(
            message = "Billing street address is required"
    )
    @Size(
            max = 200,
            message =
                    "Street address cannot exceed 200 characters"
    )
    private String streetAddress;

    @Size(
            max = 100,
            message =
                    "Suburb cannot exceed 100 characters"
    )
    private String suburb;

    @NotBlank(
            message = "Billing city is required"
    )
    @Size(max = 100)
    private String city;

    @NotBlank(
            message = "Billing province is required"
    )
    @Size(max = 100)
    private String province;

    @NotBlank(
            message = "Billing postal code is required"
    )
    @Pattern(
            regexp = "^\\d{4}$",
            message =
                    "Postal code must contain exactly 4 digits"
    )
    private String postalCode;

    @NotBlank(
            message = "Billing country is required"
    )
    @Size(max = 100)
    private String country = "South Africa";

    public BillingLocationRequest() {
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(
            String billingName
    ) {
        this.billingName = billingName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(
            String streetAddress
    ) {
        this.streetAddress = streetAddress;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(
            String suburb
    ) {
        this.suburb = suburb;
    }

    public String getCity() {
        return city;
    }

    public void setCity(
            String city
    ) {
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
}