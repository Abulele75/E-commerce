
/*
   BillingLocation.java
   Ngwana Tiyani (231266731)
   Date: 20 June 2026
 */
package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BillingLocation {

    @Column(
            name = "billing_name",
            nullable = false,
            length = 100
    )
    private String billingName;

    @Column(
            name = "billing_street_address",
            nullable = false,
            length = 200
    )
    private String streetAddress;

    @Column(
            name = "billing_suburb",
            length = 100
    )
    private String suburb;

    @Column(
            name = "billing_city",
            nullable = false,
            length = 100
    )
    private String city;

    @Column(
            name = "billing_province",
            nullable = false,
            length = 100
    )
    private String province;

    @Column(
            name = "billing_postal_code",
            nullable = false,
            length = 4
    )
    private String postalCode;

    @Column(
            name = "billing_country",
            nullable = false,
            length = 100
    )
    private String country;

    protected BillingLocation() {
    }

    private BillingLocation(Builder builder) {
        this.billingName = builder.billingName;
        this.streetAddress = builder.streetAddress;
        this.suburb = builder.suburb;
        this.city = builder.city;
        this.province = builder.province;
        this.postalCode = builder.postalCode;
        this.country = builder.country;
    }

    public String getBillingName() {
        return billingName;
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

        private String billingName;
        private String streetAddress;
        private String suburb;
        private String city;
        private String province;
        private String postalCode;
        private String country = "South Africa";

        public Builder setBillingName(
                String billingName
        ) {
            this.billingName = billingName;
            return this;
        }

        public Builder setStreetAddress(
                String streetAddress
        ) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder setSuburb(
                String suburb
        ) {
            this.suburb = suburb;
            return this;
        }

        public Builder setCity(
                String city
        ) {
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

        public BillingLocation build() {
            return new BillingLocation(this);
        }
    }
}