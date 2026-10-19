package cput.ac.za.ecommerce.response;

import cput.ac.za.ecommerce.domain.DeliveryAddress;

public record DeliveryAddressResponse(
        String recipientName,
        String recipientPhone,
        String streetAddress,
        String suburb,
        String city,
        String province,
        String postalCode,
        String country
) {

    public static DeliveryAddressResponse from(
            DeliveryAddress address
    ) {
        if (address == null) {
            return null;
        }

        return new DeliveryAddressResponse(
                address.getRecipientName(),
                address.getRecipientPhone(),
                address.getStreetAddress(),
                address.getSuburb(),
                address.getCity(),
                address.getProvince(),
                address.getPostalCode(),
                address.getCountry()
        );
    }
}