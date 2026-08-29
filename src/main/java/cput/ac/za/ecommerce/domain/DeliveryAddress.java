
/*
  DeliveryAddress.java
  Value Object for delivery address
  Author: Sinazo Ntsimbi(222765208)
  Date:19 June 2026
 */
package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class DeliveryAddress {

    @Column(
            name = "delivery_recipient_name",
            nullable = false,
            length = 100
    )
    private String recipientName;

    @Column(
            name = "delivery_recipient_phone",
            nullable = false,
            length = 10
    )
    private String recipientPhone;

    @Column(
            name = "delivery_street_address",
            nullable = false,
            length = 200
    )
    private String streetAddress;

    @Column(
            name = "delivery_suburb",
            length = 100
    )
    private String suburb;

    @Column(
            name = "delivery_city",
            nullable = false,
            length = 100
    )
    private String city;

    @Column(
            name = "delivery_province",
            nullable = false,
            length = 100
    )
    private String province;

    @Column(
            name = "delivery_postal_code",
            nullable = false,
            length = 4
    )
    private String postalCode;

    @Column(
            name = "delivery_country",
            nullable = false,
            length = 100
    )
    private String country;

    protected DeliveryAddress() {
    }

    private DeliveryAddress(Builder builder) {
        this.recipientName = builder.recipientName;
        this.recipientPhone = builder.recipientPhone;
        this.streetAddress = builder.streetAddress;
        this.suburb = builder.suburb;
        this.city = builder.city;
        this.province = builder.province;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public static class Builder {

        private String recipientName;
        private String recipientPhone;
        private String streetAddress;
        private String suburb;
        private String city;
        private String province;
        private String postalCode;
        private String country = "South Africa";

        public Builder setRecipientName(
                String recipientName
        ) {
            this.recipientName = recipientName;
            return this;
        }

        public Builder setRecipientPhone(
                String recipientPhone
        ) {
            this.recipientPhone = recipientPhone;
            return this;
        }

        public Builder setStreetAddress(
                String streetAddress
        ) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder setSuburb(String suburb) {
            this.suburb = suburb;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setProvince(
                String province
        ) {
            this.province = province;
            return this;
        }

        public Builder setPostalCode(
                String postalCode
        ) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder setCountry(
                String country
        ) {
            this.country = country;
            return this;
        }

        public DeliveryAddress build() {
            return new DeliveryAddress(this);
        }
    }
}